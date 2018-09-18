<#-- test version -->
@base			<${baseUri}>	.

<#list prefixes as prefix>
@prefix ${prefix}	.
</#list>


<${participantName}>
	a	twd:DbConnectorParticipant ;
	twd:Extends
		<http://thoughtwire.ca/ontology/2012/06/LdapConnector> ; 
	rdfs:label
		"${label}" ; #don't forget to fill in the label
	rdfs:comment
		"${comment}"	 ;
	twd:hasEnvResource
		<http://thoughtwire.ca/SystemResource> ;
	twd:hasConsumeRequest
		<${participantName}/Query> ; #don't forget to replace with the expected CR
	twd:hasShareId
		<${participantName}/Query/Share> . #don't forget to replace with the expected Share
		
<http://thoughtwire.ca/SystemResource>
    a twd:EnvResource ;
    twd:hasResourceProperty
        <resources/http> ,
        <resources/https> .
 
<resources/http>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpServer ; #Required. The URI of a server where the Ldap connector is located.
    twd:suggestedValue    <example> .
      
<resources/https>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpsServer ; #Required. The URI of a secure server where the Ldap connector is located. 
    twd:suggestedValue    <example> .		
		
<${participantName}/Query>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twconn:ldapUrl
		"${ldapUrl}" ; #full URL for the ldap server, including schema, host, and port
	twconn:username
		"${username}" ; #username for authentication
	twconn:password
        "${password}" ; #password for authentication
    twconn:searchFilterTemplate
    	"${searchFilterTemplate}" ; #filter for the search
	twd:ConsumeRequestJsonMapping
		"""
		${r"#{prefiximport}"}
		
		SELECT ?objectClass ?sn ?searchBase ?attributes ?id
        WHERE
        {
            twd:ldapTestMaster
                twd:objectClass		?objectClass ;
                twd:sn    			?sn ;
                twd:searchBase    	?searchBase;
                twd:attributes    	?attributes ;
                twd:search        	?id .
        }
		""" .	

<${participantName}/Query/Share>
	a	twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		twd:searchFinished ,
       	twd:result ,
        twd:dn ,
        twd:cn ,
        twd:sn ,
        twd:uid ,
        twd:mail ;
	twd:ShareJsonMapping
		"""
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .

		twd:ldapConnector    twd:searchFinished		"(($))(_time)" .
        twd:ldapConnector    twd:result    		 	<twd:(($))((results))(_sequence)> .
        <twd:(($))((results))(_sequence)>
            twd:dn		"(dn)" ;
            twd:cn    	"(cn|'')" ;
            twd:sn    	"(sn|'')" ;
            twd:uid    	"(uid|'')" ;
            twd:mail    "(mail|'')" ;
        .   
		""" .