package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewTWDatabaseConnectorParticipantWizardPage extends NewTWConnectorCRWizardPage
{
	public NewTWDatabaseConnectorParticipantWizardPage()
	{
		super("NewDatabaseConnectorParticipantWizardPage");
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
		
		addDataSource(composite);
		addDataOperation(composite);
		
		setControl(composite);
		setPageComplete(true);
	}
	
	private void addDataSource(final Composite composite)
	{
		Label dataSourceLabel = new Label(composite, SWT.NONE);
		dataSourceLabel.setText(DATASOURCE_LABEL);
		dataSourceLabel.setToolTipText(DATASOURCE_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		dataSourceText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		dataSourceText.setLayoutData(gridData);
		dataSourceText.setText("");
		
		addEmptyLabel(composite, 45);
	}

	private void addDataOperation(final Composite composite)
	{
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.LEFT;
		Label sqlOperationLabel = new Label(composite, SWT.NONE);
		sqlOperationLabel.setToolTipText(DATAOPERATION_TOOLTIP);
		sqlOperationLabel.setText(DATAOPERATION_LABEL);
		sqlOperationLabel.setLayoutData(gridData);
		
		Composite lowerComposite = new Composite(composite, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 12;
		lowerComposite.setLayout(rowLayout);
		
		queryOption = new Button(lowerComposite, SWT.RADIO);
		queryOption.setText(QUERY_OPTION);
		queryOption.setToolTipText(QUERY_OPTION_TOOLTIP);
		queryOption.setSelection(true);
		updateOption = new Button(lowerComposite, SWT.RADIO);
		updateOption.setText(UPDATE_OPTION);
		updateOption.setToolTipText(UPDATE_OPTION_TOOLTIP);
		storedProcedureOption = new Button(lowerComposite, SWT.RADIO);
		storedProcedureOption.setText(STORED_PROCEDURE_OPTION);
		storedProcedureOption.setToolTipText(STORED_PROCEDURE_OPTION_TOOLTIP);
		
		new Label(composite, SWT.NONE); 
	}
	
	/**
	 * Getter
	 */
	public String getDataSource()
	{
		return dataSourceText.getText();
	}
	
	public String getSQLOperationType()
	{
		if (queryOption.getSelection())
		{
			return QUERY_STR;
		}
		else if (updateOption.getSelection())
		{
			return UPDATE_STR;
		}
		else 
		{
			return STORED_PROCEDURE_STR;
		}
	}
	
	private Text dataSourceText;
	
	private Button queryOption;
	private Button updateOption;
	private Button storedProcedureOption;

	private static final String WIZARDPAGE_TITLE = "New Database Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new Database Connector Participant";
	
	//Label Name
	private static final String DATASOURCE_LABEL = "Data source:";
	private static final String DATAOPERATION_LABEL = "SQL operation:";
	
	//SQL Operation Type
	private static final String QUERY_OPTION = "query";
	private static final String UPDATE_OPTION = "update";
	private static final String STORED_PROCEDURE_OPTION = "storedProcedure";
	
	//SQL Operation Type inserted to generated ttl file
	private static final String QUERY_STR = "query";
	private static final String UPDATE_STR = "update";
	private static final String STORED_PROCEDURE_STR = "storedProcedure";
	
	//Tooltip
	private static final String DATASOURCE_TOOLTIP = "The name of the data source. This must match the name of a resource in the web application";
	private static final String DATAOPERATION_TOOLTIP = "SQL Operation, could be 'twconn:query', 'twconn:update', and 'twconn:storedProcedure'";
	private static final String QUERY_OPTION_TOOLTIP = "A parameterized SQL SELECT query";
	private static final String UPDATE_OPTION_TOOLTIP = "A parameterized SQL UPDATE or INSERT statement";
	private static final String STORED_PROCEDURE_OPTION_TOOLTIP = "The name of the Stored Procedure defined on the  DB server";

}
