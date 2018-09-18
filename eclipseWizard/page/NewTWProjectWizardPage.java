package ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.page;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.wizard.NewTWProjectWizard;

/**
 * A {@link NewTWProjectWizardPage} is the one and only page of a New yuanhuicheng Project Wizard.
 */
public final class NewTWProjectWizardPage extends WizardPage
{
	/**
	 * Create the one and only New yuanhuicheng Project wizard page.
	 */
	public NewTWProjectWizardPage()
	{
		super(NewTWProjectWizard.TITLE);
		setTitle(NewTWProjectWizard.TITLE);
		setDescription("Create a new yuanhuicheng Eclipse Project.");
	}

	@Override
	public void createControl(final Composite parent)
	{
		final Composite container = new Composite(parent, SWT.NONE);

		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);

		final Label getProjNameLabel = new Label(container, SWT.NONE);
		getProjNameLabel.setText("New yuanhuicheng Project name");

		projectName = new Text(container, SWT.BORDER | SWT.SINGLE);
		projectName.setText("");
		projectName.addKeyListener(new KeyListener()
		{
			@Override
			public void keyPressed(final KeyEvent keyEvent)
			{
				// don't need to handle key pressed events, but interface implementation requires implementation
			}

			@Override
			public void keyReleased(final KeyEvent keyEvent)
			{
				if (!projectName.getText().isEmpty())
				{
					setPageComplete(true);
				}
			}
		});
		projectName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// required to avoid an error in the system
		setControl(container);
		setPageComplete(false);
	}

	/**
	 * Get the entered project name.
	 * @return entered project name
	 */
	public String getProjectName()
	{
		return projectName.getText();
	}

	private Text projectName;
}