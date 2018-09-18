<#-- test version -->
@base			<${baseUri}>	.

<#list prefixes as prefix>
@prefix ${prefix}	.
</#list>


<${participantName}>
	a	twd:RestConnectorParticipant ;
	twd:Extends
		<http://thoughtwire.ca/ontology/2012/06/RestConnector> ; 
	rdfs:label
		"${label}" ; #don't forget to fill in the label
	rdfs:comment
		"${comment}"	 ;
	twd:hasEnvResource
		<http://thoughtwire.ca/SystemResource> ;
	twd:hasConsumeRequest
		<${participantName}/GetResponse> ; #don't forget to replace with the expected CR
	twd:hasShareId
		<${participantName}/Share> . #don't forget to replace with the expected Share
		
<http://thoughtwire.ca/SystemResource>
    a twd:EnvResource ;
    twd:hasResourceProperty
        <resources/http> ,
        <resources/https> .
 
<resources/http>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpServer ; #Required. The URI of a server where the REST connector is located.
    twd:suggestedValue    <example> .
      
<resources/https>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpsServer ; #Required. The URI of a secure server where the REST connector is located. 
    twd:suggestedValue    <example> .		
		
<${participantName}/GetResponse>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:notifySatisfiedOnly
		true ;
	twconn:inputFormat
		<#if inputFormat??>
		${inputFormat} ; 
		</#if>  
	twconn:authenticationMethod
		twAuth:basicAuthentication ; #don't forget to replace with the expected authentication method such as twAuth:openSsoAuthentication, twAuth:basicAuthentication, or twAuth:jwtAuthentication
	twconn:restEndpointServer
		<#if endpointUrl??>
		<${endpointUrl}> ; #Server URL. 
		</#if> 
	twconn:restEndpointPath
		" " ; #don't forget to fill in the path for the endpoint
	twconn:httpMethod
		<#if httpMethod??>
		"${httpMethod}" ; 
		</#if>  
	twconn:requestType
		"${requestType}" ;
	twconn:responseCookies
		"""
    	[ "fakeCookie.*" ] #Provides the connector with an array of header names to look for in the response cookie. Only look at Headers contained within the response cookie.
    	""" ; 
	twconn:acceptResponseCodes
		"[302].*" ; #A Regex matching all HTTP status codes that the participant wants to handle. That is, matching status codes will not share errors into the state context; instead the status code will be available for sharing.
	twd:hasShareId
		<${participantName}/Share> ;
	twd:ConsumeRequestJsonMapping
		"""
		${r"#{prefiximport}"}
		
		BASE <${baseUri}>
		SELECT ?input
		WHERE 
		{
			<exampleSubject>   <trigger>   ?input .
		}
		""" ;
	rdf:value
		"""
		${r"#{prefiximport}"}
		
		BASE <${baseUri}>
		CONSTRUCT
		{
			<exampleSubject>   <trigger>   "true" .
		}
		WHERE
		{
			<exampleSubject>   <trigger>   "true" .
		}
		""" .	

<${participantName}/Share>
	a	twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		<code> ,
		<status> ,
		<details> ,
		<result> ;
	twd:ShareJsonMapping
		"""
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .

		<rest> <result> "(($))(results.Cookie)" .
		
		<restErrorHandling(($))((response))>
			<code>		"(code)" ;
			<status>	"(status)" ;
			<details>	"(details)" .
		""" .