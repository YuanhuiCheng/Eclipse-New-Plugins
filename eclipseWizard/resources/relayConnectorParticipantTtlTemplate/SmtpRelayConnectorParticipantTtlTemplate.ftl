<#-- test version -->
@base			<${baseUri}>	.

<#list prefixes as prefix>
@prefix ${prefix}	.
</#list>


<${participantName}>
	a	twd:RelayConnectorParticipant ;
	twd:Extends
		<>; #please fill in the extended URI
	rdfs:label
		"${label}" ; #don't forget to fill in the label
	rdfs:comment
		"${comment}"	 ;
	twd:hasEnvResource
		<http://thoughtwire.ca/SystemResource> ;
	twd:hasConsumeRequest
		<${participantName}/StartSmtpListener> , #don't forget to replace with the correct CR
		<${participantName}/StopSmtpListener> ;
	twd:hasShareId
		<${participantName}/Smtp/Share> . #don't forget to replace with the correct Share
		
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
		
<${participantName}/StartSmtpListener>
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
		twd:Smtp ;
	twconn:shareTopicId		
		<${participantName}/SmtpTopic> ; #The shareTopicId used when sharing into Agent. It's also used as the key for a listener.  Don't forget to replace with the correct topic id.
	<#if subjectPattern??>
	twconn:subjectPattern
		"${subjectPattern}" ;
	</#if>
	<#if subjectPatternTemplate??>
	twconn:subjectPatternTemplate
		"${subjectPatternTemplate}" ;
	</#if>
	<#if recipientPattern??>
	twconn:recipientPattern
		"${recipientPattern}" ;
	</#if>
	<#if recipientPatternTemplate??>
	twconn:recipientPatternTemplate
		"${recipientPatternTemplate}" ;
	</#if>
	<#if filterGroup??>
	twconn:filterGroup
		<${filterGroup}> ; #An identifier for which group the filter belongs to
	</#if>
	<#if filterOrder??>
	twconn:filterOrder 
		${filterOrder} ; #An integer used to sort the filters in the same group; filters with lower number will be processed first
	</#if>
	twconn:transformer      
		"regexTransformer" ;
 	twconn:parameters
    	"""
   		[
        	{"pattern":"(?s)[^\\r\\n]*[\\r\\n|\\n][^\\r\\n]*[\\r\\n|\\n]\\\\s*([^\\r\\n]*).*Item Fully Qualified Reference\\\\s*([^\\r\\n]*).*Item Category\\\\s*([^\\r\\n]*).*","fields":["alarm","reference","category"]}
    	]
   		""" ;
	twd:ConsumeRequestJsonMapping                         
      	"""
      	${r"#{prefiximport}"}
      	
      	BASE <${baseUri}>
		SELECT ?subject
        WHERE 
        {
            <Connector>       <startSmtpListenerWithSubjectTemplate>        ?subject .
        }

		""".

<${participantName}/StopSmtpListener>
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
		twd:Smtp ;
	twconn:shareTopicId		
		<${participantName}/SmtpTopic> ;
	twd:ConsumeRequestJsonMapping
      	"""
      	${r"#{prefiximport}"}
      	
	 	BASE <${baseUri}>
	 	SELECT ("foo" AS ?bar)
        WHERE 
        {
            <Connector>       <stopSmtpListener>        "true" .
        }
		""".

<${participantName}/Smtp/Share>
	a twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:ShareForBroadcastTopic
		 <${participantName}/SmtpTopic> ;
	twd:hasPredicate
		<eventType> ,
        <eventTimestamp> ,
        <eventFrom> ,
        <eventTo> ,
        <eventSubject> ,
        <eventAlarm> ,
        <eventCategory> ,
        <eventReference> ;
	twd:ShareJsonMapping
		"""
      	${r"#{prefiximport}"}
      	
		@base <${baseUri}> .
		<PushParticipant/Event>
            <eventType>       "FIXED" .
        <PushParticipant/Event(($))> 
            <eventTimestamp>  "(_time)" ;
            <eventFrom>       "(message.from)" ;
            <eventTo>         "(message.to)" ;
            <eventSubject>    "(message.subject)" ;
            <eventAlarm>      "(message.content.alarm)" ;
            <eventCategory>   "(message.content.category)" ;
            <eventReference>  "(message.content.reference)" .
		""".