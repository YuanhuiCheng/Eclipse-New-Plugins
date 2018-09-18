<#-- test version -->
@base			<${baseUri}>	.

<#list prefixes as prefix>
@prefix ${prefix}	.
</#list>
<#if commandType??>
	<#if commandType == "build">
@prefix builder:<http://thoughtwire.ca/ontology/2012/01/studio/builder#>
	<#elseif commandType == "get" || commandType == "list">
@prefix film:   <http://acceptance/film#> .
	<#elseif commandType == "describe">
@prefix describe:   <http://thoughtwire.ca/acceptance/describe#> .
	</#if>
<#elseif mimeContentType == "application/vnd.ms-excel">
@prefix v:      <http://www.w3.org/2006/vcard/ns#> .
<#elseif mimeContentType == "text/plain">
@prefix film:   <http://acceptance/film#> .
</#if>


<${participantName}>
	a	twd:ContentConnector ;
	twd:Extends
		<http://thoughtwire.ca/ontology/2012/02/ContentConnector> ; 
	rdfs:label
		"${label}" ; #don't forget to fill in the label
	rdfs:comment
		"${comment}"	 ;
	twd:hasEnvResource
		<http://thoughtwire.ca/SystemResource> ;
	twconn:downloadable
		 ${ifDownloadable} ;
	twd:hasConsumeRequest
		<#if commandType?? || mimeContentType == "application/vnd.ms-excel">
		<${participantName}/Load> ; #don't forget to replace with the expected CR
		<#else>
		<${participantName}/LoadLines> ,
		<${participantName}/LoadFile> ;
		</#if>
	twd:hasShareId
		<#if commandType?? || mimeContentType == "application/vnd.ms-excel">
		<${participantName}/Load/Share> . #don't forget to replace with the expected Share.
		<#else>
		<${participantName}/LoadLines/Share> ,
		<${participantName}/LoadFile/Share> .
		</#if>

<http://thoughtwire.ca/SystemResource>
    a twd:EnvResource ;
    twd:hasResourceProperty
        <resources/http> ,
        <resources/https> .
 
<resources/http>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpServer ; #Required. The URI of a server where the content connector is located.
    twd:suggestedValue    <example> .
      
<resources/https>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpsServer ; #Required. The URI of a secure server where the content connector is located. 
    twd:suggestedValue    <example> .

<#if commandType??>
	<#if commandType == "get" || commandType == "list">
<${participantName}/Load>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		"" ;
	rdfs:comment
		"" ;
 	twconn:command 
    	"${commandType}"; #The command to be performed
	twconn:parameters
      	"""
      	[
      	{ "var" : "title" },
      	{ "var" : "year", "type" : "http://www.w3.org/2001/XMLSchema#int" },
      	{ "var" : "actor1" },
      	{ "var" : "actor2" },
      	{ "var" : "actor3" },
      	{ "var" : "actor4" }
      	]
      	""" ;
    twconn:mimeContentType 
    	"${mimeContentType}" ; #The mime type of the content being retrieved via a get command
    	<#if firstRowIndex??>
	twconn:firstRowIndex #don't forget to change these indexes
        ${firstRowIndex} ; 
   		</#if>
    	<#if lastRowIndex??>
	twconn:lastRowIndex
        ${lastRowIndex};
   		</#if>
	    <#if mimeContentType == "application/vnd.ms-excel">
	    	<#if firstSheetIndex??>
	twconn:firstSheetIndex
        ${firstSheetIndex};
        	</#if>
    		<#if lastSheetIndex??>
	twconn:lastSheetIndex
        ${lastSheetIndex} ;
			</#if>
	    </#if>
    twconn:downloadFile 
        ${ifDownloadFile} ; #Inidicates that the CR is used to download a dynamically-created-file
    twconn:path 
    	"${path}" ; #The path to a file (to be read) or a directory (from which files are to be listed or retrieved)
    twconn:dynamicFileId 
   		"${dynamicFileId}" ; #The JSON object ID that will contain the file to operate on
   	twconn:dynamicFileMask 
   		"${dynamicFileMask}" ; #A REGEX expression that the dynamic file must match
    twconn:validateUser
        false ;
	twd:allTriples
		true ;
	twd:ConsumeRequestJsonMapping
        """
        ${r"#{prefiximport}"}
        
		BASE <${baseUri}>
		SELECT ?file
		WHERE 
		{
			?subject <GetFile/Valid> ?file .
		}
		""" .
		
