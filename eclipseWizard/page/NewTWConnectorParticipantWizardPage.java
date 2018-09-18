package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import java.util.Arrays;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ca.yuanhuicheng.definition.NamespaceConstants;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.PrefixesConstants;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.NewTWConnectorParticipantWizard;

public class NewTWConnectorParticipantWizardPage extends NewTWMainWizardPage
{
	public NewTWConnectorParticipantWizardPage(final IFolder participantFolder) 
	{
		super("NewConnectorParticipantWizardPage", participantFolder);
        setTitle(WIZARDPAGE_TITLE);
        setDescription(GENERAL_MESSAGE);
	}

	@Override
	public void createControl(final Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(LAYOUT_COL_NUM, false);
		composite.setLayout(layout);
		
		addFolderGroup(composite);
		addSeperator(composite, LAYOUT_COL_NUM);
		addBaseUrl(composite);
		addParticipantName(composite);
		addParticipantType(composite);
		addSeperator(composite, LAYOUT_COL_NUM);
		addNewLine(composite,1);
		addParticipantLabel(composite);
		addParticipantDescription(composite);
		addNewLine(composite,1);
		addCheckIfCreateEventConsumeRequest(composite);
		addNewLine(composite,1);
		addGeneratePrefixButton(composite);
		addDefaultPrefixes(NamespaceConstants.RDF_NAMESPACE, NamespaceConstants.RDFS_NAMESPACE, NamespaceConstants.DIRECTORY_NAMESPACE, 
						NamespaceConstants.TWCONN_NAMESPACE);
		
		setControl(composite);
		setPageComplete(false);
	}
	
	@Override
	public void addBaseUrl(final Composite composite)
	{
		Label urlLabel = new Label(composite, SWT.NONE);
		urlLabel.setText(BASEURI_LABEL);

		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		urlText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		urlText.setLayoutData(gridData);
		urlText.setText("");
		
		addEmptyLabel(composite, 45);
		
		urlText.addKeyListener(new KeyListener() 
		{
			@Override
			public void keyPressed(org.eclipse.swt.events.KeyEvent e) 
			{
				//do nothing
			}

			@Override
			public void keyReleased(org.eclipse.swt.events.KeyEvent e)
			{
				checkIfPageIsCompleteToEnableNextButton();
			}
		});
	}
	
