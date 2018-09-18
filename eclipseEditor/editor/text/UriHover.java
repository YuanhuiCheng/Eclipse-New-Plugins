package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.text;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IFileEditorInput;

import ca.yuanhuicheng.core.directory.exception.LoadTurtleUriException;
import ca.yuanhuicheng.definition.DirectoryConstants;
import ca.yuanhuicheng.definition.NamespaceConstants;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedResourceTriplePart;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTriple;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTripleList;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTriplePart;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TextLocationRange;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TriplesComponentsLocator;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TwConfigParser;
import ca.yuanhuicheng.tools.eclipse.plugin.command.IDETurtleLoader;
import ca.yuanhuicheng.tools.eclipse.plugin.eclipse.EclipseLogger;
import ca.yuanhuicheng.tools.eclipse.plugin.ide.state.PluginState;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.FileEditorInputUtil;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.TwConfigEditor;
import ca.yuanhuicheng.tools.ide_plugin_core.MessagePriority;

public class UriHover extends DefaultTextHover implements ITextHoverExtension, ITextHoverExtension2
{

	public UriHover(final ISourceViewer sourceViewer, final TwConfigEditor twConfigEditor)
	{
		super(sourceViewer);
		this.twConfigEditor = twConfigEditor;
	}
	
	@Override
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion)
	{
		String fullUri = null;
		String label = null;
		String type = null;
		String comment = null;
		
		final String currentDocumentTtl = textViewer.getDocument().get();
		final URI fileUri =
				FileEditorInputUtil.getRestoredEditorInputFileUri((IFileEditorInput) twConfigEditor.getEditorInput());
		TriplesComponentsLocator  tripleFinder = new TriplesComponentsLocator(fileUri, null);
		new TwConfigParser().parseTtl(currentDocumentTtl, null, tripleFinder);
		LocatedTripleList triplesFound = tripleFinder.getFoundTriples();
		for(LocatedTriple foundTriple : triplesFound)
		{
			if(isCursorLocationInRange(hoverRegion.getOffset(), foundTriple.getObject().getSourceLocationRange()))
			{
				String currentResourcePredicate = foundTriple.getPredicate().getResourceUri().toASCIIString();
				if(currentResourcePredicate.equals(DirectoryConstants.HAS_PARTICIPANT_URI))
				{
					LocatedResourceTriplePart triplePart = (LocatedResourceTriplePart)foundTriple.getObject();
					fullUri = removeLocationRange(triplePart, triplePart.getSourceLocationRange()).replace("<", "").replace(">", "");
					if (ifParticipantFileExists(((LocatedResourceTriplePart)foundTriple.getObject()).getResourceUri()))
					{
						isPtFound = true;
						final IFile participantFile = (IFile) PluginState.PARTIC_FILE_MAP.getResourceUriFile(triplePart.getResourceUri()).getFile();
						final TriplesComponentsLocator ptTripleFinder = new TriplesComponentsLocator(participantFile.getLocationURI(), null);
						try
						{
							new TwConfigParser().parseTtl(IOUtils.toString(participantFile.getContents(), StandardCharsets.UTF_8.name()), null, ptTripleFinder);
							for (final LocatedTriple locatedPtTriple : ptTripleFinder.getFoundTriples())
							{
								String predicateStr = locatedPtTriple.getPredicate().getResourceUri().toASCIIString();
								LocatedTriplePart obj = locatedPtTriple.getObject();
								TextLocationRange locationRange = obj.getSourceLocationRange();
								
								if (predicateStr.equals(DirectoryConstants.NAME_URI))
								{
									label = removeLocationRange(obj, locationRange);
								}
								else if (predicateStr.equals(DirectoryConstants.TYPE_URI))
								{
									type = removeLocationRange(obj, locationRange).replace("<", "").replace(">", "").replace(NamespaceConstants.DIRECTORY_NAMESPACE, "");
								}
								else if (predicateStr.equals(DirectoryConstants.DESCRIPTION_URI))
								{
									comment = removeLocationRange(obj, locationRange);
								}
								
								if (label != null && type != null && comment != null)
								{
									return new ParticipantInfo(fullUri, type, label, comment);
								}
							}
							return new ParticipantInfo(fullUri, type == null ? "" : type, label == null ? "" : label, comment == null ? "":comment);
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
						isPtFound = false;
						return PARTICIPANT_NOT_FOUND;
					}
				}
			}
		}
		return null;
	}
	
	private String removeLocationRange(final LocatedTriplePart object, final TextLocationRange locationRange)
	{
		return object.toString().replace(String.format(STRING_FORMAT, object.getSourceLocationRange().getLine(), locationRange
				.getStartOffset(), locationRange.getEndOffset()), "");
	}

	@Override
	public IInformationControlCreator getHoverControlCreator() 
	{
		return new IInformationControlCreator() 
		{
            @Override
            public IInformationControl createInformationControl(Shell parent)
            {
            	if (isPtFound)
            	{
            		return new UriHoverInformationControl(parent, false);
            	}
            	else
            	{
            		return new NoParticipantFoundInformationControl(parent, false); 
            	}
            }
        };
	}
	
	private boolean isCursorLocationInRange(final int targetOffset, final TextLocationRange textLocationRange)
	{
		return textLocationRange.getStartOffset() < targetOffset && textLocationRange.getEndOffset() > targetOffset;
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
			e.printStackTrace();
		}
		
		return false;
	}

	private boolean isPtFound;
	private static final String PARTICIPANT_NOT_FOUND = "The participant cannot be found";
	private static final String STRING_FORMAT = "@(%d,%d-%d)";
	private final TwConfigEditor twConfigEditor;
}
