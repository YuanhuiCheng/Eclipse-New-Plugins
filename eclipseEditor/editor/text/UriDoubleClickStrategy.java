package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.text;

import java.net.URI;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IFileEditorInput;

import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedLiteralTriplePart;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTriple;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTripleList;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTriplePart;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TriplesComponentsLocator;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TwConfigParser;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.ComparableLocatedTriplePart;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.FileEditorInputUtil;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.TwConfigEditor;

public class UriDoubleClickStrategy implements ITextDoubleClickStrategy
{
	public UriDoubleClickStrategy(TwConfigEditor twConfigEditor)
	{
		this.twConfigEditor = twConfigEditor;
	}
	
	@Override
	public void doubleClicked(final ITextViewer viewer)
	{
		final URI fileUri = FileEditorInputUtil.getRestoredEditorInputFileUri((IFileEditorInput) twConfigEditor.getEditorInput());
		TriplesComponentsLocator tripleFinder = new TriplesComponentsLocator(fileUri, null);
		final String currentDocumentTtl = viewer.getDocument().get();
		new TwConfigParser().parseTtl(currentDocumentTtl, null, tripleFinder);
		Set<ComparableLocatedTriplePart> triplePartSet = new TreeSet<ComparableLocatedTriplePart>();
		LocatedTripleList triplesFound = tripleFinder.getFoundTriples();
		
		for (LocatedTriple foundTriple : triplesFound)
		{
			retrievePartFromTriple(triplePartSet, foundTriple.getSubject(), foundTriple.getPredicate(), foundTriple.getObject());
		}
		
		final int offset = viewer.getSelectedRange().x;
		for (ComparableLocatedTriplePart comparableTriplePart : triplePartSet)
		{
			int startOffset = comparableTriplePart.getStartOffset();
			int length = comparableTriplePart.getLength();
			if (offset >= startOffset && offset < startOffset + length)
			{
				if (!(comparableTriplePart.getLocatedTriplePart() instanceof LocatedLiteralTriplePart))
				{
					viewer.setSelectedRange(startOffset, length);
					break;
				}
				else
				{
					
				}
			}
		}
	}
	
	private void retrievePartFromTriple(final Set<ComparableLocatedTriplePart> triplePartSet, final LocatedTriplePart...triplePart)
	{
		for (LocatedTriplePart part : triplePart)
		{
			triplePartSet.add(new ComparableLocatedTriplePart(part));
		}
	}
	
	private final TwConfigEditor twConfigEditor;
}
