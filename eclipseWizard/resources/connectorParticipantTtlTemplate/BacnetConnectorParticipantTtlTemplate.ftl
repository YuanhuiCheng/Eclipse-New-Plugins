<#-- test version -->
@base			<${baseUri}>	.

<#list prefixes as prefix>
@prefix ${prefix}	.
</#list>


<${participantName}>
	a	twd:BacNetConnectorParticipant ;
	twd:Extends
		<http://thoughtwire.ca/ontology/2012/06/BacNetConnector> ; 
	rdfs:label
		"${label}" ; #don't forget to fill in the label
	rdfs:comment
		"${comment}"	 ;
	twd:hasEnvResource
		<http://thoughtwire.ca/SystemResource> ;
	twd:hasConsumeRequest
		<#if commandType == "ListDevices">
		<${participantName}/Devices> ; #don't forget to replace with the expected CR
		<#elseif commandType = "ListObjects">
		<${participantName}/Objects> ; #don't forget to replace with the expected CR
		<#elseif commandType = "Read">
		<${participantName}/Read> ; #don't forget to replace with the expected CR
		<#elseif commandType = "Write">
		<${participantName}/Write> ; #don't forget to replace with the expected CR
		</#if>
	twd:hasShareId
		<#if commandType == "ListDevices">
		<${participantName}/Devices/Share> . #don't forget to replace with the expected Share
		<#elseif commandType = "ListObjects">
		<${participantName}/Objects/Share> .  #don't forget to replace with the expected Share
		<#elseif commandType = "Read"> 
		<${participantName}/Read/Share> .  #don't forget to replace with the expected Share
		<#elseif commandType = "Write"> 
		<${participantName}/Write/Share> .  #don't forget to replace with the expected Share
		</#if>
		
<http://thoughtwire.ca/SystemResource>
    a twd:EnvResource ;
    twd:hasResourceProperty
        <resources/http> ,
        <resources/https> .
 
<resources/http>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpServer ; #Required. The URI of a server where the BACnet connector is located.
    twd:suggestedValue    <example> .
      
<resources/https>
    a twd:ResourceProperty ;
    twd:propertyName    twconn:HttpsServer ; #Required. The URI of a secure server where the BACnet connector is located. 
    twd:suggestedValue    <example> .		
		
<#if commandType == "ListDevices">
<${participantName}/Devices>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:notifySatisfiedOnly
		true ;
	twconn:command
		twd:ListDevices ;
	twd:ConsumeRequestJsonMapping
		"""
		${r"#{prefiximport}"}
		
		BASE <${baseUri}>
		SELECT ?ipAddress ?lowDevice ?highDevice
		WHERE 
		{
			<Discover>	<DiscoverDevices>	?criteria .

			?criteria
				<Discover/criteria#ipAddress>		?ipAddress ;
				<Discover/criteria#lowDeviceId>		?lowDevice ;
				<Discover/criteria#highDeviceId>	?highDevice ;
		}
		""" .

<${participantName}/Devices/Share>
	a	twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		<Discover/Devices/result> ,
		<Discover/Device#ID> ,
		<Discover/Device#IP_ADDRESS> ;
	twd:ShareJsonMapping
		"""
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .

		<Bacnet/Discover/Devices>	<Discover/Devices/result>	<Bacnet/Discover/Devices/result#(($))((results[*][*]))(id)> .
		<Bacnet/Discover/Devices/result#(($))((results[*][*]))(id)>
			<Discover/Device#ID>				"(id|'')" ;
			<Discover/Device#IP_ADDRESS>		"(ipAddress|'')" ;
		""" .
<#elseif commandType = "ListObjects">
<${participantName}/Objects>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:notifySatisfiedOnly
		true ;
	twconn:command
		twd:ListObjects ;
	twd:ConsumeRequestJsonMapping
		"""
		${r"#{prefiximport}"}
		
		BASE <${baseUri}>
		SELECT ?device
		WHERE 
		{
			<DeviceDiscovery>	<SelectDevice>	?device .
		}
		""" .
		
<${participantName}/Objects/Share>
	a	twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		<Discover/Objects/result> ,
		<Discover/Objects/deviceId> ,
		<Discover/Device#ID> ,
		<Discover/Device#OBJECT_NAME> ,
		<Discover/Device#DESCRIPTION> ;
	twd:ShareJsonMapping
		"""
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .

		<Bacnet/Discover/Objects>
			<Discover/Objects/deviceId>	"(($))((results[*]))(device.id)" ;
			<Discover/Objects/result>	<Bacnet/Discover/Objects/result#(($))((results[*]))((properties[*]))(id)> .
			
		<Bacnet/Discover/Objects/result#(($))((results[*]))((properties[*]))(id)>
			<Discover/Device#ID>				"(id|'')" ;
			<Discover/Device#OBJECT_NAME>		"(OBJECT_NAME|'')" ;
			<Discover/Device#DESCRIPTION>		"(DESCRIPTION|'')" .
		""" .
<#elseif commandType = "Read">
<${participantName}/Read>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:notifySatisfiedOnly
		true ;
	twconn:command
		twd:Read ;
	twconn:sheetType
		twd:Objects ;
	twd:ConsumeRequestJsonMapping
		"""
		${r"#{prefiximport}"}
		
		BASE <${baseUri}>
		SELECT ?command ?cluster ?readObjectType ?readObjectNumber ?readObjectProperty
		WHERE 
		{
			<Read>
				twd:readObjectType			?readObjectType ;
				twd:readObjectNumber		?readObjectNumber ;
				twd:readObjectProperty		?readObjectProperty ;
				twd:cluster					?cluster;
			.
			BIND( "read" AS ?command )
		}
		""" .

<${participantName}/Read/Share>
	a	twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		twd:cluster ,
		twd:value ,
		twd:property ;
	twd:ShareJsonMapping
		"""
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .

		<bacnet/readResults(($))((results))>
			twd:property	"(property)" ;
			twd:value		"(value)" ;
			twd:cluster		"(cluster)" .
		""" .
<#elseif commandType = "Write">
<${participantName}/Write>
	a	twd:SparqlConsumeRequest ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:notifySatisfiedOnly
		true ;
	twconn:command
		twd:Write ;
	twconn:sheetType
		twd:Objects ;
	twd:ConsumeRequestJsonMapping
		"""
		${r"#{prefiximport}"}
		
		BASE <${baseUri}>
		SELECT ?command ?cluster ?writeObjectType ?writeObjectNumber ?writeObjectProperty ?value
		WHERE 
		{
			BIND( "write" AS ?command )
			<Write>
					twd:writeObjectType			?writeObjectType ;
					twd:writeObjectNumber		?writeObjectNumber ;
					twd:writeObjectProperty		?writeObjectProperty ;
					twd:cluster					?cluster ;
					twd:value					?value ;
			.
		}
		""" .
<${participantName}/Write/Share>
	a	twd:Share ;
	rdfs:label
		" " ; #don't forget to fill in the label
	rdfs:comment 
		" " ;
	twd:hasPredicate
		twd:writeResult ;
	twd:ShareJsonMapping
		"""
		${r"#{prefiximport}"}
      	
		@base <${baseUri}> .

		<bacnet/writeResults>
			twd:writeResult		true .
		""" .
</#if>