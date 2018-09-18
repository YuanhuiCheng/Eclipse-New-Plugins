package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.internal.Workbench;

import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWMainWizardPage;

public abstract class NewTWWizard extends Wizard implements INewWizard
{
	public IProject getCurrentProject()
	{ 
		ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();    
        ISelection selection = selectionService.getSelection();    

        IProject currentProject = null;    
        if (selection instanceof IStructuredSelection) 
        {    
        		Object element = ((IStructuredSelection)selection).getFirstElement();    

            if (element instanceof IResource)
            {    
            		currentProject = ((IResource)element).getProject();    
            }
            else if (element instanceof PackageFragmentRootContainer)
            {
            		IJavaProject jProject =	((PackageFragmentRootContainer)element).getJavaProject();    
            		currentProject = jProject.getProject();    
            }
            else if (element instanceof IJavaElement)
            {    
            		IJavaProject jProject= ((IJavaElement)element).getJavaProject();    
            		currentProject = jProject.getProject();    
            }    
         }
        
         return currentProject;
    }
	
	public IFolder getCurrentFolder(IProject project)
    {	
		return project.getFolder(SOURCE_FOLDER ).getFolder(MAIN_FOLDER).getFolder(PARTICIPANT_FOLDER);
    }
	
	public IPath getParticipantFilePath(final NewTWMainWizardPage wizardPage, final String participantName)
	{

		final String participantFileName = participantName.replaceAll(SEPARATOR, DASH);
		return wizardPage.getFolderPath().append(IPath.SEPARATOR + participantFileName).addFileExtension(PARTICIPANT_FILE_EXTENSION);
	}
	
	//Default Folder Name
	private static final String SOURCE_FOLDER = "src";
	private static final String MAIN_FOLDER = "main";
	private static final String PARTICIPANT_FOLDER = "participants";
	
	private static final String PARTICIPANT_FILE_EXTENSION = "ttl";
	private static final String SEPARATOR = "/";
	private static final String DASH = "-";

	public static final String FAIL_CREATE_FILE = "Failed to create the expected ttl file.";
	public static final String INVALID_SELECTION_MSG = "Please select a project or folder that has 'participants' folder";
	public static final String INVALIE_OPERATION_TITLE = "Operation Unavailable";
}
