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
		<#if transformer == "regexTransformer">
		<${participantName}/Name> ; #don't forget to replace with the expected CR
		<#elseif transformer == "dateTransformer">
		<${participantName}/Date> ;
		<#else>
		<${participantName}/Encrypt> ;
		</#if>
	twd:hasShareId
		<#if transformer == "regexTransformer">
		<${participantName}/Name/Share> . #don't forget to replace with the expected Share
		<#elseif transformer == "dateTransformer">
		<${participantName}/Date/Share> . #don't forget to replace with the expected Share
		<#else>
		<${participantName}/Encrypt/Share> . #don't forget to replace with the expected Share
		</#if>
		
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
		
<#if transformer == "regexTransformer">
<${participantName}/Name>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twconn:transformer
    	"regexTransformer" ;
    twconn:parameters #don't forget to replace with the parameters
      	"""
      	[
      	{ "var" : "id" },
     	{ "var" : "name", "pattern" : "^@([^@]*)@([^@]*)@([^@]*)@$", "fields" : [ "firstName", "middleName", "lastName" ]}
     	]
     	""" ;
	twd:ConsumeRequestJsonMapping
		"""
		${r"#{prefiximport}"}
		
		BASE <${baseUri}>
		SELECT DISTINCT ?id ?name
		WHERE
		{
			?s	
				<transformConnector/id> 			?id ;
				<transformConnector/encodedName> 	?name .
		}
		""" .

<${participantName}/Name/Share>
	a	twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		<transformConnector/firstName> ,
		<transformConnector/middleName> ,
		<transformConnector/lastName> ;
	twd:ShareJsonMapping
		"""
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .

		<transformConnector/connector/(($))((results))(id)>
			<transformConnector/firstName>	"(firstName)" ;
			<transformConnector/middleName>	"(middleName)" ;
			<transformConnector/lastName> 	"(lastName)" .
		""" .
<#elseif transformer == "dateTransformer">
<${participantName}/Date>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twconn:transformer
    	"dateTransformer" ;
    twconn:parameters #don't forget to replace with the parameters
      	"""
      	[
       	{ "var" : "id" },
     	{ "var" 		: "date",
     	  "field" 		: "birthdate",
	      "inputFormat" : "yyyy-MM-dd",
	      "outputFormat": "MMMM dd, yyyy"
     	}
     	]
     	""" ;
	twd:ConsumeRequestJsonMapping
		"""
		${r"#{prefiximport}"}
		
		BASE <${baseUri}>
		SELECT DISTINCT ?id ?date
		WHERE 
		{
			?s	
				<transformConnector/id> 			?id ;
				<transformConnector/inputBirthdate> ?date .
		}
		""" .

<${participantName}/Date/Share>
	a	twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		<transformConnector/birthdate> ;
	twd:ShareJsonMapping
		"""
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .

		<transformConnector/connector/(($))((results))(id)>
			<transformConnector/birthdate>	"(birthdate)" .
		""" .
<#else>
<${participantName}/Encrypt>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twconn:transformer
    	"userIdentityEncryptTransformer" ;
    twconn:parameters #don't forget to replace with the parameters
      	"" ;
	twd:ConsumeRequestJsonMapping
		"""
		${r"#{prefiximport}"}
		
		BASE <${baseUri}>
		CONSTRUCT 
		{
            twd:credentials
                twd:username        ?username ;
                twd:password        ?password ;
            .
        }
        WHERE 
        {        
            twd:credentials
                twd:username        ?username ;
                twd:password        ?password ;
            .
        }
		""" .

<${participantName}/Encrypt/Share>
	a	twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		rdf:value ;
	twd:ShareJsonMapping
		"""
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .

		twbuild:encryptedCredentials
			rdf:value	"(($))((results))(result)" .
		""" .
</#if>