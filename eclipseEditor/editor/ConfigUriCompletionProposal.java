package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

/**
 * A {@link ConfigUriCompletionProposal} represents a Content Assist URI completion proposal.
 */
public class ConfigUriCompletionProposal implements ICompletionProposal
{
	/**
	 * Create a URI completion proposal.
	 * @param uriRefOrQName the completion URI reference or QName
	 * @param missingQNamePrefixUri URI of missing QName prefix directive, if any; otherwise {@code null}
	 * @param additionalInfo additional information about the completion proposal; {@code null} if no additional info
	 *            should be displayed - i.e. no pop-up yellow background window at all
	 * @param documentOffset the offset into the parent document at which the completion should be inserted
	 * @param documentSelectionLength the number of characters of text at the offset to replace with the proposal text
	 * @param editorSelectionProvider editor selection provider with which cursor can be positioned after the inserted
	 *            QName on {@link #apply(IDocument)}
	 */
	public ConfigUriCompletionProposal(final String uriRefOrQName, final String missingQNamePrefixUri,
			final String additionalInfo, final int documentOffset, final int documentSelectionLength, 
			final int documentStartingCharsLength, final ISelectionProvider editorSelectionProvider)
	{
		this.uriRefOrQNameToInsert = uriRefOrQName;
		this.missingQNamePrefixUri = missingQNamePrefixUri;
		this.additionalInfo = additionalInfo;
		this.documentOffset = documentOffset;
		this.documentSelectionLength = documentSelectionLength;
		this.documentStartingCharsLength = documentStartingCharsLength;
		this.editorSelectionProvider = editorSelectionProvider;
	}

	@Override
	public void apply(final IDocument document)
	{
		try
		{
			String qNamePrefix;
			if (!uriRefOrQNameToInsert.contains(":"))
			{
				qNamePrefix = "";
			}
			else
			{
				qNamePrefix  = uriRefOrQNameToInsert.substring(0, uriRefOrQNameToInsert.indexOf(':'));
			}
			final int missingQNamePrefixLen =
					missingQNamePrefixUri == null ? 0 : PrefixDirectiveInserter.insert(document, documentOffset,
							qNamePrefix, missingQNamePrefixUri); 
			
			if (documentSelectionLength == 0)
			{
				document.replace(documentOffset + missingQNamePrefixLen - documentStartingCharsLength, documentStartingCharsLength, uriRefOrQNameToInsert);
			}
			else
			{
				document.replace(documentOffset + missingQNamePrefixLen, documentSelectionLength, uriRefOrQNameToInsert);
			}
			
			Display.getDefault().asyncExec(new Runnable()   // NOPMD - not JEE webapp (managed threads environment) code
			{
				public void run()
				{
					if (documentSelectionLength == 0)
					{
						editorSelectionProvider.setSelection(new TextSelection(documentOffset
								+ missingQNamePrefixLen + uriRefOrQNameToInsert.length() - documentStartingCharsLength, 0));
					}
					else
					{
						editorSelectionProvider.setSelection(new TextSelection(documentOffset
								+ missingQNamePrefixLen + uriRefOrQNameToInsert.length(), 0));
					}
				}
			});
		}
		catch (BadLocationException badLocationEx)
		{
			assert false;
			throw new AssertionError("yuanhuicheng configuration content assist completion proposal applied at "
					+ "illegal document offset and/or replaced text length: " + badLocationEx);
		}
	}

	@Override
	public String getAdditionalProposalInfo()
	{
		return additionalInfo;
	}

	@Override
	public IContextInformation getContextInformation()
	{
		return null;   // as proposal context info is optional, leaving it out for now
	}

	@Override
	public String getDisplayString()
	{
		return uriRefOrQNameToInsert;
	}

	@Override
	public Image getImage()
	{
		return null;   // don't currently want to display an image with the completion proposal
	}

	@Override
	public Point getSelection(final IDocument document)
	{
		// place the cursor just after the inserted URI with no text selected
		return new Point(documentOffset + uriRefOrQNameToInsert.length(), 0);
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " [uriRefOrQNameToInsert=" + uriRefOrQNameToInsert
				+ ", missingQNamePrefixUri=" + missingQNamePrefixUri + ", additionalInfo=" + additionalInfo
				+ ", documentOffset=" + documentOffset + ", documentSelectionLength=" + documentSelectionLength + "]";
	}
	
	public String getUriRefOrQNameToInsert()
	{
		return uriRefOrQNameToInsert;
	}

	private final String uriRefOrQNameToInsert;
	private final String missingQNamePrefixUri;
	private final String additionalInfo;
	private final int documentOffset;
	private final int documentSelectionLength;
	private final int documentStartingCharsLength;
	private final ISelectionProvider editorSelectionProvider;
}