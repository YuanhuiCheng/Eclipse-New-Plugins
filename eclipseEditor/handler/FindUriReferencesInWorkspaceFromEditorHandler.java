package ca.yuanhuicheng.tools.eclipse.plugin.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;

import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TriplePart;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.search.TWUriController;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.search.UriSearchQuery;

public class FindUriReferencesInWorkspaceFromEditorHandler extends AbstractHandler
{

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		final TWUriController controller = new TWUriController();
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final TriplePart part = controller.getToBeSearchedTriplePart();
		final String fullUri = controller.getFullUri(part);
		final String localTriplePartName = controller.getLocalTriplePartName(part);
		
		ISearchQuery query = new UriSearchQuery(controller.getSelectedStr(), 
				fullUri, localTriplePartName, workspace, true);
		NewSearchUI.runQueryInBackground(query);
		 
		return null;
	}
}
