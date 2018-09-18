package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewTWContentConnectorCommandForTextParticipantWizardPage extends NewTWContentConnectorCommandParticipantWizardPage
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
		addNewLine(composite, 1);
		addCheckAndTextGroupOptions(composite);
		
		setControl(composite);
		setPageComplete(true);
	}
	
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
	public String getFirstRowIndex()
	{
		return firstRowIndexCheck.getSelection()? firstRowIndexText.getText() : null;
	}
	
	public String getLastRowIndex()
	{
		return lastRowIndexCheck.getSelection()? lastRowIndexText.getText() : null;
	}
	
	private Button firstRowIndexCheck;
	private Button lastRowIndexCheck;
	
	private Text firstRowIndexText;
	private Text lastRowIndexText;
}
