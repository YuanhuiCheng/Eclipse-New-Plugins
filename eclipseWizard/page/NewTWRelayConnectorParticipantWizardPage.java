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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ca.yuanhuicheng.definition.NamespaceConstants;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.NewTWRelayConnectorParticipantWizard;

public class NewTWRelayConnectorParticipantWizardPage extends NewTWMainWizardPage
{
	public NewTWRelayConnectorParticipantWizardPage(final IFolder participantFolder)
	{
		super("NewRelayConnectorParticipantWizardPage", participantFolder);
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
	public void addParticipantName(Composite composite)
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
	
	private void addParticipantType(Composite composite)
	{
		Label participantTypeLabel = new Label(composite, SWT.NONE);
		participantTypeLabel.setText(PARTICIPANT_TYPE_LABEL);
		participantTypeLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		
		String connectorPTType[] = new String[] {TAP_RELAY_CONNECTOR, BACNET_RELAY_CONNECTOR, SMTP_RELAY_CONNECTOR, 
												MLLP_RELAY_CONNECTOR, REST_RELAY_CONNECTOR, SNMP_RELAY_CONNECTOR}; 
		Arrays.sort(connectorPTType);
		
		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gridData.widthHint = COMBO_LENGTH;
		typeList = new Combo(composite, SWT.READ_ONLY);
		typeList.setLayoutData(gridData);
		typeList.setItems(connectorPTType);
		typeList.setText(BACNET_RELAY_CONNECTOR);
		
		new Label(composite, SWT.NONE);
	}
	
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
		if (isTapRelayConnectorSelected())
		{
			return ((NewTWRelayConnectorParticipantWizard) getWizard()).tapRelayConnectorWizardPage;
		}
		else if (isRestRelayConnectorSelected())
		{
			return ((NewTWRelayConnectorParticipantWizard) getWizard()).restRelayConnectorWizardPage;
		}
		else if (isMllpRelayConnectorSelected())
		{
			addDefaultPrefixes(NamespaceConstants.VARIABLE_NAMESPACE);
			return ((NewTWRelayConnectorParticipantWizard) getWizard()).mllpRelayConnectorWizardPage;
		}
		else if (isSmtpRelayConnectorSelected())
		{
			return ((NewTWRelayConnectorParticipantWizard) getWizard()).smtpRelayConnectorWizardPage;
		}
		else if (isBacnetRelayConnectorSelected())
		{
			return ((NewTWRelayConnectorParticipantWizard) getWizard()).bacnetRelayConnectorWizardPage;
		}
		else if (isSnmpRelayConnectorSelected())
		{
			return ((NewTWRelayConnectorParticipantWizard) getWizard()).snmpRelayConnectorWizardPage;
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
	
	public boolean isTapRelayConnectorSelected()
	{
		return typeList.getText().equals(TAP_RELAY_CONNECTOR)? true : false;
	}
	
	public boolean isRestRelayConnectorSelected()
	{
		return typeList.getText().equals(REST_RELAY_CONNECTOR)? true : false;
	}
	
	public boolean isMllpRelayConnectorSelected()
	{
		return typeList.getText().equals(MLLP_RELAY_CONNECTOR)? true : false;
	}
	
	public boolean isSmtpRelayConnectorSelected()
	{
		return typeList.getText().equals(SMTP_RELAY_CONNECTOR)? true : false;
	}
	
	public boolean isBacnetRelayConnectorSelected()
	{
		return typeList.getText().equals(BACNET_RELAY_CONNECTOR)? true : false;
	}
	
	public boolean isSnmpRelayConnectorSelected()
	{
		return typeList.getText().equals(SNMP_RELAY_CONNECTOR)? true : false;
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
	
	
	private boolean enableNext;
	
	private Text urlText;
	private Text participantNameText;
	private Combo typeList;
	
	private static final String PARTICIPANT_TYPE_LABEL = "Connector type :";

	private static final String WIZARDPAGE_TITLE = "New Relay Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new Relay Connector Participant";
	
	//Relay Connector Participant Types
	private static final String TAP_RELAY_CONNECTOR= "TAP Relay Connector";
	private static final String REST_RELAY_CONNECTOR = "REST Relay Connector";
	private static final String MLLP_RELAY_CONNECTOR = "MLLP Relay Connector";
	private static final String SMTP_RELAY_CONNECTOR = "SMTP Relay Connector";
	private static final String BACNET_RELAY_CONNECTOR = "BACnet Relay Connector";
	private static final String SNMP_RELAY_CONNECTOR = "SNMP Relay Connector";
}
