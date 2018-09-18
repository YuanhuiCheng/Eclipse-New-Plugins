package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewTWContentConnectorCommandForExcelParticipantWizardPage extends NewTWContentConnectorCommandParticipantWizardPage
{
	@Override
	public void createControl(final Composite parent) 
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(LAYOUT_COL_NUM, false);
		composite.setLayout(layout);
		
		addIfDownloadFileOption(composite);
		addPath(composite);
		addDynamicFileId(composite);
		addDynamicFileMask(composite);
		addSeperator(composite, LAYOUT_COL_NUM);
		addNewLine(composite, 1);
		addCheckAndTextGroupOptions(composite);
		
		setControl(composite);
		setPageComplete(true);
	}
	
	public void addCheckAndTextGroupOptions(final Composite composite)
	{
		Group group = new Group(composite, SWT.NONE);
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
		
		firstSheetIndexCheck = new Button(group, SWT.CHECK);
		Label firstSheetIndexLabel = new Label(group, SWT.NONE);
		firstSheetIndexLabel.setText(FIRST_SHEET_INDEX_LABEL);
		firstSheetIndexLabel.setToolTipText(FIRST_SHEET_INDEX_TOOLTIP);
		firstSheetIndexText = new Text(group, SWT.SINGLE | SWT.BORDER);
		firstSheetIndexText.setLayoutData(gridData2);
		firstSheetIndexText.setText("");
		firstSheetIndexText.setEnabled(false);
		enableTextBasedOnCheck(firstSheetIndexCheck, firstSheetIndexText);
		
		lastSheetIndexCheck = new Button(group, SWT.CHECK);
		Label lastSheetIndexLabel = new Label(group, SWT.NONE);
		lastSheetIndexLabel.setText(LAST_SHEET_INDEX_LABEL);
		lastSheetIndexLabel.setToolTipText(LAST_SHEET_INDEX_TOOLTIP);
		lastSheetIndexText = new Text(group, SWT.SINGLE | SWT.BORDER);
		lastSheetIndexText.setLayoutData(gridData2);
		lastSheetIndexText.setText("");
		lastSheetIndexText.setEnabled(false);
		enableTextBasedOnCheck(lastSheetIndexCheck, lastSheetIndexText);
	}
	
	public String getFirstRowIndex()
	{
		return firstRowIndexCheck.getSelection()? firstRowIndexText.getText() : null;
	}
	
	public String getLastRowIndex()
	{
		return lastRowIndexCheck.getSelection()? lastRowIndexText.getText() : null;
	}
	
	public String getFirstSheetIndex()
	{
		return firstSheetIndexCheck.getSelection()? firstSheetIndexText.getText() : null;
	}
	
	public String getLastSheetIndex()
	{
		return lastSheetIndexCheck.getSelection()? lastSheetIndexText.getText() : null;
	}
	
	private Button firstRowIndexCheck;
	private Button lastRowIndexCheck;
	private Button firstSheetIndexCheck;
	private Button lastSheetIndexCheck;
	
	private Text firstRowIndexText;
	private Text lastRowIndexText;
	private Text firstSheetIndexText;
	private Text lastSheetIndexText;
}
