package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * A {@link TtlStringRule} can scan text to determine whether or not the characters following the current scanner
 * location form a TTL string.
 */
public class TtlStringRule implements IRule
{
	/**
	 * The line ordinality of strings to be matched by this rule.
	 */
	public enum LineOrdinality
	{
		SINGLE, MULTIPLE
	}

	/**
	 * Create a new TTL string rule.
	 * @param lineOrdinality whether the rule should match multiline or single-line strings
	 * @param successToken token to return should the scanner text match this rule
	 */
	public TtlStringRule(final LineOrdinality lineOrdinality, final Token successToken)
	{
		this.lineOrdinality = lineOrdinality;
		this.successToken = successToken;
	}

	/**
	 * @return {@inheritDoc}; returned valid tokens are colored in near-black teal-grey.
	 */
	@Override
	public IToken evaluate(final ICharacterScanner scanner)
	{
		final String nextTtlTokenStartPatt = "^[\\t\\r\\n ,;.#]$";

		ScanState currentScanState = ScanState.INITIAL;
		int nExpectedDoubleQuotes = getNExpectedDoubleQuotes();
		int nExpectedHexes = 0;
		int nCurrLangTagSegmentChars = 0;
		ReadCharAndCount readCharAndCount = read(scanner, 0);
		while (readCharAndCount.readChar != ICharacterScanner.EOF)
		{
			final char nextChar = (char) readCharAndCount.readChar;
			switch (currentScanState)
			{
			case INITIAL:
				if (nextChar == '"')
				{
					nExpectedDoubleQuotes--;
					if (nExpectedDoubleQuotes == 0)
					{
						nExpectedDoubleQuotes = getNExpectedDoubleQuotes();
						currentScanState = ScanState.STRING_CHARACTER;
					}
				}
				else
				{
					unread(scanner, readCharAndCount.nCharsRead);
					return Token.UNDEFINED;   // NOPMD - multi-return more clear
				}
				break;
			case STRING_CHARACTER:
				if (nextChar == '"')
				{
					nExpectedDoubleQuotes--;
					if (nExpectedDoubleQuotes == 0)
					{
						currentScanState = ScanState.POSSIBLE_LANGUAGE_TAG;
					}
				}
				else if (nextChar == '\\')
				{
					nExpectedDoubleQuotes = getNExpectedDoubleQuotes();
					currentScanState = ScanState.AFTER_ESCAPING_BACKSLASH;
				}
				else if (lineOrdinality == LineOrdinality.SINGLE
						&& (nextChar == '\n' || nextChar == '\r' || nextChar == '\t'))
				{
					unread(scanner, readCharAndCount.nCharsRead);
					return Token.UNDEFINED;   // NOPMD - multi-return more clear
				}
				else
				{
					nExpectedDoubleQuotes = getNExpectedDoubleQuotes();
				}
				break;
			case AFTER_ESCAPING_BACKSLASH:
				if (nextChar == 't' || nextChar == 'n' || nextChar == 'r' || nextChar == '"' || nextChar == '\\')
				{
					currentScanState = ScanState.STRING_CHARACTER;
				}
				else if (nextChar == 'u')
				{
					currentScanState = ScanState.UNICODE_CODE_POINT_HEXES;
					nExpectedHexes = N_BSPLN_CDPT_HEXES;
				}
				else if (nextChar == 'U')
				{
					currentScanState = ScanState.UNICODE_CODE_POINT_HEXES;
					nExpectedHexes = N_NON_BSPLN_CDPT_HEXES;
				}
				else
				{
					unread(scanner, readCharAndCount.nCharsRead);
					return Token.UNDEFINED;   // NOPMD - multi-return more clear
				}
				break;
			case UNICODE_CODE_POINT_HEXES:
				if (Character.toString(nextChar).matches("^\\p{XDigit}$"))
				{
					nExpectedHexes--;
					if (nExpectedHexes == 0)
					{
						currentScanState = ScanState.STRING_CHARACTER;
					}
				}
				else
				{
					unread(scanner, readCharAndCount.nCharsRead);
					return Token.UNDEFINED;   // NOPMD - multi-return more clear
				}
				break;
			case POSSIBLE_LANGUAGE_TAG:
				if (nextChar == '@')
				{
					currentScanState = ScanState.LANGUAGE_TAG_START_SEGMENT;
				}
				else
				{
					scanner.unread();
					return successToken;   // NOPMD - multi-return more clear
				}
				break;
			case LANGUAGE_TAG_START_SEGMENT:
				if (Character.toString(nextChar).matches("^\\p{Lower}$"))
				{
					nCurrLangTagSegmentChars++;
				}
				else if (nextChar == '-' && nCurrLangTagSegmentChars > 0)
				{
					currentScanState = ScanState.LANGUAGE_TAG_NON_START_SEGMENT;
					nCurrLangTagSegmentChars = 0;
				}
				else if (Character.toString(nextChar).matches(nextTtlTokenStartPatt) && nCurrLangTagSegmentChars > 0)
				{
					scanner.unread();
					return successToken;   // NOPMD - multi-return more clear
				}
				else
				{
					unread(scanner, readCharAndCount.nCharsRead);
					return Token.UNDEFINED;   // NOPMD - multi-return more clear
				}
				break;
			case LANGUAGE_TAG_NON_START_SEGMENT:
				if (Character.toString(nextChar).matches("^[\\p{Lower}\\p{Digit}]$"))
				{
					nCurrLangTagSegmentChars++;
				}
				else if (nextChar == '-')
				{
					currentScanState = ScanState.LANGUAGE_TAG_NON_START_SEGMENT;
					nCurrLangTagSegmentChars = 0;
				}
				else if (Character.toString(nextChar).matches(nextTtlTokenStartPatt) && nCurrLangTagSegmentChars > 0)
				{
					scanner.unread();
					return successToken;   // NOPMD - multi-return more clear
				}
				else
				{
					unread(scanner, readCharAndCount.nCharsRead);
					return Token.UNDEFINED;   // NOPMD - multi-return more clear
				}
				break;
			default:
				assert false;
				throw new IllegalStateException("Unknown scan state " + currentScanState + " reached");
			}

			readCharAndCount = read(scanner, readCharAndCount.nCharsRead);
		}

		if (currentScanState == ScanState.POSSIBLE_LANGUAGE_TAG || nCurrLangTagSegmentChars > 0)
		{
			scanner.unread();
			return successToken;   // NOPMD - multi-return more clear
		}

		unread(scanner, readCharAndCount.nCharsRead);
		return Token.UNDEFINED;
	}