<${participantName}/Load/Share>
	a	twd:Share ;
	rdfs:comment
		"" ;
	rdfs:label
		"" ;
	twd:hasPredicate
		film:films ,
		film:title ,
		film:year ,
		film:actor1 ,
		film:actor2 ,
		film:actor3 ,
		film:actor4 ;
	twd:ShareJsonMapping
        """
        ${r"#{prefiximport}"}
        
		BASE <${baseUri}>
        <http://thoughtwire.ca/acceptance/loader>
			<http://thoughtwire.ca/acceptance/film#films>
			<http://thoughtwire.ca/acceptance/film#Bag> .
		<http://thoughtwire.ca/acceptance/film#Bag>
			<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>
			<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> .
		<http://thoughtwire.ca/acceptance/film#Bag>
			<http://www.w3.org/1999/02/22-rdf-syntax-ns#_(($))((results))(_sequence)>
			<http://thoughtwire.ca/acceptance/film#(_sequence)> .
		<http://thoughtwire.ca/acceptance/film#(($))((results))(_sequence)>
		    <http://thoughtwire.ca/acceptance/film#title> 		"(title)" ;
		    <http://thoughtwire.ca/acceptance/film#year> 		"(year)" ;
		    <http://thoughtwire.ca/acceptance/film#actor1> 		"(actor1)" ;
		    <http://thoughtwire.ca/acceptance/film#actor2> 		"(actor2)" ;
		    <http://thoughtwire.ca/acceptance/film#actor3> 		"(actor3)" ;
		    <http://thoughtwire.ca/acceptance/film#actor4> 		"(actor4)" .
        """ .	
	<#elseif commandType == "describe">
<${participantName}/Load>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		"" ;
	rdfs:comment
		"" ;
	twconn:metadataCommand
    	"${commandType}";
    twconn:mimeContentType
    	"${mimeContentType}" ; #The mime type of the content being retrieved via a get command
    twconn:downloadFile 
        ${ifDownloadFile} ; #Inidicates that the CR is used to download a dynamically-created-file
    twconn:path 
    	"${path}" ; #The path to a file (to be read) or a directory (from which files are to be listed or retrieved)
    twconn:dynamicFileId 
   		"${dynamicFileId}" ; #The JSON object ID that will contain the file to operate on
   	twconn:dynamicFileMask 
   		"${dynamicFileMask}" ; #A REGEX expression that the dynamic file must match
	    	<#if firstRowIndex??>
	twconn:firstRowIndex #don't forget to change these indexes
        ${firstRowIndex} ; 
   			</#if>
    		<#if lastRowIndex??>
	twconn:lastRowIndex
        ${lastRowIndex};
   			</#if>
	    <#if mimeContentType == "application/vnd.ms-excel">
    		<#if firstSheetIndex??>
	twconn:firstSheetIndex
        ${firstSheetIndex};
    		</#if>
    		<#if lastSheetIndex??>
	twconn:lastSheetIndex
        ${lastSheetIndex} ;
			</#if>
	    </#if>
    twconn:validateUser
        false ;
    twd:notifySatisfiedOnly     
    	true ;
    twd:ConsumeRequestJsonMapping
        """
		${r"#{prefiximport}"}
        
		BASE <${baseUri}>
		SELECT ?serviceHost ?serviceUsername ?servicePassword
		WHERE 
		{
			?subject 
					<DescribeFile/Host>		?serviceHost ;
					<DescribeFile/User>		?serviceUsername ;
					<DescribeFile/Pass>		?servicePassword .
		}
		""" .
		
<${participantName}/Load/Share>
	a	twd:Share ;
	rdfs:comment
		"" ;
	rdfs:label
		"" ;
	twd:hasPredicate
		describe:fileName ,
		describe:numberOfSheets ,
		describe:sheets ,
		describe:numberOfColumns ,
		describe:numberOfRows ,
		describe:startingRow ,
		describe:startingColumn ,
		describe:sheetNumber ,
		describe:sheetName ,
		describe:field ,
		describe:fieldName ,
		describe:dataType ;
	twd:ShareJsonMapping
        """
        ${r"#{prefiximport}"}
        
		BASE <${baseUri}>
        <http://thoughtwire.ca/acceptance/describe#describer(($))((results))>
        	<http://thoughtwire.ca/acceptance/describe#fileName> "(fileName)" ;
        	<http://thoughtwire.ca/acceptance/describe#numberOfSheets> "(numberOfSheets)" .
        	
		<http://thoughtwire.ca/acceptance/describe#describer>
			<http://thoughtwire.ca/acceptance/describe#sheets>	<http://thoughtwire.ca/acceptance/describe#Bag> .
		<http://thoughtwire.ca/acceptance/describe#Bag>
			<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#Bag> .
		<http://thoughtwire.ca/acceptance/describe#Bag>
			<http://www.w3.org/1999/02/22-rdf-syntax-ns#_(($))((results))(_sequence)>	<http://thoughtwire.ca/acceptance/describe#(_sequence)> .
			
		<http://thoughtwire.ca/acceptance/describe#(($))((results))(_sequence)>
		    <http://thoughtwire.ca/acceptance/describe#sheetName> 		"(sheetName)" ;
		    <http://thoughtwire.ca/acceptance/describe#sheetNumber> 	"(sheetNumber)" ;
		    <http://thoughtwire.ca/acceptance/describe#numberOfRows> 	"(numberOfRows)" ;
		    <http://thoughtwire.ca/acceptance/describe#numberOfColumns> "(numberOfColumns)" ;
		    <http://thoughtwire.ca/acceptance/describe#startingRow> 	"(startingRow)" ;
		    <http://thoughtwire.ca/acceptance/describe#startingColumn> 	"(startingColumn)" ;
		    <http://thoughtwire.ca/acceptance/describe#field> <http://thoughtwire.ca/acceptance/describe#(_sequence)/((fields))(fieldNumber)> .
	
		<http://thoughtwire.ca/acceptance/describe#(($))((results))(_sequence)/((fields))(fieldNumber)>
			<http://thoughtwire.ca/acceptance/describe#fieldName> "(fieldName)" ;
			<http://thoughtwire.ca/acceptance/describe#dataType> "(dataType)" .
        """ .
	<#elseif commandType == "build">
