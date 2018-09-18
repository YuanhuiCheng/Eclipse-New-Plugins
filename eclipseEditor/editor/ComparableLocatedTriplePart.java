package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor;

import org.apache.jena.ext.com.google.common.primitives.Ints;

import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTriplePart;

public class ComparableLocatedTriplePart implements Comparable<Object>
{
	public ComparableLocatedTriplePart(LocatedTriplePart part)
	{
		this.part = part;
	}
	
	public LocatedTriplePart getLocatedTriplePart()
	{
		return part;
	}
	
	public int getStartOffset()
	{
		return part.getSourceLocationRange().getStartOffset();
	}
	
	public int getEndOffset()
	{
		return part.getSourceLocationRange().getEndOffset();
	}
	
	public int getLength()
	{
		return part.getSourceLocationRange().getEndOffset() - part.getSourceLocationRange().getStartOffset() + 1;
	}
	
	@Override
	public int compareTo(Object o)
	{
		if (o instanceof ComparableLocatedTriplePart)
		{
			 ComparableLocatedTriplePart other = (ComparableLocatedTriplePart) o;
			 return Ints.compare(this.getStartOffset(), other.getStartOffset());
		}
		return 0;
	}
	
	private final LocatedTriplePart part;
}
