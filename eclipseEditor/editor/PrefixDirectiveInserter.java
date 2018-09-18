package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import ca.yuanhuicheng.tools.eclipse.configuration.antlr.DirectiveStatementsLocator;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TwConfigParser;
import ca.yuanhuicheng.tools.eclipse.plugin.eclipse.EclipseLogger;
import ca.yuanhuicheng.tools.ide_plugin_core.MessagePriority;

/**
 * A {@link PrefixDirectiveInserter} provides support for inserting a prefix directive at an appropriate place before
 * the current editor cursor location in order to define a previously undefined QName prefix.
 */
public final class PrefixDirectiveInserter
{
	/**
	 * Insert a QName prefix directive after the last directive before the insert offset, otherwise at the beginning of
	 * the document.
	 * @param document yuanhuicheng configuration document editor in which to insert a prefix directive
	 * @param offset offset before which the prefix directive should be inserted
	 * @param qNamePrefix QName prefix to be defined
	 * @param qNamePrefixUri URI to be associated with the defined QName prefix
	 * @return the length of the inserted prefix directive TTL, including its leading line separator character(s)
	 */
	public static int insert(final IDocument document, final int offset, final String qNamePrefix,
			final String qNamePrefixUri)
	{
		final DirectiveStatementsLocator dirStmtLctr = new DirectiveStatementsLocator();
		new TwConfigParser().parseTtl(document.get(), null, dirStmtLctr);
		final int insertionOffset = dirStmtLctr.endOfLastDirectiveBeforeOffset(offset);
		final String prfxDrctvTtl = String.format("@prefix %s: <%s> .", qNamePrefix, qNamePrefixUri);
		final String prfxDrctvTtlAndLineSep =
				insertionOffset == 0 ? String.format("%s%n", prfxDrctvTtl) : String.format("%n%s", prfxDrctvTtl);
		try
		{
			document.replace(insertionOffset, 0, prfxDrctvTtlAndLineSep);
		}
		catch (BadLocationException badLocnEx)
		{
			final String failureMessage =
					"Undefined QName prefix quick fix attempted insert at invalid location: " + badLocnEx;
			assert false : failureMessage;
			EclipseLogger.createDefault().log(failureMessage, MessagePriority.ERROR);
		}
		return prfxDrctvTtlAndLineSep.length();
	}

	private PrefixDirectiveInserter()
	{
	}
}