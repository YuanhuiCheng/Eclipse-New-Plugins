package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

public class NewTWSnmpRelayConnectorParticipantWizardPage extends NewTWRelayConnectorWizardPage
{
	public NewTWSnmpRelayConnectorParticipantWizardPage()
	{
		super("NewSNMPRelayConnectorParticipantWizardPage");
		setPageComplete(false);
		setTitle(WIZARDPAGE_TITLE);
        setDescription(GENERAL_MESSAGE);
	}

	private static final String WIZARDPAGE_TITLE = "New SNMP Relay Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new SNMP Relay Connector Participant";
}
