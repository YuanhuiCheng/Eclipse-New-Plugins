package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewTWRestConnectorParticipantWizardPage extends NewTWConnectorCRWizardPage
{
	public NewTWRestConnectorParticipantWizardPage()
	{
		super("NewRestConnectorParticipantWizardPage");
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
		
		addInputFormat(composite);
		addEndpointUrl(composite);
		addNewLine(composite, 1);
		addHttpMethod(composite);
		addRequestType(composite);
		
		setControl(composite);
		setPageComplete(true);
	}
	
	private void addInputFormat(final Composite composite)
	{
		Label inputFormatLabel = new Label(composite, SWT.NONE);
		inputFormatLabel.setText(INPUT_FORMAT_LABEL);
		inputFormatLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		inputFormatLabel.setToolTipText(INPUT_FORMAT_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		inputFormatText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		inputFormatText.setLayoutData(gridData);
		inputFormatText.setText("");
		
		addEmptyLabel(composite, 45);
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
		
		new Label(composite, SWT.NONE);
	}
	
	private void addHttpMethod(final Composite composite)
	{
		Label httpMethodLabel = new Label(composite, SWT.NONE);
		httpMethodLabel.setText(HTTP_METHOD_LABEL);
		httpMethodLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		httpMethodLabel.setToolTipText(HTTP_METHOD_TOOLTIP);
		
		String httpMethod[] = new String[] {GET_METHOD, POST_METHOD, PUT_METHOD, DELETE_METHOD, POST_JSON_METHOD,
											PUT_JSON_METHOD };
		Arrays.sort(httpMethod);
		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gridData.widthHint = 230;
		httpMethodList = new Combo(composite, SWT.READ_ONLY);
		httpMethodList.setLayoutData(gridData);
		httpMethodList.setItems(httpMethod);
		httpMethodList.setText(GET_METHOD);
		
		new Label(composite, SWT.NONE);
	}

	private void addRequestType(final Composite composite)
	{
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.LEFT;
		Label requestTypeLabel = new Label(composite, SWT.NONE);
		requestTypeLabel.setText(REQUEST_TYPE_LABEL);
		requestTypeLabel.setLayoutData(gridData);
		requestTypeLabel.setToolTipText(REQUEST_TYPE_TOOLTIP);
		
		Composite lowerComposite = new Composite(composite, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 15;
		lowerComposite.setLayout(rowLayout);
		
		jsonOption = new Button(lowerComposite, SWT.RADIO);
		jsonOption.setText(RESTCONNECTOR_REQUEST_TYPE_JSON);
		jsonOption.setToolTipText(REQUEST_TYPE_JSON_TOOLTIP);
		jsonOption.setSelection(true);
		xmlOption = new Button(lowerComposite, SWT.RADIO);
		xmlOption.setText(RESTCONNECTOR_REQUEST_TYPE_XML);
		xmlOption.setToolTipText(REQUEST_TYPE_XML_TOOLTIP);
		
		new Label(composite, SWT.NONE);
	}
	
	/**
	 * Getter
	 */
	public String getEndpointUrl()
	{
		return endpointUrlText.getText();
	}
	
	public String getInputFormat()
	{
		return inputFormatText.getText();
	}
	
	public String getRequestType()
	{
		return jsonOption.getSelection()? RESTCONNECTOR_REQUEST_TYPE_JSON : RESTCONNECTOR_REQUEST_TYPE_XML;
	}
	
	public String getHttpMethodListSelection()
	{
		return httpMethodList.getText();
	}
	
	private Text endpointUrlText;
	private Text inputFormatText;
	private Combo httpMethodList;
	
	private Button jsonOption;
	private Button xmlOption;

	private static final String WIZARDPAGE_TITLE = "New Rest Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new Rest Connector Participant";
	
	//Label Name
	private static final String REQUEST_TYPE_LABEL = "Request type:";
	private static final String ENDPOINT_URL_LABEL = "Endpoint path:";
	private static final String INPUT_FORMAT_LABEL = "Input format:";
	private static final String HTTP_METHOD_LABEL = "Http method:";
	
	//Request Type
	private static final String RESTCONNECTOR_REQUEST_TYPE_JSON = "JSON";
	private static final String RESTCONNECTOR_REQUEST_TYPE_XML = "XML";
	
	//Http Method
	private static final String GET_METHOD = "GET";
	private static final String POST_METHOD = "POST";
	private static final String PUT_METHOD = "PUT";
	private static final String DELETE_METHOD = "DELETE";
	private static final String POST_JSON_METHOD = "POSTJSON";
	private static final String PUT_JSON_METHOD = "PUTJSON";
	
	//Tooltip
	private static final String ENDPOINT_URL_TOOLTIP = "Server URL specified for 'twconn:restEndpointPath'";
	private static final String REQUEST_TYPE_TOOLTIP = "Request and response type specified for 'twconn:requestType'";
	private static final String INPUT_FORMAT_TOOLTIP = "Input format specified for 'twconn:inputFormat', like 'twInput:oData'";
	private static final String REQUEST_TYPE_JSON_TOOLTIP = "Request and response are supposed to be JSON";
	private static final String REQUEST_TYPE_XML_TOOLTIP = "Request and response are supposed to be XML";
	private static final String HTTP_METHOD_TOOLTIP = "Http method specified for 'twconn:httpMethod'";
}
