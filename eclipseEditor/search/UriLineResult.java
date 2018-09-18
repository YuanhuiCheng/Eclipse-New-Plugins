package ca.yuanhuicheng.tools.eclipse.plugin.ui.search;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

public class UriLineResult
{
	public UriLineResult(final Collection<UriReferenceMatch> matches)
	{
		this.matches = ImmutableList.copyOf(matches);
	}
	
	public ImmutableList<UriReferenceMatch> getMatches()
	{
        return this.matches;
    }

	private ImmutableList<UriReferenceMatch> matches;
}
