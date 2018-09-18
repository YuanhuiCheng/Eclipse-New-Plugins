package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ca.yuanhuicheng.definition.NamespaceConstants;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.PrefixesConstants;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.PrefixesSelectionDialog;

public abstract class NewTWMainWizardPage extends NewTWWizardPage
{

	public NewTWMainWizardPage(final String pageName, final IFolder participantFolder)
	{
		super(pageName);
		this.participantFolder = participantFolder;
	}
	
	public NewTWMainWizardPage(final String pageName)
	{
		super(pageName);
	}
	
	public abstract boolean enableFinishOrNextButton();
	
	public abstract void addBaseUrl(final Composite composite);
	
	public abstract void addParticipantName(final Composite composite);
	
	public void addFolderGroup(Composite composite)
	{
		Label pathLabel = new Label(composite, SWT.NONE);
		pathLabel.setText(FOLDERGROUP_LABEL);
		
		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		Label folderPathLabel = new Label(composite, SWT.SINGLE | SWT.BORDER);
		folderPathLabel.setLayoutData(gridData);
		
		new Label(composite, SWT.NONE);
		
		folderPath = participantFolder.getFullPath();
		//get rid of the first slash       
		if (folderPath.toOSString().charAt(0) == IPath.SEPARATOR)
		{
			folderPathLabel.setText(folderPath.toOSString().substring(1));
		}
		else
		{
			folderPathLabel.setText(folderPath.toOSString());
		}	
	}
	
	public void addParticipantLabel(final Composite composite)
	{
		Label participantLabelLabel = new Label(composite, SWT.NONE);
		participantLabelLabel.setText(PARTICIPANT_LABEL_LABEL);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		partiLabelText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		partiLabelText.setLayoutData(gridData);
		partiLabelText.setText("");
		
		new Label(composite, SWT.NONE);
	}
	
	public void addParticipantDescription(final Composite composite)
	{
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.LEFT;
		Label partiDescriptionLabel = new Label(composite, SWT.NONE);
		partiDescriptionLabel.setLayoutData(gridData);
		partiDescriptionLabel.setText(PARTICIPANT_DESCRIPTION_LABEL);
			
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = DESCRIPTION_WIDTH;
		gridData.heightHint = DESCRIPTION_HEIGHT;
		participantDescriptionText = new Text(composite, SWT.MULTI | SWT.BORDER);
		participantDescriptionText.setLayoutData(gridData);
		participantDescriptionText.setText("");
		
		new Label(composite, SWT.NONE);
	}
	
	public void addGeneratePrefixButton(final Composite composite)
	{
		selectedPrefixesSet = new TreeSet<String>();
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gridData.horizontalSpan = 1;
        Button generatePrefixButton = new Button(composite, SWT.NONE);
        generatePrefixButton.setLayoutData(gridData);
        generatePrefixButton.setText("Generate Prefixes");
        
        new Label(composite, SWT.NONE);
        new Label(composite, SWT.NONE);
        
        generatePrefixButton.addSelectionListener(new SelectionAdapter()
	    {
	        	@Override
	        	public void widgetSelected(SelectionEvent e)
	        	{
	        		PrefixesSelectionDialog dialog = new PrefixesSelectionDialog(new Shell(), true);
		    		dialog.setInitialPattern("**");
		    		dialog.open();

		    		if(dialog.getListResult() != null)
		    		{
		    			selectedPrefixesSet.addAll(dialog.getListResult());
		    		}
		    	}
	      });
        
        generatePrefixButton.setSelection(true);
	}

	public void addDefaultPrefixes(final String...fullPrefix)
	{
		for(String str : fullPrefix)
		{
			String  value = PREFIX_MAP.get(str);
			selectedPrefixesSet.add(String.format("%-8s", value + ":") + "<" + str + ">");
		}
	}
	
	public void removePrefixes(final String...fullPrefix)
	{
		for (String str : fullPrefix)
		{
			String  value = PREFIX_MAP.get(str);
			selectedPrefixesSet.remove(String.format("%-8s", value + ":") + "<" + str + ">");
		}
	}
	
	/**
	 * Check if participant folder/participant file exists in the project system
	 * 
	 */
	public boolean checkIfParticipantFileAlreadyExist(final IPath folderPath, final String participantName)
	{
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(getParticipantPath(folderPath, participantName));
		if (file.exists())
		{
			return true;
		}
		
		return false;
	}
	
