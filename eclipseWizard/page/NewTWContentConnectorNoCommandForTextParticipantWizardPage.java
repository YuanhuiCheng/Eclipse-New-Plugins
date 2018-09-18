package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewTWContentConnectorNoCommandForTextParticipantWizardPage extends NewTWContentConnectorCommandWizardPage
{
	@Override
	public void createControl(final Composite parent) 
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(LAYOUT_COL_NUM, false);
		composite.setLayout(layout);

		//addCheckIfShareEntireFile(composite);
		addPath(composite);
		addNewLine(composite, 1);
		addCheckAndTextGroupOptions(composite);

		setControl(composite);
		setPageComplete(true);
	}	
	
//	private void addCheckIfShareEntireFile(final Composite composite)
//	{
//		GridData gridData = new GridData();
//		gridData.verticalAlignment = SWT.LEFT;
//		gridData.widthHint = 110;
//		Label ifShareEntireFileLabel = new Label(composite, SWT.NONE);
//		ifShareEntireFileLabel.setText(IF_SHARE_ENTIRE_FILE_LABEL);
//		ifShareEntireFileLabel.setLayoutData(gridData);
//		ifShareEntireFileLabel.setToolTipText(IF_SHARE_ENTIRE_FILE_TOOLTIP);
//		
//		Composite lowerComposite = new Composite(composite, SWT.NONE);
//		RowLayout rowLayout = new RowLayout();
//		rowLayout.spacing = 30;
//		lowerComposite.setLayout(rowLayout);
//
//		trueOption = new Button(lowerComposite, SWT.RADIO);
//		trueOption.setText(IF_SHARE_ENTIRE_FILE_TRUE);
//		trueOption.setToolTipText(IF_SHARE_ENTIRE_FILE_TRUE_TOOLTIP);
//		trueOption.setSelection(true);
//		falseOption = new Button(lowerComposite, SWT.RADIO);
//		falseOption.setText(IF_SHARE_ENTIRE_FILE_FALSE);
//		falseOption.setToolTipText(IF_SHARE_ENTIRE_FILE_FALSE_TOOLTIP);
//		
//		new Label(composite, SWT.NONE);
//	}
	
	public void addCheckAndTextGroupOptions(final Composite composite)
	{
		Group group = new Group(composite, SWT.SHADOW_IN);
		GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.horizontalSpan = 6;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		group.setLayout(gridLayout);
		group.setLayoutData(gridData);
		
		firstRowIndexCheck = new Button(group, SWT.CHECK);
		firstRowIndexCheck.setSelection(true);
		Label firstRowIndexLabel = new Label(group, SWT.NONE);
		firstRowIndexLabel.setText(FIRST_ROW_INDEX_LABEL);
		firstRowIndexLabel.setToolTipText(FIRST_ROW_INDEX_TOOLTIP);
		GridData gridData2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		firstRowIndexText = new Text(group, SWT.SINGLE | SWT.BORDER);
		firstRowIndexText.setLayoutData(gridData2);
		firstRowIndexText.setText("");
		enableTextBasedOnCheck(firstRowIndexCheck, firstRowIndexText);
		
		lastRowIndexCheck = new Button(group, SWT.CHECK);
		Label lastRowIndexLabel = new Label(group, SWT.NONE);
		lastRowIndexLabel.setText(LAST_ROW_INDEX_LABEL);
		lastRowIndexLabel.setToolTipText(LAST_ROW_INDEX_TOOLTIP);
		lastRowIndexText = new Text(group, SWT.SINGLE | SWT.BORDER);
		lastRowIndexText.setLayoutData(gridData2);
		lastRowIndexText.setText("");
		lastRowIndexText.setEnabled(false);
		enableTextBasedOnCheck(lastRowIndexCheck, lastRowIndexText);
	}
	
	/**
	 * Getter
	 */
//	public String getIfShareEntireFileResult()
//	{
//		return trueOption.getSelection()?  IF_SHARE_ENTIRE_FILE_TRUE : IF_SHARE_ENTIRE_FILE_FALSE;
//	}
	
	public String getFirstRowIndex()
	{
		return firstRowIndexCheck.getSelection()? firstRowIndexText.getText() : null;
	}
	
	public String getLastRowIndex()
	{
		return lastRowIndexCheck.getSelection()? lastRowIndexText.getText() : null;
	}

//	private Button trueOption;
//	private Button falseOption;
	private Button firstRowIndexCheck;
	private Button lastRowIndexCheck;
	
	private Text firstRowIndexText;
	private Text lastRowIndexText;
	
//	//Downloadable option
//	private static final String IF_SHARE_ENTIRE_FILE_TRUE = "true";
//	private static final String IF_SHARE_ENTIRE_FILE_FALSE = "false";
//	
//	//Label Name
//	private static final String IF_SHARE_ENTIRE_FILE_LABEL = "Share Entire File:";
//	
//	//Tooltip
//	private static final String IF_SHARE_ENTIRE_FILE_TOOLTIP = "Specificed for 'twconn:shareEntireFile'";
//	private static final String IF_SHARE_ENTIRE_FILE_TRUE_TOOLTIP = "The entire contents of the file are to be shared as a whole";
//	private static final String IF_SHARE_ENTIRE_FILE_FALSE_TOOLTIP = "The entire contents of the file are not to be shared as a whole";
}
