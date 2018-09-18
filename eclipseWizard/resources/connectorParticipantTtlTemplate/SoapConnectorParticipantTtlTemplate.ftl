<#-- test version -->
@base			<${baseUri}>	.

<#list prefixes as prefix>
@prefix ${prefix}	.
</#list>


<${participantName}>
	a	twd:WebServicesConnectorParticipant ;
	twd:Extends
		<http://thoughtwire.ca/ontology/2012/06/WebServicesConnector> ; 
	rdfs:label
		"${label}" ; #don't forget to fill in the label
	rdfs:comment
		"${comment}"	 ;
	twd:hasEnvResource
		<http://thoughtwire.ca/SystemResource> ; 
	twd:hasConsumeRequest
		<${participantName}/requestConsumeRequest> ; #don't forget to replace with the expected CR
	twd:hasShareId
		<${participantName}/requestConsumeRequest/Share> . #don't forget to replace with the expected Share.

<http://thoughtwire.ca/SystemResource>
    a twd:EnvResource ;
    twd:hasResourceProperty
        <resources/http> ,
        <resources/https> .
 
<resources/http>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpServer ; #Required. The URI of a server where the SOAP connector is located.
    twd:suggestedValue    <example> .
      
<resources/https>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpsServer ; #Required. The URI of a secure server where the SOAP connector is located. 
    twd:suggestedValue    <example> .

<${participantName}/requestConsumeRequest>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		"" ;
	rdfs:comment
		"" ;
	twconn:soapEndpointUrl
		"${endpointUrl}" ; #SOAP endpoint URL to which SOAP calls will be placed. 
	twconn:soapAction
		"${soapAction}" ; #Required. The SOAP action to send with SOAP requests
	twconn:soapBodyTemplate  #Required. SOAP request body template in Freemarker template syntax that maps context information to create the SOAP request body.
		"<echo:echoRequest xmlns:echo='http://www.springframework.org/spring-ws/samples/echo'></echo:echoRequest>" ;
	twconn:validateUser 
		false ;
	twd:allTriples 
		true ;
	twd:ConsumeRequestJsonMapping
        """
        ${r"#{prefiximport}"}
        
		BASE <${baseUri}>
		SELECT ("http://res.auto.twamb.ca/soap-service/echo/services" AS ?soapEndpointUrl) ?request
		WHERE 
		{
			<echoForUrlConfig/stringSharer> <echoForUrlConfig/stringSharer/hasString> ?request .
		}
		""" ;
	rdf:value
        """
        ${r"#{prefiximport}"}
        
		BASE <${baseUri}>
		CONSTRUCT 
		{
			<echoForUrlConfig/stringSharer> <echoForUrlConfig/stringSharer/hasString> ?request .
		}
		WHERE
		{
			<echoForUrlConfig/stringSharer> <echoForUrlConfig/stringSharer/hasString> ?request .
		}
		""" .
		
<${participantName}/requestConsumeRequest/Share>
	a	twd:Share ;
	rdfs:label
		" " ; 
	rdfs:comment 
		" " ;
	twvar:echo
		"//*[local-name()='echoResponse' and namespace-uri()='http://www.springframework.org/spring-ws/samples/echo']" ;
	twd:hasPredicate
		<${participantName}/echo> ;
	twd:ShareXmlMapping
		"""
		@base <http://thoughtwire.ca/acceptance/collaboration/webServicesConnector/echo/participant/> .
		<EchoService>	<EchoService/echo>	"((echo))" .
		""" .