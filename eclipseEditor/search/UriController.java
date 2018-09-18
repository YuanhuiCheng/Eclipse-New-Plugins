package ca.yuanhuicheng.tools.eclipse.plugin.ui.search;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import ca.yuanhuicheng.definition.DirectoryConstants;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedLiteralTriplePart;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedResourceTriplePart;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTriple;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTripleList;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.LocatedTriplePart;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TextLocationRange;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TriplePart;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TriplesComponentsLocator;
import ca.yuanhuicheng.tools.eclipse.configuration.antlr.TwConfigParser;
import ca.yuanhuicheng.tools.eclipse.plugin.eclipse.EclipseLogger;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.FileEditorInputUtil;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.SparqlTriplePart;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.editor.TwConfigEditor;
import ca.yuanhuicheng.tools.eclipse.plugin.ui.handler.EclipseHandlerUtils;
import ca.yuanhuicheng.tools.ide_plugin_core.MessagePriority;

public class UriController
{
	public UriController()
	{
		twEditor = (ConfigEditor) EclipseHandlerUtils.getActiveEditor();
		editorInput = twEditor.getEditorInput();
		document = twEditor.getDocumentProvider().getDocument(editorInput);
	}
	
	public UriController(ConfigEditor twEditor, IEditorInput editorInput, IDocument document)
	{
		this.twEditor = twEditor;
		this.editorInput = editorInput;
		this.document = document;
	}
	
	public TriplesComponentsLocator parseDocumentTtl()
	{
		final URI fileUri = FileEditorInputUtil.getRestoredEditorInputFileUri((IFileEditorInput) twEditor.getEditorInput());
		TriplesComponentsLocator tripleFinder = new TriplesComponentsLocator(fileUri, null);
		final String currentDocumentTtl = document.get();
		new TwConfigParser().parseTtl(currentDocumentTtl, null, tripleFinder);
		
		return tripleFinder;
	}
	
	public TriplePart getToBeSearchedTriplePart()
	{		
		LocatedTripleList triplesFound = parseDocumentTtl().getFoundTriples();
		String crSubjectUri = "";
		for (LocatedTriple foundTriple : triplesFound)
		{
			try
			{
				LocatedTriplePart locatedTriplePart = getToBeSearchedTriplePartInTtl(document, getTextSelection(), foundTriple.getSubject(), 
						foundTriple.getPredicate(), foundTriple.getObject());
				if (locatedTriplePart != null)
				{
					return locatedTriplePart;
				}
				else
				{
					final String baseUriInTtl = getBaseUriFromText(document.get(), BASE_STR_TTL);
					final LocatedResourceTriplePart predicate = foundTriple.getPredicate();
					final String predicateName = predicate.getResourceUri().toASCIIString();
					if (predicateName.equals(DirectoryConstants.TYPE_URI))
					{
						final LocatedTriplePart object = foundTriple.getObject();
						final String objectUri = ((LocatedResourceTriplePart) object).toString();
						if (objectUri.contains(DirectoryConstants.SPARQLCONSUMEREQUEST_URI) ||
							objectUri.contains(DirectoryConstants.CONSUMEREQUEST_URI) ||
							objectUri.contains(DirectoryConstants.DBCONSUMEREQUEST_URI))
						{
							crSubjectUri = foundTriple.getSubject().getResourceUri().toASCIIString();
						}
					}

					SparqlTriplePart triplePart = getToBeSearchedTriplePartInSpqral(document, getTextSelection(), foundTriple.getObject(), 
							baseUriInTtl, foundTriple.getSubject().getResourceUri().toASCIIString(), crSubjectUri, predicateName);
					if (triplePart != null)
					{
						return triplePart;
					}
				}
			}
			catch (BadLocationException e) 
			{
				final String badLocationMsg = "cannot retrieve location info of uri from the open ttl file";
				EclipseLogger.createDefault().log(badLocationMsg + ".", e, MessagePriority.ERROR);
			}
		}
		
		return null;
	}
	
	public String getFullUri(final TriplePart part)
	{
		if (part instanceof LocatedTriplePart)
		{
			final LocatedTriplePart locatedTriplePart = (LocatedTriplePart) part;
			return removeLocationRange(locatedTriplePart, locatedTriplePart.getSourceLocationRange());	
		}
		else if (part instanceof SparqlTriplePart)
		{
			SparqlTriplePart sparqlTriplePart = (SparqlTriplePart) part;
			return LEFT_BRACKET + sparqlTriplePart.getFullUri() + RIGHT_BRACKET;
		}
		
		return null;
	}
	
