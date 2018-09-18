package ca.yuanhuicheng.tools.eclipse.plugin.ui.search;

import org.eclipse.search.ui.text.Match;

import com.google.common.primitives.Ints;

public class UriReferenceMatch extends Match implements Comparable<Object>
{
	public UriReferenceMatch(final Object element, final int offset, final int length, final UriReference uriReference) 
	{
		super(element, offset, length);
		this.uriReference =  uriReference;
	}

	@Override
	public int compareTo(final Object o)
	{
		if (o instanceof Match) 
		{
			Match other = (Match) o;

            return Ints.compare(this.getOffset(), other.getOffset());
        }

        throw new IllegalStateException();
	}
	
	public UriReference getUriReference()
	{
		return uriReference;
	}

	private final UriReference uriReference;
}
