<#-- test version -->
@base			<${baseUri}>	.

<#list prefixes as prefix>
@prefix ${prefix}	.
</#list>
@prefix twdata: <http://acceptance/KeyValueConnector/data#> .


<${participantName}>
	a	twd:KeyValueConnectorParticipant ;
	twd:Extends
		<http://thoughtwire.ca/ontology/2012/06/KeyValueConnector> ; 
	rdfs:label
		"${label}" ; #don't forget to fill in the label
	rdfs:comment
		"${comment}"	 ;
	twd:hasEnvResource
		<http://thoughtwire.ca/SystemResource> ;
	twd:hasConsumeRequest
		<#if commandType == "get">
			<#if batchTask == "false">
		<${participantName}/Get> ; #don't forget to replace with the expected CR
			<#else>
		<${participantName}/BatchGet> ; #don't forget to replace with the expected CR
			</#if>
		<#elseif commandType == "put">
			<#if batchTask == "false">
		<${participantName}/Put> ; #don't forget to replace with the expected CR
			<#else>
		<${participantName}/BatchPut> ; #don't forget to replace with the expected CR
			</#if>
		<#elseif commandType == "delete">
			<#if batchTask == "false">
		<${participantName}/Delete> ; #don't forget to replace with the expected CR
			<#else>
		<${participantName}/BatchDelete> ; #don't forget to replace with the expected CR
			</#if>
		<#elseif commandType == "keys">
		<${participantName}/Keys> ; #don't forget to replace with the expected CR
		<#elseif commandType == "values">
		<${participantName}/Values> ; #don't forget to replace with the expected CR
		</#if>
	twd:hasShareId
		<#if commandType == "get">
			<#if batchTask == "false">
		<${participantName}/Get/Share> . #don't forget to replace with the expected Share
			<#else>
		<${participantName}/BatchGet/Share> . #don't forget to replace with the expected Share
			</#if>
		<#elseif commandType == "put">
			<#if batchTask == "false">
		<${participantName}/Put/Share> . #don't forget to replace with the expected Share
			<#else>
		<${participantName}/BatchPut/Share> . #don't forget to replace with the expected Share
			</#if>
		<#elseif commandType == "delete">
			<#if batchTask == "false">
		<${participantName}/Delete/Share> . #don't forget to replace with the expected Share
			<#else>
		<${participantName}/BatchDelete/Share> . #don't forget to replace with the expected Share
			</#if>
		<#elseif commandType == "keys">
		<${participantName}/Keys/Share> . #don't forget to replace with the expected Share
		<#elseif commandType == "values">
		<${participantName}/Values/Share> . #don't forget to replace with the expected Share
		</#if>
		
<http://thoughtwire.ca/SystemResource>
    a twd:EnvResource ;
    twd:hasResourceProperty
        <resources/http> ,
        <resources/https> .
 
<resources/http>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpServer ; #Required. The URI of a server where the KeyValue connector is located.
    twd:suggestedValue    <example> .
      
<resources/https>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpsServer ; #Required. The URI of a secure server where the KeyValue connector is located. 
    twd:suggestedValue    <example> .		
		
<#if commandType == "get">	
	<#if batchTask == "false">
<${participantName}/Get>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twconn:command
        "get" ; #get a record or records from the KV store
    twconn:batchTask
        false; #Determines whether the operation performs an operation on each row of a submitted array or performs one operation on all submitted data
    twconn:keyTemplate
   		"${r"${DATA.key}"}"; #Not required. Freemarker template used to construct the key. Don't forget to replace with the key template.
   	twconn:bucketTemplate
        "${r"${DATA.bucket}"}" ; #Not required. Freemarker template used to construct the bucket identifier. Don't forget to replace with the bucket template.
	twd:ConsumeRequestJsonMapping
		"""
		${r"#{prefiximport}"}
		
		BASE <${baseUri}>
		SELECT ?bucket ?key
        WHERE 
        {
             <Sharing> 
                twdata:bucket	?bucket ;
                twdata:key 		?key .
        }
		""" .	

<${participantName}/Get/Share>
	a	twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		<response> ,
        twdata:value ,
        twdata:key ,
        twdata:bucket ;
	twd:ShareJsonMapping
		"""
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .

		<KeyValueConnectorGetResult> <response> <KeyValueConnectorGetResult/(($))(parameters[0].key)> .
        <KeyValueConnectorGetResult/(($))(parameters[0].key)>  
                twdata:bucket	"(parameters[0].bucket)" ;             
                twdata:key    	"(parameters[0].key)" ;
                twdata:value 	"(results[0].value)" .
		""" .
	<#else>
