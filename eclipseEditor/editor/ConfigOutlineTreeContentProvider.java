package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ca.yuanhuicheng.core.directory.exception.LoadTurtleUriException;
import ca.yuanhuicheng.definition.DirectoryConstants;
import ca.yuanhuicheng.tools.Assert;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedResourceTriplePart;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTriple;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTriplePart;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TextLocationRange;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TriplesComponentsLocator;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TwConfigParser;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.CollaborationConfiguration;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.Configuration;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ConfigurationFactory;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.NonResourceConfiguration;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ParticipantConfiguration;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ParticipantDeclaration;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ResourceDefinitionOrComponent;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ResourceType;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ShareIdDeclaration;
import ca.yuanhuicheng.tools.eclipse.plugin.command.IDETurtleLoader;
import ca.yuanhuicheng.tools.eclipse.plugin.eclipse.EclipseLogger;
import ca.yuanhuicheng.tools.eclipse.plugin.ide.state.PluginState;
import ca.yuanhuicheng.tools.ide_plugin_core.MessagePriority;

/**
 * A {@link ConfigOutlineTreeContentProvider} provides the content for the outline tree view.
 */
public class ConfigOutlineTreeContentProvider implements ITreeContentProvider
{
	/**
	 * Create a configuration outline tree content provider; all parameters must be non-{@code null}.
	 * @param twConfigEditor editor from which to read current content to determine child element line numbers for sort
	 *            ordering
	 * @param fileUri document URI of the configuration file - used as the initial base URI of the configuration TTL
	 */
	public ConfigOutlineTreeContentProvider(final ConfigEditor twConfigEditor, final URI fileUri)
	{
		Assert.notNullWithoutMessage(twConfigEditor, fileUri);
		this.twConfigEditor = twConfigEditor;
		this.fileUri = fileUri;
	}

	@Override
	public void dispose()
	{
		// don't need to do anything on dispose() - yet
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
		declaredResource = new ConfigurationFactory().create(newInput == null ? null : newInput.toString(), fileUri, null);
	}

	@Override
	public Object[] getChildren(final Object parentElement)   // NOPMD - don't want to return empty array
	{
		Object[] children = new Object[0];
		
		if (parentElement instanceof Configuration)
		{
			final Configuration twConfig = (Configuration) parentElement;
			if (twConfig.isResourceDefinition())
			{
				final ResourceType resourceType = twConfig.getResourceType();
				if (resourceType.equals(ResourceType.COLLABORATION))
				{
					final CollaborationConfiguration  parentCollaboration = (CollaborationConfiguration) parentElement;
					final Set<ParticipantDeclaration> childSet = new LinkedHashSet<ParticipantDeclaration>();
					childSet.addAll(parentCollaboration.getHasParticipants());
					final Collection<ParticipantDeclaration> orderedSet = orderChildrenForCollaboration(childSet);
					children = orderedSet.toArray(new ResourceDefinitionOrComponent[orderedSet.size()]);			
				}
				else if (resourceType.equals(ResourceType.PARTICIPANT))
				{
					final ParticipantConfiguration parentParticipant = (ParticipantConfiguration) parentElement;
					final Set<ResourceDefinitionOrComponent> childSet = new LinkedHashSet<ResourceDefinitionOrComponent>();
					childSet.addAll(parentParticipant.getHasShares());
					childSet.addAll(parentParticipant.getHasConsumeRequests());
					// we have to sort these all in order of appearance in the source file
					final Collection<ResourceDefinitionOrComponent> orderedSet = orderChildren(childSet);
					children = orderedSet.toArray(new ResourceDefinitionOrComponent[orderedSet.size()]);
				}
			}
		}
//		else if (parentElement instanceof ParticipantDeclaration)
//		{
//			final ParticipantDeclaration parentParticipant = (ParticipantDeclaration) parentElement;
//			children = orderChildrenForParticipantsOfCollaboration(parentParticipant).toArray();
//		}
		
		
		return children;
	}

	private Collection<ResourceDefinitionOrComponent> orderChildren(final Set<ResourceDefinitionOrComponent> children)
	{
		// values will be sorted according to keys
		final Map<Integer, ResourceDefinitionOrComponent> orderedChildren = new TreeMap<Integer, ResourceDefinitionOrComponent>();

		// need a map of URIs to children
		final Map<URI, ResourceDefinitionOrComponent> urisToChildren = new HashMap<URI, ResourceDefinitionOrComponent>();
		for (final ResourceDefinitionOrComponent child : children)
		{
			urisToChildren.put(child.getUri(), child);
		}
		final Set<URI> keySet = urisToChildren.keySet();

		// parse the TTL file and order the child URIs according to location
		final TriplesComponentsLocator tripleFinder = new TriplesComponentsLocator(fileUri, null);
		new TwConfigParser().parseTtl(getEditorInputText(), null, tripleFinder);
		for (final LocatedTriple locatedTriple : tripleFinder.getFoundTriples())
		{
			final LocatedResourceTriplePart subject = locatedTriple.getSubject();
			if (locatedTriple.getPredicate().getResourceUri().toASCIIString().equals(DirectoryConstants.TYPE_URI))
			{
				final LocatedResourceTriplePart object =  (LocatedResourceTriplePart)locatedTriple.getObject();
				if (keySet.contains(object.getResourceUri()))
				{
					orderedChildren.put(object.getSourceLocationRange().getStartOffset(), urisToChildren.get(object.getResourceUri()));
				}
			}
			
			if (keySet.contains(subject.getResourceUri()))
			{
				orderedChildren.put(subject.getSourceLocationRange().getStartOffset(), urisToChildren.get(subject.getResourceUri()));
			}
		}
		// return values in ascending order of keys
		return orderedChildren.values();
	}
	
