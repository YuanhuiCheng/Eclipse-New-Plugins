package ca.yuanhuicheng.tools.eclipse.plugin.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFileEditorInput;

import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TriplePart;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.TwConfigEditor;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.search.TWUriController;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.search.UriSearchQuery;

public class FindUriReferencesInProjectFromEditorHandler extends AbstractHandler
{

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		final TWUriController controller = new TWUriController();
		final TwConfigEditor twConfigEditor = controller.getTWConfigEditor();
		final IProject project = ((IFileEditorInput) twConfigEditor.getEditorInput()).getFile().getProject();
		final TriplePart part = controller.getToBeSearchedTriplePart();
		final String fullUri = controller.getFullUri(part);
		final String localTriplePartName = controller.getLocalTriplePartName(part);
		
		ISearchQuery query = new UriSearchQuery(controller.getSelectedStr(), 
				fullUri, localTriplePartName, project, false);
		
		NewSearchUI.runQueryInBackground(query);

		return null;
	}
}
