package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.text.UriDoubleClickStrategy;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.text.UriHover;

/**
 * A {@link ConfigSourceViewerConfiguration} provides configuration source viewing behavior.
 */
public class ConfigSourceViewerConfiguration extends SourceViewerConfiguration
{
	/**
	 * Create a configuration source viewer configuration.
	 * @param ConfigEditor the editor to associate with this yuanhuicheng configuration source viewer configuration
	 */
	public ConfigSourceViewerConfiguration(final ConfigEditor twConfigEditor)
	{
		super();
		this.twConfigEditor = twConfigEditor;
	}

	/**
	 * Returns a default annotation hover containing the marker information for display in a hover popup window when
	 * requested for the given source viewer.
	 * @param sourceViewer the source viewer to be configured by this configuration - not used
	 * @return the yuanhuicheng configuration source annotation hover object
	 */
	@Override
	public IAnnotationHover getAnnotationHover(final ISourceViewer sourceViewer)
	{
		return new DefaultAnnotationHover();
	}
	
	@Override
    public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
        return new UriHover(sourceViewer, twConfigEditor);
    }

	@Override
	public IContentAssistant getContentAssistant(final ISourceViewer sourceViewer)
	{
		final ContentAssistant contentAssistant = new ContentAssistant();
		final IContentAssistProcessor contentAssistProcessor = new TwConfigContentAssistProcessor(twConfigEditor);
		// FUTURE: partition document into TW Config specific partitions; associate different assists with each
		contentAssistant.setContentAssistProcessor(contentAssistProcessor, IDocument.DEFAULT_CONTENT_TYPE);
		contentAssistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
//	    contentAssistant.setRepeatedInvocationTrigger(getIterationBinding());
//	    contentAssistant.setRepeatedInvocationMode(true);
		return contentAssistant;
	}
	
//	 private KeySequence getIterationBinding() { 
//		  final IBindingService bindingSvc = (IBindingService) PlatformUI 
//		    .getWorkbench().getAdapter(IBindingService.class); 
//		  TriggerSequence binding = bindingSvc 
//		    .getBestActiveBindingFor(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS); 
//		  if (binding instanceof KeySequence) 
//		   return (KeySequence) binding; 
//		  return null; 
//		 } 

	/**
	 * Get the reconciler to be used with the given source viewer.
	 * @param sourceViewer the source viewer - not used
	 * @return a non-incremental {@link TwConfigReconcilingStrategy}-based {@link MonoReconciler}, regardless of the
	 *         type of source viewer handed in
	 */
	@Override
	public IReconciler getReconciler(final ISourceViewer sourceViewer)
	{
		return new MonoReconciler(new TwConfigReconcilingStrategy(twConfigEditor), false);
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(final ISourceViewer sourceViewer)
	{
		final PresentationReconciler reconciler = new PresentationReconciler();

		final RuleBasedScanner ruleBasedScanner = new RuleBasedScanner();

		final IRule qnameRule = new QNameRule(QNAME_CLR_TKN);

		// FUTURE: make this more correct: only > is escapable; \& should be interpreted as \&, not &.
		final IRule uriRefRule = new PatternRule("<", ">", URIREF_CLR_TKN, '\\', false);

		final IRule multiLineStringRule =
				new TtlStringRule(TtlStringRule.LineOrdinality.MULTIPLE, MLINE_STRING_CLR_TKN);

		final IRule singleLineStringRule = new TtlStringRule(TtlStringRule.LineOrdinality.SINGLE, STRING_CLR_TKN);

		final IRule commentRule = new EndOfLineRule("#", COMMNT_CLR_TKN);

		final WordRule keywordRule = new WordRule(new TwConfigKeywordDetector(), STRING_CLR_TKN);
		for (final String keyword : KEYWORDS)
		{
			keywordRule.addWord(keyword, KEYWORD_CLR_TKN);
		}

		final IRule whitespaceRule = new WhitespaceRule(new TwConfigWhitespaceDetector());

		/*eene
		 * Multiline rule must before single-line rule in order to avoid having the colorer recognize valid single-line
		 * strings within multiline strings as single-line TTL strings. Similarly, defensively putting keywordRule
		 * towards the end of the descending priority IRule list in order to avoid coloring "a"'s within other tokens
		 * with keyword coloring.
		 */
		ruleBasedScanner.setRules(new IRule[] { qnameRule, uriRefRule, multiLineStringRule, singleLineStringRule,
				commentRule, keywordRule, whitespaceRule });
		
		final DefaultDamagerRepairer damagerRepairer = new DefaultDamagerRepairer(ruleBasedScanner);

		reconciler.setDamager(damagerRepairer, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(damagerRepairer, IDocument.DEFAULT_CONTENT_TYPE);

		return reconciler;
	}
	
	/**
	 * Get the hyperlink detectors to be used for this source viewer. Overridden to remove the UrlHyperlinkDetector
	 * and to add yuanhuicheng configuration related hyperlink detectors.
	 * @param sourceViewer the source viewer - not used
	 * @return an array of  {@link IHyperlinkDetector} to be used by the yuanhuicheng configuration editor.
	 */
	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (sourceViewer == null)
			return null;

		return new IHyperlinkDetector[] { new TwConfigHyperlinkDetector(twConfigEditor) };
	}
	
	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(final ISourceViewer sourceViewer, final String contentType)
	{
		if (sourceViewer == null)
			return null;
		
		if (doubleClickStrategy == null)
		{
			doubleClickStrategy = new UriDoubleClickStrategy(twConfigEditor);
		}
		return doubleClickStrategy;
	}

	private static class TwConfigKeywordDetector implements IWordDetector
	{
		@Override
		public boolean isWordStart(final char possibleStartChar)
		{
			return Character.toString(possibleStartChar).matches("[" + KEYWORD_START_CHARS + "]");
		}

		@Override
		public boolean isWordPart(final char possibleWordChar)
		{
			return Character.toString(possibleWordChar).matches("[" + KEYWORD_CHARS + "]");
		}
	}

	private static class TwConfigWhitespaceDetector implements IWhitespaceDetector
	{
		@Override
		public boolean isWhitespace(final char character)
		{
			return Character.isWhitespace(character);
		}
	}

	private ITextDoubleClickStrategy doubleClickStrategy;
	
	private static final String[] KEYWORDS = new String[] { "@base", "@prefix", "true", "false", "a"};
	private static final String KEYWORD_START_CHARS = "@\\p{Alpha}";
	private static final String KEYWORD_CHARS = "@\\p{Alpha}";
	
	private static final Token QNAME_CLR_TKN = TokenUtils.createToken(0, 0, 200);   // blue
	private static final Token KEYWORD_CLR_TKN = TokenUtils.createToken(208, 0, 0);   // red
	private static final Token STRING_CLR_TKN = TokenUtils.createToken(20, 22, 26); // near-black teal-grey
	private static final Token MLINE_STRING_CLR_TKN = TokenUtils.createToken(54, 61, 69); // mid-dark teal-grey
	private static final Token URIREF_CLR_TKN = TokenUtils.createToken(108, 0, 0);   // mid-dark red
	private static final Token COMMNT_CLR_TKN = TokenUtils.createToken(63, 127, 95);   // mid-saturation green

	private final TwConfigEditor twConfigEditor;
}