<#-- @ftlvariable name="" type="com.frequentis.maritime.mcsr.dataload.InstanceFreemarkerRoot" -->
<?xml version="1.0" encoding="UTF-8"?>
<ServiceInstanceSchema:serviceInstance xmlns:ServiceInstanceSchema="http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd" xmlns:ServiceSpecificationSchema="http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceSpecificationSchema.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd ServiceInstanceSchema.xsd ">
    <id>${instanceId}</id>
    <version>1.0.0</version>
    <name>${personName}'s Address Person Lookup Service Implementation</name>
    <status>provisional</status>
    <description>A simple service implementing the ${specificationName}</description>
    <keywords>${personName} simple person address location lookup</keywords>
    <URL>https://${uuid}.${providerDomain}/mcl/addressLookupServiceImpl/</URL>

    <requiresAuthorization>true</requiresAuthorization>

    <offersServiceLevel>
        <availability>0</availability>
        <name>Development</name>
        <description>This service implementation is under active development, 0% availability is guaranteed.</description>
    </offersServiceLevel>
    <coversAreas>
        <coversArea>
            <name>${coversAreaName}</name>
            <description>${coversAreaDescription}</description>
            <geometryAsWKT>${coversAreaGeometryWKT}</geometryAsWKT>
        </coversArea>
    </coversAreas>

    <implementsServiceDesign>
        <id>${serviceDesignId}</id>
        <version>1.0.0</version>
    </implementsServiceDesign>
    <producedBy>
        <id>${personMrnID}</id>
        <name>${personName}</name>
        <description>${personName} is the authority for standards at example.com</description>
        <contactInfo>${personEmail}</contactInfo>
        <isCommercial>false</isCommercial>
    </producedBy>
    <providedBy>
        <id>${providerMrnID}</id>
        <name>${providerName}</name>
        <description>${providerName} is the authority for standards at example.com</description>
        <contactInfo>${providerEmail}</contactInfo>
        <isCommercial>false</isCommercial>
    </providedBy>
</ServiceInstanceSchema:serviceInstance>
