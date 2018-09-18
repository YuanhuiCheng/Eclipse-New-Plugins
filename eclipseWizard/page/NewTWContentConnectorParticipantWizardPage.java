package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.NewTWConnectorParticipantWizard;

public class NewTWContentConnectorParticipantWizardPage extends NewTWConnectorCRWizardPage
{
	public NewTWContentConnectorParticipantWizardPage()
	{
		super("NewContentConnectorParticipantWizardPage");
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
		
		addCheckIfRelatedToExcelOrText(composite);
		addSeperator(composite, LAYOUT_COL_NUM);
		addIfDownloadableOption(composite);
		addNewLine(composite, 1);
		addCommandOption(composite);
		
		setControl(composite);
		setPageComplete(true);
	}
	
	private void addCheckIfRelatedToExcelOrText(final Composite composite)
	{
		Composite lowerComposite = new Composite(composite, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 30;
		GridData gridData = new GridData();
		gridData.horizontalSpan = LAYOUT_COL_NUM;
		lowerComposite.setLayout(rowLayout);
		lowerComposite.setLayoutData(gridData);
		
		Label checkLabel = new Label(lowerComposite, SWT.NONE);
		checkLabel.setText(CHECK_CONNTENTTYPE_LABEL);
		
		textOption = new Button(lowerComposite, SWT.RADIO);
		textOption.setText(CONTENTTYPE_TEXT);
		textOption.setSelection(true);
		excelOption = new Button(lowerComposite, SWT.RADIO);
		excelOption.setText(CONTENTTYPE_EXCEL);
	} 
	
	private void addIfDownloadableOption(final Composite composite)
	{
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.LEFT;
		gridData.widthHint = 130;
		Label ifDownloadableLabel = new Label(composite, SWT.NONE);
		ifDownloadableLabel.setText(DOWNLOADABLE_LABEL);
		ifDownloadableLabel.setLayoutData(gridData);
		ifDownloadableLabel.setToolTipText(DOWNLOADABLE_TOOLTIP);
		
		Composite lowerComposite = new Composite(composite, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 30;
		lowerComposite.setLayout(rowLayout);
		
		trueOption = new Button(lowerComposite, SWT.RADIO);
		trueOption.setText(DOWNLOADABLE_TRUE);
		trueOption.setToolTipText(DOWNLOADABLE_TRUE_TOOLTIP );
		falseOption = new Button(lowerComposite, SWT.RADIO);
		falseOption.setText(DOWNLOADABLE_FALSE);
		falseOption.setToolTipText(DOWNLOADABLE_FALSE_TOOLTIP);
		falseOption.setSelection(true);
		
		new Label(composite, SWT.NONE);
	}	
	
	private void addCommandOption(final Composite composite)
	{
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.LEFT;
		gridData.horizontalSpan = LAYOUT_COL_NUM;
		Label commandOptionLabel = new Label(composite, SWT.NONE);
		commandOptionLabel.setText("Which command would like to perform?");
		commandOptionLabel.setLayoutData(gridData);

		new Label(composite, SWT.None);
		
		commandOption = new Button(composite, SWT.CHECK);
		commandOption.setText(COMMAND_OPTION_COMMAND);
		commandOption.setToolTipText(COMMAND_OPTION_COMMAND_TOOLTIP);
		
		Composite lowerComposite = new Composite(composite, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 15;
		lowerComposite.setLayout(rowLayout);
		getOption = new Button(lowerComposite, SWT.RADIO);
		getOption.setText(COMMANDTYPE_OPTION_GET);
		getOption.setEnabled(false);
		listOption = new Button(lowerComposite, SWT.RADIO);
		listOption.setText(COMMANDTYPE_OPTION_LIST);
		listOption.setEnabled(false);
		
		new Label(composite, SWT.None);
		
		metadataCommandOption = new Button(composite, SWT.CHECK);
		metadataCommandOption.setText(COMMAND_OPTION_METADATACOMMAND);
		metadataCommandOption.setToolTipText(COMMAND_OPTION_METADATACOMMAND_TOOLTIP);

		Composite lowerComposite2 = new Composite(composite, SWT.NONE);
		RowLayout rowLayout2 = new RowLayout();
		rowLayout2.spacing = 15;
		lowerComposite2.setLayout(rowLayout2);
		describeOption = new Button(lowerComposite2, SWT.RADIO);
		describeOption.setText(METADATA_COMMANDTYPE_OPTION_DESCRIBE);
		describeOption.setEnabled(false);
		buildOption = new Button(lowerComposite2, SWT.RADIO);;
		buildOption.setText(METADATA_COMMANDTYPE_OPTION_BUILD);
		buildOption.setEnabled(false);
		
		commandOption.addSelectionListener(new SelectionAdapter()
		{
			@Override
        	public void widgetSelected(SelectionEvent e)
        	{
				if (commandOption.getSelection())
				{
					metadataCommandOption.setSelection(false);
					getOption.setEnabled(true);
					getOption.setSelection(true);
					listOption.setEnabled(true);
					describeOption.setEnabled(false);
					describeOption.setSelection(false);
					buildOption.setEnabled(false);
					buildOption.setSelection(false);
				}
				else
				{
					getOption.setEnabled(false);
					listOption.setEnabled(false);
					getOption.setSelection(false);
					listOption.setSelection(false);
				}
	    	}
		});
		
		metadataCommandOption.addSelectionListener(new SelectionAdapter()
		{
			@Override
        	public void widgetSelected(SelectionEvent e)
        	{
				if (metadataCommandOption.getSelection())
				{
					commandOption.setSelection(false);
					getOption.setEnabled(false);
					getOption.setSelection(false);
					listOption.setEnabled(false);
					listOption.setSelection(false);
					describeOption.setEnabled(true);
					describeOption.setSelection(true);
					buildOption.setEnabled(true);
				}
				else
				{
					describeOption.setEnabled(false);
					buildOption.setEnabled(false);
					describeOption.setSelection(false);
					buildOption.setSelection(false);
				}
	    	}
		});
		
		new Label(composite, SWT.NONE);
	}
	
	/**
	 * Getter
	 */
	public String getIfDownloadableResult()
	{
		return falseOption.getSelection()? DOWNLOADABLE_FALSE : DOWNLOADABLE_TRUE;
	}
	
	public String getContentType()
	{
		return textOption.getSelection()? TEXT_MIME_CONTENTTYPE : EXCEL_MIME_CONTENT_TYPE; 
	}
	
	public String getCommandType()
	{
		if (getOption.getSelection())
		{
			return COMMANDTYPE_OPTION_GET;
		}
		else if  (listOption.getSelection())
		{
			return COMMANDTYPE_OPTION_LIST;
		}
		else if (describeOption.getSelection())
		{
			return METADATA_COMMANDTYPE_OPTION_DESCRIBE;
		}
		else if (buildOption.getSelection())
		{
			return METADATA_COMMANDTYPE_OPTION_BUILD;
		}
		else
		{
			return null;
		}
	}
	
	@Override
	public boolean canFlipToNextPage()
	{
		return true;
	}
	
	@Override
	public IWizardPage getNextPage() 
	{  
		if (commandOption.getSelection() || metadataCommandOption.getSelection())
		{
			if (textOption.getSelection())
			{
				NewTWContentConnectorCommandForTextParticipantWizardPage commandForTextPage = ((NewTWConnectorParticipantWizard) getWizard()).contentConnectorCommandForTextWizardPage;
				return commandForTextPage;
			}
			else if (excelOption.getSelection())
			{
				NewTWContentConnectorCommandForExcelParticipantWizardPage commandForExcelPage = ((NewTWConnectorParticipantWizard) getWizard()).contentConnectorCommandForExcelWizardPage;
				return commandForExcelPage;
			}
		}
		else if (textOption.getSelection())
		{
			NewTWContentConnectorNoCommandForTextParticipantWizardPage noCommandForTextPage = ((NewTWConnectorParticipantWizard) getWizard()).contentConnectorNoCommandForTextWizardPage;
			return noCommandForTextPage;
		} 
		else if (excelOption.getSelection())
		{
			NewTWContentConnectorNoCommandForExcelParticipantWizardPage noCommandForExcelPage = ((NewTWConnectorParticipantWizard) getWizard()).contentConnectorNoCommandForExcelWizardPage;
			return noCommandForExcelPage;
		}
		
		return null;
	}
	
	private Button trueOption;
	private Button falseOption;
	private Button textOption;
	private Button excelOption;
	private Button commandOption;
	private Button metadataCommandOption;
	private Button getOption;
	private Button listOption;
	private Button describeOption;
	private Button buildOption;

	private static final String WIZARDPAGE_TITLE = "New Content Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new Content Connector Participant";
	
	
	private static final String TEXT_MIME_CONTENTTYPE = "text/plain";
	private static final String EXCEL_MIME_CONTENT_TYPE = "application/vnd.ms-excel";
	
	//Downloadable option
	private static final String DOWNLOADABLE_TRUE = "true";
	private static final String DOWNLOADABLE_FALSE = "false";
	
	//Command option
	private static final String COMMAND_OPTION_COMMAND = "command";
	private static final String COMMAND_OPTION_METADATACOMMAND = "metadataCommand";
	
	//Command type option
	private static final String COMMANDTYPE_OPTION_GET = "get";
	private static final String COMMANDTYPE_OPTION_LIST = "list";
	private static final String METADATA_COMMANDTYPE_OPTION_DESCRIBE = "describe";
	private static final String METADATA_COMMANDTYPE_OPTION_BUILD = "build";
	
	//Label Name
	private static final String DOWNLOADABLE_LABEL = "Downloadable:";
	private static final String CHECK_CONNTENTTYPE_LABEL = "Type of the content being read or processed: ";
	
	private static final String CONTENTTYPE_TEXT = "Text";
	private static final String CONTENTTYPE_EXCEL = "Excel";
	
	//Tooltip
	private static final String  DOWNLOADABLE_TOOLTIP = "Specificed for 'twconn:downloadable'";
	private static final String DOWNLOADABLE_TRUE_TOOLTIP = "The connector-participant can download dynamically generated text files.";
	private static final String DOWNLOADABLE_FALSE_TOOLTIP = "The connector-participant cannot download dynamically generated text files.";
	private static final String COMMAND_OPTION_COMMAND_TOOLTIP = "Specified for 'twconn:command'";
	private static final String COMMAND_OPTION_METADATACOMMAND_TOOLTIP = "Specified for 'twconn:metadataCommand'";
}