<${participantName}/BatchGet>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twconn:command
        "get" ; #get a record or records from the KV store
    twconn:batchTask
        true; #Determines whether the operation performs an operation on each row of a submitted array or performs one operation on all submitted data
    twconn:keyTemplate
   		"${r"${DATA.key}"}"; #Not required. Freemarker template used to construct the key. Don't forget to replace with the key template.
   	twconn:bucketTemplate
        "${r"${DATA.bucket}"}" ; #Not required. Freemarker template used to construct the bucket identifier. Don't forget to replace with the bucket template.
	twd:ConsumeRequestJsonMapping
		"""
		${r"#{prefiximport}"}
		
		BASE <${baseUri}>
		SELECT ?bucket ?key
        WHERE 
        {
             <SharingBatch>  
                twdata:bucket	?bucket ;
                twdata:key 		?key .
        }
		""" .	

<${participantName}/BatchGet/Share>
	a	twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		<response> ,
        twdata:value ,
        twdata:key ,
        twdata:bucket ;
	twd:ShareJsonMapping
		"""
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .

		<KeyValueConnectorGetResult> <response> <KeyValueConnectorGetResult/(($))(_sequence)> .
        <KeyValueConnectorGetResult/(($))(_sequence)>  
                twdata:bucket	"((parameters))(bucket)" ;             
                twdata:key    	"((parameters))(key)" ;
                twdata:value 	"((results))(value)" .
		""" .
	</#if>
<#elseif commandType == "put">
	<#if batchTask == "false">
<${participantName}/Put>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twconn:command
        "put" ; #put a record or records into the KV store
    twconn:batchTask
        false; #Determines whether the operation performs an operation on each row of a submitted array or performs one operation on all submitted data
    twconn:keyTemplate
   		"${r"${DATA.key}"}"; #Not required. Freemarker template used to construct the key. Don't forget to replace with the key template.
   	twconn:bucketTemplate
        "${r"${DATA.bucket}"}" ; #Not required. Freemarker template used to construct the bucket identifier. Don't forget to replace with the bucket template.
	twd:ConsumeRequestJsonMapping
		"""
		${r"#{prefiximport}"}
		
		BASE <${baseUri}>
		SELECT ?bucket ?key ?value
        WHERE 
        {
            <Sharing>    <Request>    ?request.
             ?request 
                 twdata:bucket		?bucket ;
                 twdata:key         ?key ;
                 twdata:value		?value .
        }
		""" .	

<${participantName}/Put/Share>
	a	twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		<response> ,
        twdata:bucket ,
        twdata:status ,
        twdata:key ;
	twd:ShareJsonMapping
		"""
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .

		<KeyValueConnectorPutResult> <response> <KeyValueConnectorPutResult/(($))(parameters[0].key)> .
        <KeyValueConnectorPutResult/(($))(parameters[0].key)>
            	twdata:bucket	"(parameters[0].bucket)" ;
            	twdata:key 		"(parameters[0].key)" ;
            	twdata:status	"(response.status)" .
		""" .
	<#else>
<${participantName}/BatchPut>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twconn:command
        "put" ; #put a record or records into the KV store
    twconn:batchTask
        true; #Determines whether the operation performs an operation on each row of a submitted array or performs one operation on all submitted data
    twconn:keyTemplate
   		"${r"${DATA.key}"}"; #Not required. Freemarker template used to construct the key. Don't forget to replace with the key template.
   	twconn:bucketTemplate
        "${r"${DATA.bucket}"}" ; #Not required. Freemarker template used to construct the bucket identifier. Don't forget to replace with the bucket template.
	twd:ConsumeRequestJsonMapping
		"""
		${r"#{prefiximport}"}
		
		BASE <${baseUri}>
		SELECT	?key ? bucket ?value
        WHERE 
        {
            <SharingBatch>	<Request	?request.
             ?request 
                 twdata:bucket		?bucket ;
                 twdata:key         ?key ;
                 twdata:value		?value .
        }
		""" .	

<${participantName}/BatchPut/Share>
	a	twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		<response> ,
        twdata:bucket ,
        twdata:key ,
        twdata:status ;
	twd:ShareJsonMapping
		"""
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .

		<KeyValueConnectorPutResult> <response> <KeyValueConnectorPutResult/(($))(parameters.key)> .
        <KeyValueConnectorPutResult/(($))(parameters.key)>
            twdata:bucket	"(parameters.bucket)" ;
            twdata:key 		"(parameters.key)" ;
           	twdata:status  	"(response.status)" .
		""" .
	</#if>
<#elseif commandType == "delete">
	<#if batchTask == "false">
<${participantName}/Delete>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twconn:command
        "delete" ; #put a record or records into the KV store
    twconn:batchTask
        false; #Determines whether the operation performs an operation on each row of a submitted array or performs one operation on all submitted data
    twconn:keyTemplate
   		"${r"${DATA.key}"}"; #Not required. Freemarker template used to construct the key. Don't forget to replace with the key template.
   	twconn:bucketTemplate
        "${r"${DATA.bucket}"}" ; #Not required. Freemarker template used to construct the bucket identifier. Don't forget to replace with the bucket template.
	twd:ConsumeRequestJsonMapping
		"""
		${r"#{prefiximport}"}
		
		BASE <${baseUri}>
		SELECT ?key ?bucket
		WHERE 
		{
			 <SharingDeleteBucket>
			    twdata:bucket	?bucket ;
			    twdata:key 		?key .
		}
		""" .	

