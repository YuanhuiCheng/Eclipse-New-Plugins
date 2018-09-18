package ca.yuanhuicheng.tools.eclipse.plugin.ui.search;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.ui.text.Match;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;

public class UriSearchResultContentProvider implements ITreeContentProvider
{

	public UriSearchResultContentProvider()
	{
		this.children = LinkedHashMultimap.create();
	}
	
	@Override
	public Object[] getElements(final Object inputElement)
	{
		return this.getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(final Object parentElement)
	{
		return this.children.get(parentElement).toArray();
	}
	
	@Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
        if (newInput instanceof UriSearchResult)
        {
            this.setSearchResult((UriSearchResult) newInput);
        }
    }

	@Override
	public Object getParent(final Object element) 
	{
        if (element instanceof IResource) 
        {
            if (!(element instanceof IProject))
            {
                IResource resource = (IResource) element;

                return resource.getParent();
            }
        }
        else if (element instanceof UriLineResult)
        {
        	return ((UriLineResult) element).getMatches().get(0).getElement();
        }
		return null; 
	}

	@Override
	public boolean hasChildren(final Object element) 
	{
		return !this.children.get(element).isEmpty();
	}
	
	private void setSearchResult(final UriSearchResult searchResult)
	{
		this.children.clear();
		
		for (Object element : searchResult.getElements())
		{
			SetMultimap<Integer, UriReferenceMatch> matchesByLineNumber = TreeMultimap.create();
			for (Match match : searchResult.getMatches(element))
			{
				UriReferenceMatch findReferenceMatch = (UriReferenceMatch)match;
				UriReference uriReference = findReferenceMatch.getUriReference();
				int lineNumber = uriReference.getLineNumber();
				matchesByLineNumber.put(lineNumber, findReferenceMatch);
			}
			
			for (Integer lineNumber : matchesByLineNumber.keySet())
			{
				Set<UriReferenceMatch> lineMatches = matchesByLineNumber.get(lineNumber);
				UriLineResult lineResult = new UriLineResult(lineMatches);
				this.add(searchResult, lineResult);
			}
		}
	}
	
	private void add(final UriSearchResult searchResult, Object child) 
	{
        Object parent = this.getParent(child);

        while (parent != null)
        {
            if (!this.children.put(parent, child))
            {
                return;
            }

            child = parent;
            parent = this.getParent(child);
        }

        this.children.put(searchResult, child);
    }
	

    @Override
    public void dispose() 
    {
    	//nothing to do
    }
	
	private final Multimap<Object, Object> children;
}