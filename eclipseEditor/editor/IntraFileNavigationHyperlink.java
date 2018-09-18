package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor;

import java.net.URI;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedResourceTriplePart;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTriple;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TextLocationRange;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TriplesComponentsLocator;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TwConfigParser;

public class IntraFileNavigationHyperlink implements IHyperlink
{
	public IntraFileNavigationHyperlink(final IRegion hyperlinkRegion, final URI itemUri, final TwConfigEditor twConfigEditor)
	{
		this.hyperlinkRegion = hyperlinkRegion;
		this.itemUri = itemUri;
		this.twConfigEditor = twConfigEditor;
	}
	
	@Override
	public IRegion getHyperlinkRegion() 
	{
		return hyperlinkRegion;
	}

	@Override
	public String getTypeLabel() 
	{
		return null;
	}

	@Override
	public String getHyperlinkText() 
	{
		return null;
	}

	@Override
	public void open() 
	{
		final IEditorInput editorInput = twConfigEditor.getEditorInput();
		URI configFileUri = FileEditorInputUtil.getRestoredEditorInputFileUri((IFileEditorInput) editorInput);
		final TriplesComponentsLocator tripleFinder = new TriplesComponentsLocator(configFileUri, null);
		new TwConfigParser().parseTtl(getEditorInputText(), null, tripleFinder);

		for (final LocatedTriple locatedTriple : tripleFinder.getFoundTriples())
		{
			final LocatedResourceTriplePart subject = locatedTriple.getSubject();
			if (subject.getResourceUri().equals(itemUri))
			{
				final TextLocationRange locationRange = subject.getSourceLocationRange();
				final int length = locationRange.getEndOffset() - locationRange.getStartOffset() + 1;
				final ISelection editorSelection =
						new TextSelection(   // NOPMD - 'new' needed here
								twConfigEditor.getDocumentProvider().getDocument(twConfigEditor), locationRange
										.getStartOffset(), length);
				twConfigEditor.getSelectionProvider().setSelection(editorSelection);
				break;
			}
		}
	}
	
	private String getEditorInputText()
	{
		return twConfigEditor.getDocumentProvider().getDocument(twConfigEditor.getEditorInput()).get();
	}
	
	private URI itemUri;
	private IRegion hyperlinkRegion;
	private TwConfigEditor twConfigEditor;

}
