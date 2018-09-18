package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class NewTWKeyValueConnectorParticipantWizardPage extends NewTWConnectorCRWizardPage
{
	public NewTWKeyValueConnectorParticipantWizardPage()
	{
		super("NewKeyValueConnectorParticipantWizardPage");
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
		addBatchTask(composite);
		
		setControl(composite);
		setPageComplete(true);
	}
	
	private void addCommandOption(final Composite composite)
	{	
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.LEFT;
		Label commandOptionLabel = new Label(composite, SWT.NONE);
		commandOptionLabel.setText(COMMAND_OPTION_LABEL);
		commandOptionLabel.setLayoutData(gridData);
		commandOptionLabel.setToolTipText(COMMAND_OPTION_TOOLTIP);
		
		Composite lowerComposite = new Composite(composite, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 15;
		lowerComposite.setLayout(rowLayout);
		
		getOption = new Button(lowerComposite, SWT.RADIO);
		getOption.setText(GET_OPTION);
		getOption.setToolTipText(GET_OPTION_TOOLTIP_FALSE);
		getOption.setSelection(true);
		putOption = new Button(lowerComposite, SWT.RADIO);
		putOption.setText(PUT_OPTION);
		putOption.setToolTipText(PUT_OPTION_TOOLTIP_FALSE);
		deleteOption = new Button(lowerComposite, SWT.RADIO);
		deleteOption.setText(DELETE_OPTION);
		deleteOption.setToolTipText(DELETE_OPTION_TOOLTIP_FALSE);
		keysOption = new Button(lowerComposite, SWT.RADIO);
		keysOption.setText(KEYS_OPTION);
		keysOption.setToolTipText(KEYS_OPTION_TOOLTIP);
		valuesOption = new Button(lowerComposite, SWT.RADIO);
		valuesOption.setText(VALUES_OPTION);
		valuesOption.setToolTipText(VALUES_OPTION_TOOLTIP);
	
		new Label(composite, SWT.NONE);
	}
	
	private void addBatchTask(final Composite composite)
	{
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.LEFT;
		Label ifBatchTaskLabel = new Label(composite, SWT.NONE);
		ifBatchTaskLabel.setText(BATCH_TASK_LABEL);
		ifBatchTaskLabel.setLayoutData(gridData);
		ifBatchTaskLabel.setToolTipText(BATCH_TASK_TOOLTIP);
		
		Composite lowerComposite = new Composite(composite, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 35;
		lowerComposite.setLayout(rowLayout);
		
		trueOption = new Button(lowerComposite, SWT.RADIO);
		trueOption.setText(BATCH_TASK_TRUE);
		falseOption = new Button(lowerComposite, SWT.RADIO);
		falseOption.setText(BATCH_TASK_FALSE);
		falseOption.setSelection(true);
		
		trueOption.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if (trueOption.getSelection())
				{
					getOption.setToolTipText(GET_OPTION_TOOLTIP_TRUE);
					putOption.setToolTipText(PUT_OPTION_TOOLTIP_TRUE);
					deleteOption.setToolTipText(DELETE_OPTION_TOOLTIP_TRUE);
				}
			}
		});
		
		falseOption.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				if (falseOption.getSelection())
				{
					getOption.setToolTipText(GET_OPTION_TOOLTIP_FALSE);
					putOption.setToolTipText(PUT_OPTION_TOOLTIP_FALSE);
					deleteOption.setToolTipText(DELETE_OPTION_TOOLTIP_FALSE);
				}
			}
		});
	}
	
	/**
	 * Getter
	 */	
	public String getCommandType()
	{
		if (getOption.getSelection())
		{
			return GET_OPTION;
		}
		else if (putOption.getSelection())
		{
			return PUT_OPTION;
		}
		else if (deleteOption.getSelection())
		{
			return DELETE_OPTION;
		}
		else if (keysOption.getSelection())
		{
			return KEYS_OPTION;
		}
		else
		{
			return VALUES_OPTION;
		}
	}
	
	public String getBatchTask()
	{
		return falseOption.getSelection()? BATCH_TASK_FALSE : BATCH_TASK_TRUE;
	}
	
	private Button getOption;
	private Button putOption;
	private Button deleteOption;
	private Button keysOption;
	private Button valuesOption;
	private Button trueOption;
	private Button falseOption;

	private static final String WIZARDPAGE_TITLE = "New KeyValue Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new KeyValue Connector Participant";
	
	//Label Name
	private static final String COMMAND_OPTION_LABEL = "Command type:";
	private static final String BATCH_TASK_LABEL = "Batch task:";
	
	//Command Type
	private static final String GET_OPTION = "get";
	private static final String PUT_OPTION = "put";
	private static final String DELETE_OPTION = "delete";
	private static final String KEYS_OPTION = "keys";
	private static final String VALUES_OPTION = "values";
	
	private static final String BATCH_TASK_TRUE = "true";
	private static final String BATCH_TASK_FALSE = "false";
	
	//Tooltip
	private static final String COMMAND_OPTION_TOOLTIP = "Specified for 'twconn:command'";
	private static final String GET_OPTION_TOOLTIP_FALSE = "The connector will retrieve the value associated with the generated key";
	private static final String GET_OPTION_TOOLTIP_TRUE = "Every value associated with all the generated keys will be returned";
	private static final String PUT_OPTION_TOOLTIP_FALSE = "The entire JSON array will be stored using that key";
	private static final String PUT_OPTION_TOOLTIP_TRUE = "Each JSON object will be stored using their respective key";
	private static final String DELETE_OPTION_TOOLTIP_FALSE = "The connector will delete the value associated with the generated key";
	private static final String DELETE_OPTION_TOOLTIP_TRUE = "Each of the generated keys will be deleted";
	private static final String KEYS_OPTION_TOOLTIP = "Get a list of all the keys for a given bucket";
	private static final String VALUES_OPTION_TOOLTIP = "get a list of all the values in a given bucket";
	private static final String BATCH_TASK_TOOLTIP = "Determines whether the operation performs an operation on each row of a submitted array or performs one operation on all submitted data. Specified for 'twconn:batchTask'";
}
