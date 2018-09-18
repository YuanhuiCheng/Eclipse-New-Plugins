package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import ca.yuanhuicheng.core.directory.exception.LoadTurtleUriException;
import ca.yuanhuicheng.definition.DirectoryConstants;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedResourceTriplePart;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTriple;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTriplePart;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TextLocationRange;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TriplesComponentsLocator;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TwConfigParser;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.Configuration;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ConsumeRequestDeclaration;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ParticipantDeclaration;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ResourceDefinitionComponent;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ResourceDefinitionOrComponent;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ShareIdDeclaration;
import ca.yuanhuicheng.tools.eclipse.plugin.command.IDETurtleLoader;
import ca.yuanhuicheng.tools.eclipse.plugin.eclipse.EclipseLogger;
import ca.yuanhuicheng.tools.eclipse.plugin.ide.state.PluginState;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.JFaceUIPrimitives;
import ca.yuanhuicheng.tools.ide_plugin_core.MessagePriority;

/**
 * A {@link ConfigFileOutlinePage} provides a model for an Outline View of a yuanhuicheng configuration file editor
 * window's content.
 */
public class ConfigFileOutlinePage extends ContentOutlinePage
{
	/**
	 * Create a Configuration outline page for the given yuanhuicheng Configuration file editor
	 * @param twConfigEditor editor for which to create a TW Config outline page - must have input of type
	 *            {@link IFileEditorInput}
	 */
	public ConfigFileOutlinePage(final ConfigEditor twConfigEditor)
	{
		super();
		final IEditorInput twConfigEditorInput = twConfigEditor.getEditorInput();
		if (!(twConfigEditorInput instanceof IFileEditorInput))
		{
			throw new IllegalArgumentException("yuanhuicheng configuration editor input must be a file");
		}
		this.twConfigEditor = twConfigEditor;
		configFileUri = FileEditorInputUtil.getRestoredEditorInputFileUri((IFileEditorInput) twConfigEditorInput);
		twCfgOutlineContent = new TwConfigOutlineTreeContentProvider(twConfigEditor, configFileUri);
	}

	@Override
	public void createControl(final Composite parent)   // NOPMD - spurious UR anomaly on 'twConfig'
	{
		super.createControl(parent);
		createActions();
		createToolbar();
		treeViewer = getTreeViewer();
		treeViewer.setContentProvider(twCfgOutlineContent);
		treeViewer.setLabelProvider(new TwConfigOutlineTreeLabelProvider(switchLabelAndUrl));
		if (twConfigEditor.getDocumentProvider() != null
				&& twConfigEditor.getDocumentProvider().getDocument(twConfigEditor.getEditorInput()) != null)
		{
			treeViewer.setInput(getEditorInputText());
		}
		
		treeViewer.expandAll();
		doubleClickSelectionToOpenAndReveal(treeViewer);
		
		final MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu("NewOutlineViewerMenu", menuManager, treeViewer);
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event)
	{
		// catalog all subject URI locations
		final TriplesComponentsLocator tripleFinder = new TriplesComponentsLocator(configFileUri, null);
		new TwConfigParser().parseTtl(getEditorInputText(), null, tripleFinder);

		@SuppressWarnings("rawtypes")
		// sorry - the Eclipse API method just isn't generic
		final Iterator selectionIterator = ((TreeSelection) event.getSelection()).iterator();
		while (selectionIterator.hasNext())
		{
			final Object selectedElement = selectionIterator.next();
			final URI selectedURI;
			if (selectedElement instanceof Configuration)
			{
				selectedURI = ((Configuration) selectedElement).getResourceUri();
				setSubjectSelectionInEditor(selectedURI, tripleFinder);
			}
			else if (selectedElement instanceof ParticipantDeclaration)
			{
				selectedURI = ((ParticipantDeclaration) selectedElement).getUri();
				setParticipantSelectionInEditor(selectedURI, tripleFinder);
			}
			else if (selectedElement instanceof ConsumeRequestDeclaration || selectedElement instanceof ShareIdDeclaration)
			{
				final ResourceDefinitionOrComponent component = (ResourceDefinitionOrComponent) selectedElement;
				selectedURI = component.getUri();
				setSubjectSelectionInEditor(selectedURI, tripleFinder);
			}
		}
	}
	
