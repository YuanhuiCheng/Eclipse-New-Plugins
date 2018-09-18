package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor;

import java.util.Collection;
import java.util.LinkedList;

import org.antlr.v4.runtime.RecognitionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.swt.widgets.Display;

import ca.yuanhuicheng.tools.eclipse.configuration.antlr.FoldRegionFinder;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.FoldRegionFinder.FoldRegion;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TwConfigParser;
import ca.yuanhuicheng.tools.eclipse.plugin.eclipse.EclipseLogger;
import ca.yuanhuicheng.tools.ide_plugin_core.MessagePriority;

/**
 * A {@link ConfigReconcilingStrategy} provides configuration model reconciliation services. Used to
 * determine which sections of configuration are foldable.
 */
public class ConfigReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension
{
	/**
	 * Create a {@link ConfigReconcilingStrategy}.
	 * @param ConfigEditor the editor to associate with this reconciliation strategy
	 */
	public ConfigReconcilingStrategy(final ConfigEditor twConfigEditor)
	{
		this.twConfigEditor = twConfigEditor;
		this.document = null;   // NOPMD - perhaps dodgy, but this is how the implemented interface works
		this.foldRegionsForInitialLoad = true;
	}

	@Override
	public void setDocument(final IDocument document)
	{
		this.document = document;
	}

	@Override
	public void initialReconcile()
	{
		final FoldRegionFinder foldRegionLocator = new FoldRegionFinder();
		try
		{
			new TwConfigParser().parseTtl(document.get(), null, foldRegionLocator);
		}
		catch (RecognitionException recognizeEx)
		{
			EclipseLogger.createDefault().log(
					"Unexpected parse exception on parse of yuanhuicheng Configuration editor contents: ", recognizeEx,
					MessagePriority.ERROR);
		}

		final Collection<FoldRegion> foldRegions = new LinkedList<FoldRegion>();
		for (final FoldRegion foldRegion : foldRegionLocator.getFoundFoldRegions())
		{
			final String foldTextLessExtraWsLines =
					document.get().substring(foldRegion.getStartOffset(),
							foldRegion.getStartOffset() + foldRegion.getLength()).replaceAll("(?m)(^\\s*?$)*\\z", "");
			
			foldRegions.add(new FoldRegion(   // NOPMD - 'new' in loop necessary here
					foldRegion.getStartOffset(), foldTextLessExtraWsLines.length(), foldRegion.isInitiallyCollapsed()));
		}

		Display.getDefault().asyncExec(getFoldRegionUpdateRunnable(foldRegions, foldRegionsForInitialLoad));
		foldRegionsForInitialLoad = false;
	}

	@Override
	public void reconcile(final IRegion partition)
	{
		// FUTURE: consider performing partial reconciliation to eliminate visible delay in rendering foldable regions
		initialReconcile();
	}

	@Override
	public void reconcile(final DirtyRegion dirtyRegion, final IRegion subRegion)
	{
		// FUTURE: consider performing partial reconciliation to eliminate visible delay in rendering foldable regions
		initialReconcile();
	}

	@Override
	public void setProgressMonitor(final IProgressMonitor monitor)
	{
		// not needed - did not have any visible effect when used by initialReconcile()
	}
	
	private Runnable getFoldRegionUpdateRunnable(final Collection<FoldRegion> foldRegions, final boolean isInitialLoad)
	{
		return new Runnable()   // NOPMD - OK to create thread in code not called by J2EE webapps
		{
			public void run()
			{
				twConfigEditor.updateFoldingStructure(foldRegions, isInitialLoad);
			}
		};
	}
	
	
	private boolean foldRegionsForInitialLoad;
	private final TwConfigEditor twConfigEditor;
	private IDocument document;
}