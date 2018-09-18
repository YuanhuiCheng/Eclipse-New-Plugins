package ca.yuanhuicheng.tools.eclipse.plugin.ui.search;

import ca.yuanhuicheng.definition.NamespaceConstants;

public enum SparqlDirectoryConstants 
{
	SHARE_JSON_MAPPING_URI(NamespaceConstants.DIRECTORY_NAMESPACE + "ShareJsonMapping");
	
	public String val()
	{
		return s;
	}
	
	private SparqlDirectoryConstants (String s)
	{
		this.s = s;
	}
	
	private final String s;
}
