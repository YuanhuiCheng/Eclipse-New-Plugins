package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor;

import java.net.URI;

import org.eclipse.ui.IFileEditorInput;

import ca.yuanhuicheng.tools.eclipse.plugin.util.UriUtils;

/**
 * {@link FileEditorInputUtil} is a home for common code applied to {@link IFileEditorInput} objects.
 */
public final class FileEditorInputUtil
{
	/**
	 * Get the RFC-correct URI of the file associated with the given editor input.
	 * @param fileEditorInput file editor input for which to obtain file URI
	 * @return RFC-correct (i.e. {@code file:///}-prefixed, not {@code file:/}-prefixed) absolute URI of the underlying
	 *         file
	 * @see UriUtils#getRfcCompliantFileColonSlashUri(URI)
	 */
	public static URI getRestoredEditorInputFileUri(final IFileEditorInput fileEditorInput)
	{
		return URI_UTILS.getRfcCompliantFileColonSlashUri(fileEditorInput.getFile().getLocationURI());
	}

	private static final UriUtils URI_UTILS = new UriUtils();

	private FileEditorInputUtil()
	{
	}
}