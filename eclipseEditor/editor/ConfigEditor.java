package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import ca.yuanhuicheng.tools.eclipse.configuration.antlr.FoldRegionFinder.FoldRegion;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.Configuration;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ConfigurationFactory;
import ca.yuanhuicheng.tools.eclipse.plugin.eclipse.ide.EclipseDocument;
import ca.yuanhuicheng.tools.eclipse.plugin.eclipse.ide.EclipseFile;
import ca.yuanhuicheng.tools.eclipse.plugin.eclipse.nature.TwConfigNature;
import ca.yuanhuicheng.tools.eclipse.plugin.eclipse.parser.TwConfigValidator;
import ca.yuanhuicheng.tools.eclipse.plugin.ide.ConfigProject;
import ca.yuanhuicheng.tools.eclipse.plugin.ide.File;
import ca.yuanhuicheng.tools.eclipse.plugin.ide.state.PluginState;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.EclipseConsole;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.handler.ProjectNatureIds;
import ca.yuanhuicheng.tools.eclipse.plugin.util.EclipseTwConfigValidatorFactory;

/**
 * {@link TwConfigEditor} is an implementation of a {@link TextEditor} that provides editor features specific to a
 * Configuration TTL file
 */
public class ConfigEditor extends TextEditor implements IDocumentListener
{
	public ConfigEditor()
	{
		super();
		setSourceViewerConfiguration(new TwConfigSourceViewerConfiguration(this));
		final IWorkbenchWindow activeWrkbnchWin = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		twCfgValidator =
				new EclipseTwConfigValidatorFactory().create(new EclipseConsole(activeWrkbnchWin.getActivePage()));
		activeWrkbnchWin.getPartService().addPartListener(new ValidateOnCloseListener());
	}

	/*
	 * Code folding code closely modeled on the example code in the article at
	 * http://www.eclipse.org/articles/Article-Folding-in-Eclipse-Text-Editors/folding.html.
	 */

	/**
	 * Configure and install {@link ProjectionSupport} for this editor. This is necessary in order to present a subset
	 * of the entire document in the editor view - i.e for code folding.
	 * @param parent the parent composite
	 */
	@Override
	public void createPartControl(final Composite parent)
	{
		super.createPartControl(parent);
		final ProjectionViewer viewer = getSourceProjectionViewer();
	
		new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors()).install();
		viewer.enableProjection();
		
