package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewTWBacnetRelayConnectorParticipantWizardPage extends NewTWRelayConnectorWizardPage
{
	public NewTWBacnetRelayConnectorParticipantWizardPage()
	{
		super("NewBACnetRelayConnectorParticipantWizardPage");
		setPageComplete(false);
		setTitle(WIZARDPAGE_TITLE);
        setDescription(GENERAL_MESSAGE);
	}
	
	@Override
	public void createControl(final Composite parent) 
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(LAYOUT_COL_NUM, false);
		composite.setLayout(layout);

		addBacnetSubnet(composite);
		addFilterGroup(composite);
		addFilterOrder(composite);
		
		setControl(composite);
		setPageComplete(true);
	}
	
	private void addBacnetSubnet(final Composite composite)
	{
		Label bacnetSubnetLabel = new Label(composite, SWT.NONE);
		bacnetSubnetLabel.setText(BACNET_SUBNET_LABEL);
		bacnetSubnetLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		bacnetSubnetLabel.setToolTipText(BACNET_SUBNET_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		bacnetSubnetText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		bacnetSubnetText.setLayoutData(gridData);
		bacnetSubnetText.setText("");
		
		addEmptyLabel(composite, 45);
	}

	/**
	 * Getter
	 */
	public String getBacnetSubnet()
	{
		return bacnetSubnetText.getText();
	}
	
	private Text bacnetSubnetText;

	private static final String WIZARDPAGE_TITLE = "New BACnet Relay Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new BACnet Relay Connector Participant";
	
	//Label Name
	private static final String BACNET_SUBNET_LABEL = "BACnet subnet:";
			
	//Tooltip
	private static final String BACNET_SUBNET_TOOLTIP = "Broadcasting address used to initiate polling. The network that the devices/simulator exist on. Specified for 'twconn:bacnetSubnet'";
}
