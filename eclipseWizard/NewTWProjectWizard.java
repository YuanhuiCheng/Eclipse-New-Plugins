package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard;

import java.util.Arrays;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import ca.yuanhuicheng.tools.eclipse.plugin.eclipse.EclipseLogger;
import ca.yuanhuicheng.tools.eclipse.plugin.ide.ConfigProject;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.JFaceUIPrimitives;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWProjectWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.handler.ProjectNatureIds;
import ca.yuanhuicheng.tools.ide_plugin_core.MessagePriority;

/**
 * A {@link NewTWProjectWizard} is an Eclipse 'New... Wizard' for the creation of a new Eclipse Project having the
 * yuanhuicheng nature and containing the standard yuanhuicheng project subfolders.
 */
public final class NewTWProjectWizard extends Wizard implements INewWizard
{
	/**
	 * Create a New yuanhuicheng Project wizard.
	 */
	public NewTWProjectWizard()
	{
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void init(final IWorkbench workbench, final IStructuredSelection selection)
	{
		setWindowTitle(TITLE);
	}

	@Override
	public void addPages()
	{
		wizardPage = new NewTWProjectWizardPage();
		addPage(wizardPage);
	}

	@Override
	public boolean performFinish()
	{
		final String newProjectName = wizardPage.getProjectName();
		final IProject newProject = createProject(newProjectName);
		if (newProject == null)
		{
			return false;   // NOPMD - early exit for error more clear in this method
		}

		return createTWProjectFolders(newProject);
	}

	private IProject createProject(final String projectName)
	{
		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (project.exists())
		{
			new JFaceUIPrimitives().displayMessage(TITLE, "A project with that name already exists.",
					MessagePriority.ERROR);
			return null;   // NOPMD - early exit for error more clear in this method
		}
		try
		{
			project.create(null);
			project.open(null);
			/*
			 * As of TS-526, this workaround is used to ensure that the TwConfigBuilder would be used for
			 * projects created by the New yuanhuicheng Project Wizard. For some reason (likely a bug with
			 * Eclipse), projects created with the nature provided in .create() arguments would not behave
			 * as a yuanhuicheng Project and the TwConfigBuilder does not get triggered.
			 */
			
			ProjectNatureIds.addyuanhuicheng(Arrays.asList(project));
		}
		catch (CoreException coreEx)
		{
			final String failedProjCreateMsg = "Failed to create and open new yuanhuicheng project";
			new JFaceUIPrimitives().displayMessage(TITLE, failedProjCreateMsg + "; see Error Log for details.",
					MessagePriority.ERROR);
			EclipseLogger.createDefault().log(failedProjCreateMsg + ".", coreEx, MessagePriority.ERROR);
			return null;   // NOPMD - early exit for error more clear in this method
		}
		
		return project;
	}

	private boolean createTWProjectFolders(final IProject newProject)
	{
		try
		{
			final IFolder srcFolder = newProject.getFolder(ConfigProject.SRC_DIR);
			createIfNotExists(srcFolder);

			final IFolder srcMainFolder = newProject.getFolder(ConfigProject.SRC_MAIN_DIR);
			createIfNotExists(srcMainFolder);

			for (final String cfgSubfldrName : ConfigProject.CONFIG_FOLDERS)
			{
				final IFolder srcMainCfgSubfldr = srcMainFolder.getFolder(cfgSubfldrName);
				createIfNotExists(srcMainCfgSubfldr);
			}
		}
		catch (CoreException coreEx)
		{
			final String failedProjFldrCreateMsg = "Failed to create yuanhuicheng project subfolder";
			new JFaceUIPrimitives().displayMessage(TITLE, failedProjFldrCreateMsg + "; see Error Log for details.",
					MessagePriority.ERROR);
			EclipseLogger.createDefault().log(failedProjFldrCreateMsg + ".", coreEx, MessagePriority.ERROR);
			return false;   // NOPMD - early exit for error more clear in this method
		}

		return true;
	}

	private void createIfNotExists(final IFolder srcFolder) throws CoreException
	{
		if (!srcFolder.exists())
		{
			srcFolder.create(false, true, null);
		}
	}

	/** The title of this wizard. */
	public static final String TITLE = "New yuanhuicheng Project";

	private NewTWProjectWizardPage wizardPage;
}