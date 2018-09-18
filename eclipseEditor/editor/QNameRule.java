package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * A {@link QNameRule} can scan text to determine whether or not the characters following the current scanner location
 * form a (TTL) QName.
 */
public class QNameRule implements IRule
{
	/**
	 * Create a new (TTL) QName rule.
	 * @param successToken token to return should the scanner text match this rule
	 */
	public QNameRule(final Token successToken)
	{
		this.successToken = successToken;
	}

	@Override
	public IToken evaluate(final ICharacterScanner scanner)
	{
		final StringBuffer content = new StringBuffer();
		int nReadChars = 0;
		boolean prevIterInputIsQName = false;
		do
		{
			final int readCharInt = scanner.read();
			nReadChars++;
			if (readCharInt == ICharacterScanner.EOF)
			{
				break;
			}

			content.append((char) readCharInt);

			if (content.toString().matches("(\\p{Alpha}[\\p{Alnum}_-]*)?:([\\p{Alpha}_][\\p{Alnum}_-]*)?"))
			{
				prevIterInputIsQName = true;
			}
			else if (prevIterInputIsQName)
			{
				break;
			}
			else if (content.toString().matches("\\p{Alpha}[\\p{Alnum}_-]*"))
			{
				// QName prefix without a trailing colon - keep searching...
				continue;
			}
			else
			{
				break;
			}
		} while (true);

		if (prevIterInputIsQName)
		{
			scanner.unread();
			return successToken;   // NOPMD - two return points both near end fairly easy to read and understand
		}
		else
		{
			for (int iChar = 0; iChar < nReadChars; ++iChar)
			{
				scanner.unread();
			}
			return Token.UNDEFINED;
		}
	}

	private final Token successToken;
}