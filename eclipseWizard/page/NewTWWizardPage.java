package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public abstract class NewTWWizardPage extends WizardPage
{

	public NewTWWizardPage(final String pageName)
	{
		super(pageName);
	}
	
	public void checkStatus() 
	{
		canFlipToNextPage();
		getWizard().getContainer().updateButtons();
	}
	
	/**
	 * For grid layout
	 * 
	 */
	public void addEmptyLabel(final Composite composite, final int widthHint)
	{
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1 , 1);
		gridData.widthHint = widthHint;
		Label label = new Label(composite, SWT.NONE);
	    label.setLayoutData(gridData);
	}
	
	public void addNewLine(final Composite composite, int lines)
	{
		for (int i = 0; i < lines; i++)
		{
			Label newLine = new Label(composite, SWT.NONE);
			newLine.setLayoutData(new GridData (SWT.FILL, SWT.CENTER, true, false, LAYOUT_COL_NUM, 1));
		}
	}
	
	public void addSeperator(final Composite composite, final int horSpan)
	{
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = horSpan;
		Label seperator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		seperator.setLayoutData(data);
	}
	
	public void enableTextBasedOnCheck(final Button check, final Text text)
	{
		check.addSelectionListener(new SelectionAdapter()
		{
			@Override
        	public void widgetSelected(SelectionEvent e)
        	{
				if (check.getSelection())
				{
					text.setEnabled(true);
				}
				else
				{
					text.setEnabled(false);
				}
	    	}
		});
	}
	
	public String getTextBasedOnCheck(final Button check, final Text text)
	{
		if (check != null && check.getSelection())
		{
			return text.getText();
		}
		else
		{
			return null;
		}
	}
	
	//For gridlayout
	public static final int LAYOUT_COL_NUM = 3;
	
	//File extension
	public static final String PARTICIPANT_FILE_EXTENSION = "ttl";
}
