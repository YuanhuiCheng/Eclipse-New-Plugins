package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewTWRestRelayConnectorParticipantWizardPage extends NewTWRelayConnectorWizardPage
{
	public NewTWRestRelayConnectorParticipantWizardPage()
	{
		super("NewRESTRelayConnectorParticipantWizardPage");
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
		
		addEndpointType(composite);
		addEndpointUrl(composite);
		addFilterGroup(composite);
		addFilterOrder(composite);
		
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
	
	private void addEndpointType(final Composite composite)
	{
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.LEFT;
		Label endpointTypeLabel = new Label(composite, SWT.NONE);
		endpointTypeLabel.setText(ENDPOINT_TYPE_LABEL);
		endpointTypeLabel.setLayoutData(gridData);
		endpointTypeLabel.setToolTipText(ENDPOINT_TYPE_TOOLTIP);
		
		Composite lowerComposite = new Composite(composite, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 15;
		lowerComposite.setLayout(rowLayout);
		
		restJsonOption = new Button(lowerComposite, SWT.RADIO);
		restJsonOption.setText(ENDPOINT_TYPE_JSON);
		restJsonOption.setSelection(true);
		restXmlOption = new Button(lowerComposite, SWT.RADIO);
		restXmlOption.setText(ENDPOINT_TYPE_XML);
		
		new Label(composite, SWT.NONE);
	}
	
	/**
	 * Getter
	 */
	public String getEndpointUrl()
	{
		return endpointUrlText.getText();
	}
	
	public String getEndpointType()
	{
		return restJsonOption.getSelection()? ENDPOINT_TYPE_JSON : ENDPOINT_TYPE_XML;
	}
	
	private Button restJsonOption;
	private Button restXmlOption;
	
	private Text endpointUrlText;

	private static final String WIZARDPAGE_TITLE = "New REST Relay Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new REST Relay Connector Participant";
	
	//Label Name
	private static final String ENDPOINT_URL_LABEL = "Endpoint url:";
	private static final String ENDPOINT_TYPE_LABEL = "Endpoint type:";
	
	//Endpoint Type
	private static final String ENDPOINT_TYPE_JSON = "RestJSON";
	private static final String ENDPOINT_TYPE_XML = "RestXML";
	
	//Tooltip
	private static final String ENDPOINT_URL_TOOLTIP = "The endpoint that external services will call. Specified for 'twconn:restEndpointUrl'";
	private static final String ENDPOINT_TYPE_TOOLTIP = "Specified for 'twconn:endpointType'";
}
