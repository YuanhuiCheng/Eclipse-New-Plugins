package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import ca.yuanhuicheng.tools.eclipse.plugin.eclipse.EclipseLogger;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.FreeMarkerController;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.JFaceUIPrimitives;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWBacnetRelayConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWMllpRelayConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWRelayConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWRestRelayConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWSmtpRelayConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWSnmpRelayConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWTapRelayConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.ide_plugin_core.MessagePriority;
import freemarker.template.Configuration;

public class NewTWRelayConnectorParticipantWizard extends NewTWWizard
{
	public NewTWRelayConnectorParticipantWizard()
	{
		super();
		setNeedsProgressMonitor(true);
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
			relayConnectorWizardPage = new NewTWRelayConnectorParticipantWizardPage(getCurrentFolder(getCurrentProject()));
			tapRelayConnectorWizardPage = new NewTWTapRelayConnectorParticipantWizardPage();
			restRelayConnectorWizardPage = new NewTWRestRelayConnectorParticipantWizardPage();
			mllpRelayConnectorWizardPage = new NewTWMllpRelayConnectorParticipantWizardPage();
			smtpRelayConnectorWizardPage = new NewTWSmtpRelayConnectorParticipantWizardPage();
			bacnetRelayConnectorWizardPage = new NewTWBacnetRelayConnectorParticipantWizardPage();
			snmpRelayConnectorWizardPage = new NewTWSnmpRelayConnectorParticipantWizardPage();
			
			addPage(relayConnectorWizardPage);
			addPage(tapRelayConnectorWizardPage);
			addPage(restRelayConnectorWizardPage);
			addPage(mllpRelayConnectorWizardPage);
			addPage(smtpRelayConnectorWizardPage);
			addPage(bacnetRelayConnectorWizardPage);
			addPage(snmpRelayConnectorWizardPage);
		}
	}
	
	@Override
	public boolean performFinish() 
	{
		try
		{
			outputInfoToConnectorParticipantFile(relayConnectorWizardPage);
		}
		catch (PartInitException e)
		{
			 EclipseLogger.createDefault().log(FAIL_CREATE_FILE , e, MessagePriority.ERROR);
			 new JFaceUIPrimitives().displayMessage(INVALIE_OPERATION_TITLE, FAIL_CREATE_FILE + "; see Error Log for details.",
						MessagePriority.ERROR);
		} catch (IOException e) 
		{	
			EclipseLogger.createDefault().log(FAIL_CREATE_FILE , e, MessagePriority.ERROR);
			new JFaceUIPrimitives().displayMessage(INVALIE_OPERATION_TITLE, FAIL_CREATE_FILE + "; see Error Log for details.",
					MessagePriority.ERROR);
		}
		
		return true;
	}
	
	private void outputInfoToConnectorParticipantFile(final NewTWRelayConnectorParticipantWizardPage relayConnectorWizardPage) throws PartInitException, IOException
	{
		baseUriHead = relayConnectorWizardPage.getBaseUri();
		prefixesSet = relayConnectorWizardPage.getPrefixesSet();
		participantName = relayConnectorWizardPage.getParticipantHeader();	
		participantLabel = relayConnectorWizardPage.getParticipantLabel();
		participantComment = relayConnectorWizardPage.getParticipantComment();
		
		FreeMarkerController fmc = new FreeMarkerController(baseUriHead, prefixesSet, participantName,
													participantLabel, participantComment);
		Map<String, Object> root = new HashMap<String, Object>();
		String templateName = null;
		
		if (relayConnectorWizardPage.isTapRelayConnectorSelected())
		{
			templateName = TAP_RELAYCONNECTOR_TEMPLATE_NAME;
			root = getDataModelForTapRelayConnector(fmc, tapRelayConnectorWizardPage);
		}
		else if (relayConnectorWizardPage.isRestRelayConnectorSelected())
		{
			templateName = REST_RELAYCONNECTOR_TEMPLATE_NAME;
			root = getDataModelForRestRelayConnector(fmc, restRelayConnectorWizardPage);
		}
		else if (relayConnectorWizardPage.isMllpRelayConnectorSelected())
		{
			templateName = MLLP_RELAYCONNECTOR_TEMPLATE_NAME;
			root = getDataModelForMllpRelayConnector(fmc, mllpRelayConnectorWizardPage);
		}
		else if (relayConnectorWizardPage.isBacnetRelayConnectorSelected())
		{
			templateName = BACNET_RELAYCONNECTOR_TEMPLATE_NAME;
			root = getDataModelForBacnetRelayConnector(fmc, bacnetRelayConnectorWizardPage);
		}
		else if (relayConnectorWizardPage.isSmtpRelayConnectorSelected())
		{
			templateName = SMTP_RELAYCONNECTOR_TEMPLATE_NAME;
			root = getDataModelForSmtpRelayConnector(fmc, smtpRelayConnectorWizardPage);
		}
		else if (relayConnectorWizardPage.isSnmpRelayConnectorSelected())
		{
			templateName = SNMP_RELAYCONNECTOR_TEMPLATE_NAME;
			root = getDataModelForSnmpRelayConnector(fmc, snmpRelayConnectorWizardPage);
		}	
		
		Configuration cfg = fmc.initialTemplateConfiguration(TEMPLATE_FOLDER);
		IPath partiTtlPath = getParticipantFilePath(relayConnectorWizardPage, participantName);
		IFile mainParticipantFile  = fmc.createParticipantFile(cfg, ResourcesPlugin.getWorkspace().getRoot().getFile(partiTtlPath), root, templateName);
		
		IWorkbenchWindow dwindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = dwindow.getActivePage();
 
		if (page != null)
		{
			IDE.openEditor(page, mainParticipantFile, true);
		}
	}

	private Map<String, Object> getDataModelForTapRelayConnector(final FreeMarkerController fmc, final NewTWTapRelayConnectorParticipantWizardPage tapRelayConnectorWizardPage)
	{
		//final String allow = tapRelayConnectorWizardPage.getAllow();
		final String filterGroup = tapRelayConnectorWizardPage.getFilterGroup();
		final String filterOrder = tapRelayConnectorWizardPage.getFilterOrder();
		return fmc.buildDataModelForTapRelayConnector(filterGroup, filterOrder);
	}
	
	private Map<String, Object> getDataModelForRestRelayConnector(final FreeMarkerController fmc, final NewTWRestRelayConnectorParticipantWizardPage restRelayConnectorWizardPage)
	{
		final String filterGroup = restRelayConnectorWizardPage.getFilterGroup();
		final String filterOrder = restRelayConnectorWizardPage.getFilterOrder();
		final String endpointUrl = restRelayConnectorWizardPage.getEndpointUrl();
		final String endpointType = restRelayConnectorWizardPage.getEndpointType();
		return fmc.buildDataModelForRestRelayConnector(filterGroup, filterOrder, endpointUrl, endpointType);
	}
	
	private Map<String, Object> getDataModelForMllpRelayConnector(final FreeMarkerController fmc, final NewTWMllpRelayConnectorParticipantWizardPage mllpRelayConnectorWizardPage)
	{
		final String filterGroup = mllpRelayConnectorWizardPage.getFilterGroup();
		final String filterOrder = mllpRelayConnectorWizardPage.getFilterOrder();
		final String allowedMessageType = mllpRelayConnectorWizardPage.getAllowedMessageType();
		return fmc.buildDataModelForMllpRelayConnector(filterGroup, filterOrder, allowedMessageType);
	}
	
	private Map<String, Object> getDataModelForBacnetRelayConnector(final FreeMarkerController fmc, final NewTWBacnetRelayConnectorParticipantWizardPage bacnetRelayConnectorWizardPage)
	{
		final String filterGroup = bacnetRelayConnectorWizardPage.getFilterGroup();
		final String filterOrder = bacnetRelayConnectorWizardPage.getFilterOrder();
		final String bacnetSubnet = bacnetRelayConnectorWizardPage.getBacnetSubnet();
		return fmc.buildDataModelForBacnetRelayConnector(filterGroup, filterOrder, bacnetSubnet);
	}
	
	private Map<String, Object> getDataModelForSmtpRelayConnector(final FreeMarkerController fmc, final NewTWSmtpRelayConnectorParticipantWizardPage smtpRelayConnectorWizardPage)
	{
		final String filterGroup = smtpRelayConnectorWizardPage.getFilterGroup();
		final String filterOrder = smtpRelayConnectorWizardPage.getFilterOrder();
		final String subjectPattern = smtpRelayConnectorWizardPage.getSubjectPattern();
		final String subjectPatternTemplate = smtpRelayConnectorWizardPage.getSubjectPatternTemplate();
		final String recipientPattern = smtpRelayConnectorWizardPage.getRecipientPattern();
		final String recipientPatternTempalte = smtpRelayConnectorWizardPage.getRecipientPatternTemplate();
		return fmc.buildDataModelForSmtpRelayConnector(filterGroup, filterOrder, subjectPattern, subjectPatternTemplate, recipientPattern, recipientPatternTempalte);
	}
	
	private Map<String, Object> getDataModelForSnmpRelayConnector(final FreeMarkerController fmc, final NewTWSnmpRelayConnectorParticipantWizardPage snmpRelayConnectorWizardPage)
	{
		final String filterGroup = snmpRelayConnectorWizardPage.getFilterGroup();
		final String filterOrder = snmpRelayConnectorWizardPage.getFilterOrder();
		return fmc.buildDataModelForSnmpRelayConnector(filterGroup, filterOrder);
	}
	
	@Override
	public boolean canFinish()
	{
		return relayConnectorWizardPage.isPageComplete() && getContainer().getCurrentPage() != getPages()[0];
	}
	
	private NewTWRelayConnectorParticipantWizardPage relayConnectorWizardPage;
	public static NewTWTapRelayConnectorParticipantWizardPage tapRelayConnectorWizardPage;
	public static NewTWRestRelayConnectorParticipantWizardPage restRelayConnectorWizardPage;
	public static NewTWMllpRelayConnectorParticipantWizardPage mllpRelayConnectorWizardPage;
	public static NewTWSmtpRelayConnectorParticipantWizardPage smtpRelayConnectorWizardPage;
	public static NewTWBacnetRelayConnectorParticipantWizardPage bacnetRelayConnectorWizardPage;
	public static NewTWSnmpRelayConnectorParticipantWizardPage snmpRelayConnectorWizardPage;
	
	private IWorkbench workbench;
	
	private String baseUriHead;
	private Set<String> prefixesSet;
	private String participantName;
	private String participantLabel;
	private String participantComment;
	
	private static final String TEMPLATE_FOLDER = "relayConnectorParticipantTtlTemplate";
	
	private static final String WINDOW_TITLE = "New yuanhuicheng Relay Connector Participant";
	
	//participant ttl file name 
	private static final String TAP_RELAYCONNECTOR_TEMPLATE_NAME = "TapRelayConnectorParticipantTtlTemplate.ftl";
	private static final String REST_RELAYCONNECTOR_TEMPLATE_NAME = "RestRelayConnectorParticipantTtlTemplate.ftl";
	private static final String MLLP_RELAYCONNECTOR_TEMPLATE_NAME = "MllpRelayConnectorParticipantTtlTemplate.ftl";
	private static final String BACNET_RELAYCONNECTOR_TEMPLATE_NAME = "BacnetRelayConnectorParticipantTtlTemplate.ftl";
	private static final String SMTP_RELAYCONNECTOR_TEMPLATE_NAME = "SmtpRelayConnectorParticipantTtlTemplate.ftl";
	private static final String SNMP_RELAYCONNECTOR_TEMPLATE_NAME = "SnmpRelayConnectorParticipantTtlTemplate.ftl";
}
