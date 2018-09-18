package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewTWSmtpRelayConnectorParticipantWizardPage extends NewTWRelayConnectorWizardPage
{
	public NewTWSmtpRelayConnectorParticipantWizardPage()
	{
		super("NewSMTPRelayConnectorParticipantWizardPage");
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

		addFilterGroup(composite);
		addFilterOrder(composite);
		addNewLine(composite, 1);
		addCheckAndTextGroupOptions(composite);
		
		setControl(composite);
		setPageComplete(true);
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
		group.setBackground(null);
		
		subjectPatternCheck = new Button(group, SWT.CHECK);
		subjectPatternCheck.setSelection(true);
		Label subjectPatternLabel = new Label(group, SWT.NONE);
		subjectPatternLabel.setText(SUBJECT_PATTERN_LABEL);
		subjectPatternLabel.setToolTipText(SUBJECT_PATTERN_TOOLTIP);
		GridData gridData2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		subjectPatternText = new Text(group, SWT.SINGLE | SWT.BORDER);
		subjectPatternText.setLayoutData(gridData2);
		subjectPatternText.setText("");
		enableTextBasedOnCheck(subjectPatternCheck, subjectPatternText);
		
		subjectPatternTemplateCheck = new Button(group, SWT.CHECK);
		Label subjectPatternTemplateLabel = new Label(group, SWT.NONE);
		subjectPatternTemplateLabel.setText(SUBJECT_PATTERN_TEMPLATE_LABEL);
		subjectPatternTemplateLabel.setToolTipText(SUBJECT_PATTERN_TEMPLATE_TOOLTIP);
		subjectPatternTemplateText = new Text(group, SWT.SINGLE | SWT.BORDER);
		subjectPatternTemplateText.setLayoutData(gridData2);
		subjectPatternTemplateText.setText("");
		subjectPatternTemplateText.setEnabled(false);
		enableTextBasedOnCheck(subjectPatternTemplateCheck, subjectPatternTemplateText);
		
		recipientPatternCheck = new Button(group, SWT.CHECK);
		Label recipientPatternLabel = new Label(group, SWT.NONE);
		recipientPatternLabel.setText(RECIPIENT_PATTERN_LABEL);
		recipientPatternLabel.setToolTipText(RECIPIENT_PATTERN_TOOLTIP);
		recipientPatternText = new Text(group, SWT.SINGLE | SWT.BORDER);
		recipientPatternText.setLayoutData(gridData2);
		recipientPatternText.setText("");
		recipientPatternText.setEnabled(false);
		enableTextBasedOnCheck(recipientPatternCheck, recipientPatternText);
		
		recipientPatternTemplateCheck = new Button(group, SWT.CHECK);
		recipientPatternTemplateCheck.setSelection(true);
		Label recipientPatternTemplateLabel = new Label(group, SWT.NONE);
		recipientPatternTemplateLabel.setText(RECIPIENT_PATTERN_TEMPLATE_LABEL);
		recipientPatternTemplateLabel.setToolTipText(RECIPIENT_PATTERN_TEMPLATE_TOOLTIP);
		recipientPatternTemplateText = new Text(group, SWT.SINGLE | SWT.BORDER);
		recipientPatternTemplateText.setLayoutData(gridData2);
		recipientPatternTemplateText.setText("");
		enableTextBasedOnCheck(recipientPatternTemplateCheck, recipientPatternTemplateText);
	}
	
	/**
	 * Getter
	 */
	public String getSubjectPattern()
	{
		return subjectPatternCheck.getSelection()? subjectPatternText.getText() : null;
	}
	
	public String getRecipientPattern()
	{
		return recipientPatternCheck.getSelection()? recipientPatternText.getText() : null;
	}
	
	public String getSubjectPatternTemplate()
	{
		return subjectPatternTemplateCheck.getSelection()? subjectPatternTemplateText.getText() : null;
	}
	
	public String getRecipientPatternTemplate()
	{
		return recipientPatternTemplateCheck.getSelection()? recipientPatternTemplateText.getText() : null;
	}
	
	@Override
	public boolean canFlipToNextPage()
	{
		return false;
	}
	
	private Button subjectPatternCheck;
	private Button recipientPatternCheck;
	private Button subjectPatternTemplateCheck;
	private Button recipientPatternTemplateCheck;

	private Text subjectPatternText;
	private Text recipientPatternText;
	private Text subjectPatternTemplateText;
	private Text recipientPatternTemplateText;

	private static final String WIZARDPAGE_TITLE = "New SMTP Relay Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new SMTP Relay Connector Participant";
	
	//Label Name
	private static final String SUBJECT_PATTERN_LABEL = "subjectPattern:";
	private static final String RECIPIENT_PATTERN_LABEL = "recipientPattern:";
	private static final String SUBJECT_PATTERN_TEMPLATE_LABEL = "subjectPatternTemplate";
	private static final String RECIPIENT_PATTERN_TEMPLATE_LABEL = "recipientPatternTemplate:";

	//Tooltip
	private static final String SUBJECT_PATTERN_TOOLTIP = "Specified for 'twconn:subjectPattern'";
	private static final String RECIPIENT_PATTERN_TOOLTIP = "Specified for 'twconn:recipientPattern'";
	private static final String SUBJECT_PATTERN_TEMPLATE_TOOLTIP = "Specified for 'twconn:subjectPatternTemplate'";
	private static final String RECIPIENT_PATTERN_TEMPLATE_TOOLTIP = "Specified for 'twconn:recipientPatternTemplate'";
}
