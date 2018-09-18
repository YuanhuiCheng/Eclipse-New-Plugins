package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewTWRelayConnectorWizardPage extends NewTWWizardPage
{
	public NewTWRelayConnectorWizardPage(final String pageName) 
	{
		super(pageName);
	}
	
	@Override
	public void createControl(final Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(LAYOUT_COL_NUM, false);
		composite.setLayout(layout);
	
		addFilterGroup(composite);
		addFilterOrder(composite);

		setControl(composite);
		setPageComplete(true);
	}
	
	public void addFilterGroup(final Composite composite)
	{
		Composite lowerComposite = new Composite(composite, SWT.NONE);
		lowerComposite.setLayout(new RowLayout());
		
		filterGroupCheck = new Button(lowerComposite, SWT.CHECK);
		filterGroupCheck.setSelection(true);
		
		Label filterGroupLabel = new Label(lowerComposite, SWT.NONE);
		filterGroupLabel.setText(FILTER_GROUP_LABEL);
		filterGroupLabel.setToolTipText(FILTER_GROUP_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		filterGroupText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		filterGroupText.setLayoutData(gridData);
		filterGroupText.setText(""); 
		
		enableTextBasedOnCheck(filterGroupCheck, filterGroupText);
		
		addEmptyLabel(composite, 45);
	}
	
	public void addFilterOrder(final Composite composite)
	{
		Composite lowerComposite = new Composite(composite, SWT.NONE);
		lowerComposite.setLayout(new RowLayout());
		
		filterOrderCheck = new Button(lowerComposite, SWT.CHECK);
		filterOrderCheck.setSelection(true);
		
		Label filterOrderLabel = new Label(lowerComposite, SWT.NONE);
		filterOrderLabel.setText(FILTER_ORDER_LABEL);
		filterOrderLabel.setToolTipText(FILTER_ORDER_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		filterOrderText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		filterOrderText.setLayoutData(gridData);
		filterOrderText.setText("");
		
		enableTextBasedOnCheck(filterOrderCheck, filterOrderText);
		
		new Label(composite, SWT.NONE);
	}
	
	public String getFilterGroup()
	{
		return filterGroupCheck.getSelection()? filterGroupText.getText() : null;
	}
	
	public String getFilterOrder()
	{
		return filterOrderCheck.getSelection()? filterOrderText.getText() : null;
	}
	
	@Override
	public boolean canFlipToNextPage()
	{
		return false;
	}
	
	private Button filterGroupCheck;
	private Button filterOrderCheck;

	private Text filterGroupText;
	private Text filterOrderText;
	
	//Label Name
	private static final String FILTER_GROUP_LABEL = "Filter group:";
	private static final String FILTER_ORDER_LABEL = "Filter order:";
	
	//Tooltip
	private static final String FILTER_GROUP_TOOLTIP = "An identifier for which group the filter belongs to. Specified for 'twconn:filterGroup'";
	private static final String FILTER_ORDER_TOOLTIP = "An integer used to sort the filters in the same group. Specified for 'twconn:filterOrder'";
}