	private void doubleClickSelectionToOpenAndReveal(final TreeViewer treeViewer)
	{
		treeViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			@Override
		    public void doubleClick(DoubleClickEvent event)
		    {
				final Iterator selectionIterator = ((TreeSelection) event.getSelection()).iterator();
				IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
				while (selectionIterator.hasNext())
				{
					final Object selectedElement = selectionIterator.next();
					final URI selectedURI;
					if (selectedElement instanceof ParticipantDeclaration)
					{
						selectedURI = ((ParticipantDeclaration) selectedElement).getUri();
						if (ifParticipantFileExists(selectedURI))
						{
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							final IFile participantFile = (IFile) PluginState.PARTIC_FILE_MAP.getResourceUriFile(selectedURI).getFile();
							try
							{
								IEditorPart editorPart = IDE.openEditor(page, participantFile);
								if (editorPart instanceof ITextEditor)
								{
									final TriplesComponentsLocator tripleFinder = new TriplesComponentsLocator(selectedURI, null);
									new TwConfigParser().parseTtl(IOUtils.toString(participantFile.getContents(), StandardCharsets.UTF_8.name()), null, tripleFinder);
									setSelectionInTargetedParticipantFileInEditor((ITextEditor) editorPart, selectedURI, tripleFinder);
								}
							} 
							catch (PartInitException e)
							{
								final String failedToOpenParticipantFile = "The participant file, " + participantFile.getName() + "cannot be opened in the file system";
								EclipseLogger.createDefault().log(failedToOpenParticipantFile + ".", e, MessagePriority.ERROR);
							}
							catch (IOException e)
							{
								final String failedToConvertExceptionMsg = "Failed to convert the contents in " + participantFile.getName() + "to string";
								EclipseLogger.createDefault().log(failedToConvertExceptionMsg + ".", e, MessagePriority.ERROR);
							}
							catch (CoreException e)
							{
								final String failedToConvertExceptionMsg = "Failed to convert the contents in " + participantFile.getName() + "to string";
								EclipseLogger.createDefault().log(failedToConvertExceptionMsg + ".", e, MessagePriority.ERROR);
							}
						}
						else
						{
							final String failedToFindParticipantFile = "The participant file cannot be found in the file system";
							new JFaceUIPrimitives().displayMessage(TITLE, failedToFindParticipantFile, MessagePriority.INFO);
						}
					}
				}
				
		    }
		}
		);
	}
	
	private void setSubjectSelectionInEditor(final URI selectedURI, final TriplesComponentsLocator tripleFinder)
	{
		for (final LocatedTriple locatedTriple : tripleFinder.getFoundTriples())
		{
			final LocatedResourceTriplePart subject = locatedTriple.getSubject();
			if (subject.getResourceUri().equals(selectedURI))
			{
				setSelectionInEditor(subject);
				break;
			}
		}
	}
	
	private void setParticipantSelectionInEditor(final URI selectedURI, final TriplesComponentsLocator tripleFinder)
	{
		for (final LocatedTriple locatedTriple : tripleFinder.getFoundTriples())
		{
			String currentResourcePredicate = locatedTriple.getPredicate().getResourceUri().toASCIIString();
			if (currentResourcePredicate.equals(DirectoryConstants.HAS_PARTICIPANT_URI))
			{
				final LocatedTriplePart object = locatedTriple.getObject();
				final URI participantUri = ((LocatedResourceTriplePart) object).getResourceUri();
				if (participantUri.equals(selectedURI))
				{
					setSelectionInEditor(object);
					break;
				}
			}
		}
	}
	
	private void setSelectionInEditor(final LocatedTriplePart triplePart)
	{
		final IEditorInput editorInput = twConfigEditor.getEditorInput();
		final TextLocationRange locationRange = triplePart.getSourceLocationRange();
		final int length = locationRange.getEndOffset() - locationRange.getStartOffset() + 1;
		final ISelection editorSelection =
				new TextSelection(   
						twConfigEditor.getDocumentProvider().getDocument(editorInput), locationRange
								.getStartOffset(), length);
		twConfigEditor.getSelectionProvider().setSelection(editorSelection);
	}
	
	private void setSelectionInTargetedParticipantFileInEditor(final ITextEditor textEditor, final URI selectedURI, final TriplesComponentsLocator tripleFinder)
	{
		for (final LocatedTriple locatedTriple : tripleFinder.getFoundTriples())
		{
			final LocatedResourceTriplePart subject = locatedTriple.getSubject();
			if (subject.getResourceUri().equals(selectedURI))
			{
				final TextLocationRange locationRange = subject.getSourceLocationRange();
				final int length = locationRange.getEndOffset() - locationRange.getStartOffset() + 1;
				//IDocument document= textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
				textEditor.selectAndReveal(locationRange.getStartOffset(), length);
				break;
			}
		}
	}

	/**
	 * Update the contents of the outline page based on the current {@link TextEditor} content.
	 */
	public void update()
	{
		final TreeViewer viewer = getTreeViewer();
		if (viewer != null)
		{
			final Control control = viewer.getControl();
			if (control != null && !control.isDisposed())
			{
				control.setRedraw(false);
				viewer.setInput(getEditorInputText());
				viewer.expandAll();
				control.setRedraw(true);
			}
		}
	}

	private String getEditorInputText()
	{
		return twConfigEditor.getDocumentProvider().getDocument(twConfigEditor.getEditorInput()).get();
	}
	
	private void createActions()
	{
		switchLabelAndUrl = new Action("Switch between Url and Label") 
		{
			public void run()
			{
				TreeItem[] treeItems = treeViewer.getTree().getItems();
				for (TreeItem config : treeItems)
				{
					for (TreeItem declaration : config.getItems())
					{
						Object declarationData = declaration.getData();
						if (declaration.getData() instanceof ParticipantDeclaration)
						{
							ParticipantDeclaration pd = (ParticipantDeclaration) declarationData;
							final String label = pd.getLabel();
							if (!switchLabelAndUrl.isChecked() || label.trim().length() == 0)
							{
								//switch to url
								declaration.setText(getUriForDeclarations(pd));
							}
							else
							{
								//switch to label
								declaration.setText(surroundWithBrackets(label));
							}
						}
						else if (declarationData instanceof ConsumeRequestDeclaration)
						{
							ConsumeRequestDeclaration crd = (ConsumeRequestDeclaration) declarationData;
							final String label = crd.getLabel();
							if (!switchLabelAndUrl.isChecked() || label.trim().length() == 0)
							{
								//switch to url
								declaration.setText(getUriForDeclarations(crd));
							}
							else
							{
								//switch to label
								declaration.setText(surroundWithBrackets(label));
							}
						}
						else if (declarationData instanceof ShareIdDeclaration)
						{
							//switch to url
							ShareIdDeclaration sid = (ShareIdDeclaration) declarationData;
							final String label = sid.getLabel();
							if (!switchLabelAndUrl.isChecked() || label.trim().length() == 0)
							{
								declaration.setText(getUriForDeclarations(sid));
							}
							else
							{
								//switch to label
								declaration.setText(surroundWithBrackets(label));
							}
						}
					}
					
				}
			}
		};
		switchLabelAndUrl.setImageDescriptor(getImageDescriptor("switch_label_uri_16x16.png"));
		switchLabelAndUrl.setChecked(false);
	}
	
	private void createToolbar()
	{
		IToolBarManager mgr = getSite().getActionBars().getToolBarManager();
        mgr.add(switchLabelAndUrl);
	}
	
	private String getUriForDeclarations(final ResourceDefinitionComponent rdc)
	{
		return surroundWithBrackets(rdc.getUri().toASCIIString().replace(rdc.getParentUri().toASCIIString(), ""));
	}
	
	private ImageDescriptor getImageDescriptor(final String file) 
	{
		final Bundle bundle = FrameworkUtil.getBundle(getClass());
		final URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
		final ImageDescriptor image = ImageDescriptor.createFromURL(url);
		
		return image;
	}
	
	private String surroundWithBrackets(final String originalStr)
	{
		return String.format("<%s>", originalStr);
	}
	
	private boolean ifParticipantFileExists(final URI participantUrl)
	{
		final IDETurtleLoader turtleLoader = new IDETurtleLoader(PluginState.COLLAB_DOC_MAP, PluginState.COLLAB_FILE_MAP,
				 												 PluginState.PARTIC_DOC_MAP, PluginState.PARTIC_FILE_MAP);
		
		try 
		{
			return turtleLoader.participantExists(participantUrl.toASCIIString())? true : false;
		}
		catch (LoadTurtleUriException e)
		{
			final String failedToLoadTurtleUriMsg = "Failed to load turtle uri, " + participantUrl.toASCIIString();
			EclipseLogger.createDefault().log(failedToLoadTurtleUriMsg + ".", e, MessagePriority.ERROR);
		}
		
		return false;
	} 
	
	private Action switchLabelAndUrl;
	private TreeViewer treeViewer;

	private final URI configFileUri;
	private final TwConfigEditor twConfigEditor;
	private final TwConfigOutlineTreeContentProvider twCfgOutlineContent;
	private static final String STRING_FORMAT = "@(%d,%d-%d)";
	
	private static final String TITLE = "Operation Unavailable";
}