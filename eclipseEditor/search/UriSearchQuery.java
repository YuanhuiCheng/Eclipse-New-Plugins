 package ca.yuanhuicheng.tools.eclipse.plugin.ui.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RiotException;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;

import ca.yuanhuicheng.definition.DirectoryConstants;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedLiteralTriplePart;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedResourceTriplePart;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTriple;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTriplePart;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TextLocationRange;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TriplesComponentsLocator;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TwConfigParser;
import ca.yuanhuicheng.tools.eclipse.plugin.eclipse.EclipseLogger;
import ca.yuanhuicheng.tools.ide_plugin_core.MessagePriority;

public class UriSearchQuery implements ISearchQuery
{
	public UriSearchQuery(final String selectedUri, final String fullUri,
			final String localTriplePartName, final IResource resource, final boolean ifSSearchedInWorkspace)
	{
		this.selectedUri = selectedUri;
		this.resource = resource;
		this.fullUri = fullUri;
		this.localTriplePartName = localTriplePartName;
		uriResult = new UriSearchResult(this, ifSSearchedInWorkspace);
		treeUriReferenceSet = new TreeSet<UriReference>();
	}
	
	public UriSearchQuery(final String selectedUri, final String fullUri,
			final String localTriplePartName, final IWorkspace workspace, final boolean ifSSearchedInWorkspace)
	{
		this.selectedUri = selectedUri;
		this.workspace = workspace;
		this.fullUri = fullUri;
		this.localTriplePartName = localTriplePartName;
		uriResult = new UriSearchResult(this, ifSSearchedInWorkspace);
		treeUriReferenceSet = new TreeSet<UriReference>();
	}

	@Override
	public IStatus run(final IProgressMonitor monitor) throws OperationCanceledException 
	{	
		if (workspace != null)
		{
			IProject [] projects = workspace.getRoot().getProjects();
			for (IProject project : projects)
			{
				searchContainer(project, fullUri, localTriplePartName);
			}
		}
		else
		{
			//resource instanceof IProject
			searchContainer((IProject)resource, fullUri, localTriplePartName);
		}
		
		List<UriReference> referenceList = new ArrayList<UriReference>(treeUriReferenceSet);
		for (UriReference uriReference : referenceList)
		{
			IFile file = uriReference.getFile();
			uriResult.addMatch(new UriReferenceMatch(file, uriReference.getStartOffset(), uriReference.getLength(), uriReference));
		}
		
		return Status.OK_STATUS;
	}
	
	private void searchContainer(final IContainer container, final String fullUri, final String localTriplePartName)
	{
		try 
		{
			IResource [] members = container.members();
			
			for (IResource member : members)
			{
				if (member instanceof IContainer)
				{
					//Resource is a folder, search that
					searchContainer((IContainer)member, fullUri, localTriplePartName);
				}
				else if (member instanceof IFile)
				{
					//Resource is a file, search that
					searchUriInFile((IFile)member, fullUri, localTriplePartName);
				}
			}
		}
		catch (CoreException e) 
		{
			final String failedToFindResourceMsg = "Failed to get the elements from " + container.getName();
			EclipseLogger.createDefault().log(failedToFindResourceMsg + ".", e, MessagePriority.ERROR);
		}
	}
	
