package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public abstract class NewTWContentConnectorCommandWizardPage extends NewTWConnectorCRWizardPage
{
	public NewTWContentConnectorCommandWizardPage()
	{
		super("NewContentConnectorParticipantWizardPage");
		setPageComplete(false);
		setTitle(WIZARDPAGE_TITLE);
        setDescription(GENERAL_MESSAGE);
	}
	
	public void addPath(final Composite composite)
	{
		Label pathLabel = new Label(composite, SWT.NONE);
		pathLabel.setText(PATH_LABEL);
		pathLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		pathLabel.setToolTipText(PATH_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		pathText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		pathText.setLayoutData(gridData);
		pathText.setText("");
		
		addEmptyLabel(composite, 45);
	}
	
	public String getIfDownloadableResult()
	{
		return null;
	}
	
	public String getCommandType()
	{
		return null;
	}
	
	public String getContentType()
	{
		return null;
	}
	
	public String getIfDownloadFileResult()
	{
		return null;
	}
	
	public String getPath()
	{
		return pathText != null? pathText.getText() : null;
	}
	
	public String getDynamicFileId()
	{
		return null;
	}
	
	public String getDynamicFileMask()
	{
		return null;
	}
	
//	public String getIfShareEntireFileResult()
//	{
//		return null;
//	}
	
	public String getFirstRowIndex()
	{
		return null;
	}
	
	public String getLastRowIndex()
	{
		return null;
	}
	
	public String getFirstSheetIndex()
	{
		return null;
	}
	
	public String getLastSheetIndex()
	{
		return null;
	}
	
	private Text pathText;
	
	//Label Name
	public static final String PATH_LABEL = "File path:";
	public static final String FIRST_ROW_INDEX_LABEL = "firstRowIndex:";
	public static final String LAST_ROW_INDEX_LABEL = "lastRowIndex:";
	public static final String FIRST_SHEET_INDEX_LABEL = "firstSheetIndex:";
	public static final String LAST_SHEET_INDEX_LABEL = "lastSheetIndex:";
	
	//Tooltip
	public static final String PATH_TOOLTIP = "The path to a file (to be read) or a directory (from which files are to be listed or retrieved). Specificed for 'twconn:path'";
	public static final String FIRST_ROW_INDEX_TOOLTIP = "Specificed for 'twconn:firstRowIndex'";
	public static final String LAST_ROW_INDEX_TOOLTIP = "Specificed for 'twconn:lastRowIndex'";
	public static final String FIRST_SHEET_INDEX_TOOLTIP = "Specificed for 'twconn:firstSheetIndex'";
	public static final String LAST_SHEET_INDEX_TOOLTIP = "Specificed for 'twconn:lastSheetIndex'";
	
	public static final String WIZARDPAGE_TITLE = "New Content Connector Participant CR";
	public static final String GENERAL_MESSAGE = "Create a CR for new Content Connector Participant";
}
