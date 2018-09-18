package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import ca.yuanhuicheng.tools.eclipse.plugin.eclipse.EclipseLogger;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.FreeMarkerController;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.JFaceUIPrimitives;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWInterCollabParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWMainWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWParticipantWizardPage;
import ca.yuanhuicheng.tools.ide_plugin_core.MessagePriority;
import freemarker.template.Configuration;

public class NewTWParticipantWizard extends NewTWWizard
{
	
	public NewTWParticipantWizard()
	{
		super();
 		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void createPageControls(Composite pageContainer) 
	{
        // the default behavior is to create all the pages controls
    }
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) 
	{
	    this.workbench = workbench;
	    setWindowTitle(WINDOW_TITLE);
	}
	
	@Override
	public void addPages() 
	{
		if (getCurrentProject() == null || !getCurrentFolder(getCurrentProject()).exists())
	    {
	    	new JFaceUIPrimitives().displayMessage(WINDOW_TITLE , INVALID_SELECTION_MSG, MessagePriority.ERROR);
	    }
		else
		{
			mainWizardPage = new NewTWParticipantWizardPage(getCurrentFolder(getCurrentProject()));
			interCollabWizardPage = new NewTWInterCollabParticipantWizardPage();
			
		    addPage(mainWizardPage);
		    addPage(interCollabWizardPage);
		}
	 }
	
	@Override
	public boolean performFinish() 
	{
		try
		{
			if (mainWizardPage.isSemanticFormChecked())
			{
				outputInfoToParticipantFile(mainWizardPage, DEFAULT_TEMPLATE_NAME);
			}
			else if (mainWizardPage.isControllerChecked())
			{
				outputInfoToParticipantFile(mainWizardPage, CONTROLLER_TEMPLATE_NAME);
			}
			else if (mainWizardPage.isInterCollabChecked())
			{
				boolean isCurrentPageInterCollab = isCurrentPageIsTheSecondPage();
				if (mainWizardPage.isReceiverOptionChecked())
				{
					outputInfoToParticipantFile(mainWizardPage, INTERCOLLABORATION_RECEIVER_TEMPLATE_NAME);
					if (isCurrentPageInterCollab)
					{
						outputInfoToParticipantFile(interCollabWizardPage, INTERCOLLABORATION_SENDER_TEMPLATE_NAME);
					}
				}
				else
				{
					outputInfoToParticipantFile(mainWizardPage, INTERCOLLABORATION_SENDER_TEMPLATE_NAME);
					if (isCurrentPageInterCollab)
					{
						outputInfoToParticipantFile(interCollabWizardPage, INTERCOLLABORATION_RECEIVER_TEMPLATE_NAME);
					}
				}
			}
		}
		catch (PartInitException e)
		{
			 EclipseLogger.createDefault().log(FAIL_CREATE_FILE, e, MessagePriority.ERROR);
			 new JFaceUIPrimitives().displayMessage(INVALIE_OPERATION_TITLE, FAIL_CREATE_FILE + "; see Error Log for details.",
						MessagePriority.ERROR);
		}
		catch (IOException e)
		{
			EclipseLogger.createDefault().log(FAIL_CREATE_FILE, e, MessagePriority.ERROR);
			 new JFaceUIPrimitives().displayMessage(INVALIE_OPERATION_TITLE, FAIL_CREATE_FILE + "; see Error Log for details.",
						MessagePriority.ERROR);
		}
		
		return true;
	}	
	
	private void outputInfoToParticipantFile(final NewTWMainWizardPage wizardPage, final String templateName) throws PartInitException, IOException
	{
		final String baseUriHead = wizardPage.getBaseUri();
		final Set<String> prefixesSet = wizardPage.getPrefixesSet();
		final String participantName = wizardPage.getParticipantHeader();	
		final String participantLabel = wizardPage.getParticipantLabel();
		final String participantComment = wizardPage.getParticipantComment();
		
		FreeMarkerController fmc = new FreeMarkerController(baseUriHead, prefixesSet, participantName, participantLabel, participantComment);
		Map<String, Object> root = fmc.buildDataModel();
		
		Configuration cfg = fmc.initialTemplateConfiguration(TEMPLATE_FOLDER);
		IPath partiTtlPath = getParticipantFilePath(wizardPage, participantName);
		IFile mainParticipantFile  = fmc.createParticipantFile(cfg, ResourcesPlugin.getWorkspace().getRoot().getFile(partiTtlPath), root, templateName);
		IWorkbenchWindow dwindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = dwindow.getActivePage();
 
		if (page != null)
		{
			IDE.openEditor(page, mainParticipantFile, true);
		}
	}
	
	private boolean isCurrentPageIsTheSecondPage()
	{
		return getContainer().getCurrentPage() == getPages()[1];
	}
	
	@Override
	public boolean canFinish()
	{
		if (isCurrentPageIsTheSecondPage())
		{
			return interCollabWizardPage.isPageComplete();
		}
		else
		{
			return mainWizardPage.isPageComplete();
		}
	}
	

	private NewTWParticipantWizardPage mainWizardPage;
	public static NewTWInterCollabParticipantWizardPage interCollabWizardPage;
    private IWorkbench workbench;
    
    private static final String TEMPLATE_FOLDER = "participantTtlTemplates";

    private static final String DEFAULT_TEMPLATE_NAME = "SemanticFormParticipantTtlTemplate.ftl";
	private static final String INTERCOLLABORATION_RECEIVER_TEMPLATE_NAME = "InterCollaborationParticipantReceiverTtlTemplate.ftl";
	private static final String INTERCOLLABORATION_SENDER_TEMPLATE_NAME = "InterCollaborationParticipantSenderTtlTemplate.ftl";
	private static final String CONTROLLER_TEMPLATE_NAME  = "ControllerParticipantTtlTemplate.ftl";
   
    private static final String WINDOW_TITLE = "New yuanhuicheng Participant";
}
