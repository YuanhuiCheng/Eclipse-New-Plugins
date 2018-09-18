package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import ca.yuanhuicheng.core.directory.exception.LoadTurtleUriException;
import ca.yuanhuicheng.tools.eclipse.plugin.command.IDETurtleLoader;
import ca.yuanhuicheng.tools.eclipse.plugin.eclipse.EclipseLogger;
import ca.yuanhuicheng.tools.eclipse.plugin.ide.state.PluginState;
import ca.yuanhuicheng.tools.ide_plugin_core.MessagePriority;

public class InterFileNavigationHyperlink implements IHyperlink
{
	public InterFileNavigationHyperlink(IRegion hyperlinkRegion, URI targetUri)
	{
		this.hyperlinkRegion = hyperlinkRegion;
		this.targetUri = targetUri;
	}
	
	@Override
	public IRegion getHyperlinkRegion() 
	{
		return hyperlinkRegion;
	}

	@Override
	public String getTypeLabel() 
	{
		return null;
	}

	@Override
	public String getHyperlinkText() 
	{
		return null;
	}

	@Override
	public void open() 
	{
		final IDETurtleLoader turtleLoader =
				new IDETurtleLoader(PluginState.COLLAB_DOC_MAP, PluginState.COLLAB_FILE_MAP,
						PluginState.PARTIC_DOC_MAP, PluginState.PARTIC_FILE_MAP);
		
		try 
		{
			if(turtleLoader.participantExists(targetUri.toASCIIString()))
			{
			    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			    IDE.openEditor(page, (IFile) PluginState.PARTIC_FILE_MAP.getResourceUriFile(targetUri).getFile());
			}
			else
			{
				EclipseLogger.createDefault().log("The item <" + targetUri + "> was not found.", MessagePriority.ERROR);
			}
		} 
		catch (LoadTurtleUriException ltue) 
		{
			EclipseLogger.createDefault().log("Failed to load turtles: " + ltue.getMessage(), MessagePriority.ERROR);
		}
		catch (PartInitException pie)
		{
			EclipseLogger.createDefault().log("Failed to open file: " + pie.getMessage(), MessagePriority.ERROR);
		}
	}
	
	private URI targetUri;
	private IRegion hyperlinkRegion;

}
