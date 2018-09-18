package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import ca.yuanhuicheng.tools.eclipse.plugin.eclipse.EclipseLogger;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.FreeMarkerController;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.JFaceUIPrimitives;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWBacnetConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWCommunicationConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWContentConnectorCommandForExcelParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWContentConnectorCommandForTextParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWContentConnectorCommandWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWContentConnectorNoCommandForExcelParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWContentConnectorNoCommandForTextParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWContentConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWDatabaseConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWKeyValueConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWLdapConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWMllpConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWRestConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWSoapConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWTelnetConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page.NewTWTransformConnectorParticipantWizardPage;
import ca.yuanhuicheng.tools.ide_plugin_core.MessagePriority;
import freemarker.template.Configuration;

public class NewTWConnectorParticipantWizard extends NewTWWizard
{
	public NewTWConnectorParticipantWizard()
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
			connectorWizardPage = new NewTWConnectorParticipantWizardPage(getCurrentFolder(getCurrentProject()));
			restConnectorWizardPage = new NewTWRestConnectorParticipantWizardPage();
			databaseConnectorWizardPage = new NewTWDatabaseConnectorParticipantWizardPage();
			soapConnectorWizardPage = new NewTWSoapConnectorParticipantWizardPage();
			contentConnectorWizardPage = new NewTWContentConnectorParticipantWizardPage();
			contentConnectorCommandForTextWizardPage = new NewTWContentConnectorCommandForTextParticipantWizardPage();
			contentConnectorCommandForExcelWizardPage = new NewTWContentConnectorCommandForExcelParticipantWizardPage();
			contentConnectorNoCommandForTextWizardPage = new NewTWContentConnectorNoCommandForTextParticipantWizardPage();
			contentConnectorNoCommandForExcelWizardPage = new NewTWContentConnectorNoCommandForExcelParticipantWizardPage();
			communicationConnectorWizardPage = new NewTWCommunicationConnectorParticipantWizardPage();
			bacnetConnectorWizardPage = new NewTWBacnetConnectorParticipantWizardPage();
			keyValueConnectorWizardPage = new NewTWKeyValueConnectorParticipantWizardPage();
			ldapConnectorWizardPage = new NewTWLdapConnectorParticipantWizardPage();
			mllpConnectorWizardPage = new NewTWMllpConnectorParticipantWizardPage();
			transformConnectorWizardPage = new NewTWTransformConnectorParticipantWizardPage();
			telnetConnectorWizardPage = new NewTWTelnetConnectorParticipantWizardPage();
			
