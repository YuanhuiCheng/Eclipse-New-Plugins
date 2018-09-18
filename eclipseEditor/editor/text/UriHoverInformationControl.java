package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.text;

import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class UriHoverInformationControl extends AbstractInformationControl implements IInformationControlExtension2
{

	public UriHoverInformationControl(final Shell parentShell, final boolean isResizable)
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
		if (input instanceof ParticipantInfo)
		{
			uriText.setText(((ParticipantInfo)input).getFullUri());
			typeText.setText(((ParticipantInfo)input).getType());
			labelText.setText(((ParticipantInfo)input).getLabel());
			commentText.setText(((ParticipantInfo)input).getComment());
		}
	}

	@Override
	protected void createContent(final Composite parent)
	{
		composite = new Composite(parent, SWT.NONE);
		composite.setBackground(parent.getBackground());
		composite.setForeground(parent.getForeground());
		
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 5;
        composite.setLayout(layout);
        
        FontData fontData = parent.getFont().getFontData()[0];
        Font boldFont = new Font(Display.getCurrent(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
        
        uriText = new StyledText(composite, SWT.NONE);
        uriText.setBackground(parent.getBackground());
        uriText.setForeground(parent.getForeground());
        uriText.setWordWrap(true);
        uriText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        uriText.setFont(boldFont);
        
    	Label typeLabel = new Label(composite, SWT.NONE);
        typeLabel.setText(TYPE_LABEL);
        typeLabel.setFont(boldFont);
        new Label(composite, SWT.NONE);
        new Label(composite, SWT.NONE);
        typeText = new StyledText(composite, SWT.NONE);
        typeText.setBackground(parent.getBackground());
        typeText.setForeground(parent.getForeground());
        typeText.setWordWrap(true);
        typeText.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 1, 1));
        
        Label labelLabel = new Label(composite, SWT.NONE);
        labelLabel.setText(LABEL_LABEL);
        labelLabel.setFont(boldFont);
        new Label(composite, SWT.NONE);
        new Label(composite, SWT.NONE);
        labelText = new StyledText(composite, SWT.SINGLE);
        labelText.setBackground(parent.getBackground());
        labelText.setForeground(parent.getForeground());
        labelText.setWordWrap(true);
        labelText.setLayoutData(new GridData(SWT.FILL, SWT.FILL , true, false, 1, 1));
        
        Label commentLabel = new Label(composite, SWT.NONE);
        commentLabel.setText(COMMENT_LABEL);
        commentLabel.setFont(boldFont);
        new Label(composite, SWT.NONE);
        new Label(composite, SWT.NONE);
        commentText = new StyledText(composite, SWT.MULTI);
        commentText.setBackground(parent.getBackground());
        commentText.setForeground(parent.getForeground());
        commentText.setWordWrap(true);
        commentText.setLayoutData(new GridData(SWT.FILL, SWT.FILL , true, false, 1, 1));
	}
	
	@Override
    public void setBackgroundColor(Color background) 
	{
        super.setBackgroundColor(background);

        uriText.setBackground(background);
        typeText.setBackground(background);
        labelText.setBackground(background);
        commentText.setBackground(background);
    }

    @Override
    public void setForegroundColor(Color foreground)
    {
        super.setForegroundColor(foreground);

        uriText.setForeground(foreground);
        typeText.setForeground(foreground);
        labelText.setForeground(foreground);
        commentText.setForeground(foreground);
    }
	
	@Override
    public IInformationControlCreator getInformationPresenterControlCreator() 
	{
        return new IInformationControlCreator() 
        {
            @Override
            public IInformationControl createInformationControl(Shell parent)
            {
                return new UriHoverInformationControl(parent, false);
            }
        };
    }

	private Composite composite;
	private StyledText uriText;
	private StyledText typeText;
	private StyledText labelText;
	private StyledText commentText;
	
	private static final String TYPE_LABEL = "Type:";
	private static final String LABEL_LABEL = "Label:";
	private static final String COMMENT_LABEL = "Comment:";
}