	public boolean checkIfBaseUrlIsValid(final String urlStr, final String generalMessage)
	{
		int urlStrLength = urlStr.length();
		int httpProtocolLength = HTTP_PROTOCOL_STR.length();
		
		if (urlStr.isEmpty())
		{
			setMessage(EMPTY_BASEURI_MSG, IMessageProvider.ERROR);
		}
		else if (!urlStr.startsWith(HTTP_PROTOCOL_STR))
		{
			setMessage(INVALID_PROTOCOL_MSG, IMessageProvider.ERROR);
		}
		else if (urlStrLength == httpProtocolLength)
		{
			setMessage(INCOMPLETE_BASEURI_MSG, IMessageProvider.ERROR);
		}
		else if (urlStr.split(String.valueOf(SEPARATOR)).length == 1)
		{
			setMessage(ERR_FORMAT_BASEURI_MSG, IMessageProvider.ERROR);
		}
		else if (!urlStr.endsWith(String.valueOf(SEPARATOR)))
		{
			setMessage(INVALID_BASEURL_END_MSG, IMessageProvider.ERROR);
		}
		else
		{
			for (int i = urlStrLength ; i >= httpProtocolLength + 1; i--)
			{
				if (urlStr.charAt(i-1) == SEPARATOR && urlStr.charAt(i-1) == urlStr.charAt(i-2))
				{
					setMessage(ERR_FORMAT_BASEURI_MSG, IMessageProvider.ERROR);
					break;
				}
				
				if (i == httpProtocolLength + 1)
				{
					setMessage(generalMessage, IMessageProvider.NONE);
				}
			}
		}
		
		return enableFinishOrNextButton();
	}
	
	public boolean checkIfParticipantNameIsValid(final String participantNameStr, final IPath folderPath, final String generalMessage)
	{
		int participantNameStrLength = participantNameStr.length();
		
		if (participantNameStr.isEmpty())
		{
			setMessage(EMPTY_PARTICIPANTNAME_MSG, IMessageProvider.ERROR);
		}
		else if (participantNameStr.startsWith(String.valueOf(SEPARATOR)))
		{
			setMessage(INVALID_PARTICIPANTNAME_START_MSG, IMessageProvider.ERROR);
		}
		else if (participantNameStr.endsWith(String.valueOf(SEPARATOR)))
		{
			setMessage(INVALID_PARTICIPANTNAME_END_MSG, IMessageProvider.ERROR);
		}
		else
		{
			if (checkIfParticipantFileAlreadyExist(folderPath, participantNameStr))
			{
				setMessage(PARTICIPANT_ALREADY_EXIST_MSG, IMessageProvider.ERROR);
			}
			else
			{
				for (int i = participantNameStrLength; i >= 0; i--)
				{
					if (i >= 3 && participantNameStr.charAt(i-1) == SEPARATOR && participantNameStr.charAt(i-1) == participantNameStr.charAt(i-2))
					{
						setMessage(ERR_FORMAT_PARTICIPANTNAME_MSG, IMessageProvider.ERROR);
						break;
					}
					
					if (i == 0)
					{
						setMessage(generalMessage, IMessageProvider.NONE);
					}
				}
				
			}
		}
		
		return enableFinishOrNextButton();
	}
	
	public boolean checkIfUrlAndParticipantNameExist(final String urlStr, final String participantNameStr)
	{
		return !urlStr.isEmpty() && !participantNameStr.isEmpty();
	}
	
	public void checkToEnableFinishButton()
	{
		if (enableFinishOrNextButton())
		{
			getWizard().getContainer().updateButtons();
		}
		else
		{
			setPageComplete(false);
		}
	}
	
	public void checkStatus() 
	{
		canFlipToNextPage();
		getWizard().getContainer().updateButtons();
	}
	
	/**
	 * Getter 
	 *
	 */
	public abstract String getBaseUri();
	
	public abstract String getParticipantHeader();	
	
	public String getParticipantLabel()
	{
		return partiLabelText.getText();
	}
	
	public String getParticipantComment()
	{
		return participantDescriptionText.getText();
	}
	
	public Set<String> getPrefixesSet()
	{
		return selectedPrefixesSet;
	}
	
	public IPath getFolderPath()
	{
		return folderPath;
	}
	