	private Collection<ParticipantDeclaration> orderChildrenForCollaboration(final Set<ParticipantDeclaration> children)
	{
		// values will be sorted according to keys
		final Map<Integer, ParticipantDeclaration> orderedChildren =
				new TreeMap<Integer, ParticipantDeclaration>();

		// need a map of URIs to children
		final Map<URI, ParticipantDeclaration> urisToChildren =
				new HashMap<URI, ParticipantDeclaration>();
		
		for (final ParticipantDeclaration child : children)
		{
			urisToChildren.put(child.getUri(), child);
//			if (ifParticipantFileExists(child.getUri()))
//			{
//				urisToChildren.put(child.getUri(), child);
//			}
		}
		
		final Set<URI> keySet = urisToChildren.keySet();
		
		// parse the TTL file and order the child URIs according to location
		final TriplesComponentsLocator tripleFinder = new TriplesComponentsLocator(fileUri, null);
		new TwConfigParser().parseTtl(getEditorInputText(), null, tripleFinder);
		
		for (final LocatedTriple locatedTriple : tripleFinder.getFoundTriples())
		{
			if (locatedTriple.getPredicate().getResourceUri().toASCIIString().equals(DirectoryConstants.HAS_PARTICIPANT_URI))
			{
				final LocatedResourceTriplePart object = (LocatedResourceTriplePart) locatedTriple.getObject();
				URI objectUri = object.getResourceUri();
				ParticipantDeclaration parent = urisToChildren.get(object.getResourceUri());
				if (keySet.contains(objectUri))
				{
					if (ifParticipantFileExists(objectUri))
					{
						final IFile participantFile = (IFile) PluginState.PARTIC_FILE_MAP.getResourceUriFile(objectUri).getFile();
						final TriplesComponentsLocator ptTripleFinder = new TriplesComponentsLocator(participantFile.getLocationURI(), null);
						try 
						{
							String label = null;
							String type = null;
							new TwConfigParser().parseTtl(IOUtils.toString(participantFile.getContents(), StandardCharsets.UTF_8.name()), null, ptTripleFinder);
							
							for (final LocatedTriple locatedPtTriple : ptTripleFinder.getFoundTriples())
							{
								final String labelPredicateStr = locatedPtTriple.getPredicate().getResourceUri().toASCIIString();
								LocatedTriplePart obj = locatedPtTriple.getObject();
								TextLocationRange locationRange = obj.getSourceLocationRange();
								
								if (labelPredicateStr.equals(DirectoryConstants.NAME_URI))
								{
									label = removeLocationRange(obj, locationRange);
									parent.setLabel(label);
								}
								else if (labelPredicateStr.equals(DirectoryConstants.TYPE_URI))
								{
									type = removeLocationRange(obj, locationRange);
									parent.setType(type);
								}
								if (label != null && type != null)
								{
									break;
								}
							}
						} 
						catch (IOException e)
						{
							final String failedToConvertExceptionMsg = "Failed to convert the contents in " + participantFile.getName() + "to string";
							EclipseLogger.createDefault().log(failedToConvertExceptionMsg + ".", e, MessagePriority.ERROR);
						} catch (CoreException e) 
						{
							final String failedToConvertExceptionMsg = "Failed to convert the contents in " + participantFile.getName() + "to string";
							EclipseLogger.createDefault().log(failedToConvertExceptionMsg + ".", e, MessagePriority.ERROR);
						}
					}
					
					
					orderedChildren.put(object.getSourceLocationRange().getStartOffset(), parent);
				}
			}
		}
		
		return orderedChildren.values();
	}
	
//	private List<ParticipantForCollaborationDeclaration> orderChildrenForParticipantsOfCollaboration(final ParticipantDeclaration parentParticipant)
//	{
//		final URI participantUri = parentParticipant.getUri();
//		List<ParticipantForCollaborationDeclaration> participantList;participantList = new ArrayList<ParticipantForCollaborationDeclaration>();
//		String type = null;
//		
//		final IFile participantFile = (IFile) PluginState.PARTIC_FILE_MAP.getResourceUriFile(participantUri).getFile();
//		final TriplesComponentsLocator ptTripleFinder = new TriplesComponentsLocator(participantFile.getLocationURI(), null);
//		try
//		{
//			new TwConfigParser().parseTtl(IOUtils.toString(participantFile.getContents(), StandardCharsets.UTF_8.name()), null, ptTripleFinder);
//			
//			for (final LocatedTriple locatedPtTriple : ptTripleFinder.getFoundTriples())
//			{
//				final String labelPredicateStr = locatedPtTriple.getPredicate().getResourceUri().toASCIIString();
//				LocatedTriplePart object = locatedPtTriple.getObject();
//				TextLocationRange locationRange = object.getSourceLocationRange();
//				
//				if (labelPredicateStr.equals(DirectoryConstants.TYPE_URI))
//				{
//					type = TYPE_PREFIX + removeLocationRange(object, locationRange).replace("<", "").replace(">", "");
//				}
//				
//				if (type!= null)
//				{
//					participantList.add(new ParticipantForCollaborationDeclaration(type));
//					break;
//				}
//			}
//			
//		}
//		catch (IOException e)
//		{
//			final String failedToConvertExceptionMsg = "Failed to convert the contents in " + participantFile.getName() + "to string";
//			EclipseLogger.createDefault().log(failedToConvertExceptionMsg + ".", e, MessagePriority.ERROR);
//		}
//		catch (CoreException e)
//		{
//			final String failedToConvertExceptionMsg = "Failed to convert the contents in " + participantFile.getName() + "to string";
//			EclipseLogger.createDefault().log(failedToConvertExceptionMsg + ".", e, MessagePriority.ERROR);
//		}
//		
//		return participantList;
//	}
	
