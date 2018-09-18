<#-- test version -->
@base			<${baseUri}>	.

<#list prefixes as prefix>
@prefix ${prefix}	.
</#list>


<${participantName}>
	a twd:ControllerParticipant ;
	twd:Extends
		<> ; #please fill in the extended URI
	rdfs:label
		"${label}" ;
	rdfs:comment
		"${comment}" ;
	twd:hasConsumeRequest
		<${participantName}/ControllerCR> ; #don't forget to replace with the correct CR
	twd:hasShareId
		<${participantName}/ControllerCR/Share> . #don't forget to replace with the correct Share
		
		
<${participantName}/ControllerCR>
	a twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:notifySatisfiedOnly
		true ; 
	twd:hasShareId 
		<${participantName}/ControllerCR/Share> ;
	twd:ConsumeRequestJsonMapping
	"""
		${r"#{prefiximport}"}
		
		BASE <${baseUri}>
		SELECT ?exampleObject1 ?exampleObject2
		WHERE {
			?exampleSubject   twd:examplePredicate1   ?exampleObject1 ;
			                  twd:examplePredicate2   ?exampleObject2 .
		}
	""" .
	
<${participantName}/ControllerCR/Share>
	a twd:Share ;
	twd:hasPredicate
		twd:examplePredicate	 ;	#don't forget to replace with the correct Predicate for Share
	twd:ShareJsonMapping
	"""	
		${r"#{prefiximport}"}
		@base <${baseUri}> .
		twd:exampleSubject   twd:examplePredicate   "exampleObject" ;
	""" .