	public String getLocalTriplePartName(final TriplePart part)
	{
		if (part instanceof LocatedTriplePart)
		{
			return getUriInDocument(document, ((LocatedTriplePart) part).getSourceLocationRange());	
		}
		else if (part instanceof SparqlTriplePart)
		{
			SparqlTriplePart sparqlTriplePart = (SparqlTriplePart) part;
			return sparqlTriplePart.getLocalTriplePartName();
		}
		
		return null;
	}
	
	private LocatedTriplePart getToBeSearchedTriplePartInTtl(final IDocument document, final ITextSelection selection, final LocatedTriplePart...triplePart) throws BadLocationException
	{
		for (LocatedTriplePart part : triplePart)
		{
			if (ifSelectedTextIsLocatedTriplePart(document, selection, part))
			{
				return part;
			}
		}
		
		return null;
	}
	
	private SparqlTriplePart getToBeSearchedTriplePartInSpqral(final IDocument document, final ITextSelection selection, final LocatedTriplePart objectPart, final String baseUriInTtl,
			final String subjectName, final String subjectCrName, final String predicateName)
	{
		if (checkIfObjectIsSparqlBasedOnPredicte(predicateName, subjectName, subjectCrName))
		{
			final LocatedLiteralTriplePart object = (LocatedLiteralTriplePart) objectPart;
			
			if (ifSelectedTextIsInSparqlRange(selection, object))
			{
				final Query query = createQuery(document.get(), object);
				String baseUriInQuery = getBaseUriFromText(object.getLiteralText(), BASE_STR);
				if (baseUriInQuery == null)
				{
					if (baseUriInTtl != null)
					{
						baseUriInQuery = baseUriInTtl;
					}
					else
					{
						baseUriInQuery = query.getBaseURI();
					}
				}
				final String baseUri = baseUriInQuery;
				final Set<SparqlTriplePart> sparqlTriplePartSet = new HashSet<SparqlTriplePart>();
				ElementWalker.walk(query.getQueryPattern(),
					    new ElementVisitorBase()
						{
					        public void visit(ElementPathBlock el)
					        {
					            Iterator<TriplePath> triples = el.patternElts();
					            while (triples.hasNext())
					            {
					            	TriplePath path = triples.next();
					            	addSparqlTriplePartToSet(sparqlTriplePartSet, query, path.getSubject(), path, baseUri);
					            	addSparqlTriplePartToSet(sparqlTriplePartSet, query, path.getPredicate(), path, baseUri);
					            	addSparqlTriplePartToSet(sparqlTriplePartSet, query, path.getObject(), path, baseUri);
					            }
					        }
					     }
				);
				
				for (SparqlTriplePart triplePart : sparqlTriplePartSet)
				{
					final String selectionText = getTextSelection().getText();
					Node node = triplePart.getTripleNode();
					if (node.isURI())
					{
						if (selectionText.equals(triplePart.getLocalTriplePartName()) || 
							selectionText.equals(LEFT_BRACKET + triplePart.getFullUri() + RIGHT_BRACKET))
						{
							return triplePart;
						}
					}
				}
			}
		}
		
		return null;
	}

	public boolean decideIfSelectedTextIsUri(final TwConfigEditor twEditor, final IEditorInput editorInput, final IDocument document, final ITextSelection selection)
	{
		boolean ifSelectedTextIsUri = false;
	
		LocatedTripleList triplesFound = parseDocumentTtl().getFoundTriples();
		final String baseUriInTtl = getBaseUriFromText(document.get(), BASE_STR_TTL);
		String crSubjectUri = "";
		for(LocatedTriple foundTriple : triplesFound)
		{
			final LocatedResourceTriplePart predicate = foundTriple.getPredicate();
			final String predicateName = predicate.getResourceUri().toASCIIString();
			if (predicateName.equals(DirectoryConstants.TYPE_URI))
			{
				final LocatedTriplePart object = foundTriple.getObject();
				final String objectUri = ((LocatedResourceTriplePart) object).toString();
				if (objectUri.contains(DirectoryConstants.SPARQLCONSUMEREQUEST_URI) ||
					objectUri.contains(DirectoryConstants.CONSUMEREQUEST_URI) ||
					objectUri.contains(DirectoryConstants.DBCONSUMEREQUEST_URI))
				{
					crSubjectUri = foundTriple.getSubject().getResourceUri().toASCIIString();
				}
			}
			
			if (decideIfSelectedTextIsTriplePart(document, selection, foundTriple.getSubject(), foundTriple.getPredicate(), foundTriple.getObject())) 
			{
				return true;
			}
			else if (checkIfObjectIsSparqlBasedOnPredicte(predicateName, foundTriple.getSubject().getResourceUri().toASCIIString(), crSubjectUri))
			{
				final LocatedLiteralTriplePart object = (LocatedLiteralTriplePart) foundTriple.getObject();
			
				if (ifSelectedTextIsInSparqlRange(selection, object))
				{
					final Query query = createQuery(document.get(), object);
					if (query == null)
					{
						return false;
					}
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

					String baseUri = getBaseUriFromText(object.getLiteralText(), BASE_STR);
					if (baseUri == null)
					{
						if (baseUriInTtl != null)
						{
							baseUri = baseUriInTtl;
						}
						else
						{
							baseUri = query.getBaseURI();
						}
					}
					
					for (Node node : triplePart)
					{
						if (node.isURI())
						{
							if (ifSelectedTextIsEqualToNodeLocalName(query, selection, node, baseUri))
							{
								return true;
							}	
						}
					}
				}
			}
		}
		
		return ifSelectedTextIsUri;
	}
	
