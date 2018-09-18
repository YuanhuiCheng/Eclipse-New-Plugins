package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewTWLdapConnectorParticipantWizardPage extends NewTWConnectorCRWizardPage
{
	public NewTWLdapConnectorParticipantWizardPage()
	{
		super("NewLdapConnectorParticipantWizardPage");
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
		
		addLdapUrl(composite);
		addUsername(composite);
		addPassword(composite);
		//addSearchFilterTemplate(composite);
		
		setControl(composite);
		setPageComplete(true);
	}
	
	private void addLdapUrl(final Composite composite)
	{
		Label ldapUrlLabel = new Label(composite, SWT.NONE);
		ldapUrlLabel.setText(LDAP_URL_LABEL);
		ldapUrlLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		ldapUrlLabel.setToolTipText(LDAP_URL_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		ldapUrlText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		ldapUrlText.setLayoutData(gridData);
		ldapUrlText.setText("");
		
		addEmptyLabel(composite, 45);
	}
	
	private void addUsername(final Composite composite)
	{
		Label usernameLabel = new Label(composite, SWT.NONE);
		usernameLabel.setText(USERNAME_LABEL);
		usernameLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		usernameLabel.setToolTipText(USERNAME_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		usernameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		usernameText.setLayoutData(gridData);
		usernameText.setText("");
		
		new Label(composite, SWT.NONE);
	}
	
	private void addPassword(final Composite composite)
	{
		Label passwordLabel = new Label(composite, SWT.NONE);
		passwordLabel.setText(PASSWORD_LABEL);
		passwordLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
		passwordLabel.setToolTipText(PASSWORD_TOOLTIP);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		passwordText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		passwordText.setLayoutData(gridData);
		passwordText.setText("");
		
		new Label(composite, SWT.NONE);
	}
	
//	private void addSearchFilterTemplate(final Composite composite)
//	{
//		Label searchFilterTemplateLabel = new Label(composite, SWT.NONE);
//		searchFilterTemplateLabel.setText(SEARCH_FILTER_TEMPLATE_LABEL);
//		searchFilterTemplateLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false));
//		searchFilterTemplateLabel.setToolTipText(SEARCH_FILTER_TEMPLATE_TOOLTIP);
//		
//		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
//		searchFilterTemplateText = new Text(composite, SWT.SINGLE | SWT.BORDER);
//		searchFilterTemplateText.setLayoutData(gridData);
//		searchFilterTemplateText.setText("");
//		
//		new Label(composite, SWT.NONE);
//	}
	
	/**
	 * Getter
	 */
	public String getLdapUrl()
	{
		return ldapUrlText.getText();
	}
	
	public String getUsername()
	{
		return usernameText.getText();
	}
	
	public String getPassword()
	{
		return passwordText.getText();
	}
	
//	public String getSearchFilterTemplate()
//	{
//		return searchFilterTemplateText.getText();
//	}

	private Text ldapUrlText;
	private Text usernameText;
	private Text passwordText;
	private Text searchFilterTemplateText;

	private static final String WIZARDPAGE_TITLE = "New Ldap Connector Participant";
	private static final String GENERAL_MESSAGE = "Create a new Ldap Connector Participant";
	
	//Label Name
	private static final String LDAP_URL_LABEL = "Ldap url:";
	private static final String USERNAME_LABEL = "Username:";
	private static final String PASSWORD_LABEL = "Password:";
	//private static final String SEARCH_FILTER_TEMPLATE_LABEL = "Search Filter Template:";
	
	//Tooltip
	private static final String LDAP_URL_TOOLTIP = "Full URL for the ldap server, including schema, host, and port. Specified for 'twconn:ldapUrl'";
	private static final String USERNAME_TOOLTIP = "Username for authentication. Specified for 'twconn:username'";
	private static final String PASSWORD_TOOLTIP = "Password for authentication. Specified for 'twconn:password'";
	//private static final String SEARCH_FILTER_TEMPLATE_TOOLTIP = "Filter for the search. Specified for 'twconn:searchFilterTemplate'";
}