<${participantName}/Load>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		"" ;
	rdfs:comment
		"" ;
	twconn:metadataCommand
    	"${commandType}";
	    <#if firstRowIndex??>
	twconn:firstRowIndex #don't forget to change these indexes
        ${firstRowIndex} ; 
   		</#if>
    	<#if lastRowIndex??>
	twconn:lastRowIndex
        ${lastRowIndex};
   		</#if>
	    <#if mimeContentType == "application/vnd.ms-excel">
	    	<#if firstSheetIndex??>
	twconn:firstSheetIndex
        ${firstSheetIndex};
    		</#if>
    		<#if lastSheetIndex??>
	twconn:lastSheetIndex
        ${lastSheetIndex} ;
			</#if>
	    </#if>
    rdf:value
        """
		${r"#{prefiximport}"}
        
		BASE <${baseUri}>
		PREFIX builder: <http://thoughtwire.ca/ontology/2012/01/studio/builder#>
		CONSTRUCT 
		{
			?subject 	builder:hasBaseUri 				?baseUri .
			?subject 	builder:hasParticipantName 		?participantName .
			?subject 	builder:hasPrefix	 			?prefixName .
			?subject 	builder:hasPrefixValue	 		?prefixValue .
			?subject 	builder:hasStartingRow	 		?startingRow .
			?subject 	builder:hasStartingColumn	 	?startingColumn .
			?subject 	builder:hasStartingSheet	 	?startingSheet .
			?subject 	builder:hasFileName	 			?fileName .
			
			?subject2	builder:fieldName				?fieldName .
			?subject2	builder:dataType				?dataType .
			?subject2	builder:fieldNumber				?fieldNumber .
		}
		WHERE {
			?subject 	builder:hasBaseUri 			?baseUri .
			?subject 	builder:hasParticipantName 	?participantName .
			?subject 	builder:hasPrefix 			?prefixName .
			?subject 	builder:hasPrefixValue 		?prefixValue .
			?subject 	builder:hasStartingRow		?startingRow .
			?subject 	builder:hasStartingColumn	?startingColumn .
			?subject 	builder:hasStartingSheet	?startingSheet .
			?subject 	builder:hasFileName	 		?fileName .
			
			?subject2	builder:fieldName				?fieldName .
			?subject2	builder:dataType				?dataType .
			?subject2	builder:fieldNumber				?fieldNumber .
		}
		""" .
		
<${participantName}/Load/Share>
	a	twd:Share ;
	rdfs:comment
		"" ;
	rdfs:label
		"" ;
	twd:hasPredicate
		builder:config ,
		builder:cr ,
		builder:form ;
	twd:ShareJsonMapping
        """
        ${r"#{prefiximport}"}
      	
		@base <${baseUri}> .
		<(($))((results))>
			builder:config		"(content)" ;
			builder:cr 			"(cr)" ;
			builder:form  		"(form)" .
        """ .
	</#if>
<#else>
	<#if mimeContentType == "application/vnd.ms-excel">
