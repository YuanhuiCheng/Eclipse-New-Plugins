package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor;

import java.net.URI;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IFileEditorInput;

import ca.yuanhuicheng.definition.DirectoryConstants;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedResourceTriplePart;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTriple;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTripleList;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TextLocationRange;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TriplesComponentsLocator;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TwConfigParser;

public class ConfigHyperlinkDetector extends AbstractHyperlinkDetector
{
	public ConfigHyperlinkDetector(final ConfigEditor twConfigEditor)
	{
		this.twConfigEditor = twConfigEditor;
	}
	
	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer viewer, IRegion region, boolean canShowMultipleHyperlinks)
	{
		if (region == null || viewer == null)
		{
			return null;
		}
		
		final String currentDocumentTtl = viewer.getDocument().get();
		
		final URI fileUri =
				FileEditorInputUtil.getRestoredEditorInputFileUri((IFileEditorInput) twConfigEditor.getEditorInput());
		TriplesComponentsLocator  tripleFinder = new TriplesComponentsLocator(fileUri, null);
		new TwConfigParser().parseTtl(currentDocumentTtl, null, tripleFinder);
		LocatedTripleList triplesFound = tripleFinder.getFoundTriples();

		for(LocatedTriple foundTriple : triplesFound)
		{
			if(isCursorLocationInRange(region.getOffset(), foundTriple.getObject().getSourceLocationRange()))
			{
				String currentResourcePredicate = foundTriple.getPredicate().getResourceUri().toASCIIString();
				if(currentResourcePredicate.equals(DirectoryConstants.HAS_PARTICIPANT_URI))
//						currentResourcePredicate.equals(DirectoryConstants.HAS_INSTANCE_URI) ||
//						currentResourcePredicate.equals(DirectoryConstants.HAS_OVERRIDE_URI) ||
//						currentResourcePredicate.equals(DirectoryConstants.HAS_RESOURCE_URI) )
				{
					final URI participantUri = ((LocatedResourceTriplePart) foundTriple.getObject()).getResourceUri();
					
					final int hyperlinkOffset = foundTriple.getObject().getSourceLocationRange().getStartOffset();
					final int hyperlinkLength = foundTriple.getObject().getSourceLocationRange().getEndOffset() - hyperlinkOffset;
					final IRegion participantUriRegion = new Region(hyperlinkOffset, hyperlinkLength);
					return new IHyperlink [] {new InterFileNavigationHyperlink(participantUriRegion, participantUri) };
				}
				
				if(currentResourcePredicate.equals(DirectoryConstants.HAS_CONSUMEREQUEST_URI) || 
						currentResourcePredicate.equals(DirectoryConstants.HAS_SHARE_ID) ||
						currentResourcePredicate.equals(DirectoryConstants.HAS_QUEUE_URI))	
				{
					final URI itemUri = ((LocatedResourceTriplePart) foundTriple.getObject()).getResourceUri();
					final int hyperlinkOffset = foundTriple.getObject().getSourceLocationRange().getStartOffset();
					final int hyperlinkLength = foundTriple.getObject().getSourceLocationRange().getEndOffset() - hyperlinkOffset;
					final IRegion itemUriRegion = new Region(hyperlinkOffset, hyperlinkLength);
					
					return new IHyperlink [] {new IntraFileNavigationHyperlink(itemUriRegion, itemUri, twConfigEditor)};
				}
			}

		}
		
		return null;
	}
	
	private boolean isCursorLocationInRange(final int targetOffset, final TextLocationRange textLocationRange)
	{
		return textLocationRange.getStartOffset() < targetOffset && textLocationRange.getEndOffset() > targetOffset;
	}
	
	private final TwConfigEditor twConfigEditor;
}
