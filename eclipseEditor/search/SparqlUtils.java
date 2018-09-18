package ca.yuanhuicheng.tools.eclipse.plugin.ui.search;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * The class is used to escape some characters inside SPARQL thus making
 * the generated query can be successfully parsed.
 * 
 */
public class SparqlUtils
{
	private static final ImmutableMap<String, String> SPARQL_ESCAPE_SEARCH_REPLACEMENTS = ImmutableMap.<String, String>builder()
			.put("\\\"", "\"")
			.build();
	
	public static String escape(String string)
	{
		String newStr = string;
		for (Map.Entry<String, String> entry : SPARQL_ESCAPE_SEARCH_REPLACEMENTS.entrySet())
		{
			if (string.contains(entry.getKey()))
			{
				newStr = string.replace(entry.getKey(), entry.getValue());
			}
		}
		
		return newStr;
	}
}
