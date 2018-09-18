package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.jena.ontology.OntModel;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.ui.IFileEditorInput;

import ca.yuanhuicheng.core.directory.core.definition.ShareDefinition;
import ca.yuanhuicheng.definition.DirectoryConstants;
import ca.yuanhuicheng.definition.NamespaceConstants;
import ca.yuanhuicheng.tools.Option;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.ResourceUriResolver;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TtlInsertionContextLocator;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TwConfigParser;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.Configuration;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ConsumeRequestDefinition;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.DescribableResource;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ParticipantConfiguration;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ResourceType;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ontology.ContentAssistDocumentedResource;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ontology.EditorAssistOntModel;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ontology.yuanhuichengOntModelFactory;
import ca.yuanhuicheng.tools.eclipse.plugin.eclipse.EclipseLogger;
import ca.yuanhuicheng.tools.ide_plugin_core.MessagePriority;

// FUTURE: class getting rather complex - reconsider core logic out to ide-plugin-core
public class ConfigContentAssistProcessor implements IContentAssistProcessor
{
	public ConfigContentAssistProcessor(final ConfigEditor twConfigEditor)
	{
		this.twConfigEditor = twConfigEditor;
		this.twOntModel = new yuanhuichengOntModelFactory().getOntModel();
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(final ITextViewer viewer, final int offset)
	{
		final int selectionLength = viewer.getSelectedRange().y;
		// Find object contexts and their predicates
		final URI fileUri =
				FileEditorInputUtil.getRestoredEditorInputFileUri((IFileEditorInput) twConfigEditor.getEditorInput());
		final String startingChars = getStartingChars(twConfigEditor, offset, fileUri.toASCIIString());
 		final int startingCharsLength = startingChars.length();
		final TtlInsertionContextLocator ttlCtxtLocator = new TtlInsertionContextLocator(fileUri, null, startingCharsLength);
		final String currentDocumentTtl = viewer.getDocument().get();
		new TwConfigParser().parseTtl(currentDocumentTtl, null, ttlCtxtLocator);

		/*
		 * FUTURE: This implementation will of course not scale well beyond two or three context types - rearchitect as
		 * needed, likely moving much of the smarts into Eclipse document partitioners and/or ide-plugin-core /
		 * directory-model config model smarts.
		 */
		final List<TwConfigUriCompletionProposal> legalCompletions = new LinkedList<TwConfigUriCompletionProposal>();
		final URI predCtxtSubject = ttlCtxtLocator.getPredicateContextSubject(offset);
 		final URI objCtxtPredicate = ttlCtxtLocator.getObjectContextPredicate(offset);
 		
		if (predCtxtSubject != null)   // NOPMD - avoidance of != results in unnatural code flow in this case
		{
			List<TwConfigUriCompletionProposal> tempProposalList = getContextSensitiveTWPropertyCompletions(predCtxtSubject, currentDocumentTtl,
					fileUri.toASCIIString(), ttlCtxtLocator, offset, selectionLength, startingCharsLength);
			legalCompletions.addAll(getProposalListBasedOnStartingChars(tempProposalList, startingChars));
			
//			if (legalCompletions.isEmpty())
//			{
//				legalCompletions.addAll(getCommonTWPropertyCompletions(offset, selectionLength));
//			}
		}
		else if (objCtxtPredicate != null)
		{
			List<TwConfigUriCompletionProposal> tempProposalList = getContextSensitiveResourceCompletions(objCtxtPredicate, currentDocumentTtl,
					fileUri.toASCIIString(), ttlCtxtLocator, offset, selectionLength, startingCharsLength);
			legalCompletions.addAll(getProposalListBasedOnStartingChars(tempProposalList, startingChars));
		}

		if (legalCompletions.isEmpty() && inSemanticFormElementCRContext(offset, ttlCtxtLocator))
		{
			List<TwConfigUriCompletionProposal> tempProposalList = getFormCRLegalCompletions(offset, selectionLength, startingCharsLength, ttlCtxtLocator);
			legalCompletions.addAll(getProposalListBasedOnStartingChars(tempProposalList, startingChars));
		}

		return legalCompletions.toArray(new TwConfigUriCompletionProposal[legalCompletions.size()]);
	}

	@Override
	public IContextInformation[] computeContextInformation(final ITextViewer viewer, final int offset) // NOPMD - nullok
	{
		return null;   // FUTURE: if useful, calculate and return some contexts if in a valid CR URI (or p/o) location
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() // NOPMD - interface recommends null return
	{
		return null;   // don't want automatic activation of completion proposals
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() // NOPMD - interface recommends null return
	{
		return null;   // don't want automatic display of context information
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() // NOPMD - interface recommends null
	{
		return null;   // this processor cannot compute context information. FUTURE: if useful, fix this
	}

	@Override
	public String getErrorMessage()
	{
		return "Have not yet implemented completion proposal and context information calculation.";
	}

	private boolean inSemanticFormElementCRContext(final int offset, final TtlInsertionContextLocator predObjCtxtFinder)
	{
		return SEM_FORM_ELEM_CR_URIS.contains(predObjCtxtFinder.getObjectContextPredicate(offset));
	}

//	private List<TwConfigUriCompletionProposal> getCommonTWPropertyCompletions(final int offset, final int selectionLength)
//	{
//		final List<TwConfigUriCompletionProposal> legalCompletions = new LinkedList<TwConfigUriCompletionProposal>();
//		for (final QName twPropertyQName : TW_CFG_PROP_QNAMES)
//		{
//			legalCompletions.add(new TwConfigUriCompletionProposal   // NOPMD - 'new' necc. & OK - small loop
//				(twPropertyQName.getPrefix() + twPropertyQName.getLocalPart(), null, null, offset, selectionLength, twConfigEditor.getSelectionProvider()));
//		}
//		return legalCompletions;
//	}

	private List<TwConfigUriCompletionProposal> getContextSensitiveTWPropertyCompletions(final URI predCtxtSubject,
			final String currentDocumentTtl, final String fileUri, final ResourceUriResolver resourceUriResolver,
			final int offset, final int selectionLength, final int startingCharsLength)
	{
		return getDocumentedResourceCompletionProposals(new EditorAssistOntModel(twOntModel).getApplicableProperties(
				predCtxtSubject.toASCIIString(), currentDocumentTtl, fileUri), resourceUriResolver, offset, selectionLength, startingCharsLength);
	}

	private List<TwConfigUriCompletionProposal> getContextSensitiveResourceCompletions(final URI objCtxtPredicate,
			final String currentDocumentTtl, final String fileUri, final ResourceUriResolver resourceUriResolver,
			final int offset, final int selectionLength, final int startingCharsLength)
	{
		return getDocumentedResourceCompletionProposals(new EditorAssistOntModel(twOntModel).getInRangeResources(
				objCtxtPredicate.toASCIIString(), currentDocumentTtl, fileUri), resourceUriResolver, offset, selectionLength, startingCharsLength);
	}

	private List<TwConfigUriCompletionProposal> getDocumentedResourceCompletionProposals(
			final Collection<ContentAssistDocumentedResource> applicableResources,
			final ResourceUriResolver resourceUriResolver, final int offset, final int selectionLength, final int startingCharsLength)
	{
		final List<TwConfigUriCompletionProposal> legalCompletions = new LinkedList<TwConfigUriCompletionProposal>();
		for (final ContentAssistDocumentedResource applicableResource : applicableResources)
		{
			// TODO: extend to display domain and range!
			String applRsrcUriTknTxt = applicableResource.getUriTokenText();
			if (isUriRef(applRsrcUriTknTxt))
			{
				final URI applResrcUri = URI.create(applRsrcUriTknTxt.substring(1, applRsrcUriTknTxt.length() - 1));
				final String deresolvedUri = resourceUriResolver.deresolve(applResrcUri);
				if (deresolvedUri != null)
				{
					applRsrcUriTknTxt = deresolvedUri;
				}
			}
			legalCompletions.add(new TwConfigUriCompletionProposal   // NOPMD - 'new' necc. & OK - small loop
					(applRsrcUriTknTxt, applicableResource.getMissingQNamePrefixUri(),
							getAdditionalInfo(applicableResource), offset, selectionLength, startingCharsLength, twConfigEditor
									.getSelectionProvider()));
		}
		Collections.sort(legalCompletions, new TwdThenTwThenQNameThenUriRefCompletionUriLexComparator());
		return legalCompletions;
	}

	/**
	 * Comparator that sorts yuanhuicheng Directory URIs first, then other yuanhuicheng URIs, then QNames, and then URI
	 * references, each in lexicographical order.
	 */
	private class TwdThenTwThenQNameThenUriRefCompletionUriLexComparator implements
			Comparator<TwConfigUriCompletionProposal>
	{
		@Override
		public int compare(final TwConfigUriCompletionProposal twCfgUriCP1,
				final TwConfigUriCompletionProposal twCfgUriCP2)
		{
			final String uriToken1 = twCfgUriCP1.getDisplayString();
			final String uriToken2 = twCfgUriCP2.getDisplayString();

			final boolean uriToken1IsTwd = isTwdUri(uriToken1);
			final boolean uriToken2IsTwd = isTwdUri(uriToken2);
			if (uriToken1IsTwd && !uriToken2IsTwd)
			{
				return -1;
			}
			else if (uriToken2IsTwd && !uriToken1IsTwd)
			{
				return 1;
			}

			final boolean uriToken1IsTw = isTwUri(uriToken1);
			final boolean uriToken2IsTw = isTwUri(uriToken2);
			if (uriToken1IsTw && !uriToken2IsTw)
			{
				return -1;
			}
			else if (uriToken2IsTw && !uriToken1IsTw)
			{
				return 1;
			}

			final boolean uriToken1IsUriRef = isUriRef(uriToken1);
			final boolean uriToken2IsUriRef = isUriRef(uriToken2);
			if (uriToken1IsUriRef == uriToken2IsUriRef)
			{
				return uriToken1.compareTo(uriToken2);   // NOPMD - multi-return more clear
			}
			else
			{
				// treat URI references as 'greater than' QNames, thereby causing them to sort after QNames by default
				return uriToken1IsUriRef ? 1 : -1;
			}
		}

		private boolean isTwdUri(final String uriToken)
		{
			return uriToken.startsWith("twd:") || uriToken.startsWith("<" + NamespaceConstants.ONTOLOGY_NAMESPACE);
		}

		private boolean isTwUri(final String uriToken)
		{
			return uriToken.startsWith("tw") || uriToken.startsWith("<" + "http://yuanhuicheng.ca/ontology/");
		}
	}

	private boolean isUriRef(final String uriToken)
	{
		return uriToken.startsWith("<");   // NOPMD - more clear than charAt(0) == '<'!
	}

	private List<TwConfigUriCompletionProposal> getFormCRLegalCompletions(final int offset, final int selectionLength, 
			final int startingCharsLength, final ResourceUriResolver resourceUriResolver)
	{
		final List<TwConfigUriCompletionProposal> legalCompletions = new LinkedList<TwConfigUriCompletionProposal>();
		final Configuration latestConfig = twConfigEditor.getMostRecentResourceDefConfig();
		if (latestConfig != null && latestConfig.getResourceType() == ResourceType.PARTICIPANT)
		{
			final ParticipantConfiguration latestParticConfig = (ParticipantConfiguration) latestConfig;
			for (final ConsumeRequestDefinition crDef : latestParticConfig.getConsumeRequestDefinitions())
			{
				final String ttlRsrcTokenStr = resourceUriResolver.deresolve(crDef.getUri());
				// null when older Jena parse has CR not in the more recent ANTLR parse
				if (ttlRsrcTokenStr != null)
				{
					legalCompletions.add(new TwConfigUriCompletionProposal(   // NOPMD - 'new' necc. & OK - small loop
							ttlRsrcTokenStr, null, getAdditionalInfo(crDef), offset, selectionLength, startingCharsLength,
							twConfigEditor.getSelectionProvider()));
				}
			}
		}
		return legalCompletions;
	}

	private String getAdditionalInfo(final ContentAssistDocumentedResource applicableProperty)
	{
		return getAdditionalInfo(applicableProperty.getRdfsLabel(), applicableProperty.getRdfsComment());
	}

	private String getAdditionalInfo(final DescribableResource descResource)
	{
		return getAdditionalInfo(descResource.getRdfsLabel(), descResource.getRdfsComment());
	}

	private String getAdditionalInfo(final String rdfsLabel, final String rdfsComment)
	{
		return String.format("%s%n%n%s", contentAssistAddlInfoFormat(rdfsLabel),
				contentAssistAddlInfoFormat(rdfsComment));
	}

	private String contentAssistAddlInfoFormat(final String str)
	{
		final String leadingWsRegex = "(?m)^\\s+";
		return Option.getOrElse(str, "").trim().replaceAll(leadingWsRegex, "");
	}

	private static List<QName> getDirectoryQNames(final String... directoryUris)
	{
		final List<QName> dirQNames = new LinkedList<QName>();
		for (final String directoryUri : directoryUris)
		{
			dirQNames.add(new QName(   // NOPMD - repeated 'new' needed somewhere for this use case
					NamespaceConstants.DIRECTORY_NAMESPACE, directoryUri.replace(
							NamespaceConstants.DIRECTORY_NAMESPACE, ""), "twd:"));
		}
		return dirQNames;
	}

	private static List<QName> getQNames(final String namespaceUri, final String prefix, final String... localParts)
	{
		final List<QName> qNames = new ArrayList<QName>();
		for (final String localPart : localParts)
		{
			qNames.add(new QName(   // NOPMD - repeated 'new' needed somewhere for this use case
					namespaceUri, localPart, prefix));
		}
		Collections.sort(qNames, new QNameLexicographicalComparator());
		return qNames;
	}
	
	private List<TwConfigUriCompletionProposal> getProposalListBasedOnStartingChars(final List<TwConfigUriCompletionProposal> tempProposalList, final String startingChars)
	{	
		Iterator<TwConfigUriCompletionProposal> iterProposal = tempProposalList.iterator();
		
		if (!startingChars.isEmpty())
		{
			while (iterProposal.hasNext())
			{
				TwConfigUriCompletionProposal proposal = iterProposal.next();
				if (proposal != null)
				{
					if (proposal.getUriRefOrQNameToInsert().equals(startingChars) || !proposal.getUriRefOrQNameToInsert().startsWith(startingChars))
					{
						iterProposal.remove();
					}
				}
			}
		}
		
		return tempProposalList;
	}
	
	private String getStartingChars(final TwConfigEditor twConfigEditor, int offset, final String fileUri)
	{
		IDocument document = twConfigEditor.getDocumentProvider().getDocument(twConfigEditor.getEditorInput());
 		StringBuffer sb = new StringBuffer();
		while (true)
 		{
 			try 
 			{
				char c = document.getChar(--offset);
				if (Character.isWhitespace(c))
				{
					if (sb.length() == 0)
					{
						return "";
					}
					else
					{
						return sb.reverse().toString();
					}
				}
				
				sb.append(c);
			} 
 			catch (BadLocationException e)
 			{
 				final String badLocationMsg = "Failed to get the char at offset, " + offset + " from " + fileUri;
 				EclipseLogger.createDefault().log(badLocationMsg + ".", e, MessagePriority.ERROR);
			}
 		}
	}

	private static final String TWFORM_URI = "http://yuanhuicheng.ca/ontology/2010/10/Form#";

	// TODO: remove below properties fully defined in yuanhuicheng.ontology.ttl
	private static final List<QName> TW_CFG_PROP_QNAMES;
	static
	{
		TW_CFG_PROP_QNAMES = new ArrayList<QName>();
		// (property use counts in 2013-10-16 configs in comments)

		// collaboration Directory properties
		TW_CFG_PROP_QNAMES.addAll(getDirectoryQNames(DirectoryConstants.EXTENDS,   // 1,219
				DirectoryConstants.HAS_PARTICIPANT_URI,   // 301
				DirectoryConstants.HAS_INSTANCE_URI   // 65
				));

		// participant Directory properties
		TW_CFG_PROP_QNAMES.addAll(getDirectoryQNames(DirectoryConstants.HAS_CONSUMEREQUEST_URI,   // 887
				DirectoryConstants.HAS_SHARE_ID,   // 886
				DirectoryConstants.HAS_OVERRIDE_URI   // 109
				));

		// consume request Directory properties
		TW_CFG_PROP_QNAMES.addAll(getDirectoryQNames(DirectoryConstants.CONSUMEREQUEST_JSON_MAPPING_URI,   // 1,083
				DirectoryConstants.CONSUMEREQUEST_NOTIFY_SATISFIED_ONLY_URI,   // 617
				DirectoryConstants.USES_QUEUE_URI,   // 223
				DirectoryConstants.CONSUMEREQUEST_ALL_TRIPLES_URI   // 190
				));

		// share ID Directory properties
		TW_CFG_PROP_QNAMES.addAll(getDirectoryQNames(DirectoryConstants.HAS_PREDICATE_URI,   // 1,440
				ShareDefinition.SHARE_JSON_MAPPING_URI,   // 1,107
				ShareDefinition.SHARE_TIME_TO_LIVE_URI,   // 406
				ShareDefinition.SHARE_XML_MAPPING_URI   // 153
				));

		// sort all twd: prefixes before add of other sorted groups
		Collections.sort(TW_CFG_PROP_QNAMES, new QNameLexicographicalComparator());

		TW_CFG_PROP_QNAMES.addAll(getQNames("http://yuanhuicheng.ca/ontology/2011/05/Connector#", "twconn:",
				"validateUser",   // 413
				"parameters",   // 368
				"HttpServer",   // 182
				"HttpsServer",   // 182
				"datasource",   // 150
				"query"   // 103
		));

		TW_CFG_PROP_QNAMES.addAll(getQNames("http://yuanhuicheng.ca/ontology/2010/10/ContentHost#", "twch:",
				"HostedUrl",   // 162
				"InjectorWhiteList",   // 159
				"Tab",   // 135
				"InitialUrlOverride"   // 100
		));

		TW_CFG_PROP_QNAMES.addAll(getQNames(TWFORM_URI, "twform:", "dataVar",   // 1,978
				"classes",   // 1,558
				"layout",   // 1,248
				"shareId",   // 622
				"singleCell",   // 548
				"hasSortIndex",   // 511
				"Field",   // 390
				"cr",   // 298
				"labelClasses",   // 264
				"readonly",   // 263
				"labelPos",   // 211
				"options",   // 189
				"allDataCr",   // 163
				"initial",   // 143
				"maxLength",   // 129
				"classVar",   // 128
				"puri",   // 102
				"unshareCr"   // 5
		));

		TW_CFG_PROP_QNAMES.addAll(getQNames("http://yuanhuicheng.ca/ontology/2010/10/Form/Element#", "twelem:", "box",   // 592
				"text",   // 564
				"html",   // 547
				"plaintext",   // 472
				"button",   // 364
				"form",   // 342
				"select",   // 147
				"subform",   // 146
				"options",   // 137
				"checkbox"   // 108
		));
	};

	private static class QNameLexicographicalComparator implements Comparator<QName>
	{
		@Override
		public int compare(final QName qName1, final QName qName2)
		{
			final int prefixComparison = qName1.getPrefix().compareToIgnoreCase(qName2.getPrefix());
			return prefixComparison == 0 ? qName1.getLocalPart().compareToIgnoreCase(qName2.getLocalPart())
					: prefixComparison;
		}
	}

	private static final Collection<URI> SEM_FORM_ELEM_CR_URIS = Arrays.asList(URI.create(TWFORM_URI + "cr"), URI
			.create(TWFORM_URI + "allDataCr"), URI.create(TWFORM_URI + "unshareCr"));

	private final TwConfigEditor twConfigEditor;
	private final OntModel twOntModel;
}