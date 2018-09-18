<#-- test version -->
@base			<${baseUri}>	.

<#list prefixes as prefix>
@prefix ${prefix}	.
</#list>
@prefix ag:     <http://acceptance/Agent#> .

#This is a sender
<${participantName}>
	a	twd:InterCollaborationParticipant ;
	twd:Extends
		<> ; #please fill in the extended URI
	rdfs:label
		"${label}" ;
	rdfs:comment
		"${comment}"	 ;
	twd:hasConsumeRequest
		<${participantName}/onStartup> . #don't forget to replace with the correct CR
	
			
<${participantName}/onStartup>
	a twd:SparqlConsumeRequest ;
	rdfs:label
		"" ; #don't forget to fill in the label
	rdfs:comment
		"" ;
	twd:BroadcastMessage 
		true ; #if true, send the triples to all users that have collaborations with shares registered for this topic
	twd:ShareTopicId
		<AutoCollab/startup> ; #identifying which share to use in the target user's collaboration(s), don't forget to replace with the expected topic URI
	twd:notifySatisfiedOnly 
		true ;
	twd:notifyOnCondition
		"""
		${r"#{prefiximport}"}
		
		CONSTRUCT WHERE {
			?collab
				ag:collab	?collab .
		}
		""" ;
	rdf:value
        """
        ${r"#{prefiximport}"}
		
		CONSTRUCT {
			?collab
				ag:time		?time ;
				ag:collab	?collab .
		}
		WHERE {
			?s	ag:collab	?collab .
			
			BIND(NOW() AS ?time) .
		}
		""" .
	