		annotationModel = viewer.getProjectionAnnotationModel();
	}
	
	/**
	 * Create the {@link ProjectionViewer} source viewer to be used by this editor. {@link ProjectionViewer} provides
	 * folded code view support.
	 * @param parent the parent control
	 * @param ruler the vertical ruler
	 * @param styles style bits
	 * @return the {@link ProjectionViewer} source viewer
	 */
	@Override
	protected ISourceViewer createSourceViewer(final Composite parent, final IVerticalRuler ruler, final int styles)
	{
		final ISourceViewer viewer =
				new ProjectionViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);
		getSourceViewerDecorationSupport(viewer);   // ensure that decoration support has been created and configured
		return viewer;
	}

	/**
	 * Replace the current {@link ProjectionAnnotationModel} annotations with new ones based on the given fold boundary
	 * positions.
	 * @param foldRegions code fold boundary regions
	 */
	public void updateFoldingStructure(final Collection<FoldRegion> foldRegions, boolean doInitialFolding)
	{
		final ArrayList<Annotation> annotations = new ArrayList<Annotation>(foldRegions.size());
		final HashMap<Position, ProjectionAnnotation> mapOfAnnotationsToDelete = new HashMap<Position, ProjectionAnnotation>();
		final HashMap<ProjectionAnnotation, Position> newAnnotations = new HashMap<ProjectionAnnotation, Position>();
		
		for (final ProjectionAnnotation annotation : getCurrentAnnotations())
		{
			mapOfAnnotationsToDelete.put(annotationModel.getPosition(annotation), annotation);
		}
		
		for (final FoldRegion foldRegion : foldRegions)
		{
			final Position annotationPosition = new Position(foldRegion.getStartOffset(), foldRegion.getLength());
			
			if(mapOfAnnotationsToDelete.containsKey(annotationPosition))// This fold region still exists, do not remove it.
			{
				mapOfAnnotationsToDelete.remove(annotationPosition);
			}
			else // This fold region is new, contruct new annotation for it.
			{
				final ProjectionAnnotation annotation = new ProjectionAnnotation();   // NOPMD: 'new' inside loop unavoidable
				newAnnotations.put(annotation, annotationPosition);
				annotations.add(annotation);
				
				if(doInitialFolding && foldRegion.isInitiallyCollapsed() && !annotation.isCollapsed()) // The user just opened this file, collapse all regions that are marked initially collapsed
				{
					annotation.markCollapsed();
				}
			}
		}
		
		if (annotationModel != null)   // null if opened on a remote SVN file via Subclipse
		{
			annotationModel.modifyAnnotations(mapOfAnnotationsToDelete.values().toArray(new Annotation[mapOfAnnotationsToDelete.size()]), newAnnotations, null);
		}
	}

	/**
	 * Collapse all code blocks in the editor.
	 */
	public void collapseAllCodeBlocks()
	{
		getSourceProjectionViewer().doOperation(ProjectionViewer.COLLAPSE_ALL);
	}

	/**
	 * Collapse the innermost code block containing the current cursor location, if any.
	 */
	public void collapseCodeBlockAtCursor()
	{
		getSourceProjectionViewer().doOperation(ProjectionViewer.COLLAPSE);
	}

	/**
	 * Expand all code blocks in the editor.
	 */
	public void expandAllCodeBlocks()
	{
		getSourceProjectionViewer().doOperation(ProjectionViewer.EXPAND_ALL);
	}

	/**
	 * Expand the innermost code block containing the current cursor location, if any.
	 */
	public void expandCodeBlockAtCursor()
	{
		getSourceProjectionViewer().doOperation(ProjectionViewer.EXPAND);
	}

	/**
	 * Defines actions to perform when editor's {@link IEditorInput} is set.
	 */
	@Override
	protected void doSetInput(final IEditorInput newInput) throws CoreException
	{
		super.doSetInput(newInput);
		this.input = newInput;

		final IDocument configDocument = getDocumentProvider().getDocument(input);
		configDocument.addDocumentListener(this);
		updateMostRecentValidResourceDefinitionConfig(configDocument);

		final IProject parentProject = ((IFileEditorInput) input).getFile().getProject();
		if (!Arrays.asList(parentProject.getDescription().getNatureIds()).contains(TwConfigNature.NATURE_ID)
				&& promptToAddTwNatureOnOpen)
		{
			Display.getDefault().asyncExec(new Runnable()   // NOPMD - not a JEE app; thread manip. OK
					{
						public void run()
						{
							if (MessageDialog.openQuestion(getSite().getWorkbenchWindow().getShell(),
									"Add yuanhuicheng Nature",
									"Would you like to add the yuanhuicheng Nature to parent project '"
											+ parentProject.getName() + "'?"))
							{
								ProjectNatureIds.addyuanhuicheng(Arrays.asList(parentProject));
							}
							else if (!MessageDialog.openQuestion(getSite().getWorkbenchWindow().getShell(),
									"Add yuanhuicheng Nature",
									"Would you like to be prompted in the future to add the yuanhuicheng "
											+ "Nature on open of a yuanhuicheng configuration TTL file?"))
							{
								promptToAddTwNatureOnOpen = false;
							}
						}
					});
		}
	}

	/**
	 * Defines actions to perform when editor's {@link org.eclipse.jface.text.IDocument} is about to change.
	 */
	@Override
	public void documentAboutToBeChanged(final DocumentEvent event)
	{
		// will only validate when changed
	}

	/**
	 * Defines actions to perform when editor's {@link org.eclipse.jface.text.IDocument} has changed.
	 */
	@Override
	public void documentChanged(final DocumentEvent event)
	{
		final IDocument configDocument = getDocumentProvider().getDocument(input);
		validateDocumentTwConfig(configDocument);
		updateMostRecentValidResourceDefinitionConfig(configDocument);
		triggerOutlineViewUpdate();
	}

	/**
	 * Adapts default editor components to yuanhuicheng specific components (if necessary)
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapter)
	{
		if (IContentOutlinePage.class.equals(adapter))
		{
			if (twCfgOutlinePage == null && getEditorInput() instanceof IFileEditorInput)
			{
				twCfgOutlinePage = new TwConfigFileOutlinePage(this);
			}
			return twCfgOutlinePage;   // NOPMD - multiple return points reasonably clear here
		}
		
		return super.getAdapter(adapter);
	}

	/**
	 * Get the most recent successful parse of this editor's content as a yuanhuicheng resource (collaboration or
	 * participant) configuration.
	 * @return most recent available resource {@link Configuration}, otherwise {@code null}
	 */
	public Configuration getMostRecentResourceDefConfig()
	{
		return mostRecentRsrcDefConfig;
	}

	private class ValidateOnCloseListener implements IPartListener
	{
		@Override
		public void partActivated(final IWorkbenchPart part)
		{
			// don't need to do anything here
		}

		@Override
		public void partBroughtToTop(final IWorkbenchPart part)
		{
			// don't need to do anything here
		}

		@Override
		public void partClosed(final IWorkbenchPart part)
		{
			// don't need to do anything here
		}

		@Override
		public void partDeactivated(final IWorkbenchPart part)
		{
			// don't need to do anything here
		}

		@Override
		public void partOpened(final IWorkbenchPart part)
		{
			// don't need to do anything here
		}
	}

	private ProjectionAnnotation[] getCurrentAnnotations()
	{
		final List<ProjectionAnnotation> currAnnotList = new LinkedList<ProjectionAnnotation>();
		if (annotationModel != null)   // false if opened on a remote SVN file via Subclipse
		{
			@SuppressWarnings("unchecked")
			// Eclipse API not generic but Javadoc says Iterator returns Annotation objects
			final Iterator<Annotation> annotationIter = annotationModel.getAnnotationIterator();
			while (annotationIter.hasNext())
			{
				final ProjectionAnnotation annotation = (ProjectionAnnotation)annotationIter.next();
				currAnnotList.add(annotation);
			}
		}
		return currAnnotList.toArray(new ProjectionAnnotation[currAnnotList.size()]);
	}

	private ProjectionViewer getSourceProjectionViewer()
	{
		return (ProjectionViewer) getSourceViewer();
	}

	private void updateMostRecentValidResourceDefinitionConfig(final IDocument configDocument)
	{
		/*
		 * false if opened on a remote SVN file via Subclipse; type is
		 * org.tigris.subversion.subclipse.ui.editor.RemoteFileEditorInput for Subclipse, for example.
		 */
		if (input instanceof IFileEditorInput)
		{
			final Configuration declaredResource =
					new ConfigurationFactory().create(configDocument.get(), FileEditorInputUtil
							.getRestoredEditorInputFileUri((IFileEditorInput) input), null);
			if (declaredResource.isResourceDefinition())
			{
				mostRecentRsrcDefConfig = declaredResource;
			}
		}
	}

	private void validateDocumentTwConfig(final IDocument configDocument)
	{
		final File configFile = new EclipseFile(((IFileEditorInput) input).getFile());
		
		// don't check TTL syntax of non-collaboration, non-participant files
		final String deltaFileRsrcRelPath = configFile.getProjectRelativePath();
		final boolean isCollabDefFile = deltaFileRsrcRelPath.startsWith(ConfigProject.SRC_MAIN_COLLABS_DIR);
		final boolean isParticDefFile = deltaFileRsrcRelPath.startsWith(ConfigProject.SRC_MAIN_PARTICS_DIR);
		if (isCollabDefFile || isParticDefFile)
		{
			twCfgValidator.validateConfigurationDocument(new EclipseDocument(configDocument), configFile, 
					isCollabDefFile ? PluginState.COLLAB_FILE_MAP : PluginState.PARTIC_FILE_MAP);
		}
	}

	private void triggerOutlineViewUpdate()
	{
		if (twCfgOutlinePage != null)
		{
			/*
			 * Clearing the outline view's selection as it is no longer valid and will cause the outline view to
			 * incorrectly attempt to restore the text editor selection that corresponds to its own selection. (Such an
			 * attempt also causes Eclipse SWT/JFace assertion violations, rendering the text editor and outline view
			 * unusable by the user until closed and reopened.)
			 */
			twCfgOutlinePage.setSelection(null);
			twCfgOutlinePage.update();
		}
	}

	private final TwConfigValidator twCfgValidator;

	/** The ID of this editor type. */
	public static final String EDITOR_ID = "ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.TwConfigEditor";

	private static boolean promptToAddTwNatureOnOpen = true;

	private IEditorInput input;
	private Configuration mostRecentRsrcDefConfig;
	private ProjectionAnnotationModel annotationModel;
	private TwConfigFileOutlinePage twCfgOutlinePage;
}