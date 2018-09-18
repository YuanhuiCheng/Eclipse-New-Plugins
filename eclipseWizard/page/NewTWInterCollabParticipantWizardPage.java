package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ca.yuanhuicheng.definition.NamespaceConstants;

public class NewTWInterCollabParticipantWizardPage extends NewTWMainWizardPage
{

	public NewTWInterCollabParticipantWizardPage() 
	{
		super("NewInterCollabParticipantWizardPage");
        setTitle(WIZARDPAGE_TITLE);
        setDescription(GENERAL_MESSAGE);
	}

	@Override
	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(LAYOUT_COL_NUM, false);
		composite.setLayout(layout);
		
		mainPage = (NewTWParticipantWizardPage) getWizard().getPreviousPage(this);
		
		addSeperator(composite, LAYOUT_COL_NUM);
		addBaseUrl(composite);
		addParticipantName(composite);
		addNewLine(composite,1);
		addParticipantLabel(composite);
		addParticipantDescription(composite);
		addNewLine(composite,1);
		addGeneratePrefixButton(composite);
		addDefaultPrefixes(NamespaceConstants.RDF_NAMESPACE, NamespaceConstants.RDFS_NAMESPACE, NamespaceConstants.DIRECTORY_NAMESPACE);
		
		setControl(composite);
		setPageComplete(false);
	}

	public void addBaseUrl(final Composite composite)
	{
		Label urlLabel = new Label(composite, SWT.NONE);
		urlLabel.setText(BASEURI_LABEL);

		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		urlText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		urlText.setLayoutData(gridData);
		
		addEmptyLabel(composite, 45);
		
		urlText.addKeyListener(new KeyListener() 
		{
			@Override
			public void keyPressed(org.eclipse.swt.events.KeyEvent e) 
			{
				//do nothing
			}

			@Override
			public void keyReleased(org.eclipse.swt.events.KeyEvent e)
			{
				checkIfBaseUrlIsValid(urlText.getText(), GENERAL_MESSAGE);
				checkToEnableFinishButton();
			}
		});
	}
	
	public void addParticipantName(final Composite composite)
	{
		Label partiNamelLabel = new Label(composite, SWT.NONE);
		partiNamelLabel.setText(PARTICIPANT_NAME_LABEL);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		participantNameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		participantNameText.setLayoutData(gridData);
		participantNameText.setText("");
		
		new Label(composite, SWT.NONE);
		
		participantNameText.addKeyListener(new KeyListener()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				//do nothing 
			}

			@Override
			public void keyReleased(KeyEvent e) 
			{
				checkIfParticipantNameIsValid(participantNameText.getText(), mainPage.getFolderPath(), GENERAL_MESSAGE);
				checkToEnableFinishButton();
			}
		});
	}
	
	@Override
	public boolean enableFinishOrNextButton()
	{
		if (getMessageType() == IMessageProvider.NONE)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public void setVisible(final boolean visible)
	{
	  super.setVisible(visible);

	  if (visible)
	  {
		  urlText.setText(mainPage.getBaseUri() + participantNameText.getText().replaceAll(String.valueOf(DASH), String.valueOf(SEPARATOR)));
	  }
	}
	
	@Override
	public boolean isPageComplete()
	{
		if (checkIfUrlAndParticipantNameExist(urlText.getText(), participantNameText.getText()))
		{
			return enableFinishOrNextButton();
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public IPath getFolderPath()
	{
		return mainPage.getFolderPath();
	}
	
	@Override
	public String getBaseUri() 
	{
		return urlText.getText();
	}
	
	@Override
	public String getParticipantHeader()
	{
		return participantNameText.getText();
	}
	
	private NewTWParticipantWizardPage mainPage;
	private Text urlText;
	private Text participantNameText;
	
	private static final String WIZARDPAGE_TITLE = "New Receiver/Sender";
	private static final String GENERAL_MESSAGE = "Create a new Receiver/Sender";
}