<${participantName}/Load>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		"" ;
	rdfs:comment
		"" ;	
	twconn:validateUser
        false ;
    twconn:mimeContentType
        "${mimeContentType}" ;
	twconn:path
		"${path}" ; #The path to a file (to be read) or a directory (from which files are to be listed or retrieved)
		<#if firstRowIndex??>
	twconn:firstRowIndex #don't forget to change these indexes
        ${firstRowIndex} ;
    	</#if>
    	<#if lastRowIndex??>
	twconn:lastRowIndex
        ${lastRowIndex};
    	</#if>
    	<#if firstSheetIndex??>
	twconn:firstSheetIndex
        ${firstSheetIndex};
    	</#if>
   	    <#if lastSheetIndex??>
	twconn:lastSheetIndex
        ${lastSheetIndex} ;
		</#if>
	twconn:parameters
      	"""
      	[
      	{ "var" : "siteId", "type" : "http://www.w3.org/2001/XMLSchema#int"},
     	{ "var" : "sshId", "mandatory" : true},
    	{ "var" : "name", "mandatory" : true},
    	{ "var" : "city"},
    	{ "var" : "address"},
    	{ "var" : "postalCode", "mandatory" : true},
   		{ "var" : "membership"},
   		{ "var" : "contactId"},
   		{ "var" : "contactName"},
    	{ "var" : "phone"},
    	{ "var" : "email"},
   		{ "var" : "contactType"},
   		{ "var" : "lat"},
  		{ "var" : "long"}
		]
      	""" ;
    twd:allTriples 
    	true ;  	
	twd:ConsumeRequestJsonMapping
        """
		${r"#{prefiximport}"}
        
		BASE <${baseUri}>
		SELECT ?object
		WHERE 
		{
        	?subject	<Load>		?object .
		}
		""".
		
<${participantName}/Load/Share>
	a	twd:Share ;
	rdfs:comment
		"" ;
	rdfs:label
		"" ;
	twd:hasPredicate
		v:key ,
		v:organization-name ,
		v:latitude ,
		v:longitude ,
		v:locality ,
		v:street-address ,
		v:tel ,
		v:fn ,
		v:email ,
		v:postal-code ;
	twd:ShareJsonMapping
        """
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .
		<loc#(($))((results))(sshId)>
			v:key	"(sshId)" ;
 			v:fn	    "(contactName)" ;
 			v:email	"(email)" ;
			v:organization-name	"(name)" ;
 			v:street-address "(address)" ;
 			v:locality "(city)" ;
 			v:postal-code "(postalCode)" ;
 			v:latitude	"(lat)" ;
 			v:longitude	"(long)" ;
			v:tel 	"(phone)" .
        """ .    
	<#elseif mimeContentType == "text/plain">
<${participantName}/LoadLines>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		"" ;
	rdfs:comment
		"" ;	
	twconn:validateUser
        false ;
    twconn:mimeContentType
        "${mimeContentType}" ;
	twconn:path
		"${path}" ; #The path to a file (to be read) or a directory (from which files are to be listed or retrieved)
		<#if firstRowIndex??>
	twconn:firstRowIndex #don't forget to change these indexes
        ${firstRowIndex} ; 
   		</#if>
    	<#if lastRowIndex??>
	twconn:lastRowIndex
        ${lastRowIndex};
   		</#if>
	twd:ConsumeRequestJsonMapping
        """
		${r"#{prefiximport}"}
        
		BASE <${baseUri}>
		SELECT ?id
		WHERE 
		{
			?subject	<LoadLines/Valid>	?id .
		}
		""".
		
<${participantName}/LoadFile>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		"" ;
	rdfs:comment
		"" ;	
	twconn:validateUser
        false ;
    twconn:mimeContentType
        "${mimeContentType}" ;
	twconn:path
		"${path}" ; #The path to a file (to be read) or a directory (from which files are to be listed or retrieved)
	twconn:shareEntireFile
		${ifShareEntireFile} ;
	twd:ConsumeRequestJsonMapping
        """
		${r"#{prefiximport}"}
        
		BASE <${baseUri}>
		SELECT ?id
		WHERE 
		{
			?subject	<LoadFile/Valid>	?id .
		}
		""" .

<${participantName}/LoadLines/Share>
	a	twd:Share ;
	rdfs:comment
		"" ;
	rdfs:label
		"" ;
	twd:hasPredicate
		film:title ;
	twd:ShareJsonMapping
        """
        ${r"#{prefiximport}"}
      	
		@base <${baseUri}> .
		<http://acceptance/film#(($))(_sequence)>
		    <http://acceptance/film#title>	"((results))(line)" .
        """ .
        
<${participantName}/LoadFile/Share>
	a	twd:Share ;
	rdfs:label
		"" ;
	rdfs:comment
		"" ;
	twd:hasPredicate
		film:all ;
	twd:ShareJsonMapping
        """
        ${r"#{prefiximport}"}
      	
		@base <${baseUri}> .
		<http://acceptance/film>
		    <http://acceptance/film#all>	"(($))((results))(file)" .
        """ .
	</#if>
</#if>	