package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public abstract class NewTWContentConnectorCommandParticipantWizardPage extends NewTWContentConnectorCommandWizardPage
{
	public void addIfDownloadFileOption(final Composite composite)
	{
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.LEFT;
		gridData.widthHint = 120;
		Label ifDownloadFileLabel = new Label(composite, SWT.NONE);
		ifDownloadFileLabel.setText(DOWNLOADFILE_LABEL);
		ifDownloadFileLabel.setLayoutData(gridData);
		ifDownloadFileLabel.setToolTipText(DOWNLOADFILE_TOOLTIP);
		
		Composite lowerComposite = new Composite(composite, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 35;
		lowerComposite.setLayout(rowLayout);
		
		trueOption = new Button(lowerComposite, SWT.RADIO);
		trueOption.setText(DOWNLOADFILE_TRUE);
		trueOption.setToolTipText(DOWNLOADFILE_TRUE_TOOLTIP);
		falseOption = new Button(lowerComposite, SWT.RADIO);
		falseOption.setText(DOWNLOADFILE_FALSE);
		falseOption.setToolTipText(DOWNLOADFILE_FALSE_TOOLTIP);
		falseOption.setSelection(true);
		
		new Label(composite, SWT.NONE);
	}	

	public void addDynamicFileId(final Composite composite)
	{
		Label dynamicFileIdLabel = new Label(composite, SWT.NONE);
		dynamicFileIdLabel.setText(DYNAMICFILE_ID_LABEL);
		dynamicFileIdLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		dynamicFileIdLabel.setToolTipText(DYNAMICFILE_ID_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		dynamicFileIdText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		dynamicFileIdText.setLayoutData(gridData);
		dynamicFileIdText.setText("");
		
		new Label(composite, SWT.NONE);
	}
	
	public void addDynamicFileMask(final Composite composite)
	{
		Label dynamicFileMaskLabel = new Label(composite, SWT.NONE);
		dynamicFileMaskLabel.setText(DYNAMICFILE_MASK_LABEL);
		dynamicFileMaskLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		dynamicFileMaskLabel.setToolTipText(DYNAMICFILE_MASK_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		dynamicFileMaskText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		dynamicFileMaskText.setLayoutData(gridData);
		dynamicFileMaskText.setText("");
		
		new Label(composite, SWT.NONE);
	}
	
	/**
	 * Getter
	 */
	public String getIfDownloadFileResult()
	{
		return falseOption.getSelection()?  DOWNLOADFILE_FALSE : DOWNLOADFILE_TRUE;
	}

	public String getDynamicFileId()
	{
		return dynamicFileIdText.getText();
	}
	
	public String getDynamicFileMask()
	{
		return dynamicFileMaskText.getText();
	}
	
	private Button trueOption;
	private Button falseOption;
	
	private Text dynamicFileIdText;
	private Text dynamicFileMaskText;
	
	//Downloadable option
	public static final String DOWNLOADFILE_TRUE = "true";
	public static final String DOWNLOADFILE_FALSE = "false";
	
	//Label Name
	public static final String DOWNLOADFILE_LABEL = "Download file:";
	public static final String DYNAMICFILE_ID_LABEL = "Dynamic file id:";
	public static final String DYNAMICFILE_MASK_LABEL = "Dynamic file mask:";
	
	public static final String DOWNLOADFILE_TOOLTIP = "Specificed for 'twconn:downloadFile'";
	public static final String DOWNLOADFILE_TRUE_TOOLTIP = "The CR is used to download a dynamically-created-file";
	public static final String DOWNLOADFILE_FALSE_TOOLTIP = "The CR is not used to download a dynamically-created-file";

	public static final String DYNAMICFILE_ID_TOOLTIP = "The JSON object ID that will contain the file to operate on";
	public static final String DYNAMICFILE_MASK_TOOLTIP = "A REGEX expression that the dynamic file must match";
}