	private void searchUriInFile(final IFile file, final String fullUri, final String localTriplePartName) 
	{	
		final String result = FilenameUtils.getExtension(file.getName());
		try 
		{
			if (!result.isEmpty() && file.getFileExtension().equals("ttl"))	
			{
				final TriplesComponentsLocator ptTripleFinder = new TriplesComponentsLocator(file.getLocationURI(), null);
				new TwConfigParser().parseTtl(IOUtils.toString(file.getContents(), StandardCharsets.UTF_8.name()), null, ptTripleFinder);
				String crSubjectUri = ""; //the subject uri of the consume request, it should be passed to check if 'rdf:value' is belong to a CR rather than some other types 
				String tempSubjectUri = ""; 
				for (final LocatedTriple foundTriple : ptTripleFinder.getFoundTriples())
				{
					final LocatedResourceTriplePart subject = foundTriple.getSubject();
					final LocatedResourceTriplePart predicate = foundTriple.getPredicate();
					final String subjectUri = subject.getResourceUri().toASCIIString();
					//if different triples have the same subject, the subject should be checked only once
					if (!subjectUri.equals(tempSubjectUri))
					{
						tempSubjectUri = subjectUri;
						addUriReferenceToSetBasedOnTriplePart(file, subject, fullUri);
					}
					
					final String predicateName = predicate.getResourceUri().toASCIIString();
					if (predicateName.equals(DirectoryConstants.TYPE_URI))
					{
						final LocatedTriplePart object = foundTriple.getObject();
						if (object instanceof LocatedResourceTriplePart)
						{
							final String objectUri = ((LocatedResourceTriplePart) object).toString();
							if (objectUri.contains(DirectoryConstants.SPARQLCONSUMEREQUEST_URI) ||
								objectUri.contains(DirectoryConstants.CONSUMEREQUEST_URI) ||
								objectUri.contains(DirectoryConstants.DBCONSUMEREQUEST_URI))
							{
								crSubjectUri = foundTriple.getSubject().getResourceUri().toASCIIString();
							}
						}
					}
					addUriReferenceToSet(file, foundTriple, crSubjectUri, fullUri, localTriplePartName);
				}
			}
		} 
		catch (IOException e) 
		{
			final String failedToAccessResourceMsg = "Failed to get the contents from " + file.getName();
			EclipseLogger.createDefault().log(failedToAccessResourceMsg + ".", e, MessagePriority.ERROR);
		} 
		catch (CoreException e)
		{
			final String failedToFindResourceMsg = "Failed to get the elements from " + file.getName();
			EclipseLogger.createDefault().log(failedToFindResourceMsg + ".", e, MessagePriority.ERROR);
		}
		catch (BadLocationException e)
		{
			final String badLocationMsg = "Failed to get the contents location from " + file.getName();
			EclipseLogger.createDefault().log(badLocationMsg + ".", e, MessagePriority.ERROR);
		}
		catch (NullPointerException e)
		{
			final String tempNullPointerMsg = "null pointer problem existing in " + file.getName();
			EclipseLogger.createDefault().log(tempNullPointerMsg + ".", e, MessagePriority.ERROR);
		}
	} 
	
	private void addUriReferenceToSet(final IFile file, final LocatedTriple foundTriple, final String crSubjectUri,
			final String fullUri, final String localTriplePartName) throws BadLocationException, CoreException, IOException
	{
		final LocatedResourceTriplePart predicate = foundTriple.getPredicate();
		
		final String predicateName = predicate.getResourceUri().toASCIIString();
		if (predicateName.equals(DirectoryConstants.CONSUMEREQUEST_JSON_MAPPING_URI)||
			(predicateName.equals(DirectoryConstants.VALUE_URI) && 
			foundTriple.getSubject().getResourceUri().toASCIIString().equals(crSubjectUri)))
		{
			final LocatedLiteralTriplePart literalObject = (LocatedLiteralTriplePart) foundTriple.getObject();
			TextLocationRange locationRange = literalObject.getSourceLocationRange();
			int sparqlLine = locationRange.getLine() - 1;
			String objectStr = literalObject.getLiteralText();
			String prefixes = "";
			try
			{
				Model model = ModelFactory.createDefaultModel();
				model.read(file.getContents(), null, "TTL");
				Map<String, String> prefixMap = model.getNsPrefixMap();
				for (Map.Entry<String, String> entry : prefixMap.entrySet())
				{
					prefixes += "PREFIX " + entry.getKey() + ":" + LEFT_BRACKET + entry.getValue() + RIGHT_BRACKET + " \n";
				}
			}
			catch (RiotException e)
			{	
				BufferedReader br = new BufferedReader(new InputStreamReader(file.getContents()));
				for(String line; (line = br.readLine()) != null; )
				{
					String newLine = line.trim();
					if (newLine.startsWith(PREFIX_STR))
					{
						prefixes += "PREFIX " + Pattern.compile("@prefix(.*)").matcher(newLine).group(1).replace(".", "");
					}
				}
			}
			
			try
			{
				parseQuery(prefixes, objectStr, file, fullUri, localTriplePartName, locationRange, sparqlLine);
			}
			catch (QueryParseException e)
			{
				try
				{	
					//if query parse exception is caught, first escaping the characters of the query.
					//if the exception still exist, there should be other parsing problems that cannot
					//be resolved now, such like having the undefined prefixes or having some syntax errors.
					final String newObjectStr = SparqlUtils.escape(objectStr);
					parseQuery(prefixes, newObjectStr, file, fullUri, localTriplePartName, locationRange, sparqlLine);
				}
				catch(QueryParseException e1)
				{
					final String failedToParseQueryMsg = "Failed to parse the query from the file, " + file.getName() + "\n" + prefixes + objectStr;
					EclipseLogger.createDefault().log(failedToParseQueryMsg + ".", e1, MessagePriority.ERROR);
				}
//				final String failedToParseQueryMsg = "Failed to parse the query from the file, " + file.getName() + "\n" + prefixes + objectStr;
//				EclipseLogger.createDefault().log(failedToParseQueryMsg + ".", e, MessagePriority.ERROR);
			}
		}
		else
		{
			final LocatedTriplePart object = foundTriple.getObject();
			addUriReferenceToSetBasedOnTriplePart(file, predicate, fullUri);
			addUriReferenceToSetBasedOnTriplePart(file, object, fullUri);
		}
	}
	
