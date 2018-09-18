package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

public abstract class NewTWConnectorCRWizardPage extends NewTWWizardPage
{
	public NewTWConnectorCRWizardPage(final String pageName) 
	{
		super(pageName);
	}

	@Override
	public boolean canFlipToNextPage()
	{
		return false;
	}
	
//	public void addCheckIfCreateEventConsumeRequest(final Composite composite)
//	{
//		GridData gridData = new GridData();
//		gridData.verticalAlignment = SWT.LEFT;
//		gridData.horizontalSpan = LAYOUT_COL_NUM;
//		Label createEventCRLabel = new Label(composite, SWT.NONE);
//		createEventCRLabel.setText("Do you want to add an event consume request?");
//		createEventCRLabel.setLayoutData(gridData);
//
//		new Label(composite, SWT.None);
//		
//		ifCreateEventCROption = new Button(composite, SWT.CHECK);
//		ifCreateEventCROption.setText(IF_CREATE_EVENTCR_LABEL);
//	}
//	
//	public boolean getIfCreateEventCR()
//	{
//		return ifCreateEventCROption.getSelection()? true : false;
//	}
//
//	private Button ifCreateEventCROption;
//	
//	private static final String IF_CREATE_EVENTCR_LABEL = "Generate event consume request";
}
