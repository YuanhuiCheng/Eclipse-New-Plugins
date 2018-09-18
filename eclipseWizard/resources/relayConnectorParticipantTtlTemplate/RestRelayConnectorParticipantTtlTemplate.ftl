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
		<${participantName}/StartRestListener> , #don't forget to replace with the correct CR
		<${participantName}/StopRestListener> ;
	twd:hasShareId
		<${participantName}/Rest/Share> . #don't forget to replace with the correct Share
		
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
		
<${participantName}/StartRestListener>
	a twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:notifySatisfiedOnly	
		true ; 
	twconn:startListener		
		true ; #Not required, default is false. Determine if this CR should start a listener.
	twconn:endpointType
		twd:${endpointType} ;
	twconn:shareTopicId		
		<${participantName}/RestTopic> ; #The shareTopicId used when sharing into Agent. It's also used as the key for a listener.  Don't forget to replace with the correct topic id.
	<#if filterGroup??>
	twconn:filterGroup
		<${filterGroup}> ; #An identifier for which group the filter belongs to
	</#if>
	<#if filterOrder??>
	twconn:filterOrder 
		${filterOrder} ; #An integer used to sort the filters in the same group; filters with lower number will be processed first
	</#if>
	twconn:restEndpointUrl	
		"${endpointUrl}" ; #don't forget to fill in the path for the endpoint
	twd:MultiGraph     
		true ;
	twd:ConsumeRequestJsonMapping                         
      	"""
      	${r"#{prefiximport}"}
      	
      	BASE <${baseUri}>
		SELECT ?collaborationId
      	WHERE 
      	{
      		?twd:Collaboration	twd:CollaborationIdentifier	?collaborationId .
      	}
		""".

<${participantName}/StopRestListener>
	a twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:notifySatisfiedOnly	
		true ;
	twd:NotifyOnTeardown	 	
		true ; #Not required, default is false. True if the participant should be notified before the consume request is torn down, which happens when the participant unregisters or is evicted. 
	twconn:stopListener		
		true ; #Not required, default is false. Determine if this CR should stop a listener.
	twconn:endpointType				
		twd:${endpointType} ;
	twconn:shareTopicId		
		<${participantName}/RestTopic> ;
	twconn:restEndpointUrl	
		"${endpointUrl}" ; #don't forget to fill in the path for the endpoint
	twd:MultiGraph    
		true;
	twd:ConsumeRequestJsonMapping
      	"""
      	${r"#{prefiximport}"}
      	
	 	BASE <${baseUri}>
	 	SELECT	?collaborationId ("" AS ?stopMessage)
	 	WHERE
	 	{
	 		twd:Collaboration	twd:CollaborationIdentifier	?collaborationId .
 		}
		""" .

<${participantName}/Rest/Share>
	a twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:ShareForBroadcastTopic
		<${participantName}/RestTopic> ; #don't forget to replace with the correct topic id
	twd:hasPredicate
		<ncEvent> ,
		<location> ,
		<room> ,
		<bed> ;
	twd:ShareJsonMapping
		"""
      	${r"#{prefiximport}"}
      	
		@base <${baseUri}> .
		<source>	<ncEvent>	<event/(($))(_guid)> .
		
		<event/(($))(_guid)>
			<location>	<event/(_guid)/location> .
			
		<event/(($))(_guid)/location>
			<room>		"(location.room)" ;
			<bed>		"(location.bed)" .
		""".