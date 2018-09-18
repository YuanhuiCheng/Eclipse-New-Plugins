package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewTWTelnetConnectorParticipantWizardPage extends NewTWConnectorCRWizardPage
{
	public NewTWTelnetConnectorParticipantWizardPage()
	{
		super("NewTelnetConnectorParticipantWizardPage");
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
		//addCommandBodyTemplate(composite);
		
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
	
//	private void addCommandBodyTemplate(final Composite composite)
//	{
//		Label commandBodyTemplateLabel = new Label(composite, SWT.NONE);
//		commandBodyTemplateLabel.setText(COMMAND_BODY_TEMPLATE_LABEL);
//		commandBodyTemplateLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
//		commandBodyTemplateLabel.setToolTipText(COMMAND_BODY_TEMPLATE_TOOLTIP);
//		
//		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
//		commandBodyTemplateText = new Text(composite, SWT.SINGLE | SWT.BORDER);
//		commandBodyTemplateText.setLayoutData(gridData);
//		commandBodyTemplateText.setText("");
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
	
//	public String getCommandBodyTemplate()
//	{
//		return commandBodyTemplateText.getText();
//	}

	private Text hostText;
	private Text portText;
	//private Text commandBodyTemplateText;

	private static final String WIZARDPAGE_TITLE = "New Telnet Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new Telnet Connector Participant";
	
	//Label Name
	private static final String HOST_LABEL = "Host:";
	private static final String PORT_LABEL = "Port:";
	//private static final String COMMAND_BODY_TEMPLATE_LABEL = "Command body template:";
	
	//Tooltip
	private static final String HOST_TOOLTIP = "Host address of the target. Specified for 'twconn:host'";
	private static final String PORT_TOOLTIP = "Port of the target to be used. Specified for 'twconn:port'";
	//private static final String COMMAND_BODY_TEMPLATE_TOOLTIP = "A FreeMarker template for the telnet commands. Specified for 'twconn:commandBodyTemplate'";
}
