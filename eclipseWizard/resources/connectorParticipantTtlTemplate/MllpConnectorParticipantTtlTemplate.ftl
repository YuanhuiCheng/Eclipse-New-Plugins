<#-- test version -->
@base			<${baseUri}>	.

<#list prefixes as prefix>
@prefix ${prefix}	.
</#list>


<${participantName}>
	a	twd:TransformConnectorParticipant ;
	twd:Extends
		<http://thoughtwire.ca/ontology/2012/06/TransformConnector> ; 
	rdfs:label
		"${label}" ; #don't forget to fill in the label
	rdfs:comment
		"${comment}"	 ;
	twd:hasEnvResource
		<http://thoughtwire.ca/SystemResource> ;
	twd:hasConsumeRequest
		<${participantName}/Trigger> ; #don't forget to replace with the expected CR
	twd:hasShareId
		<${participantName}/Trigger/Share> . #don't forget to replace with the expected Share
		
<http://thoughtwire.ca/SystemResource>
    a twd:EnvResource ;
    twd:hasResourceProperty
        <resources/http> ,
        <resources/https> .
 
<resources/http>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpServer ; #Required. The URI of a server where the Mllp connector is located.
    twd:suggestedValue    <example> .
      
<resources/https>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpsServer ; #Required. The URI of a secure server where the Mllp connector is located. 
    twd:suggestedValue    <example> .		
		
<${participantName}/Trigger> 
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twconn:host
		"${host}" ; #the host name of the MLLP server
	twconn:port
		"${port}" ; #the port to use to access the MLLP server
	twconn:timeout
		"${timeout}" ; #the number of milliseconds to wait for a response from the MLLP server
	twconn:retries
		"${retries}" ;	#the number of times to retry sending a failed message to the MLLP server
	twconn:messageTemplate
		"${messageTemplate}" ; #the template used to construct the message to send to the MLLP server
	twd:ConsumeRequestJsonMapping
		"""
		${r"#{prefiximport}"}
		
		SELECT ?value
        WHERE 
        {
            twd:Master	twd:selected	?content .
            
            ?content
                twd:column2    ?value .
        }
		""" .	

<${participantName}/Trigger/Share>
	a	twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		<result> ;
	twd:ShareJsonMapping
		"""
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .

		<SimpleMllpConnector>	<result>	"(($))((results))(status)" .
		""" .