	@Override
	public void addParticipantName(final Composite composite)
	{
		Label partiNamelLabel = new Label(composite, SWT.NONE);
		partiNamelLabel.setText(PARTICIPANT_NAME_LABEL);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		participantNameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		participantNameText.setLayoutData(gridData);
		participantNameText.setText("");
		
		new Label(composite, SWT.NONE);
		
		participantNameText.addKeyListener(new KeyListener()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				//do nothing 
			}

			@Override
			public void keyReleased(KeyEvent e) 
			{
				checkIfPageIsCompleteToEnableNextButton();
			}
		});
	}
	
	private void addParticipantType(final Composite composite)
	{
		Label participantTypeLabel = new Label(composite, SWT.NONE);
		participantTypeLabel.setText(PARTICIPANT_TYPE_LABEL);
		participantTypeLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		
		String connectorPTType[] = new String[] {CONTENT_CONNECTOR, DATABASE_CONNECTOR, REST_CONNECTOR, TRANSFORM_CONNECTOR,
											 	SOAP_CONNECTOR, COMMUNICATION_CONNECTOR, MLLP_CONNECTOR,BACNET_CONNECTOR,
												KEY_VALUE_CONNECTOR, TELNET_CONNECTOR, LDAP_CONNECTOR}; 
		Arrays.sort(connectorPTType);
		
		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gridData.widthHint = COMBO_LENGTH;
		typeList = new Combo(composite, SWT.READ_ONLY);
		typeList.setLayoutData(gridData);
		typeList.setItems(connectorPTType);
		typeList.setText(BACNET_CONNECTOR);
		
		new Label(composite, SWT.NONE);
	}
	
	public void addCheckIfCreateEventConsumeRequest(final Composite composite)
	{
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.LEFT;
		gridData.horizontalSpan = LAYOUT_COL_NUM;
		Label createEventCRLabel = new Label(composite, SWT.NONE);
		createEventCRLabel.setText("Do you want to add an event consume request?");
		createEventCRLabel.setLayoutData(gridData);

		new Label(composite, SWT.None);
		
		ifCreateEventCROption = new Button(composite, SWT.CHECK);
		ifCreateEventCROption.setText(IF_CREATE_EVENTCR_LABEL);
		
		new Label(composite, SWT.None);
	}
	
	

	private Button ifCreateEventCROption;
	
	private void checkIfPageIsCompleteToEnableNextButton()
	{
		if (isPageComplete())
		{
			enableNext = true;
			checkStatus();
		}
		else
		{
			enableNext = false;
			checkStatus();
		}
	}
	
	@Override
	public boolean enableFinishOrNextButton() 
	{
		if (getMessageType() == IMessageProvider.NONE)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public boolean canFlipToNextPage()
	{
		return enableNext;
	}
	
	@Override
	public IWizardPage getNextPage()
	{
		if (isRestConnectorSelected())
		{
			addDefaultPrefixes(PrefixesConstants.TWAUTH_NAMESPACE.val());
			return ((NewTWConnectorParticipantWizard) getWizard()).restConnectorWizardPage;
		}
		else if (isDatabaseConnectorSelected())
		{
			return ((NewTWConnectorParticipantWizard) getWizard()).databaseConnectorWizardPage;
		}
		else if (isSoapConnectorSelected())
		{
			addDefaultPrefixes(NamespaceConstants.VARIABLE_NAMESPACE);
			return ((NewTWConnectorParticipantWizard) getWizard()).soapConnectorWizardPage;
		}
		else if (isContentConnectorSelected())
		{
			return ((NewTWConnectorParticipantWizard) getWizard()).contentConnectorWizardPage;
		}
		else if (isCommunicationConnectorSelected())
		{
			return ((NewTWConnectorParticipantWizard) getWizard()).communicationConnectorWizardPage;
		}
		else if (isBacnetConnectorSelected())
		{
			return ((NewTWConnectorParticipantWizard) getWizard()).bacnetConnectorWizardPage;
		}
		else if (isKeyValueConnectorSelected())
		{
			return ((NewTWConnectorParticipantWizard) getWizard()).keyValueConnectorWizardPage;
		}
		else if (isLdapConnectorSelected())
		{
			return ((NewTWConnectorParticipantWizard) getWizard()).ldapConnectorWizardPage;
		}
		else if (isMllpConnectorSelected())
		{
			return ((NewTWConnectorParticipantWizard) getWizard()).mllpConnectorWizardPage;
		}
		else if (isTransformConnectorSelected())
		{
			return ((NewTWConnectorParticipantWizard) getWizard()).transformConnectorWizardPage;
		}
		else if (isTelnetConnectorSelected())
		{
			return ((NewTWConnectorParticipantWizard) getWizard()).telnetConnectorWizardPage;
		}
		
		return null;
	}
	
	@Override
	public boolean isPageComplete()
	{
		if (!checkIfBaseUrlIsValid(urlText.getText(), GENERAL_MESSAGE))
		{
			return false;
		}
		else 
		{
			return checkIfParticipantNameIsValid(participantNameText.getText(), getFolderPath(), GENERAL_MESSAGE);
		}
	}
	
	public boolean isRestConnectorSelected()
	{
		return typeList.getText().equals(REST_CONNECTOR)? true : false;
	}
	
	public boolean isDatabaseConnectorSelected()
	{
		return typeList.getText().equals(DATABASE_CONNECTOR)? true : false;
	}
	
	public boolean isSoapConnectorSelected()
	{
		return typeList.getText().equals(SOAP_CONNECTOR)? true : false;
	}
	
	public boolean isContentConnectorSelected()
	{
		return typeList.getText().equals(CONTENT_CONNECTOR)? true : false;
	}
	
	public boolean isCommunicationConnectorSelected()
	{
		return typeList.getText().equals(COMMUNICATION_CONNECTOR)? true : false;
	}
	
	public boolean isBacnetConnectorSelected()
	{
		return typeList.getText().equals(BACNET_CONNECTOR)? true : false;
	}
	
	public boolean isKeyValueConnectorSelected()
	{
		return typeList.getText().equals(KEY_VALUE_CONNECTOR)? true : false;
	}
	
	public boolean isLdapConnectorSelected()
	{
		return typeList.getText().equals(LDAP_CONNECTOR)? true : false;
	}
	
	public boolean isMllpConnectorSelected()
	{
		return typeList.getText().equals(MLLP_CONNECTOR)? true : false;
	}
	
	public boolean isTransformConnectorSelected()
	{
		return typeList.getText().equals(TRANSFORM_CONNECTOR)? true : false;
	}
	
	public boolean isTelnetConnectorSelected()
	{
		return typeList.getText().equals(TELNET_CONNECTOR)? true : false; 
	}
	
	/**
	 * Getter
	 */	
	@Override
	public String getBaseUri()
	{
		return urlText.getText();
	}
	
	@Override
	public String getParticipantHeader()
	{
		return participantNameText.getText();
	}
	
	public String getTypeListSelection()
	{
		return typeList.getText();
	}
	
	public boolean getIfCreateEventCR()
	{
		return ifCreateEventCROption.getSelection()? true : false;
	}
	
	private boolean enableNext;
	
	private Text urlText;
	private Text participantNameText;
	private Combo typeList;

	private static final String PARTICIPANT_TYPE_LABEL = "Connector type :";
	
	private static final String WIZARDPAGE_TITLE = "New Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new Connector Participant ttl File";
	
	private static final String IF_CREATE_EVENTCR_LABEL = "Generate collaboration started event consume request";
	
	//Connector Participant Types
	private static final String CONTENT_CONNECTOR = "Content Connector";
	private static final String DATABASE_CONNECTOR = "Database Connector";
	private static final String REST_CONNECTOR = "REST Connector";
	private static final String TRANSFORM_CONNECTOR = "Transform Connector";
	private static final String SOAP_CONNECTOR = "SOAP Connector";
	private static final String COMMUNICATION_CONNECTOR = "Communication Connector";
	private static final String MLLP_CONNECTOR = "MLLP Connector";
	private static final String BACNET_CONNECTOR = "BACnet Connector";
	private static final String KEY_VALUE_CONNECTOR = "Key Value Connector";
	private static final String TELNET_CONNECTOR = "Telnet Connector";
	private static final String LDAP_CONNECTOR = "Ldap Connector";
	
}
