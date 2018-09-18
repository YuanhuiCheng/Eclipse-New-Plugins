package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewTWSoapConnectorParticipantWizardPage extends NewTWConnectorCRWizardPage
{
	public NewTWSoapConnectorParticipantWizardPage()
	{
		super("NewSoapConnectorParticipantWizardPage");
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
		
		addEndpointUrl(composite);
		addSoapAction(composite);
		
		setControl(composite);
		setPageComplete(true);
	}
	
	private void addEndpointUrl(final Composite composite)
	{
		Label endpointUrlLabel = new Label(composite, SWT.NONE);
		endpointUrlLabel.setText(ENDPOINT_URL_LABEL);
		endpointUrlLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		endpointUrlLabel.setToolTipText(ENDPOINT_URL_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		endpointUrlText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		endpointUrlText.setLayoutData(gridData);
		endpointUrlText.setText("");
		
		addEmptyLabel(composite, 45);
	}
	
	private void addSoapAction(final Composite composite)
	{
		Label soapActionLabel = new Label(composite, SWT.NONE);
		soapActionLabel.setText(SOAPACTION_LABEL);
		soapActionLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		soapActionLabel.setToolTipText(SOAPACTION_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		soapActionText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		soapActionText.setLayoutData(gridData);
		soapActionText.setText("");
		
		new Label(composite, SWT.NONE);
	}
	
	/**
	 * Getter
	 */
	public String getEndpointUrl()
	{
		return endpointUrlText.getText();
	}
	
	public String getSoapAction()
	{
		return soapActionText.getText();
	}
	
	private Text endpointUrlText;
	private Text soapActionText;

	private static final String WIZARDPAGE_TITLE = "New Soap Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new Soap Connector Participant";
	
	//Label Name
	private static final String ENDPOINT_URL_LABEL = "Endpoint url:";
	private static final String SOAPACTION_LABEL = "Soap action:";

	//Tooltip
	private static final String ENDPOINT_URL_TOOLTIP = "SOAP endpoint URL to which SOAP calls will be placed. Specified for 'twconn:soapEndpointUrl'";
	private static final String SOAPACTION_TOOLTIP = "SOAP action to send with SOAP requests. Specified for 'twconn:soapAction'";
}
