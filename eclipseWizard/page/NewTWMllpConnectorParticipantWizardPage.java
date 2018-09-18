package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewTWMllpConnectorParticipantWizardPage extends NewTWConnectorCRWizardPage
{
	public NewTWMllpConnectorParticipantWizardPage()
	{
		super("NewMllpConnectorParticipantWizardPage");
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
		
		addHost(composite);
		addPort(composite);
		addTimeout(composite);
		addRetries(composite);
		//addMessageTemplate(composite);
		
		setControl(composite);
		setPageComplete(true);
	}
	
	private void addHost(final Composite composite)
	{
		Label hostlLabel = new Label(composite, SWT.NONE);
		hostlLabel.setText(HOST_LABEL);
		hostlLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		hostlLabel.setToolTipText(HOST_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		hostText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		hostText.setLayoutData(gridData);
		hostText.setText("");
		
		addEmptyLabel(composite, 45);
	}
	
	private void addPort(final Composite composite)
	{
		Label portLabel = new Label(composite, SWT.NONE);
		portLabel.setText(PORT_LABEL);
		portLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		portLabel.setToolTipText(PORT_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		portText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		portText.setLayoutData(gridData);
		portText.setText("");
		
		new Label(composite, SWT.NONE);
	}
	
	private void addTimeout(final Composite composite)
	{
		Label timeoutLabel = new Label(composite, SWT.NONE);
		timeoutLabel.setText(TIMEOUT_LABEL);
		timeoutLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		timeoutLabel.setToolTipText(TIMEOUT_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		timeoutText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		timeoutText.setLayoutData(gridData);
		timeoutText.setText("");
		
		new Label(composite, SWT.NONE);
	}
	
	private void addRetries(final Composite composite)
	{
		Label retriesLabel = new Label(composite, SWT.NONE);
		retriesLabel.setText(RETRIES_LABEL);
		retriesLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		retriesLabel.setToolTipText(RETRIES_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		retriesText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		retriesText.setLayoutData(gridData);
		retriesText.setText("");
		
		new Label(composite, SWT.NONE);
	}
	
//	private void addMessageTemplate(final Composite composite)
//	{
//		Label messageTemplateLabel = new Label(composite, SWT.NONE);
//		messageTemplateLabel.setText(MESSAGE_TEMPLATE_LABEL);
//		messageTemplateLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
//		messageTemplateLabel.setToolTipText(MESSAGE_TEMPLATE_TOOLTIP);
//		
//		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
//		messageTemplateText = new Text(composite, SWT.SINGLE | SWT.BORDER);
//		messageTemplateText.setLayoutData(gridData);
//		messageTemplateText.setText("");
//		
//		new Label(composite, SWT.NONE);
//	}
	
	/**
	 * Getter
	 */
	public String getHost()
	{
		return hostText.getText();
	}
	
	public String getPort()
	{
		return portText.getText();
	}
	
	public String getTimeout()
	{
		return timeoutText.getText();
	}
	
	public String getRetries()
	{
		return retriesText.getText();
	}
	
//	public String getMessageTemplate()
//	{
//		return messageTemplateText.getText();
//	}

	private Text hostText;
	private Text portText;
	private Text timeoutText;
	private Text retriesText;
	//private Text messageTemplateText;

	private static final String WIZARDPAGE_TITLE = "New Mllp Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new Mllp Connector Participant";
	
	//Label Name
	private static final String HOST_LABEL = "Host:";
	private static final String PORT_LABEL = "Port:";
	private static final String TIMEOUT_LABEL = "Timeout:";
	private static final String RETRIES_LABEL = "Retries:";
	//private static final String MESSAGE_TEMPLATE_LABEL = "Message template:";
	
	//Tooltip
	private static final String HOST_TOOLTIP = "The host name of the MLLP server. Specified for 'twconn:host'";
	private static final String PORT_TOOLTIP = "The port to use to access the MLLP server. Specified for 'twconn:port'";
	private static final String TIMEOUT_TOOLTIP = "The number of milliseconds to wait for a response from the MLLP server. Specified for 'twconn:timeout'";
	private static final String RETRIES_TOOLTIP = "The number of times to retry sending a failed message to the MLLP server. Specified for 'twconn:retries'";
	//private static final String MESSAGE_TEMPLATE_TOOLTIP = "The template used to construct the message to send to the MLLP server. Specified for 'twconn:messageTemplate'";
}
