package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class NewTWTransformConnectorParticipantWizardPage extends NewTWConnectorCRWizardPage
{
	public NewTWTransformConnectorParticipantWizardPage()
	{
		super("NewTransformConnectorParticipantWizardPage");
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
		
		addTransformer(composite);
		
		setControl(composite);
		setPageComplete(true);
	}
	
	private void addTransformer(final Composite composite)
	{
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.LEFT;
		Label transformerLabel = new Label(composite, SWT.NONE);
		transformerLabel.setText(TRANSFORMER_LABEL);
		transformerLabel.setLayoutData(gridData);
		transformerLabel.setToolTipText(TRANSFORMER_TOOLTIP);
		
		Composite lowerComposite = new Composite(composite, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 15;
		lowerComposite.setLayout(rowLayout);
		
		regexTransformerOption = new Button(lowerComposite, SWT.RADIO);
		regexTransformerOption.setText(REGEX_TRANSFORMER_OPTION);
		regexTransformerOption.setSelection(true);
		dateTransformerOption = new Button(lowerComposite, SWT.RADIO);
		dateTransformerOption.setText(DATE_TRANSFORMER_OPTION);
		userIdentityEncryptTransformerOption = new Button(lowerComposite, SWT.RADIO);
		userIdentityEncryptTransformerOption.setText(USER_IDENTITY_ENCRYPT_TRANSFORMER_OPTION);
	
		new Label(composite, SWT.NONE);
	}
	
	/**
	 * Getter
	 */
	public String getTransformer()
	{
		if (regexTransformerOption.getSelection())
		{
			return REGEX_TRANSFORMER_OPTION;
		}
		else if (dateTransformerOption.getSelection())
		{
			return DATE_TRANSFORMER_OPTION;
		}
		else
		{
			return USER_IDENTITY_ENCRYPT_TRANSFORMER_OPTION;
		}
	}
	
	private Button regexTransformerOption;
	private Button dateTransformerOption;
	private Button userIdentityEncryptTransformerOption;


	private static final String WIZARDPAGE_TITLE = "New Transform Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new Transform Connector Participant";
	
	//Label Name
	private static final String TRANSFORMER_LABEL = "Transformer:";
	
	//Transformer options
	private static final String REGEX_TRANSFORMER_OPTION = "regexTransformer";
	private static final String DATE_TRANSFORMER_OPTION = "dateTransformer";
	private static final String USER_IDENTITY_ENCRYPT_TRANSFORMER_OPTION = "userIdentityEncryptTransformer";
	
	//Tooltip
	private static final String TRANSFORMER_TOOLTIP = "Specified for 'twconn:transformer'";
}
