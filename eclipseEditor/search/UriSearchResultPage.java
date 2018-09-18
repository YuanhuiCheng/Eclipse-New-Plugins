package ca.yuanhuicheng.tools.eclipse.plugin.ui.search;


import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.ImmutableList;

import ca.yuanhuicheng.tools.eclipse.plugin.ui.eclipse.JFaceUIPrimitives;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.TwConfigEditor;
import ca.yuanhuicheng.tools.ide_plugin_core.MessagePriority;

public class UriSearchResultPage extends AbstractTextSearchViewPage 
{
	@Override
	protected void configureTreeViewer(final TreeViewer viewer)
	{
		viewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new UriSearchResultLabelProvider(this)));
		viewer.setContentProvider(new UriSearchResultContentProvider());
		viewer.setUseHashlookup(true);
		viewer.expandAll();
		
		selectionChangedToOpenAndReveal(viewer);
	}
	
	@Override
	protected void configureTableViewer(final TableViewer viewer)
	{
		
	}
	
	private void selectionChangedToOpenAndReveal(final TreeViewer viewer)
	{
		viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event) 
			{
				final Iterator<ISelection> selectionIterator = ((TreeSelection) event.getSelection()).iterator();
				while (selectionIterator.hasNext())
				{
					final Object selectedElement = selectionIterator.next();
					if (selectedElement instanceof UriLineResult)
					{
						IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						IWorkbenchPart workbenchPart = workbenchPage.getActivePart(); 
						IFile openedfile = (IFile) workbenchPart.getSite().getPage().getActiveEditor().getEditorInput().getAdapter(IFile.class);
						
						UriLineResult lineResult = (UriLineResult) selectedElement;
						UriReference reference = lineResult.getMatches().get(0).getUriReference();
						
						if (reference instanceof UriReference)
						{
							UriReference uriReference = (UriReference) reference;
							int startOffset = uriReference.getStartOffset();
							int length = uriReference.getLength();
							
							if (openedfile == uriReference.getFile())
							{
								final TwConfigEditor twConfigEditor = (TwConfigEditor) workbenchPage.getActiveEditor();
								final IEditorInput editorInput = workbenchPage.getActiveEditor().getEditorInput();
								final ISelection editorSelection = new TextSelection(twConfigEditor.getDocumentProvider().getDocument(editorInput), startOffset, length);
								twConfigEditor.getSelectionProvider().setSelection(editorSelection);
								break;
							}
							else
							{
								try
								{
									openAndSelect(workbenchPage, uriReference.getFile(), startOffset, length, true);
								} 
								catch (PartInitException e) 
								{
									final String failedToOpenResourceMsg = "Failed to open " + uriReference.getFile().getName();
									new JFaceUIPrimitives().displayMessage(TITLE, failedToOpenResourceMsg + "; see Error Log for details.", MessagePriority.ERROR);
								}
							}
						}
					}
				}
			}
		});
	}
	
	@Override
	protected void elementsChanged(Object[] objects)
	{
		AbstractTextSearchResult input = this.getInput();

        this.getViewer().setInput(input);
	}
	
	@Override
	protected void clear()
	{
		this.getViewer().setInput(null);
	}
	
	@Override
    protected void showMatch(final Match match, final int offset, final int length, final boolean activate) throws PartInitException 
	{
        IWorkbenchPage page = this.getSite().getPage();
        IFile file = (IFile) match.getElement();

        this.openAndSelect(page, file, offset, length, activate);
    }
	
	@Override
    public Match[] getDisplayedMatches(Object element) 
	{
        if (element instanceof UriLineResult)
        {
        	UriLineResult lineResult = (UriLineResult) element;
            ImmutableList<UriReferenceMatch> matches = lineResult.getMatches();

            return matches.toArray(new Match[matches.size()]);
        }
        else if (element instanceof IResource)
        {
            UriSearchResult input = (UriSearchResult) this.getInput();

            return input.getMatches(element);
        }

        return super.getDisplayedMatches(element);
    }
	
	private static final String TITLE = "Operation unavailable";
}
