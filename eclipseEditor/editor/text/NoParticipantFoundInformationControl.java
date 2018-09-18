package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.text;

import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class NoParticipantFoundInformationControl extends AbstractInformationControl implements IInformationControlExtension2
{

	public NoParticipantFoundInformationControl(final Shell parentShell, final boolean isResizable) 
	{
		super(parentShell, isResizable);
		this.create();
	}

	@Override
	public boolean hasContents()
	{
		return true;
	}

	@Override
	public void setInput(final Object input)
	{
		if (input instanceof String)
		{
			noPtFoundText.setText((String)input);
		}
	}

	@Override
	protected void createContent(final Composite parent)
	{
		composite = new Composite(parent, SWT.NONE);
		composite.setBackground(parent.getBackground());
		composite.setForeground(parent.getForeground());
		
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);
		
		noPtFoundText = new StyledText(composite, SWT.NONE);
		noPtFoundText.setBackground(parent.getBackground());
		noPtFoundText.setForeground(parent.getForeground());
	}
	
	@Override
    public void setBackgroundColor(Color background) 
	{
        super.setBackgroundColor(background);

        noPtFoundText.setBackground(background);
    }

    @Override
    public void setForegroundColor(Color foreground)
    {
        super.setForegroundColor(foreground);

        noPtFoundText.setForeground(foreground);
    }
    
    @Override
    public IInformationControlCreator getInformationPresenterControlCreator() 
	{
        return new IInformationControlCreator() 
        {
            @Override
            public IInformationControl createInformationControl(Shell parent)
            {
                return new NoParticipantFoundInformationControl(parent, true);
            }
        };
    }

	private Composite composite;
	private StyledText noPtFoundText;
}
