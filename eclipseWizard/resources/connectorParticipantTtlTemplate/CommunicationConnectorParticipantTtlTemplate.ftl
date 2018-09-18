<#-- test version -->
@base			<${baseUri}>	.

<#list prefixes as prefix>
@prefix ${prefix}	.
</#list>


<${participantName}>
	a	twd:CommunicationConnectorParticipant ;
	twd:Extends
		<http://thoughtwire.ca/ontology/2012/02/CommunicationConnector> ; 
	rdfs:label
		"${label}" ; #don't forget to fill in the label
	rdfs:comment
		"${comment}"	 ;
	twd:hasEnvResource
		<http://thoughtwire.ca/SystemResource> ; #don't forget to replace with the expected CR
	twd:hasConsumeRequest
		<${participantName}/Load/SingleEmail>;
	twd:hasShareId
		<${participantName}/Load/Share>. #don't forget to replace with the expected Share.

<http://thoughtwire.ca/SystemResource>
    a twd:EnvResource ;
    twd:hasResourceProperty
        <resources/http> ,
        <resources/https> .
 
<resources/http>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpServer ; #Required. The URI of a server where the communication connector is located.
    twd:suggestedValue    <example> .
      
<resources/https>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpsServer ; #Required. The URI of a secure server where the communication connector is located. 
    twd:suggestedValue    <example> .

<${participantName}/Load/SingleEmail>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		"" ;
	rdfs:comment
		"" ;
	twconn:command
        "${commandType}" ;
    <#if recipient??>
	twconn:recipient
		"${recipient}" ;
	</#if>
	<#if recipientField??>
	twconn:recipientField
		"${recipientField}" ;
	</#if>
	<#if from??>
	twconn:from
		"${from}" ;
	</#if>
	<#if fromField??>
	twconn:fromField
		"${fromField}" ;
	</#if>
	<#if cc??>
	twconn:cc
		"${cc}" ;
	</#if>
	<#if ccField??>
	twconn:ccField	
		"${ccField}";
	</#if>
	<#if ifSubject>
	twconn:subject
		"${subject}" ;
	</#if>
	<#if subjectTemplate??>
	twconn:subjectTemplate
		"${subjectTemplate}" ;
	</#if>
	<#if template??>
	twconn:template
		"${template}" ;
	</#if>
	<#if deviceToken??>
	twconn:deviceToken
		"${deviceToken}" ;
	</#if>
	<#if deviceTokenField??>
	twconn:deviceTokenField
		"${deviceTokenField}" ;
	</#if>
	twd:ConsumeRequestJsonMapping
        """
        ${r"#{prefiximport}"}
        
		BASE <${baseUri}>
		SELECT	?recipient ?subject ?message
        WHERE
         {
            ?subject   
                    <send/singleEmail/recipient>	?recipient ;
                    <send/singleEmail/message>		?message ;
                    <send/singleEmail/subject>		?subject .
        }
		""" .

<${participantName}/Load/Share>
	a	twd:Share ;
	rdfs:comment
		"" ;
	rdfs:label
		"" ;
	twd:hasPredicate
		<Connector/results/response> ,
		<Connector/results/errorMessage> ;
	twd:ShareJsonMapping
        """
        ${r"#{prefiximport}"}
        
        @base <${baseUri}> .
		<Connector(($))>
				<Connector/results/response>	"(results.response)" ;
				<Connector/results/response>	"(results.errorMessage)" .
        """ .