	private String removeLocationRange(final LocatedTriplePart object, final TextLocationRange locationRange)
	{
		return object.toString().replace(String.format(STRING_FORMAT, object.getSourceLocationRange().getLine(), locationRange
				.getStartOffset(), locationRange.getEndOffset()), "");
	}

	@Override
	public Object[] getElements(final Object inputElement)
	{
		return new Object[] { declaredResource };
	}

	@Override
	public Object getParent(final Object element)
	{
		if (element instanceof ParticipantDeclaration || element instanceof ShareIdDeclaration)
		{  
			return declaredResource;
		}
		else 
		{
			return null;
		}
	}

	@Override
	public boolean hasChildren(final Object element)
	{
		boolean hasChildren = false;
		if (element instanceof Configuration)
		{
			final Configuration twConfig = (Configuration) element;
			if (twConfig.isResourceDefinition() && twConfig.getResourceType() == ResourceType.COLLABORATION)
			{
				final CollaborationConfiguration twCollabDefCfg = (CollaborationConfiguration) twConfig;
				for (ParticipantDeclaration ptDeclaration : twCollabDefCfg.getHasParticipants())
				{
					if (ifParticipantFileExists(ptDeclaration.getUri()))
					{
						hasChildren = true;
						break;
					}
				}
			}
			else if (twConfig.isResourceDefinition() && twConfig.getResourceType() == ResourceType.PARTICIPANT)
			{
				final ParticipantConfiguration twParticDefCfg = (ParticipantConfiguration) twConfig;   // NOSONAR
				hasChildren =
						!twParticDefCfg.getHasShares().isEmpty() || !twParticDefCfg.getHasConsumeRequests().isEmpty();
			}
		}
//		else if (element instanceof ParticipantDeclaration)
//		{
//			ParticipantDeclaration ptDeclaration = (ParticipantDeclaration)element;
//			hasChildren = ifParticipantFileExists(ptDeclaration.getUri())? true : false;
//		}
		
		return hasChildren;
	}
	
	private boolean ifParticipantFileExists(URI participantUrl)
	{
		final IDETurtleLoader turtleLoader = new IDETurtleLoader(PluginState.COLLAB_DOC_MAP, PluginState.COLLAB_FILE_MAP,
				 												 PluginState.PARTIC_DOC_MAP, PluginState.PARTIC_FILE_MAP);
		
		try 
		{
			return turtleLoader.participantExists(participantUrl.toASCIIString())? true : false;
		}
		catch (LoadTurtleUriException e)
		{
			final String failedToLoadTurtleMsg = "Failed to load turtle url: " + participantUrl.toASCIIString();
			EclipseLogger.createDefault().log(failedToLoadTurtleMsg + ".", e, MessagePriority.ERROR);
		}
		
		return false;
	}

	private String getEditorInputText()
	{
		return twConfigEditor.getDocumentProvider().getDocument(twConfigEditor.getEditorInput()).get();
	}

	private Configuration declaredResource = new NonResourceConfiguration("editor TTL not yet received");
    
	/** The format string used by {@link #toString()}. */
	private static final String STRING_FORMAT = "@(%d,%d-%d)";
	private final TwConfigEditor twConfigEditor;
	private final URI fileUri;
}