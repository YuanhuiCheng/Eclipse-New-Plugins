package ca.yuanhuicheng.tools.eclipse.plugin.ui.search;

import org.eclipse.core.resources.IFile;

import com.google.common.primitives.Ints;

public class UriReference implements Comparable<Object> 
{
	public UriReference(final IFile file, final int startOffset, final int length, final int lineNumber, 
			final String line, final String fullLine, final String fullUri)
	{
		this.file = file;
		this.startOffset = startOffset;
		this.length = length;
		this.lineNumber = lineNumber;
		this.line = line; //the selected string
		this.fullLine = fullLine; //the whole line that contains the matched result
		this.fullUri = fullUri;
	}
	
	public IFile getFile()
	{
		return file;
	}
	
	public String getFileName()
	{
		return file.getName();
	}
	
	public int getStartOffset() 
	{
		return startOffset;
	}
	
	public int getLength()
	{
		return length;
	}
	
	public int getLineNumber()
	{
		return lineNumber;
	}
	
	public String getLine() 
	{
		return line;
	}
	
	public String getFullLine()
	{
		return fullLine;
	}
	
	public String getFullUri()
	{
		return fullUri;
	}
	
	@Override
	public int compareTo(Object o)
	{
		if (o instanceof UriReference) 
		{
			UriReference other = (UriReference) o;
			int compareResult = this.getFileName().compareTo(other.getFileName());
            return compareResult == 0? Ints.compare(this.getStartOffset(), other.getStartOffset()) : compareResult;
        }

        throw new IllegalStateException();
	}

	private final IFile file;
	private final int startOffset;
	private final int length;
	private final int lineNumber;
	private final String line;
	private final String fullLine;	
	private final String fullUri;
}
