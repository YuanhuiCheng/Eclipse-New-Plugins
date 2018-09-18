<#-- test version -->
@base			<${baseUri}>	.

<#list prefixes as prefix>
@prefix ${prefix}	.
</#list>
@prefix frm:    <FormParticipantExample/Form/Master#> .
#don't forget to change the prefix for 'frm'

<${participantName}>
	a	twd:Participant ;
	twd:Extends
		<> ; #please fill in the extended URI
	rdfs:label
		"${label}" ;
	rdfs:comment
		"${comment}"	 ;
	twd:hasConsumeRequest
		<${participantName}/SemanticFormCR> ; #don't forget to replace with the correct CR
	twd:hasShareId
		<${participantName}/SemanticFormCR/Share> . #don't forget to replace with the correct Share

frm:form
	a twelem:form ;
	twform:layout
	"""
	{
		"prefixes" : { "frm" : "<${baseUri}>FormParticipantExample/Form/Master#" },
		"contents" : [			
			{ "id": "frm:FormParticipantExample" }
		]
	}
	""" .

frm:FormParticipantExample
	a	twelem:box;
	twform:layout
	"""
	{ "contents" : [
			{ "id" : "frm:header" },
			{ "id" : "frm:main" }
					
		]
	}
	""" .

frm:header
	a	twelem:box;
	twform:layout
	"""
	{ "contents" : [
			{ "id": "frm:headerText" }
		]
	}
	""" .

frm:headerText
	a	twelem:box;
	twform:layout
	"""
	{ "contents" : [
			{ "id": "frm:appTitle" }
		]
	}
	""" .

frm:appTitle
	a			twelem:html ;
	rdf:value	"""Form Example""" .
	
frm:main
	a twelem:box;
	twform:layout
	#please fill in the contents
	"""
	{ "contents" : [
			
		]
	}
	""" .		
	
<${participantName}/SemanticFormCR>
	a twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:notifySatisfiedOnly
		true ; #default is false
	twd:hasShareId 
		<${participantName}/SemanticFormCR/Share> ;
	twd:ConsumeRequestJsonMapping
	"""
		${r"#{prefiximport}"}
		#The construct causes the import of all prefixes defined in the participant file into the SPARQL statement
		
		BASE	 <${baseUri}>
		SELECT ?exampleObject1 ?exampleObject2
		WHERE {
			?exampleSubject   twd:examplePredicate1   ?exampleObject1 ;
			                  twd:examplePredicate2   ?exampleObject2 .
		}
	""" .
	
<${participantName}/SemanticFormCR/Share>
	a twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		twd:examplePredicate ;	#don't forget to replace with the correct predicate for Share
	twd:ShareJsonMapping
	"""	
		${r"#{prefiximport}"}
		@base <${baseUri}> .
		twd:exampleSubject   twd:examplePredicate   "exampleObject" .
	""" .