	private Query createQuery(final String ttlStr, final LocatedLiteralTriplePart object)
	{
		InputStream stream;
		String objectStr = object.getLiteralText();
		String prefixes = "";
		try 
		{
			stream = new ByteArrayInputStream(ttlStr.getBytes(StandardCharsets.UTF_8.name()));
			
			Model model = ModelFactory.createDefaultModel();
			model.read(stream, null, "TTL");
			
			Map<String, String> prefixMap = model.getNsPrefixMap();
			for (Map.Entry<String, String> entry : prefixMap.entrySet())
			{
				prefixes += "PREFIX " + entry.getKey() + ":" + LEFT_BRACKET + entry.getValue() + RIGHT_BRACKET + " \n";
			}
			
			return QueryFactory.create(prefixes + objectStr);
		} 
		catch (RiotException e)
		{
			//if the ontology model has no syntax error (RiotException), the code block should not be arrived
			String[] lines = ttlStr.split(System.getProperty("line.separator"));
			for (String line : lines)
			{
				String newLine = line.trim();
				if (newLine.startsWith(PREFIX_STR))
				{
					prefixes += "PREFIX " + Pattern.compile("@prefix(.*)").matcher(newLine).group(1).replace(".", "");
				}
			}
		}
		catch (QueryParseException e)
		{
			try
			{	
				//if query parse exception is caught, first escaping the characters of the query.
				//if the exception still exist, there should be other parsing problems that cannot
				//be resolved now, such like having the undefined prefixes or having some syntax errors.
				final String newObjectStr = SparqlUtils.escape(objectStr);
				return QueryFactory.create(prefixes + newObjectStr);
			}
			catch(QueryParseException e1)
			{
				final String failedToParseQueryMsg = "Failed to parse the query: " + prefixes + objectStr;
				EclipseLogger.createDefault().log(failedToParseQueryMsg + ".", e1, MessagePriority.ERROR);
			}
			final String failedToParseQueryMsg = "Failed to parse the query: " + prefixes + objectStr;
			EclipseLogger.createDefault().log(failedToParseQueryMsg + ".", e, MessagePriority.ERROR);
		}
		catch (UnsupportedEncodingException e)
		{
			final String badEncodingMsg = "cannot encode the string " + ttlStr; 
			EclipseLogger.createDefault().log(badEncodingMsg + ".", e, MessagePriority.ERROR);
		}

		return null;
	}
	
	private String getBaseUriFromText(final String str, final String basePrefix)
	{
		Scanner sc = new Scanner(str);
		try
		{
			while (sc.hasNextLine())
			{
				String newLine = sc.nextLine().trim();
				if (newLine.startsWith(basePrefix))
				{
					Matcher m = Pattern.compile(STRING_BASE_FORMAT).matcher(newLine);
				    while(m.find()) 
				    {
				       return m.group(1);
				    }
				}
			}
		}
		finally
		{
			sc.close();
		}
		
		return null;
	}
	

	private boolean ifSelectedTextIsEqualToNodeLocalName(final Query query, final ITextSelection selection, final Node node, final String baseUri)
	{
		final String selectionText = selection.getText();
		final String uriPrefix = query.getPrefixMapping().getNsURIPrefix(node.getNameSpace());
		if (uriPrefix != null)
		{
			if (selectionText.contains(node.getNameSpace()))
			{
				return selectionText.equals(LEFT_BRACKET + node.getURI() + RIGHT_BRACKET);
			}
			return selectionText.equals(uriPrefix + ":" + node.getLocalName());
		}
		else if (node.getURI().startsWith(baseUri))
		{
			return selectionText.equals(LEFT_BRACKET + node.getURI().substring(baseUri.length()) + RIGHT_BRACKET);
		}
		
		return false;
	}
	
