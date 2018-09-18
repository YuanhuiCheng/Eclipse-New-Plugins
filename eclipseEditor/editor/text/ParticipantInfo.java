package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.text;

public class ParticipantInfo
{
	public ParticipantInfo(final String fullUri, final String type, final String label, final String comment)
	{
		this.fullUri = fullUri;
		this.type = type;
		this.label = label;
		this.comment = comment;
	}
	
	public String getFullUri()
	{
		return fullUri;
	}
	
	public String getType() 
	{
		return type;
	}
	
	public String getLabel() 
	{
		return label;
	}
	
	public String getComment()
	{
		return comment;
	}
	
	private final String fullUri;
	private final String type;
	private final String label;
	private final String comment;
}