	private int getNExpectedDoubleQuotes()
	{
		return lineOrdinality == LineOrdinality.SINGLE ? N_SINGLE_LINE_STR_DBLQTS : N_MULTI_LINE_STR_DBLQTS;
	}

	private ReadCharAndCount read(final ICharacterScanner scanner, final int nCharsReadSoFar)
	{
		return new ReadCharAndCount(scanner.read(), nCharsReadSoFar + 1);
	}

	private class ReadCharAndCount
	{
		ReadCharAndCount(final int readChar, final int nCharsRead)
		{
			this.readChar = readChar;
			this.nCharsRead = nCharsRead;
		}

		private final int readChar;
		private final int nCharsRead;
	}

	private void unread(final ICharacterScanner scanner, final int nChars)
	{
		for (int iChar = 0; iChar < nChars; iChar++)
		{
			scanner.unread();
		}
	}

	private enum ScanState
	{
		INITIAL, STRING_CHARACTER, AFTER_ESCAPING_BACKSLASH, UNICODE_CODE_POINT_HEXES, POSSIBLE_LANGUAGE_TAG, LANGUAGE_TAG, LANGUAGE_TAG_START_SEGMENT, LANGUAGE_TAG_NON_START_SEGMENT
	}

	private static final int N_SINGLE_LINE_STR_DBLQTS = 1;
	private static final int N_MULTI_LINE_STR_DBLQTS = 3;
	private static final int N_BSPLN_CDPT_HEXES = 4;
	private static final int N_NON_BSPLN_CDPT_HEXES = 8;

	private final LineOrdinality lineOrdinality;
	private final Token successToken;
}