	private void addSparqlTriplePartToSet(final Set<SparqlTriplePart> sparqlTriplePartSet, final Query query, 
			final Node node, final TriplePath path, final String baseUri)
	{
		if (node.isURI())
    	{
			final String uriPrefix = query.getPrefixMapping().getNsURIPrefix(node.getNameSpace());
			if (uriPrefix != null)
			{
				sparqlTriplePartSet.add(new SparqlTriplePart(node, path, uriPrefix + ":" + node.getLocalName()));
			}
			else if (node.getURI().startsWith(baseUri))
			{
				sparqlTriplePartSet.add(new SparqlTriplePart(node, path, LEFT_BRACKET + node.getURI().substring(baseUri.length()) + RIGHT_BRACKET));
			}
    	}
	}
	
	private boolean checkIfObjectIsSparqlBasedOnPredicte(final String predicateName, final String subjectName,
			final String subjectCrName)
	{
		return predicateName.equals(DirectoryConstants.CONSUMEREQUEST_JSON_MAPPING_URI) || 
				(predicateName.equals(DirectoryConstants.VALUE_URI) && subjectName.equals(subjectCrName));
	}
	
	private boolean decideIfSelectedTextIsTriplePart(final IDocument document, final ITextSelection selection,
			final LocatedTriplePart...triplePart)
	{
		for (LocatedTriplePart part : triplePart)
		{
			if (ifSelectedTextIsLocatedTriplePart(document, selection, part))
			{
				return true;
			}
		}
		
		return false;
	}

	private boolean ifSelectedTextIsLocatedTriplePart(final IDocument document, final ITextSelection selection, 
			final LocatedTriplePart part) 
	{
		final String str = getUriInDocument(document, part.getSourceLocationRange());
		final String bracketRemovedStr = getBracketRemovedStr(str);
		TextLocationRange locationRange = part.getSourceLocationRange();
		int selectionStartoffset = selection.getOffset();
		int selectionEndoffset = selectionStartoffset + selection.getLength() - 1;
		
		return selectionStartoffset >= locationRange.getStartOffset() && selectionEndoffset <= locationRange.getEndOffset() && (
				selection.getText().equals(str) || selection.getText().equals(bracketRemovedStr));
	}
	
	private boolean ifSelectedTextIsInSparqlRange(final ITextSelection selection, final LocatedLiteralTriplePart object)
	{
		final int selectionOffset = selection.getOffset();
		final TextLocationRange locationRange = object.getSourceLocationRange(); 
		return selectionOffset >= locationRange.getStartOffset() && selectionOffset <= locationRange.getEndOffset();
	}
	
	private String getUriInDocument(final IDocument document, final TextLocationRange range)
	{
		try 
		{
			return document.get(range.getStartOffset(), range.getEndOffset() - range.getStartOffset() + 1);
		}
		catch (BadLocationException e)
		{
			final String badLocationMsg = "cannot retrieve location info of uri from the open ttl file";
			EclipseLogger.createDefault().log(badLocationMsg + ".", e, MessagePriority.ERROR);
		}
		
		return null;
	}
	
	private String getBracketRemovedStr(final String str)
	{
		return str.startsWith(LEFT_BRACKET) && str.endsWith(RIGHT_BRACKET) ? str.replace(RIGHT_BRACKET, "").replace(LEFT_BRACKET, "") : str;
	}
	
	private String removeLocationRange(final LocatedTriplePart object, final TextLocationRange locationRange)
	{
		return object.toString().replace(String.format(STRING_FORMAT, object.getSourceLocationRange().getLine(),
			locationRange.getStartOffset(), locationRange.getEndOffset()), "");
	}
	
	public ITextSelection getTextSelection()
	{
		ISelectionProvider selectionProvider = twEditor.getEditorSite().getSelectionProvider();
		return (ITextSelection) selectionProvider.getSelection();
	}
	
	public String getSelectedStr()
	{
		return getTextSelection().getText();
	}
	
	public TwConfigEditor getTWConfigEditor()
	{
		return twEditor;
	}
	
	public IEditorInput getEditorInput()
	{
		return editorInput;
	}
	
	public IDocument getDocument()
	{
		return document;
	}
	
	private static final String STRING_BASE_FORMAT = "\\<([^)]+)\\>";
	private static final String STRING_FORMAT = "@(%d,%d-%d)";
	
	private static final String PREFIX_STR = "@prefix";
	private static final String BASE_STR = "BASE";
	private static final String BASE_STR_TTL = "@base";
	private static final String LEFT_BRACKET = "<";
	private static final String RIGHT_BRACKET = ">";
	
	private final TwConfigEditor twEditor;
	private final IEditorInput editorInput;
	private final IDocument document;
}
