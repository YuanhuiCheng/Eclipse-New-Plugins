<#-- test version -->
@base			<${baseUri}>	.

<#list prefixes as prefix>
@prefix ${prefix}	.
</#list>


<${participantName}>
	a	twd:RelayConnectorParticipant ;
	twd:Extends
		<http://thoughtwire.ca/ontology/2012/02/RelayConnector> ;
	rdfs:label
		"${label}" ; #don't forget to fill in the label
	rdfs:comment
		"${comment}"	 ;
	twd:hasEnvResource
		<http://thoughtwire.ca/SystemResource> ;
	twd:hasConsumeRequest
		<${participantName}/StartTapListener> , #don't forget to replace with the correct CR
		<${participantName}/StopTapListener> ;
	twd:hasShareId
		<${participantName}/Tap/Share> . #don't forget to replace with the correct Share
		
<http://thoughtwire.ca/SystemResource>
    a twd:EnvResource ;
    twd:hasResourceProperty
        <resources/http> ,
        <resources/https> .
 
<resources/http>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpServer ;
    twd:suggestedValue  <example> .
      
<resources/https>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpsServer ;
    twd:suggestedValue  <example> .
		
<${participantName}/StartTapListener>
	a twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:notifySatisfiedOnly	
		true ; 
	twconn:startListener		
		true ; 
	twconn:endpointType		
		twd:Tap ;
	twconn:shareTopicId		
		<${participantName}/TapTopic> ; #Used when sharing into Agent. It's also used as the key for a listener. Don't forget to replace with the correct topic id.
	<#if filterGroup??>
	twconn:filterGroup
		<${filterGroup}> ; #An identifier for which group the filter belongs to
	</#if>
	<#if filterOrder??>
	twconn:filterOrder 
		${filterOrder} ; #An integer used to sort the filters in the same group; filters with lower number will be processed first
	</#if>
	<#--  <#if allow??>
	twconn:allow 
		"${allow}" ; #A regex string that is used to decide if the message should be parsed. Don't forget to replace with the correct regex string.
	</#if> -->
	twconn:parseConfig #A JSON string that specifies how to parse a csv message
		"""
		{"delimiter":"|",
		"fieldDefinition":[	{"fieldName":"room","required":true},
							{"fieldName":"priority"},
							{"fieldName":"date","type":"Date","format":"yyyy-MM-dd","required":true},
							{"fieldName":"comment","defaultValue":""}]}
		""" ;
	twd:MultiGraph   
		true ;
	twd:ConsumeRequestJsonMapping                         
      	"""
      	${r"#{prefiximport}"}
      	
      	BASE <${baseUri}>
		SELECT ?collaborationId ?username ?password ?startMessage
      	WHERE 
      	{
			   twd:Collaboration twd:CollaborationIdentifier ?collaborationId .
               ?page <startTapListener> ?startMessage .
               <data>
                	<username> ?username ;
                	<password> ?password .
      	}
		""".

<${participantName}/StopTapListener>
	a twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:notifySatisfiedOnly	
		true ;
	twd:NotifyOnTeardown	 	
		true ; #Default is false. True if the participant should be notified before the consume request is torn down, which happens when the participant unregisters or is evicted.
	twd:hasShareId
		<${participantName}/Tap/Share> ;
	twconn:stopListener		
		true ; 
	twconn:endpointType		
		twd:Tap ;
	twconn:shareTopicId		
		<${participantName}/TapTopic> ; #don't forget to replace with the correct topic id
	twd:MultiGraph    
		true;
	twd:ConsumeRequestJsonMapping
      	"""
      	${r"#{prefiximport}"}
      	
	 	BASE <${baseUri}>
	 	SELECT	?stopMessage
	 	WHERE 
	 	{
				twd:Collaboration twd:CollaborationIdentifier ?collaborationId .
                ?page <stopTapListener> ?stopMessage .
	 	}
		""".

<${participantName}/Tap/Share>
	a twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:ShareForBroadcastTopic
		 <${participantName}/TapTopic> ; #don't forget to replace with the correct topic id
	twd:hasPredicate 
		<room> , #don't forget to replace with the correct predicate for Share
		<priority> ,
		<date> ,
		<comment> ;
	twd:ShareJsonMapping
		"""
      	${r"#{prefiximport}"}
      	
		@base <${baseUri}> .
		<RelayedMessage(($))>	
			<room>		"(room)" ;
			<priority>	"(priority)" ;
			<date>		"(date)" ;
			<comment>	"(comment)" .
		""".