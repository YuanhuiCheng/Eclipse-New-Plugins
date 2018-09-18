package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ca.yuanhuicheng.definition.NamespaceConstants;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.NewTWParticipantWizard;


public class NewTWParticipantWizardPage extends NewTWMainWizardPage
{

	public NewTWParticipantWizardPage(final IFolder participantFolder)
	{
		super("NewParticipantWizardPage", participantFolder);
        setTitle(WIZARDPAGE_TITLE);
        setDescription(GENERAL_MESSAGE);
	}
	
	@Override
	public void createControl(final Composite parent) 
	{    
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(LAYOUT_COL_NUM, false);
		composite.setLayout(layout);
		
		addFolderGroup(composite);
		addSeperator(composite, LAYOUT_COL_NUM);
		addBaseUrl(composite);
		addParticipantName(composite);
		addParticipantType(composite);
		addInterCollabParticipantType(composite);
		addCheckIfCreateReceiverOrSender(composite);
		addNewLine(composite,1);
		addParticipantLabel(composite);
		addParticipantDescription(composite);
		addNewLine(composite,1);
		addGeneratePrefixButton(composite);
		addDefaultPrefixes(NamespaceConstants.RDF_NAMESPACE, NamespaceConstants.RDFS_NAMESPACE, NamespaceConstants.DIRECTORY_NAMESPACE,
				NamespaceConstants.TWFORM_NAMESPACE, NamespaceConstants.TWELEMENT_NAMESPACE);
		
		setControl(composite);
		setPageComplete(false);
	}

