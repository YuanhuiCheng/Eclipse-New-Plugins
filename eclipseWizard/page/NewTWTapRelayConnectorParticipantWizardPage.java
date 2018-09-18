package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewTWTapRelayConnectorParticipantWizardPage extends NewTWRelayConnectorWizardPage
{
	public NewTWTapRelayConnectorParticipantWizardPage()
	{
		super("NewTAPRelayConnectorParticipantWizardPage");
		setPageComplete(false);
		setTitle(WIZARDPAGE_TITLE);
        setDescription(GENERAL_MESSAGE);
	}
	
	private void addAllow(final Composite composite)
	{
		Composite lowerComposite = new Composite(composite, SWT.NONE);
		lowerComposite.setLayout(new RowLayout());
		
		allowCheck = new Button(lowerComposite, SWT.CHECK);
		
		Label allowLabel = new Label(lowerComposite, SWT.NONE);
		allowLabel.setText(ALLOW_LABEL);
		allowLabel.setToolTipText(ALLOW_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		allowText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		allowText.setLayoutData(gridData);
		allowText.setText("");
		allowText.setEnabled(false);
		
		enableTextBasedOnCheck(allowCheck, allowText);

		addEmptyLabel(composite, 45);	
	}
	
	/**
	 * Getter
	 */
//	public String getAllow()
//	{
//		return allowCheck.getSelection()? allowText.getText() : null;
//	}
	
	private Button allowCheck;
	
	private Text allowText;

	private static final String WIZARDPAGE_TITLE = "New TAP Relay Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new TAP Relay Connector Participant";
	
	//Label Name
	private static final String ALLOW_LABEL = "Allow: ";

	//Tooltip
	private static final String ALLOW_TOOLTIP = "A regex string that is used to decide if the message should be parsed. Specified for 'twconn:allow'";
}