<${participantName}/Delete/Share>
	a	twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		<response> ,
		twdata:value ,
		twdata:key ,
		twdata:bucket ;
	twd:ShareJsonMapping
		"""
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .

		<KeyValueConnectorDeleteResult> <response> <KeyValueConnectorDeleteResult/(($))(parameters[0].key)> .
		<KeyValueConnectorDeleteResult/(($))(parameters[0].key)>
		        twdata:bucket	"(parameters[0].bucket)" ;
				twdata:key		"(parameters[0].key)" ;
				twdata:value 	"(results[0].value)" .
		""" .
	<#else>
<${participantName}/BatchDelete>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twconn:command
        "delete" ; #put a record or records into the KV store
    twconn:batchTask
       	true; #Determines whether the operation performs an operation on each row of a submitted array or performs one operation on all submitted data
    twconn:keyTemplate
   		"${r"${DATA.key}"}"; #Not required. Freemarker template used to construct the key. Don't forget to replace with the key template.
   	twconn:bucketTemplate
        "${r"${DATA.bucket}"}" ; #Not required. Freemarker template used to construct the bucket identifier. Don't forget to replace with the bucket template.
	twd:ConsumeRequestJsonMapping
		"""
		${r"#{prefiximport}"}
		
		BASE <${baseUri}>
		SELECT ?bucket ?key
     	WHERE 
     	{
     		<SharingBatch>
            	twdata:bucket	?bucket ; 
            	twdata:key 		?key .
      }
		""" .	

<${participantName}/BatchDelete/Share>
	a	twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		<response> ,
      	twdata:value ,
      	twdata:key ,
      	twdata:bucket ;
	twd:ShareJsonMapping
		"""
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .
		
		<KeyValueConnectorDeleteResult> <response> <KeyValueConnectorDeleteResult/(($))(_sequence)> .
      	<KeyValueConnectorDeleteResult/(($))(_sequence)>
            twdata:bucket	"((parameters))(bucket)" ;
            twdata:key		"((parameters))(key)" ;
            twdata:value	"((results))(value)" .
		""" .
	</#if>
<#elseif commandType == "keys">
<${participantName}/Keys>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twconn:command
        "keys" ; #put a record or records into the KV store
    twconn:batchTask
        ${batchTask}; #Determines whether the operation performs an operation on each row of a submitted array or performs one operation on all submitted data
    twconn:keyTemplate
   		"${r"${DATA.key}"}"; #Not required. Freemarker template used to construct the key. Don't forget to replace with the key template.
   	twconn:bucketTemplate
        "${r"${DATA.bucket}"}" ; #Not required. Freemarker template used to construct the bucket identifier. Don't forget to replace with the bucket template.
	twd:ConsumeRequestJsonMapping
		"""
		${r"#{prefiximport}"}
		
		BASE <${baseUri}>
		SELECT ?bucket
		WHERE 
		{
			 <SharingKeys>	twdata:bucket	?bucket .
		}
		""" .	

<${participantName}/Keys/Share>
	a	twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		<response> ,
		twdata:value ,
		twdata:key ;
	twd:ShareJsonMapping
		"""
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .

		<KeyValueConnectorGetResult>   <response>	<KeyValueConnectorGetResult/(($))(parameters[0].key)> .
		<KeyValueConnectorGetResult/(($))(parameters[0].key)>
				twdata:key		"(parameters[0].key)" ;
				twdata:value 	"(results[0].value)" .
		""" .
<#elseif commandType == "values">
<${participantName}/Values>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twconn:command
        "keys" ; #put a record or records into the KV store
    twconn:batchTask
        ${batchTask}; #Determines whether the operation performs an operation on each row of a submitted array or performs one operation on all submitted data
    twconn:keyTemplate
   		"${r"${DATA.key}"}"; #Not required. Freemarker template used to construct the key. Don't forget to replace with the key template.
   	twconn:bucketTemplate
        "${r"${DATA.bucket}"}" ; #Not required. Freemarker template used to construct the bucket identifier. Don't forget to replace with the bucket template.
	twd:ConsumeRequestJsonMapping
		"""
		${r"#{prefiximport}"}
		
		BASE <${baseUri}>
		SELECT ?bucket
		WHERE 
		{
			 <SharingValues>	twdata:bucket	 ?bucket .
		}
		""" .	

<${participantName}/Values/Share>
	a	twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		<response> ,
		twdata:value ,
		twdata:key ;
	twd:ShareJsonMapping
		"""
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .

		<KeyValueConnectorGetResult>	<response>	<KeyValueConnectorGetResult/(($))(parameters[0].key)> .
		<KeyValueConnectorGetResult/(($))(parameters[0].key)>
				twdata:key		"(parameters[0].key)" ;
				twdata:value 	"(results[0].value)" .
</#if>