		    addPage(connectorWizardPage);
		    addPage(restConnectorWizardPage);
		    addPage(databaseConnectorWizardPage);
		    addPage(soapConnectorWizardPage);
		    addPage(contentConnectorWizardPage);
		    addPage(contentConnectorCommandForTextWizardPage);
		    addPage(contentConnectorCommandForExcelWizardPage);
		    addPage(contentConnectorNoCommandForTextWizardPage);
		    addPage(contentConnectorNoCommandForExcelWizardPage);
		    addPage(communicationConnectorWizardPage);
		    addPage(bacnetConnectorWizardPage);
		    addPage(keyValueConnectorWizardPage);
		    addPage(ldapConnectorWizardPage);
		    addPage(mllpConnectorWizardPage);
		    addPage(transformConnectorWizardPage);
		    addPage(telnetConnectorWizardPage);
		}
	 }

	@Override
	public boolean performFinish()
	{
		try
		{
			outputInfoToConnectorParticipantFile(connectorWizardPage);
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
	
	private void outputInfoToConnectorParticipantFile(final NewTWConnectorParticipantWizardPage connectorWizardPage) throws PartInitException, IOException
	{
		baseUriHead = connectorWizardPage.getBaseUri();
		prefixesSet = connectorWizardPage.getPrefixesSet();
		participantName = connectorWizardPage.getParticipantHeader();	
		participantLabel = connectorWizardPage.getParticipantLabel();
		participantComment = connectorWizardPage.getParticipantComment();
		ifCreateEventCR = connectorWizardPage.getIfCreateEventCR();
		
		FreeMarkerController fmc = new FreeMarkerController(baseUriHead, prefixesSet, participantName,
													participantLabel, participantComment, ifCreateEventCR);
		Map<String, Object> root = new HashMap<String, Object>();
		String templateName = null;
		
		if (connectorWizardPage.isRestConnectorSelected())
		{	templateName = RESTCONNECTOR_TEMPLATE_NAME;
			root = getDataModelForRestConnector(fmc, restConnectorWizardPage);
		}
		else if (connectorWizardPage.isDatabaseConnectorSelected())
		{
			templateName = DATABASECONNECTOR_TEMPLATE_NAME;
			root = getDataModelForDatabaseConnector(fmc, databaseConnectorWizardPage);
		}
		else if (connectorWizardPage.isSoapConnectorSelected())
		{
			templateName  = SOAPCONNECTOR_TEMPLATE_NAME;
			root = getDataModelForSoapConnector(fmc, soapConnectorWizardPage);
		}
		else if (connectorWizardPage.isContentConnectorSelected())
		{
			templateName = CONTENTCONNECTOR_TEMPLATE_NAME;
			final IWizardPage page = getContainer().getCurrentPage();
			if (page == contentConnectorCommandForTextWizardPage)
			{
				root = getDataModelForContentConnector(fmc, contentConnectorWizardPage, contentConnectorCommandForTextWizardPage);
			}
			else if (page == contentConnectorCommandForExcelWizardPage)
			{
				root = getDataModelForContentConnector(fmc, contentConnectorWizardPage, contentConnectorCommandForExcelWizardPage);
			}
			else if (page == contentConnectorNoCommandForExcelWizardPage)
			{
				root = getDataModelForContentConnector(fmc, contentConnectorWizardPage, contentConnectorNoCommandForExcelWizardPage);
			}
			else
			{
				root = getDataModelForContentConnector(fmc, contentConnectorWizardPage, contentConnectorNoCommandForTextWizardPage);
			}
		}
		else if (connectorWizardPage.isCommunicationConnectorSelected())
		{
			templateName = COMMUNICATIONCONNECTOR_TEMPLATE_NAME;
			root = getDataModelForCommunicationConnector(fmc, communicationConnectorWizardPage);
		}
		else if (connectorWizardPage.isBacnetConnectorSelected())
		{
			templateName = BACNETCONNECTOR_TEMPLATE_NAME;
			root = getDataModelForBacnetConnector(fmc, bacnetConnectorWizardPage);
		}
		else if (connectorWizardPage.isKeyValueConnectorSelected())
		{
			templateName = KEYVALUECONNECTOR_TEMPLATE_NAME;
			root = getDataModelForKeyValueConnector(fmc, keyValueConnectorWizardPage);
		}
		else if (connectorWizardPage.isLdapConnectorSelected())
		{
			templateName = LDAPCONNECTOR_TEMPLATE_NAME;
			root = getDataModelForLdapConnector(fmc, ldapConnectorWizardPage);
		}
		else if (connectorWizardPage.isMllpConnectorSelected())
		{
 			templateName = MLLPCONNECTOR_TEMPLATE_NAME;
			root = getDataModelForMllpConnector(fmc, mllpConnectorWizardPage);
		}
		else if (connectorWizardPage.isTransformConnectorSelected())
		{
			templateName = TRANSFORMCONNECTOR_TEMPLATE_NAME;
			root = getDataModelForTransformConnector(fmc, transformConnectorWizardPage);
		}
		else if (connectorWizardPage.isTelnetConnectorSelected())
		{
			templateName = TELNETCONNECTOR_TEMPLATE_NAME;
			root = getDataModelForTelnetConnector(fmc, telnetConnectorWizardPage);
		}
		
		Configuration cfg = fmc.initialTemplateConfiguration(TEMPLATE_FOLDER);
		IPath partiTtlPath = getParticipantFilePath(connectorWizardPage, participantName);
		IFile mainParticipantFile  = fmc.createParticipantFile(cfg, ResourcesPlugin.getWorkspace().getRoot().getFile(partiTtlPath), root, templateName);
		
		IWorkbenchWindow dwindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = dwindow.getActivePage();
 
		if (page != null)
		{
			IDE.openEditor(page, mainParticipantFile, true);
		}
	}
	
	private Map<String, Object> getDataModelForRestConnector(final FreeMarkerController fmc,  final NewTWRestConnectorParticipantWizardPage restConnectorWizardPage)
	{
		final String inputFormat = restConnectorWizardPage.getInputFormat();
		final String endpointUrl = restConnectorWizardPage.getEndpointUrl();
		final String httpMethod = restConnectorWizardPage.getHttpMethodListSelection();
		final String requestType = restConnectorWizardPage.getRequestType();
		return fmc.buildDataModelForRestConnector(inputFormat, endpointUrl, httpMethod, requestType);
	}
	
	private Map<String, Object> getDataModelForDatabaseConnector(final FreeMarkerController fmc,  final NewTWDatabaseConnectorParticipantWizardPage databaseConnectorWizardPage)
	{
		final String dataSource = databaseConnectorWizardPage.getDataSource();
		final String sqlOperation = databaseConnectorWizardPage.getSQLOperationType();
		return fmc.buildDataModelForDatabaseConnector(dataSource, sqlOperation);
	}
	
	private Map<String, Object> getDataModelForSoapConnector(final FreeMarkerController fmc,  final NewTWSoapConnectorParticipantWizardPage soapConnectorWizardPage)
	{
		final String endpointUrl = soapConnectorWizardPage.getEndpointUrl();
		final String soapAction = soapConnectorWizardPage.getSoapAction();
		return fmc.buildDataModelForSoapConnector(endpointUrl, soapAction);
	}
	
	private Map<String, Object> getDataModelForContentConnector(final FreeMarkerController fmc, 
				final NewTWContentConnectorParticipantWizardPage contentConnectorWizardPage,
				final NewTWContentConnectorCommandWizardPage contentConnectorCommandWizardPage)
	{
		final String ifDownloadable = contentConnectorWizardPage.getIfDownloadableResult();
		final String commandType = contentConnectorWizardPage.getCommandType();
		final String mimeContentType = contentConnectorWizardPage.getContentType();
		final String ifDownloadFile = contentConnectorCommandWizardPage.getIfDownloadFileResult();
		final String path = contentConnectorCommandWizardPage.getPath();
		final String dynamicFileId = contentConnectorCommandWizardPage.getDynamicFileId();
		final String dynamicFileMask = contentConnectorCommandWizardPage.getDynamicFileMask();
		//final String ifShareEntireFile = contentConnectorCommandWizardPage.getIfShareEntireFileResult();
		final String firstRowIndex = contentConnectorCommandWizardPage.getFirstRowIndex();
		final String lastRowIndex = contentConnectorCommandWizardPage.getLastRowIndex();
		final String firstSheetIndex = contentConnectorCommandWizardPage.getFirstSheetIndex();
		final String lastSheetIndex = contentConnectorCommandWizardPage.getLastSheetIndex();

		return fmc.buildDataModelForContentConnector(ifDownloadable, commandType, mimeContentType, ifDownloadFile, path,
				dynamicFileId, dynamicFileMask, firstRowIndex, lastRowIndex, firstSheetIndex,
				lastSheetIndex);
	}
	
	private Map<String, Object> getDataModelForCommunicationConnector(final FreeMarkerController fmc, 
						final NewTWCommunicationConnectorParticipantWizardPage communicationConnectorWizardPage)
	{
		final String commandType = communicationConnectorWizardPage.getCommandType();
		final String message = communicationConnectorWizardPage.getMessage();
		final String recipient = communicationConnectorWizardPage.getRecipient();
		final String recipientField = communicationConnectorWizardPage.getRecipientField();
		final String from = communicationConnectorWizardPage.getFrom();
		final String fromField = communicationConnectorWizardPage.getFromField();
		final String cc = communicationConnectorWizardPage.getCc();
		final String ccField = communicationConnectorWizardPage.getCcField();
		final String subject = communicationConnectorWizardPage.getSubject();
		final String subjectTemplate = communicationConnectorWizardPage.getSubjectTemplate();
		final String template = communicationConnectorWizardPage.getTemplate();
		final String deviceToken = communicationConnectorWizardPage.getDeviceToken();
		final String deviceTokenField = communicationConnectorWizardPage.getDeviceTokenField();
		return fmc.buildDataModelForCommunicationConnector(commandType, message, recipient, recipientField, from,
				fromField, cc, ccField, subject, subjectTemplate, template, deviceToken, deviceTokenField);
	}
	
	private Map<String, Object> getDataModelForBacnetConnector(final FreeMarkerController fmc, 
				final NewTWBacnetConnectorParticipantWizardPage bacnetConnectorWizardPage)
	{
		final boolean ifListDevices = bacnetConnectorWizardPage.getIfListDeviceOptionCheckedResult();
		final boolean ifListObjects = bacnetConnectorWizardPage.getIfListObjectsOptionCheckedResult();
		final boolean ifRead = bacnetConnectorWizardPage.getIfReadOptionCheckedResult();
		final boolean ifWrite = bacnetConnectorWizardPage.getIfWriteOptionCheckedResult();
		
		return fmc.buildDataModelForBacnetConnector(ifListDevices, ifListObjects, ifRead, ifWrite);
	}
	
	private Map<String, Object> getDataModelForKeyValueConnector(final FreeMarkerController fmc, 
			final NewTWKeyValueConnectorParticipantWizardPage keyValueConnectorWizardPage)
	{
		final String commandType = keyValueConnectorWizardPage.getCommandType();
		final String batchTask = keyValueConnectorWizardPage.getBatchTask();
		return fmc.buildDataModelForKeyValueConnector(commandType, batchTask);
	}
	
	private Map<String, Object> getDataModelForLdapConnector(final FreeMarkerController fmc, 
			final NewTWLdapConnectorParticipantWizardPage ldapConnectorWizardPage)
	{
		final String ldapUrl = ldapConnectorWizardPage.getLdapUrl();
		final String username = ldapConnectorWizardPage.getUsername();
		final String password = ldapConnectorWizardPage.getPassword();
		//final String searchFilterTemplate = ldapConnectorWizardPage.getSearchFilterTemplate();
		return fmc.buildDataModelForLdapConnector(ldapUrl, username, password);
	}
	
	private Map<String, Object> getDataModelForMllpConnector(final FreeMarkerController fmc, 
			final NewTWMllpConnectorParticipantWizardPage mllpConnectorWizardPage)
	{
		final String host = mllpConnectorWizardPage.getHost();
		final String port = mllpConnectorWizardPage.getPort();
		final String timeout = mllpConnectorWizardPage.getTimeout();
		final String retries = mllpConnectorWizardPage.getRetries();
		//final String messageTemplate = mllpConnectorWizardPage.getMessageTemplate();
		return fmc.buildDataModelForMllpConnector(host, port, timeout, retries);
	}
	
	private Map<String, Object> getDataModelForTransformConnector(final FreeMarkerController fmc, 
			final NewTWTransformConnectorParticipantWizardPage transformConnectorWizardPage)
	{
		final String transformer = transformConnectorWizardPage.getTransformer();
		return fmc.buildDataModelForTransformConnector(transformer);
	}
	
	private Map<String, Object> getDataModelForTelnetConnector(final FreeMarkerController fmc, 
			final NewTWTelnetConnectorParticipantWizardPage telnetConnectorWizardPage)
	{
		final String host = telnetConnectorWizardPage.getHost();
		final String port = telnetConnectorWizardPage.getPort();
		//final String comandBodyTemplate = telnetConnectorWizardPage.getCommandBodyTemplate();
		return fmc.buildDataModelForTelnetConnector(host, port);
	}

	@Override
	public boolean canFinish()
	{
		if (getContainer().getCurrentPage() == bacnetConnectorWizardPage)
		{
			return bacnetConnectorWizardPage.isPageComplete()? true : false;
		}
		return connectorWizardPage.isPageComplete() && getContainer().getCurrentPage() != getPages()[0] && 
				getContainer().getCurrentPage() != contentConnectorWizardPage;
	}
	
	private NewTWConnectorParticipantWizardPage connectorWizardPage;
	public static NewTWRestConnectorParticipantWizardPage restConnectorWizardPage;
	public static NewTWDatabaseConnectorParticipantWizardPage databaseConnectorWizardPage;
	public static NewTWSoapConnectorParticipantWizardPage soapConnectorWizardPage;
	public static NewTWContentConnectorParticipantWizardPage contentConnectorWizardPage;
	public static NewTWContentConnectorCommandForTextParticipantWizardPage contentConnectorCommandForTextWizardPage;
	public static NewTWContentConnectorCommandForExcelParticipantWizardPage contentConnectorCommandForExcelWizardPage;
	public static NewTWContentConnectorNoCommandForTextParticipantWizardPage contentConnectorNoCommandForTextWizardPage;
	public static NewTWContentConnectorNoCommandForExcelParticipantWizardPage contentConnectorNoCommandForExcelWizardPage;
	public static NewTWCommunicationConnectorParticipantWizardPage communicationConnectorWizardPage;
	public static NewTWBacnetConnectorParticipantWizardPage bacnetConnectorWizardPage;
	public static NewTWKeyValueConnectorParticipantWizardPage keyValueConnectorWizardPage;
	public static NewTWLdapConnectorParticipantWizardPage ldapConnectorWizardPage;
	public static NewTWMllpConnectorParticipantWizardPage mllpConnectorWizardPage;
	public static NewTWTransformConnectorParticipantWizardPage transformConnectorWizardPage;
	public static NewTWTelnetConnectorParticipantWizardPage telnetConnectorWizardPage;
	
	private IWorkbench workbench;
	
	private String baseUriHead;
	private Set<String> prefixesSet;
	private String participantName;
	private String participantLabel;
	private String participantComment;
	private boolean ifCreateEventCR;
	
	private static final String TEMPLATE_FOLDER = "connectorParticipantTtlTemplate";
	
	//participant ttl file name 
	private static final String RESTCONNECTOR_TEMPLATE_NAME = "RestConnectorParticipantTtlTemplate.ftl";
	private static final String DATABASECONNECTOR_TEMPLATE_NAME = "DatabaseConnectorParticipantTtlTemplate.ftl";
	private static final String SOAPCONNECTOR_TEMPLATE_NAME = "SoapConnectorParticipantTtlTemplate.ftl";
	private static final String CONTENTCONNECTOR_TEMPLATE_NAME = "ContentConnectorParticipantTtlTemplate.ftl";
	private static final String COMMUNICATIONCONNECTOR_TEMPLATE_NAME = "CommunicationConnectorParticipantTtlTemplate.ftl";
	private static final String BACNETCONNECTOR_TEMPLATE_NAME = "BacnetConnectorParticipantTtlTemplate.ftl";
	private static final String KEYVALUECONNECTOR_TEMPLATE_NAME = "KeyValueConnectorParticipantTtlTemplate.ftl";
	private static final String LDAPCONNECTOR_TEMPLATE_NAME = "LdapConnectorParticipantTtlTemplate.ftl";
	private static final String MLLPCONNECTOR_TEMPLATE_NAME = "MllpConnectorParticipantTtlTemplate.ftl";
	private static final String TRANSFORMCONNECTOR_TEMPLATE_NAME = "TransformConnectorParticipantTtlTemplate.ftl";
	private static final String TELNETCONNECTOR_TEMPLATE_NAME = "TelnetConnectorParticipantTtlTemplate.ftl";
	
	private static final String WINDOW_TITLE = "New yuanhuicheng Connector Participant";
}
