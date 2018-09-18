<#-- test version -->
@base			<${baseUri}>	.

<#list prefixes as prefix>
@prefix ${prefix}	.
</#list>


<${participantName}>
	a	twd:DbConnectorParticipant ;
	twd:Extends
		<http://thoughtwire.ca/ontology/2012/02/DatabaseConnector> ; 
	rdfs:label
		"${label}" ; #don't forget to fill in the label
	rdfs:comment
		"${comment}"	 ;
	twd:hasEnvResource
		<http://thoughtwire.ca/SystemResource> ; 
	twd:hasConsumeRequest
		<${participantName}/Date> , #don't forget to replace with the expected CR
		<${participantName}/Id> ,
		<${participantName}/All>;
	twd:hasShareId
		<${participantName}/Error> . #don't forget to replace with the expected Share.

<http://thoughtwire.ca/SystemResource>
    a twd:EnvResource ;
    twd:hasResourceProperty
        <DatabaseConnector/resources/http> ,
        <DatabaseConnector/resources/https> .
 
<DatabaseConnector/resources/http>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpServer ; #Required. The URI of a server where the database connector is located.
    twd:suggestedValue    <example> .
      
<DatabaseConnector/resources/https>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpsServer ; #Required. The URI of a secure server where the database connector is located. 
    twd:suggestedValue    <example> .

<${participantName}/Date>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		"" ;
	rdfs:comment
		"" ;
	twconn:datasource
		<#if dataSource??>
		"${dataSource}" ; #The name of the data source. This must match the name of a resource in the web application
		</#if> 
	twconn:validateUser
        false ;
	twconn:${sqlOperation}
        "" ; #please declare the specific query operation
	twconn:parameters #a JSON string encoding a set of parameters
      	"""
      	{ 
      		"id" : "http://www.w3.org/2001/XMLSchema#int",
      	 	"date" : "http://www.w3.org/2001/XMLSchema#date"
      	}
      	""" ;
	twd:allTriples 
		true ;
	twd:hasShareId 
		<${participantName}/Error> ;
	twd:ConsumeRequestJsonMapping
        """
        ${r"#{prefiximport}"}
        
		BASE <${baseUri}>
		SELECT ?id ?date
		WHERE {
			<Id> <Id> ?id .
			<Date> <Date> ?date .
		}
		""" ;
	rdf:value
        """
        ${r"#{prefiximport}"}
        
		BASE <${baseUri}>
		CONSTRUCT {
			<Id> <Id> ?id .
			<Date> <Date> ?date .
		}
		WHERE {
			<QueryResult> <Id> ?id .
			<QueryResult> <Date> ?date .
		}
		""" .
		
<${participantName}/Id>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		"" ;
	rdfs:comment
		"" ;
	twconn:datasource
		<#if dataSource??>
		"${dataSource}" ; 
		</#if> 
	twconn:validateUser
        "false" ;
	twconn:${sqlOperation}
        "" ;
	twconn:parameters
      	"""
      	{ "id" : "http://www.w3.org/2001/XMLSchema#int" }
      	""" ;
	twd:allTriples 
		true ;
	twd:hasShareId 
		<${participantName}/Error> ;
	twd:ConsumeRequestJsonMapping
        """
        ${r"#{prefiximport}"}
        
        BASE <${baseUri}>
		SELECT ?id
		WHERE {
			 <IdOnly> <Id> ?id 
		}
		""" ;
	rdf:value
        """
        ${r"#{prefiximport}"}
        
        BASE <${baseUri}>
		CONSTRUCT {
			 <IdOnly> <Id> ?id 
		}
		WHERE {
			 <IdOnly> <Id> ?id 
		}
		""" .
		
<${participantName}/All>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		"" ;
	rdfs:comment
		"" ;
	twconn:datasource
		<#if dataSource??>
		"${dataSource}" ; 
		</#if> 
	twconn:validateUser
        "false" ;
	twconn:${sqlOperation}
        "" ;
	twconn:parameters
      	"""
      	{ 
      		"id" : "http://www.w3.org/2001/XMLSchema#int",
      	 	"date" : "http://www.w3.org/2001/XMLSchema#date"
      	}
      	""" ;
	twd:allTriples 
		true ;
	twd:hasShareId 
		<${participantName}/Error> ;
	twd:ConsumeRequestJsonMapping
        """
        ${r"#{prefiximport}"}
        
		BASE <${baseUri}>
		SELECT ?id ?date
		WHERE {
			<All> <Id> ?id .
			<All> <Date> ?date .
		}
		""" ;
	rdf:value
        """
        ${r"#{prefiximport}"}
        
        BASE <${baseUri}>
		CONSTRUCT {
			<All> <Id> ?id .
			<All> <Date> ?date .
		}
		WHERE {
			<All> <Id> ?id .
			<All> <Date> ?date .
		}
		""" .

<${participantName}/Error>
	a	twd:Share ;
	rdfs:comment
		"" ;
	rdfs:label
		"" ;
	twd:hasPredicate
		twd:Code ,
		twd:Status ;
	twd:ShareJsonMapping
        """
        ${r"#{prefiximport}"}
        
        @base <${baseUri}> .
		@prefix error: <http://acceptance/DatabaseConnector/Error/> .
		
		<error>		twd:Code		"(($))(response.code)" .
		<error>		twd:Status	"(($))(response.status)" .
        """ .