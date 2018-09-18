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
		<${participantName}/StartBacnetListener> , #don't forget to replace with the correct CR
		<${participantName}/StopBacnetListener> ;
	twd:hasShareId
		<${participantName}/Bacnet/Share> . #don't forget to replace with the correct Share
		
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
		
<${participantName}/StartBacnetListener>
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
		twd:Bacnet ;
	twconn:shareTopicId		
		<${participantName}/BacnetTopic> ; #The shareTopicId used when sharing into Agent. It's also used as the key for a listener.  Don't forget to replace with the correct topic id.
	<#if filterGroup??>
	twconn:filterGroup
		<${filterGroup}> ; #An identifier for which group the filter belongs to
	</#if>
	<#if filterOrder??>
	twconn:filterOrder 
		${filterOrder} ; #An integer used to sort the filters in the same group; filters with lower number will be processed first
	</#if>
	twconn:bacnetSubnet
		"${bacnetSubnet}" ;
	twd:ConsumeRequestJsonMapping                         
      	"""
      	${r"#{prefiximport}"}
      	
      	BASE <${baseUri}>
		SELECT	?clusterUri ?twProperty ?readObjectType ?readObjectNumber ?readObjectProperty ?interval
      	WHERE
      	 {
      		<Connector>		<startBacnetListener>		"true" .
      		
			<Connector>		<request>	?request .
			
			?request
				<propertyId>			?twProperty ;
				<clusterUri>			?clusterUri ;
				<interval>				?interval;				
				<readObjectType>		?readObjectType ;
				<readObjectNumber>		?readObjectNumber ;
				<readObjectProperty>	?readObjectProperty .
      	}
		""".

<${participantName}/StopBacnetListener>
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
		twd:Bacnet ;
	twconn:shareTopicId		
		<${participantName}/BacnetTopic> ; #don't forget to replace with the correct topic id
	twd:ConsumeRequestJsonMapping
      	"""
      	${r"#{prefiximport}"}
      	
	 	BASE <${baseUri}>
	 	SELECT	("foo" AS ?bar)
      	WHERE 
      	{
      		<Connector>		<stopBacnetListener>	"true" .
      	}
		""".

<${participantName}/Bacnet/Share>
	a twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:ShareForBroadcastTopic
		 <${participantName}/BacnetTopic> ; #don't forget to replace with the correct topic id
	twd:hasPredicate
		<payload> ,
		<cluster> ,
		<property> ,
		<value> ;
	twd:ShareJsonMapping
		"""
      	${r"#{prefiximport}"}
      	
		@base <${baseUri}> .
		<Connector>		<payload>	  <payload/(($))((payload))(_sequence)> .
		<payload/(($))((payload))(_sequence)>
				<cluster>	"(cluster)" ;
				<property>	"(property)" ;
				<value>		"(value)" .
		""".