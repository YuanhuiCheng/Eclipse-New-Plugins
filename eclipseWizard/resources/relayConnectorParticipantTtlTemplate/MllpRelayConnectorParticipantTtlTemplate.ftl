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
		<${participantName}/StartMllpListener> , #don't forget to replace with the correct CR
		<${participantName}/StopMllpListener> ;
	twd:hasShareId
		<${participantName}/Mllp/Share> . #don't forget to replace with the correct Share
		
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
		
<${participantName}/StartMllpListener>
	a twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:notifySatisfiedOnly	
		true ; 
	twconn:startListener		
		true ; 
	twconn:allowedMessageType
		"${allowedMessageType}" ; #The message type being filtered. ADT/OMG support.
	twconn:endpointType		
		twd:Mllp ;
	twconn:shareTopicId		
		<${participantName}/MllpTopic> ; #The shareTopicId used when sharing into Agent. It's also used as the key for a listener.  Don't forget to replace with the correct topic id.
	<#if filterGroup??>
	twconn:filterGroup
		<${filterGroup}> ; #An identifier for which group the filter belongs to
	</#if>
	<#if filterOrder??>
	twconn:filterOrder 
		${filterOrder} ; #An integer used to sort the filters in the same group; filters with lower number will be processed first
	</#if>
	twd:MultiGraph     
		true ;
	twd:ConsumeRequestJsonMapping                         
      	"""
      	${r"#{prefiximport}"}
      	
      	BASE <${baseUri}>
		SELECT	("Started" AS ?status)          
        WHERE 
        {
        	<http://acceptance/Relay/MllpRelay>
                    twd:status	"Started" .
        }
		""".

<${participantName}/StopMllpListener>
	a twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:notifySatisfiedOnly	
		true ;
	twd:NotifyOnTeardown	 	
		true ; #Default is false. True if the participant should be notified before the consume request is torn down, which happens when the participant unregisters or is evicted. 
	twconn:stopListener		
		true ;
	twconn:endpointType		
		twd:Mllp ;
	twconn:shareTopicId		
		<${participantName}/MllpTopic> ; #don't forget to replace with the correct topic id
	twd:MultiGraph    
		true;
	twd:ConsumeRequestJsonMapping
      	"""
      	${r"#{prefiximport}"}
      	
	 	BASE <${baseUri}>
	 	SELECT	("Stopped" AS ?status)          
        WHERE 
        {
        	<http://acceptance/Relay/MllpRelay>
                    twd:status	"Stopped" .
        }
		""".

<${participantName}/Mllp/Share>
	a twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:ShareForBroadcastTopic
		 <${participantName}/MllpTopic> ; #don't forget to replace with the correct topic id
	twd:hasPredicate
		<firstName> ,
        <lastName> ,
        <room> ;
	twd:ShareJsonMapping
		"""
      	${r"#{prefiximport}"}
      	
		@base <${baseUri}> .
		<ADT_A01/event(($))>
			<firstName>	"(patientName[0].firstName)" ;
			<lastName>	"(patientName[0].lastName)" ;
			<room>     	"(room)" .
		""".