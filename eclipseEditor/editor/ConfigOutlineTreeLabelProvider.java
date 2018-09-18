package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import ca.yuanhuicheng.tools.eclipse.configuration.jena.Configuration;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ConsumeRequestDeclaration;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ParticipantDeclaration;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ResourceDefinitionComponent;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ResourceType;
import ca.yuanhuicheng.tools.eclipse.configuration.jena.ShareIdDeclaration;

/**
 * A {@link ConfigOutlineTreeLabelProvider} provides the labels to be displayed in a configuration Outline
 * View {@link org.eclipse.jface.viewers.TreeViewer}.
 */
public class ConfigOutlineTreeLabelProvider extends LabelProvider
{
	public ConfigOutlineTreeLabelProvider(final Action action)
	{
		this.action = action;
	}
	
	@Override
	public String getText(final Object element)
	{
		String elementText = null;

		if (element instanceof Configuration)
		{
			final Configuration twConfig = (Configuration) element;
			elementText = twConfig.isResourceDefinition() ? surroundWithAngleBrackets(twConfig.getResourceUri().toASCIIString()) : twConfig.getNonResourceDescription();
		}
		else if (element instanceof ParticipantDeclaration)
		{
			final ParticipantDeclaration pd = (ParticipantDeclaration) element;
			final String label = pd.getLabel();
			if (!action.isChecked() || label.isEmpty())
			{
				elementText = getUriForDeclarations(pd);
			}
			else
			{
				elementText = surroundWithAngleBrackets(label);
			}
			
		}
//		else if (element instanceof ParticipantForCollaborationDeclaration)
//		{
//			elementText = ((ParticipantForCollaborationDeclaration) element).getType().replace(NamespaceConstants.DIRECTORY_NAMESPACE, "");
//		}
		else if (element instanceof ConsumeRequestDeclaration)
		{
			final ConsumeRequestDeclaration crd = (ConsumeRequestDeclaration) element;
			final String label = crd.getLabel();
			if (!action.isChecked() || label.isEmpty())
			{
				elementText = getUriForDeclarations(crd);
			}
			else
			{
				elementText = surroundWithAngleBrackets(label);
			}
		}
		else if (element instanceof ShareIdDeclaration)
		{
			final ShareIdDeclaration sid = (ShareIdDeclaration) element;
			final String label = sid.getLabel();
			if (!action.isChecked() || label.isEmpty())
			{
				elementText = getUriForDeclarations(sid);
			}
			else
			{
				elementText = surroundWithAngleBrackets(label);
			}
		}
		
		return elementText;
	}
	
	private String getUriForDeclarations(final ResourceDefinitionComponent rdc)
	{
		return surroundWithAngleBrackets(rdc.getUri().toASCIIString().replace(rdc.getParentUri().toASCIIString(), ""));
	}

	private String surroundWithAngleBrackets(final String uri)
	{
		return String.format("<%s>", uri);
	}

	@Override
	public Image getImage(final Object element)   // NOPMD - anomalous 'UR'-anomaly
	{
		final Image image;
		if (element instanceof Configuration)
		{
			final Configuration twConfig = (Configuration) element;
			if (twConfig.isResourceDefinition())
			{
				if (twConfig.getResourceType() == ResourceType.COLLABORATION)
				{
					image = collabImg;
				}
				else
				{
					assert twConfig.getResourceType() == ResourceType.PARTICIPANT;
					image = particImg;
				}
			}
			else
			{
				image = invalidCfgImg;
			}
		}
		else if (element instanceof ShareIdDeclaration)
		{
			image = shareImg;
		}
		else if (element instanceof ParticipantDeclaration)
		{
			image = particImg;
			
			ParticipantDeclaration participantElement = (ParticipantDeclaration) element;
			if (participantElement.getType().equals(""))
			{
				
			}
		}
//		else if (element instanceof ParticipantForCollaborationDeclaration)
//		{
//			image = null;
//		}
		else
		{
			// assume it's a CR declaration
			image = crImg;
		}
		return image;
	}

	private Image getImage(final String file)
	{
		final Bundle bundle = FrameworkUtil.getBundle(getClass());
		final URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
		final ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();
	}

	private Action action;
	
	private final transient Image collabImg = getImage("collaboration_16x16.png");
	private final transient Image particImg = getImage("participant_16x16.png");
	private final transient Image shareImg = getImage("declared_share_16x16.png");
	private final transient Image crImg = getImage("declared_cr_16x16.png");
	private final transient Image invalidCfgImg = PlatformUI.getWorkbench().getSharedImages().getImage(
			ISharedImages.IMG_OBJS_ERROR_TSK);
}