	@Override
	public void addBaseUrl(final Composite composite)
	{
		Label urlLabel = new Label(composite, SWT.NONE);
		urlLabel.setText(BASEURI_LABEL);
		urlLabel.setToolTipText(BASE_URI_TOOLTIP);

		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		urlText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		urlText.setLayoutData(gridData);
		urlText.setText("");
		
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
				//checkIfBaseUrlIsValid(urlText.getText(), GENERAL_MESSAGE);
				checkYesAndNoOptionToEnableFinishOrNextButton();
			}
		});
	}
	
	@Override
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
				//checkIfParticipantNameIsValid(participantNameText.getText(), getFolderPath(), GENERAL_MESSAGE);
				checkYesAndNoOptionToEnableFinishOrNextButton();
			}
		});
	}
	
	public void addParticipantType(Composite composite)
	{
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.LEFT;
		Label participantTypeLabel = new Label(composite, SWT.NONE);
		participantTypeLabel.setText(PARTICIPANT_TYPE_LABEL);
		participantTypeLabel.setLayoutData(gridData);
		
		Composite lowerComposite = new Composite(composite, SWT.NONE);
		lowerComposite.setLayout(new RowLayout());

		semanticFormOption = new Button(lowerComposite, SWT.RADIO);
		semanticFormOption.setText(PARTICIPANT_TYPE_SEMANTICFORM);
		semanticFormOption.setToolTipText(PARTICIPANT_TYPE_SEMANTICFORM_TOOLTIP);
		semanticFormOption.setSelection(true);
		interCollaborationOption = new Button(lowerComposite, SWT.RADIO);
		interCollaborationOption.setText(PARTICIPANT_TYPE_INTERCOLLABORATION);
		interCollaborationOption.setToolTipText(PARTICIPANT_TYPE_INTERCOLLABORATION_TOOLTIP);
		controllerOption = new Button(lowerComposite, SWT.RADIO);
		controllerOption.setText(PARTICIPANT_TYPE_CONTROLLER);
		controllerOption.setToolTipText(PARTICIPANT_TYPE_CONTROLLER_TOOLTIP);
		
		new Label(composite, SWT.NONE);
		
		interCollaborationOption.addSelectionListener(new SelectionAdapter()
		{
			@Override
        	public void widgetSelected(SelectionEvent e)
        	{
				receiverOption.setEnabled(true);
				receiverOption.setSelection(true);
				senderOption.setEnabled(true);
				yesOption.setEnabled(true);
				noOption.setEnabled(true);
				removePrefixes(NamespaceConstants.TWFORM_NAMESPACE, NamespaceConstants.TWELEMENT_NAMESPACE);
	    	}
		});
		
		semanticFormOption.addSelectionListener(new SelectionAdapter()
		{
			@Override
        	public void widgetSelected(SelectionEvent e)
        	{
				disableInterCollabOption();
				addDefaultPrefixes(NamespaceConstants.TWFORM_NAMESPACE, NamespaceConstants.TWELEMENT_NAMESPACE);
	    	}
		});
		
		controllerOption.addSelectionListener(new SelectionAdapter()
		{
			@Override
        	public void widgetSelected(SelectionEvent e)
        	{
				disableInterCollabOption();
				removePrefixes(NamespaceConstants.TWFORM_NAMESPACE, NamespaceConstants.TWELEMENT_NAMESPACE);
	    	}
		});	
	}
	
	private void addInterCollabParticipantType(Composite composite)
	{
		new Label(composite, SWT.NONE);
		
		Composite lowerComposite = new Composite(composite, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.marginTop = 0;
		rowLayout.spacing = 10;
		lowerComposite.setLayout(rowLayout);
		
		receiverOption = new Button(lowerComposite, SWT.RADIO);
		receiverOption.setText(INTERCOLLAB_PARTICIPANT_TYPE_RECEIVER);
		receiverOption.setEnabled(false);
		senderOption = new Button(lowerComposite, SWT.RADIO);
		senderOption.setText(INTERCOLLAB_PARTICIPANT_TYPE_SENDER);
		senderOption.setEnabled(false);
		
		new Label(composite, SWT.NONE);
	}
	
	private void addCheckIfCreateReceiverOrSender(Composite composite)
	{
		Composite lowerComposite = new Composite(composite, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.spacing = 10;
		GridData gridData = new GridData();
		gridData.horizontalSpan = LAYOUT_COL_NUM;
		lowerComposite.setLayout(rowLayout);
		lowerComposite.setLayoutData(gridData);
		
		Label checkLabel = new Label(lowerComposite, SWT.NONE);
		checkLabel.setText("Do you want to create the corresponding receiver/sender?");
		
		yesOption = new Button(lowerComposite, SWT.RADIO);
		yesOption.setText(YES_OPTION); 
		yesOption.setEnabled(false);
		noOption = new Button(lowerComposite, SWT.RADIO);
		noOption.setText(NO_OPTION);
		noOption.setEnabled(false);
		
		yesOption.addSelectionListener(new SelectionAdapter()
		{
			@Override
        	public void widgetSelected(SelectionEvent e)
        	{
				checkYesAndNoOptionToEnableFinishOrNextButton();
			}
		});
		
		
		noOption.addSelectionListener(new SelectionAdapter()
		{
			@Override
        	public void widgetSelected(SelectionEvent e)
        	{
				checkYesAndNoOptionToEnableFinishOrNextButton();
			}
		});
	}
	
	private void checkYesAndNoOptionToEnableFinishOrNextButton()
	{
		if (!checkIfBaseUrlIsValid(urlText.getText(), GENERAL_MESSAGE))
		{
			enableNext = false;
		}
		else if (!checkIfParticipantNameIsValid(participantNameText.getText(), getFolderPath(), GENERAL_MESSAGE))
		{
			enableNext = false;
		}
		else if (yesOption.getSelection())
		{
			enableNext = true;
		}
		else
		{
			enableNext = false;
		}
		
		checkStatus();
	}
	
	private void disableInterCollabOption()
	{
		receiverOption.setSelection(false);
		senderOption.setSelection(false);
		receiverOption.setEnabled(false);
		senderOption.setEnabled(false);
		yesOption.setEnabled(false);
		noOption.setEnabled(false);
		
		enableNext = false;
		checkStatus();
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
	public boolean canFlipToNextPage()
	{
		return enableNext;
	}
	
	@Override
	public IWizardPage getNextPage() 
	{  
	   NewTWInterCollabParticipantWizardPage interCollabPage = ((NewTWParticipantWizard)getWizard()).interCollabWizardPage;
	   
	   return interCollabPage;
	}
	
	@Override
	public boolean isPageComplete()
	{
		//if yes option is checked, only Next button would be enabled
		if (yesOption.getSelection())
		{
			return false;
		}
		
		if (!checkIfBaseUrlIsValid(urlText.getText(), GENERAL_MESSAGE))
		{
			return false;
		}
		else
		{
			return checkIfParticipantNameIsValid(participantNameText.getText(), getFolderPath(), GENERAL_MESSAGE);
		}
	}
	
	public boolean isSemanticFormChecked()
	{
		return semanticFormOption.getSelection()? true : false;
	}
	
	public boolean isInterCollabChecked()
	{
		return interCollaborationOption.getSelection()? true : false;
	}
	
	public boolean isControllerChecked()
	{
		return controllerOption.getSelection()? true : false;
	}
	
	public boolean isReceiverOptionChecked()
	{
		return receiverOption.getSelection()? true : false;
	}
	
	public boolean isSenderOptionChecked()
	{
		return senderOption.getSelection()? true : false;
	}
	
	public boolean isYesOptionChecked()
	{
		return yesOption.getSelection()? true : false;
	}

	public String getBaseUri()
	{
		return urlText.getText();
	}

	public String getParticipantHeader()
	{
		return participantNameText.getText();
	}
	
	private boolean enableNext;
	
	private Text urlText;
	private Text participantNameText;
	
	private Button semanticFormOption;
	private Button interCollaborationOption;
	private Button controllerOption;
	
	private Button receiverOption;
	private Button senderOption;
	private Button yesOption;
	private Button noOption;
	
	private static final String WIZARDPAGE_TITLE = "New Participant";
	private static final String GENERAL_MESSAGE = "Create a new Participant ttl File";

	//Label Name
	private static final String PARTICIPANT_TYPE_LABEL = "Participant type :";
	
	//Participant type 
	private static final String PARTICIPANT_TYPE_SEMANTICFORM = "Semantic Form";
	private static final String PARTICIPANT_TYPE_INTERCOLLABORATION = "Inter Collaboration";
	private static final String PARTICIPANT_TYPE_CONTROLLER = "Controller";
	
	//Tooltips
	private static final String PARTICIPANT_TYPE_SEMANTICFORM_TOOLTIP = "Semantic Form Participants are used to display context data in a rich flexible and interactive Form-based UI";
	private static final String PARTICIPANT_TYPE_INTERCOLLABORATION_TOOLTIP = "Inter-Collaboration Participants are used to publish and subscribe to messages across collaborations";
	private static final String PARTICIPANT_TYPE_CONTROLLER_TOOLTIP = "Controller Participants are used to drive the flow of data through a typical yuanhuicheng application";
	
	//InterCollaboration Participant type
	private static final String INTERCOLLAB_PARTICIPANT_TYPE_RECEIVER = "Receiver";
	private static final String INTERCOLLAB_PARTICIPANT_TYPE_SENDER = "Sender";
	
	private static final String YES_OPTION = "Yes";
	private static final String NO_OPTION = "No";
	
}
