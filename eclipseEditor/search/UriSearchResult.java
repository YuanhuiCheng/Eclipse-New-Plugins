package ca.yuanhuicheng.tools.eclipse.plugin.ui.search;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public class UriSearchResult extends AbstractTextSearchResult
{

	public UriSearchResult(final UriSearchQuery query, final boolean ifSSearchedInWorkspace)
	{
		this.query = query;
		this.ifSSearchedInWorkspace = ifSSearchedInWorkspace;
	}

	@Override
	public String getLabel() 
	{
		String searchString = this.query.getSearchString();
		int matchCount = this.getMatchCount();
		
		String pattern = "''{0}'' - {1,choice,1#1 match|1<{1,number,integer} matches} in ";
		return ifSSearchedInWorkspace? MessageFormat.format(pattern + "workspace", searchString, matchCount) :
									 MessageFormat.format(pattern + "project", searchString, matchCount);
	}

	@Override
	public String getTooltip() 
	{
		return "found uris in the filesystem";
	}

	@Override
	public ImageDescriptor getImageDescriptor()
	{
		return null;
	}

	@Override
	public ISearchQuery getQuery()
	{
		return query;
	}

	@Override
	public IEditorMatchAdapter getEditorMatchAdapter()
	{
		return new TWEditorMatchAdapter();
	}

	@Override
	public IFileMatchAdapter getFileMatchAdapter()
	{
		return new TWFileMatchAdapter();
	}
	
	private static final class TWEditorMatchAdapter implements IEditorMatchAdapter
	{
		@Override
		public Match[] computeContainedMatches(final AbstractTextSearchResult result, final IEditorPart editor)
		{
			IEditorInput editorInput = editor.getEditorInput();
			if (editorInput instanceof IFileEditorInput)
			{
				IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
				IFile file = fileEditorInput.getFile();
				return result.getMatches(file);
			}
			
			return null;
		}
		
		@Override
		public boolean isShownInEditor(final Match match, final IEditorPart editor)
		{
			IFile file = (IFile) match.getElement();
			IEditorInput editorInput = editor.getEditorInput();
			if (editorInput instanceof IFileEditorInput)
			{
				IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
				return fileEditorInput.getFile().equals(file);
			}
			
			return false;
		}
	}
	
	private static final class TWFileMatchAdapter implements IFileMatchAdapter
	{
		@Override
		public Match[] computeContainedMatches(final AbstractTextSearchResult result, final IFile file)
		{
			return result.getMatches(file);
		}
		
		@Override
		public IFile getFile(final Object element) 
		{
			 return (IFile) element;
		}
	}

	private final UriSearchQuery query;
	private boolean ifSSearchedInWorkspace;
}