	private void addUriReferenceToSetBasedOnTriplePart(final IFile file, final LocatedTriplePart part, final String fullUri) throws CoreException, IOException
	{
		if (fullUri.equals(removeLocationRange(part, part.getSourceLocationRange())))
		{
			TextLocationRange range = part.getSourceLocationRange();
			int lineNumber = range.getLine();
			int startOffset = range.getStartOffset();
			int length = range.getEndOffset() - range.getStartOffset() + 1;
			
			InputStream is = file.getContents();
			String fullLine = null;
			try 
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	            if (is != null)
	            {                            
	            	for(int i = 0; i < lineNumber-1; ++i)
	            	{
	            		reader.readLine();
	            	}
	            		
	            	fullLine = reader.readLine();           
	            }
	        } 
			finally
			{
	            is.close();
	        }
			
			treeUriReferenceSet.add(new UriReference(file, startOffset, length, lineNumber, selectedUri, fullLine, fullUri));
		}
	}
	
	private void parseQuery(final String prefixes, final String objectStr, final IFile file, final String fullUri, final String localTriplePartName,
			final TextLocationRange locationRange, int sparqlLine)
	{
		final Query query = QueryFactory.create(prefixes + objectStr);
		final Set<Node> triplePart = new HashSet<Node>();
		ElementWalker.walk(query.getQueryPattern(),
			    new ElementVisitorBase()
				{
			        public void visit(ElementPathBlock el)
			        {
			            Iterator<TriplePath> triples = el.patternElts();
			            while (triples.hasNext())
			            {
			            	TriplePath path = triples.next();
			            	triplePart.add(path.getSubject());
			            	triplePart.add(path.getPredicate());
			            	triplePart.add(path.getObject());
			            }
			        }
			    }
		);
		
		for (Node node : triplePart)
		{
			if (node != null && node.isURI())
			{
				if ((LEFT_BRACKET + node.getURI() + RIGHT_BRACKET).equals(fullUri))
				{
					//so far the specific start offset of uris inside SPARQL cannot be obtained, so using
	            	//the start offset of the whole literal object instead. 
					final int length = locationRange.getEndOffset() - locationRange.getStartOffset() + 1;
					
					Scanner sc = new Scanner(objectStr);
					while (sc.hasNextLine())
					{
						sparqlLine++;
						String line = sc.nextLine();
						
						if (line.contains(localTriplePartName) ||
							line.contains(fullUri))
						{
							treeUriReferenceSet.add(new UriReference(file, locationRange.getStartOffset(), length, sparqlLine, selectedUri, line, fullUri));
						}
				    }
					sc.close();
				}
			}
		}
	}

	private String removeLocationRange(final LocatedTriplePart object, final TextLocationRange locationRange)
	{
		return object.toString().replace(String.format(STRING_FORMAT, object.getSourceLocationRange().getLine(),
			locationRange.getStartOffset(), locationRange.getEndOffset()), "");
	}
	
	@Override
	public boolean canRerun() 
	{
		return true;
	}

	@Override
	public boolean canRunInBackground() 
	{
		return true;
	}
	
	@Override
	public String getLabel() 
	{
		return "Uri Search";
	}

	@Override
	public ISearchResult getSearchResult() 
	{
		return uriResult;
	}
	
	public String getSearchString()
	{
		return selectedUri;
	}
	
	private static final String PREFIX_STR = "@prefix";
	private static final String LEFT_BRACKET = "<";
	private static final String RIGHT_BRACKET = ">";
	
	private static final String STRING_FORMAT = "@(%d,%d-%d)";

	private final String fullUri;
	private final String localTriplePartName;
	private final String selectedUri;
	private IWorkspace workspace;
	private IResource resource;
	private UriSearchResult uriResult;
	private Set<UriReference > treeUriReferenceSet;
}
