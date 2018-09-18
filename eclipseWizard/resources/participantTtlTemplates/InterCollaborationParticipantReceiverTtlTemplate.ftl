<#-- test version -->
@base			<${baseUri}>	.

<#list prefixes as prefix>
@prefix ${prefix}	.
</#list>
@prefix ag:     <http://acceptance/Agent#> .

#This is a receiver
<${participantName}>
	a	twd:InterCollaborationParticipant ;
	twd:Extends
		<> ; #please fill in the extended URI
	rdfs:label
		"${label}" ;
	rdfs:comment
		"${comment}"	 ;
	twd:hasShareId
		<${participantName}/Share> . #don't forget to replace with the correct Share
	
<${participantName}/Share>
	a twd:Share ;
	rdfs:label
		"" ; #don't forget to fill in the label
	rdfs:comment
		"" ;
	twd:ShareForBroadcastTopic
		<AutoCollab/startup> ; #defining the topic URI that this share should listen on for messages from other collaborations for the same user, don't forget to replace with the expected topic URI
	twd:ShareUpdateOperation
		twd:Insert ;
	twd:hasPredicate
		ag:time ,
		ag:collab .