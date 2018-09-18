package ca.yuanhuicheng.tools.eclipse.plugin.ui.search;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;

import ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.TwConfigEditor;

public class UriPropertyTester extends PropertyTester
{

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
	{
		IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		boolean ifSelectionIsUri = false;
		
		if (editorPart instanceof TwConfigEditor)
		{
			TwConfigEditor twEditor = (TwConfigEditor) editorPart;
			IDocumentProvider provider = twEditor.getDocumentProvider();
			IEditorInput editorInput = twEditor.getEditorInput();
		    IDocument document = provider.getDocument(editorInput);
		    
		    if (document != null)
		    {
		    	ITextSelection selection = null;
				ISelectionProvider selectionProvider= twEditor.getEditorSite().getSelectionProvider(); 
				
				if (selectionProvider != null) 
				{
					ISelection s = selectionProvider.getSelection();
				    if (s instanceof ITextSelection)
				    { 
				    	selection= (ITextSelection) s; 
				    } 
				    
				    if (selection != null)
				    {
				    	TWUriController controller = new TWUriController();
				    	return controller.decideIfSelectedTextIsUri(twEditor, editorInput, document, selection);
				    }
				}
		    }
		}
		
		return ifSelectionIsUri;
	}
}
