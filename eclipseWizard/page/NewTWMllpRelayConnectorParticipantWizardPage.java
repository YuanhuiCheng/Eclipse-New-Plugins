package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewTWMllpRelayConnectorParticipantWizardPage extends NewTWRelayConnectorWizardPage
{
	public NewTWMllpRelayConnectorParticipantWizardPage()
	{
		super("NewMLLPRelayConnectorParticipantWizardPage");
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

		addAllowedMessageType(composite);
		addFilterGroup(composite);
		addFilterOrder(composite);
		
		setControl(composite);
		setPageComplete(true);
	}
	
	private void addAllowedMessageType(final Composite composite)
	{
		Label allowedMessageTypeLabel = new Label(composite, SWT.NONE);
		allowedMessageTypeLabel.setText(ALLOWED_MESSAGE_TYPE_LABEL);
		allowedMessageTypeLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		allowedMessageTypeLabel.setToolTipText(ALLOWED_MESSAGE_TYPE_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		allowedMessageTypeText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		allowedMessageTypeText.setLayoutData(gridData);
		allowedMessageTypeText.setText("");
		
		addEmptyLabel(composite, 45);
	}
	
	/**
	 * Getter
	 */
	public String getAllowedMessageType()
	{
		return allowedMessageTypeText.getText();
	}
	
	private Text allowedMessageTypeText;

	private static final String WIZARDPAGE_TITLE = "New MLLP Relay Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new MLLP Relay Connector Participant";
	
	//Label Name
	private static final String ALLOWED_MESSAGE_TYPE_LABEL = "Allowed message type:";
	
	//Tooltip
	private static final String ALLOWED_MESSAGE_TYPE_TOOLTIP = "The message type being filtered. Specified for 'twconn:allowedMessageType'";
}