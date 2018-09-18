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

public class NewTWBacnetConnectorParticipantWizardPage extends NewTWConnectorCRWizardPage
{
	public NewTWBacnetConnectorParticipantWizardPage()
	{
		super("NewBACnetConnectorParticipantWizardPage");
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
		
		listDeviceOption = new Button(lowerComposite, SWT.CHECK);
		listDeviceOption.setText(LIST_DEVICE_OPTION);
		listDeviceOption.setToolTipText(LIST_DEVICE_OPTION_TOOLTIP);
		listDeviceOption.setSelection(true);
		listObjectsOption = new Button(lowerComposite, SWT.CHECK);
		listObjectsOption.setText(LIST_OBJECTS_OPTION);
		listObjectsOption.setToolTipText(LIST_OBJECTS_OPTION_TOOLTIP);
		readOption = new Button(lowerComposite, SWT.CHECK);
		readOption.setText(READ_OPTION);
		readOption.setToolTipText(READ_OPTION_TOOLTIP);
		writeOption = new Button(lowerComposite, SWT.CHECK);
		writeOption.setText(WRITE_OPTION);
		writeOption.setToolTipText(WRITE_OPTION_TOOLTIP);
	
		addListenerToOptionToEnableFinishButton(listDeviceOption);
		addListenerToOptionToEnableFinishButton(listObjectsOption);
		addListenerToOptionToEnableFinishButton(readOption);
		addListenerToOptionToEnableFinishButton(writeOption);
		
		new Label(composite, SWT.NONE);
	}
	
	private void addListenerToOptionToEnableFinishButton(final Button option)
	{
		option.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				enableFinishButtonBasedOnOption();
			}
		});
	}
	
	private void enableFinishButtonBasedOnOption()
	{
		if (checkIfAnyOptionIsSelected())
		{
			getWizard().getContainer().updateButtons();
		}
		else
		{
			setPageComplete(false);
		}
	}
	
	private boolean checkIfAnyOptionIsSelected()
	{
		return listDeviceOption.getSelection() || listObjectsOption.getSelection() || readOption.getSelection() || writeOption.getSelection();
	}
	
	@Override
	public boolean isPageComplete()
	{
		if (checkIfAnyOptionIsSelected())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Getter
	 */	
	public boolean getIfListDeviceOptionCheckedResult()
	{
		return listDeviceOption.getSelection()? true : false;
	}
	
	public boolean getIfListObjectsOptionCheckedResult()
	{
		return listObjectsOption.getSelection()? true : false;
	}
	
	public boolean getIfReadOptionCheckedResult()
	{
		return readOption.getSelection()? true : false;
	}
	
	public boolean getIfWriteOptionCheckedResult()
	{
		return writeOption.getSelection()? true : false;
	}
	
	private Button listDeviceOption;
	private Button listObjectsOption;
	private Button readOption;
	private Button writeOption;

	private static final String WIZARDPAGE_TITLE = "New BACnet Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new BACnet Connector Participant";
	
	//Label Name
	private static final String COMMAND_OPTION_LABEL = "Command type:";
	
	//Command Type
	private static final String LIST_DEVICE_OPTION = "ListDevices";
	private static final String LIST_OBJECTS_OPTION = "ListObjects";
	private static final String READ_OPTION = "Read";
	private static final String WRITE_OPTION = "Write";
	
	//Tooltip
	private static final String COMMAND_OPTION_TOOLTIP = "Specified for 'twconn:command'";
	private static final String LIST_DEVICE_OPTION_TOOLTIP = "Specified for 'twd:ListDevices'";
	private static final String LIST_OBJECTS_OPTION_TOOLTIP = "Specified for 'twd:ListObjects'";
	private static final String READ_OPTION_TOOLTIP = "Specified for 'twd:Read'";
	private static final String WRITE_OPTION_TOOLTIP = "Specified for 'twd:Write'";
}
