package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.TriplePath;

import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TriplePart;

public class SparqlTriplePart implements TriplePart
{
	public SparqlTriplePart(final Node tripleNode, final TriplePath triplePath, final String localTriplePartName)
	{
		this.tripleNode = tripleNode;
		this.triplePath = triplePath;
		this.localTriplePartName = localTriplePartName;
	}
	
	public Node getTripleNode()
	{
		return tripleNode;
	}
	
	public TriplePath getTriplePath()
	{
		return triplePath;
	}
	
	public boolean isURI()
	{
		return tripleNode.isURI() ? true : false;
	}
	
	public String getFullUri()
	{
		return tripleNode.getURI();
	}
	
	public String getLocalTriplePartName()
	{
		return localTriplePartName;
	}
	
	private final Node tripleNode;
	private final TriplePath triplePath;
	private final String localTriplePartName;
}