	public IPath getParticipantPath(final IPath folderPath, final String participantName)
	{
		return folderPath.append(IPath.SEPARATOR + participantName).addFileExtension(PARTICIPANT_FILE_EXTENSION);
	}

	private IPath folderPath; 
	private IFolder participantFolder;
	
	private Set<String> selectedPrefixesSet;
	
	private Text partiLabelText;
	private Text participantDescriptionText;

	//For gridlayout
	private static final int GENERAL_EQUAL_WIDTH = 350;
	public static final int DESCRIPTION_WIDTH = GENERAL_EQUAL_WIDTH;
	public static final int DESCRIPTION_HEIGHT = 80;
	public static final int COMBO_LENGTH = 270;
	
	public static final String HTTP_PROTOCOL_STR = "http://";
	public static final char SEPARATOR = '/';
	public static final char DASH = '-';
	
	//Error Message
	public static final String EMPTY_BASEURI_MSG = "Base url cannot be empty";
	public static final String INVALID_PROTOCOL_MSG = "The base url must start with "+ HTTP_PROTOCOL_STR;
	public static final String ERR_FORMAT_BASEURI_MSG = "The format of base url is invalid";
	public static final String EMPTY_PARTICIPANTNAME_MSG = "Participant Name cannot be empty";
	public static final String ERR_FORMAT_PARTICIPANTNAME_MSG = "The format of participant name is invalid";
	public static final String INVALID_BASEURL_END_MSG = "The base url must end with a separator";
	public static final String INVALID_PARTICIPANTNAME_START_MSG = "The participant name should not start with a separator";
	public static final String INVALID_PARTICIPANTNAME_END_MSG = "The participant name should not end with a separator";
	public static final String INCOMPLETE_BASEURI_MSG = "Please input a complete base url";
	public static final String CANNOT_HAVE_DASH_MSG = "The base url cannot include '-'";
	public static final String INVALID_CONCAT_SYMBOL = "Please use '-' to concate the participant name segments";
	public static final String NOTALIGNED_PARTICIPANTNAME_MSG = "The participant name is invalid, must be in conformity with the base url provided";
	public static final String PARTICIPANT_ALREADY_EXIST_MSG = "A participant with that name already exists";
	
	//Label Name
	public static final String FOLDERGROUP_LABEL = "Participant folder :";
	public static final String BASEURI_LABEL = "Base url :";
	public static final String PARTICIPANT_NAME_LABEL = "Participant name :";
	public static final String PARTICIPANT_LABEL_LABEL = "Participant label :";
	public static final String PARTICIPANT_DESCRIPTION_LABEL = "Participant description :";
	
	//Tooltips
	public static final String BASE_URI_TOOLTIP = "The base for all other URIs, specified for @base";

	private static final Map<String, String> PREFIX_MAP = new HashMap<String, String>();
	
	static
	{
		PREFIX_MAP.put(NamespaceConstants.RDF_NAMESPACE, "rdf");
		PREFIX_MAP.put(NamespaceConstants.RDFS_NAMESPACE, "rdfs");
		PREFIX_MAP.put(NamespaceConstants.TWCH_NAMESPACE, "twch");
		PREFIX_MAP.put(NamespaceConstants.TWCO_NAMESPACE, "twco");
		PREFIX_MAP.put(NamespaceConstants.TWCONN_NAMESPACE, "twconn");
		PREFIX_MAP.put(NamespaceConstants.DIRECTORY_NAMESPACE, "twd");
		PREFIX_MAP.put(NamespaceConstants.TWELEMENT_NAMESPACE, "twelem");
		PREFIX_MAP.put(NamespaceConstants.ERROR_NAMESPACE, "twerr");
		PREFIX_MAP.put(NamespaceConstants.TWFORM_NAMESPACE, "twform");
		PREFIX_MAP.put(NamespaceConstants.QUEUE, "twq");
		PREFIX_MAP.put(NamespaceConstants.QUEUE_NAMESPACE, "twqMeta");
		PREFIX_MAP.put(NamespaceConstants.TWSER_NAMESPACE, "twser");
		PREFIX_MAP.put(NamespaceConstants.VARIABLE_NAMESPACE, "twvar");
		PREFIX_MAP.put(PrefixesConstants.TWINPUT_NAMESPACE.val(), "twInput");
		PREFIX_MAP.put(PrefixesConstants.TWAUTH_NAMESPACE.val(), "twAuth");
	}
}
