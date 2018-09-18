package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewTWCommunicationConnectorParticipantWizardPage extends NewTWConnectorCRWizardPage
{
	public NewTWCommunicationConnectorParticipantWizardPage()
	{
		super("NewCommunicationConnectorParticipantWizardPage");
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
		
		addCommandOption(composite);
		addMessage(composite);
		addNewLine(composite, 1);
		addCheckAndTextGroupOptions(composite);
		
		setControl(composite);
		setPageComplete(true);
	}
	
	
	private void addCommandOption(final Composite composite)
	{
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.LEFT;
		Label commandOptionLabel = new Label(composite, SWT.NONE);
		commandOptionLabel.setText(COMMAND_TYPE_LABEL);
		commandOptionLabel.setLayoutData(gridData);
		commandOptionLabel.setToolTipText(COMMAND_TYPE_TOOLTIP);
		
		Composite lowerComposite = new Composite(composite, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 30;
		lowerComposite.setLayout(rowLayout);

		smtpOption = new Button(lowerComposite, SWT.RADIO);
		smtpOption.setText(COMMAND_SMTP_OPTION);
		smtpOption.setSelection(true);
		pushOption = new Button(lowerComposite, SWT.RADIO);
		pushOption.setText(COMMAND_PUSH_OPTION);
		
		smtpOption.addSelectionListener(new SelectionAdapter()
		{
			@Override
        	public void widgetSelected(SelectionEvent e)
        	{
				if (smtpOption.getSelection())
				{
					deviceTokenCheck.setSelection(false);
					deviceTokenCheck.setEnabled(false);
					deviceTokenFieldCheck.setSelection(false);
					deviceTokenFieldCheck.setEnabled(false);
					deviceTokenText.setEnabled(false);
					deviceTokenFieldText.setEnabled(false);
				}
	    	}
		});
		
		pushOption.addSelectionListener(new SelectionAdapter()
		{
			@Override
        	public void widgetSelected(SelectionEvent e)
        	{
				if (pushOption.getSelection())
				{
					deviceTokenCheck.setSelection(true);
					deviceTokenCheck.setEnabled(true);
					deviceTokenText.setEnabled(true);
					deviceTokenFieldCheck.setEnabled(true);
				}
	    	}
		});
		
		addEmptyLabel(composite, 45);
	}
	
	private void addMessage(final Composite composite)
	{
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.LEFT;
		Label messageLabel = new Label(composite, SWT.NONE);
		messageLabel.setLayoutData(gridData);
		messageLabel.setText(MESSAGE_LABEL);
		messageLabel.setToolTipText(MESSAGE_TOOLTIP);
			
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		messageText = new Text(composite, SWT.MULTI | SWT.BORDER);
		messageText.setLayoutData(gridData);
		messageText.setText("");
		
		new Label(composite, SWT.NONE);
	}
	
	private void addCheckAndTextGroupOptions(final Composite composite)
	{
		Group group = new Group(composite, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.horizontalSpan = 6;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		group.setLayout(gridLayout);
		group.setLayoutData(gridData);
		
		recipientCheck = new Button(group, SWT.CHECK);
		recipientCheck.setSelection(true);
		GridData gridData2 = new GridData();
		Label recipientLabel = new Label(group, SWT.NONE);
		recipientLabel.setText(RECIPIENT_LABEL);
		recipientLabel.setToolTipText(RECIPIENT_TOOLTIP);
		recipientLabel.setLayoutData(gridData2);
		GridData gridData3 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		recipientText = new Text(group, SWT.SINGLE | SWT.BORDER);
		recipientText.setLayoutData(gridData3);
		recipientText.setText("");
		enableTextBasedOnCheck(recipientCheck, recipientText);
		
		recipientFieldCheck = new Button(group, SWT.CHECK);
		Label recipientFieldLabel = new Label(group, SWT.NONE);
		recipientFieldLabel.setText(RECIPIENT_FIELD_LABEL);
		recipientFieldLabel.setToolTipText(RECIPIENT_FIELD_TOOLTIP);
		recipientFieldText = new Text(group, SWT.SINGLE | SWT.BORDER);
		recipientFieldText.setLayoutData(gridData3);
		recipientFieldText.setEnabled(false);
		recipientFieldText.setText("");
		enableTextBasedOnCheck(recipientFieldCheck, recipientFieldText);
		
		fromCheck = new Button(group, SWT.CHECK);
		fromCheck.setSelection(true);;
		Label fromLabel = new Label(group, SWT.NONE);
		fromLabel.setText(FROM_LABEL);
		fromLabel.setToolTipText(FROM_TOOLTIP);
		fromText = new Text(group, SWT.SINGLE | SWT.BORDER);
		fromText.setLayoutData(gridData3);
		fromText.setText("");
		enableTextBasedOnCheck(fromCheck, fromText);
		
		fromFieldCheck = new Button(group, SWT.CHECK);
		Label fromFieldLabel = new Label(group, SWT.NONE);
		fromFieldLabel.setText(FROM_FIELD_LABEL);
		fromFieldLabel.setToolTipText(FROM_FIELD_TOOLTIP);
		fromFieldText = new Text(group, SWT.SINGLE | SWT.BORDER);
		fromFieldText.setLayoutData(gridData3);
		fromFieldText.setEnabled(false);
		fromFieldText.setText("");
		enableTextBasedOnCheck(fromFieldCheck, fromFieldText);
		
		ccCheck = new Button(group, SWT.CHECK);
		Label ccLabel = new Label(group, SWT.NONE);
		ccLabel.setText(CC_LABEL);
		fromLabel.setToolTipText(CC_TOOLTIP);
		ccText = new Text(group, SWT.SINGLE | SWT.BORDER);
		ccText.setLayoutData(gridData3);
		ccText.setEnabled(false);
		ccText.setText("");
		enableTextBasedOnCheck(ccCheck, ccText);
		
		ccFieldCheck = new Button(group, SWT.CHECK);
		Label ccFieldLabel = new Label(group, SWT.NONE);
		ccFieldLabel.setText(CC_FIELD_LABEL);
		fromLabel.setToolTipText(CC_FIELD_TOOLTIP);
		ccFieldText = new Text(group, SWT.SINGLE | SWT.BORDER);
		ccFieldText.setLayoutData(gridData3);
		ccFieldText.setEnabled(false);
		ccFieldText.setText("");
		enableTextBasedOnCheck(ccFieldCheck, ccFieldText);
		
		subjectCheck = new Button(group, SWT.CHECK);
		subjectCheck.setSelection(true);
		Label subjectLabel = new Label(group, SWT.NONE);
		subjectLabel.setText(SUBJECT_LABEL);
		subjectLabel.setToolTipText(SUBJECT_TOOLTIP);
		subjectText = new Text(group, SWT.SINGLE | SWT.BORDER);
		subjectText.setLayoutData(gridData3);
		subjectText.setText("");
		enableTextBasedOnCheck(subjectCheck, subjectText);
		
		subjectTemplateCheck = new Button(group, SWT.CHECK);
		Label subjectTemplateLabel = new Label(group, SWT.NONE);
		subjectTemplateLabel.setText(SUBJECT_TEMPLATE_LABEL);
		subjectTemplateLabel.setToolTipText(SUBJECT_TEMPLATE_TOOLTIP);
		subjectTemplateText = new Text(group, SWT.SINGLE | SWT.BORDER);
		subjectTemplateText.setLayoutData(gridData3);
		subjectTemplateText.setEnabled(false);
		subjectTemplateText.setText("");
		enableTextBasedOnCheck(subjectTemplateCheck, subjectTemplateText);
		
		templateCheck = new Button(group, SWT.CHECK);
		templateCheck.setSelection(true);
		Label templateLabel = new Label(group, SWT.NONE);
		templateLabel.setText(TEMPLATE_LABEL);
		templateLabel.setToolTipText(TEMPLATE_TOOLTIP);
		templateText = new Text(group, SWT.SINGLE | SWT.BORDER);
		templateText.setLayoutData(gridData3);
		templateText.setText("");
		enableTextBasedOnCheck(templateCheck, templateText);
		
		deviceTokenCheck = new Button(group, SWT.CHECK);
		deviceTokenCheck.setEnabled(false);
		Label deviceTokenLabel = new Label(group, SWT.NONE);
		deviceTokenLabel.setText(DEVICE_TOKEN_LABEL);
		deviceTokenLabel.setToolTipText(DEVICE_TOKEN_TOOLTIP);
		deviceTokenText = new Text(group, SWT.SINGLE | SWT.BORDER);
		deviceTokenText.setLayoutData(gridData3);
		deviceTokenText.setEnabled(false);
		deviceTokenText.setText("");
		enableTextBasedOnCheck(deviceTokenCheck, deviceTokenText);
		
		deviceTokenFieldCheck = new Button(group, SWT.CHECK);
		deviceTokenFieldCheck.setEnabled(false);
		Label deviceTokenFieldLabel = new Label(group, SWT.NONE);
		deviceTokenFieldLabel.setText(DEVICE_TOKEN_FIELD_LABEL);
		deviceTokenFieldLabel.setToolTipText(DEVICE_TOKEN_FIELD_TOOLTIP);
		deviceTokenFieldText = new Text(group, SWT.SINGLE | SWT.BORDER);
		deviceTokenFieldText.setLayoutData(gridData3);
		deviceTokenFieldText.setEnabled(false);
		deviceTokenFieldText.setText("");
		enableTextBasedOnCheck(deviceTokenFieldCheck, deviceTokenFieldText);
	}
	
	/**
	 * Getter
	 */
	public String getCommandType()
	{
		return smtpOption.getSelection()? COMMAND_SMTP_OPTION : COMMAND_PUSH_OPTION;
	}
	
	public String getMessage()
	{
		return messageText.getText();
	}
	
	public String getRecipient()
	{
		return recipientCheck.getSelection()? recipientText.getText() : null;
	}
	
	public String getRecipientField()
	{
		return recipientFieldCheck.getSelection()? recipientFieldText.getText() : null;
	}
	
	public String getFrom()
	{
		return fromCheck.getSelection()? fromText.getText() : null;
	}

	public String getFromField()
	{
		return fromFieldCheck.getSelection()? fromFieldText.getText() : null;
	}

	public String getCc()
	{
		return ccCheck.getSelection()? ccText.getText() : null;
	}
	
	public String getCcField()
	{
		return ccFieldCheck.getSelection()? ccFieldText.getText() : null;
	}
	
	public String getSubject()
	{
		return subjectCheck.getSelection()? subjectText.getText() : null;
	}
	
	public String getSubjectTemplate()
	{
		return subjectTemplateCheck.getSelection()? subjectTemplateText.getText() : null;
	}
	
	public String getTemplate()
	{
		return templateCheck.getSelection()? templateText.getText() : null;
	}
	
	public String getDeviceToken()
	{
		return deviceTokenCheck.getSelection()? deviceTokenText.getText() : null;
	}
	
	public String getDeviceTokenField()
	{
		return deviceTokenFieldCheck.getSelection()? deviceTokenFieldText.getText() : null;
	}
	
	private Button smtpOption;
	private Button pushOption;
	
	private Button recipientCheck;
	private Button recipientFieldCheck;
	private Button fromCheck;
	private Button fromFieldCheck;
	private Button ccCheck;
	private Button ccFieldCheck;
	private Button subjectCheck;
	private Button subjectTemplateCheck;
	private Button templateCheck;
	private Button deviceTokenCheck;
	private Button deviceTokenFieldCheck;
	
	private Text messageText;
	private Text recipientText;
	private Text recipientFieldText;
	private Text fromText;
	private Text fromFieldText;
	private Text ccText;
	private Text ccFieldText;
	private Text subjectText;
	private Text subjectTemplateText;
	private Text templateText;
	private Text deviceTokenText;
	private Text deviceTokenFieldText;

	private static final String WIZARDPAGE_TITLE = "New Communication Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new Communication Connector Participant";
	
	//Command Option
	private static final String COMMAND_SMTP_OPTION = "smtp";
	private static final String COMMAND_PUSH_OPTION = "push";
	
	//Label Name
	private static final String COMMAND_TYPE_LABEL = "Command type:";
	private static final String MESSAGE_LABEL = "Message:";
	private static final String RECIPIENT_LABEL = "recipient:";
	private static final String RECIPIENT_FIELD_LABEL = "recipientField:";
	private static final String FROM_LABEL = "from:";
	private static final String FROM_FIELD_LABEL = "fromField:";
	private static final String CC_LABEL = "cc:";
	private static final String CC_FIELD_LABEL = "ccField:";
	private static final String SUBJECT_LABEL = "subject:";
	private static final String SUBJECT_TEMPLATE_LABEL = "subjectTemplate:";
	private static final String TEMPLATE_LABEL = "template:";
	private static final String DEVICE_TOKEN_LABEL = "deviceToken:";
	private static final String DEVICE_TOKEN_FIELD_LABEL = "deviceTokenField:";
	
	//Tooltip
	private static final String COMMAND_TYPE_TOOLTIP = "The type of communication to perform. Specified for 'twconn:command'";
	private static final String MESSAGE_TOOLTIP = "The (static) message to send. Specified for 'twconn:message'";
	private static final String RECIPIENT_TOOLTIP = "Specified for 'twconn:recipient'";
	private static final String RECIPIENT_FIELD_TOOLTIP = "Specified for 'twconn:recipientField'";
	private static final String FROM_TOOLTIP = "Specified for 'twconn:from'";
	private static final String FROM_FIELD_TOOLTIP = "Specified for 'twconn:fromField'";
	private static final String CC_TOOLTIP = "Specified for 'twconn:cc'";
	private static final String CC_FIELD_TOOLTIP = "Specified for 'twconn:ccField'";
	private static final String SUBJECT_TOOLTIP = "Specified for 'twconn:subject'";
	private static final String SUBJECT_TEMPLATE_TOOLTIP = "Specified for 'twconn:subjectTemplate'";
	private static final String TEMPLATE_TOOLTIP = "Specified for 'twconn:template'";
	private static final String DEVICE_TOKEN_TOOLTIP = "Specified for 'twconn:deviceToken'";
	private static final String DEVICE_TOKEN_FIELD_TOOLTIP = "Specified for 'twconn:deviceTokenField'";
}
