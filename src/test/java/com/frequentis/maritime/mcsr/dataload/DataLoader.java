/*
 * MaritimeCloud Service Registry
 * Copyright (c) 2016 Frequentis AG
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.frequentis.maritime.mcsr.dataload;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frequentis.maritime.mcsr.domain.*;
import com.frequentis.maritime.mcsr.domain.enumeration.SpecificationTemplateType;
import com.frequentis.maritime.mcsr.web.rest.DocResource;
import org.springframework.http.HttpEntity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Load sample data using a unit test.
 *
 * @see DocResource
 */
public class DataLoader {

    private static final String TARGET_HOST = "http://127.0.0.1:8081";
    private static String host = null;

    private CSRFRestTemplate restTemplate;

    public static void main(String[] args) throws Exception {
    	if(args.length == 1) {
    		host = args[0];
    	}
    	System.out.println("Aaaaaaaaa. Jsem vzhůru!");
        DataLoader dataLoader = new DataLoader();
        dataLoader.run();
    }
    
    private String getHost() {
    	if(host == null) {
    		return TARGET_HOST;
    	}
    	return host;
    }
    
    private void run() throws Exception {
        restTemplate = new CSRFRestTemplate(getHost());
        restTemplate.login();
        System.out.println("Vím kdo jsem!");
//        loadBaseData();
        generateInstances(5);
        System.out.println("end");
    }

    private void generateInstances(long numberOfInstances) throws Exception {

        InstanceGenerator instanceGenerator = new InstanceGenerator();
        instanceGenerator.generateInstances(this, numberOfInstances);
    }


    private void loadBaseData() throws Exception {
        loadDoc(null, "Guidelines Document", "dataload/docs/E2_Deliverable D3.4 - Service Documentation Guidelines.docx");
        loadDoc(null, "Service Specification Template", "dataload/docs/E2_Deliverable D3.4 - Service Specification Template.docx");
        loadDoc(null, "Service Technical Design Template", "dataload/docs/E2_Deliverable D3.4 - Service Technical Design Template.docx");
        loadDoc(null, "Service Instance Template", "dataload/docs/E2_Deliverable D3.4 - Service Instance Template.docx");
        loadDoc(null, "Conceptual Model", "dataload/docs/E2_D3_2_ConceptualModel.docx");
        loadDoc(null, "Analyis Report", "dataload/docs/EfficienSea2 D3.1- Analysis report on communication and infrastructure.pdf");
        loadDoc(null, "Address Lookup Specification Documentation", "dataload/docs/AddressForPersonLookupServiceSpecification Documentation.docx");
        loadDoc(null, "Address Lookup Design Documentation", "dataload/docs/AddressForPersonLookupServiceDesign Documentation.docx");
        loadDoc(null, "Address Lookup Bermuda Triangle Instance Documentation", "dataload/docs/AddressForPersonLookupServiceInstance - Bermuda Trinagle - Documentation.docx");

        loadXSD(null, "Service Base Types Schema", "dataload/specification-template/ServiceBaseTypesSchema.xsd");
        loadXSD(null, "Service Design Schema", "dataload/specification-template/ServiceDesignSchema.xsd");
        loadXSD(null, "Service Instance Schema", "dataload/specification-template/ServiceInstanceSchema.xsd");
        loadXSD(null, "Service Specification Schema", "dataload/specification-template/ServiceSpecificationSchema.xsd");

        loadSpecificationTemplate(
            "Specification 1", "1.0.0", SpecificationTemplateType.SPECIFICATION,
            "First version of service spec template",
            "Service Specification Schema",
            "E2_Deliverable D3.4 - Service Documentation Guidelines",
            "E2_Deliverable D3.4 - Service Specification Template"
            );
        loadSpecificationTemplate(
            "Design 1", "1.0.0", SpecificationTemplateType.DESIGN,
            "First version of design spec template",
            "Service Design Schema",
            "E2_Deliverable D3.4 - Service Documentation Guidelines",
            "E2_Deliverable D3.4 - Service Technical Design Template"
        );
        loadSpecificationTemplate(
            "Instance 1", "1.0.0", SpecificationTemplateType.INSTANCE,
            "First version of instance spec template",
            "Service Instance Schema",
            "E2_Deliverable D3.4 - Service Documentation Guidelines",
            "E2_Deliverable D3.4 - Service Instance Template"
        );

        loadSpecificationTemplateSet(
            "Service Specification Set 1", "1.0.0", "Deliverable D3.4 from EfficienSea2, the service specification.",
            "Specification 1","Design 1","Instance 1");


        loadXML(null, "Address Lookup Service Specification", "dataload/xml/AddressForPersonLookupServiceSpecification.xml");
        loadXML(null, "Address Lookup Service Design REST", "dataload/xml/AddressForPersonLookupServiceDesignREST.xml");
        loadXML(null, "Address Lookup for Bermuda Triangle", "dataload/xml/AddressForPersonLookupServiceInstance.xml");

        loadServiceSpecification("urn:mrn:mcl:service:specification:example:32c11e45-1fa0-42db-bbff-cfe687382fde",
            "Address Lookup Service Specification", "1.0.0", "Find Addresses",
            "AddressForPersonLookupServiceSpecification.xml",
            "Address Lookup Specification Documentation",
            "Specification 1");

        loadServiceDesign("urn:mrn:mcl:service:design:example:32c11e45-1fa0-42db-bbff-cfe687382fee",
            "Address Lookup Service Design", "1.0.0", "Find Addresses Rest Design",
            "Address Lookup Service Specification",
            "AddressForPersonLookupServiceDesignREST.xml",
            "Address Lookup Design Documentation",
            "Design 1");

        loadServiceInstance("urn:mrn:mcl:service:instance:example:13d293c8-bee5-4d63-aba3-c735983885a7",
            "Address Lookup Service Instance", "1.0.0", "Find Addresses in Bermuda Triangle REST Style",
            "POLYGON(-80.190 25.774, -66.118 18.466, -64.757 32.321, -80.190 25.774)",
            "Address Lookup Service Design",
            "AddressForPersonLookupServiceInstance.xml",
            "Address Lookup Bermuda Triangle Instance Documentation",
            "Instance 1");
    }

    public void loadServiceInstance(String instanceId, String name, String version, String comment, String geometry,
                                     String designSearchString,
                                   String xmlSearchString, String docSearchString,
                                   String implementedServiceTemplateSearchString) throws Exception {

        Instance instance = new Instance();
        instance.setInstanceId(instanceId);
        instance.setName(name);
        instance.setVersion(version);
        instance.setComment(comment);
        ObjectMapper mapper = new ObjectMapper();
        String geometryJson =
            "{ \n" +
                "\"type\":\"Polygon\",\n" +
                "\"coordinates\":[\n" +
                "[[-85.0000, 37.1301],[-85.001, 37.1301],[-85.001, 37.1303],[-85.0000, 37.1303],[-85.0000, 37.1301]]]\n" +
                "}\n";

        JsonNode geometryJsonNode = null;
        try {
            geometryJsonNode = mapper.readTree(geometryJson);
        } catch (IOException e) {
            e.printStackTrace();
        }

        instance.setGeometry(geometryJsonNode);
        instance.setGeometryContentType("application/vnd.geo+json");

        Set<Design> designSet = new HashSet<>();
        Design[] specs = restTemplate.getForObject(getHost() + "/api/_search/designs?query=" + designSearchString, Design[].class);
        for (Design design : specs) {
            designSet.add(design);
        }
        instance.setDesigns(designSet);

        Xml[] xmls = restTemplate.getForObject(getHost() + "/api/_search/xmls?query=" + xmlSearchString, Xml[].class);
        instance.setInstanceAsXml(xmls[0]);

        Doc[] docs = restTemplate.getForObject(getHost() + "/api/_search/docs?query=" + docSearchString, Doc[].class);
        instance.setInstanceAsDoc(docs[0]);

        SpecificationTemplate[] templates = restTemplate.getForObject(getHost() + "/api/_search/specification-templates?query=" + implementedServiceTemplateSearchString, SpecificationTemplate[].class);
        instance.setImplementedSpecificationVersion(templates[0]);

        HttpEntity<Instance> request = new HttpEntity<>(instance);
        restTemplate.postForLocation(getHost() + "/api/instances", request);
    }

    public void loadServiceDesign(String designId, String name, String version, String comment, String specificationSearchString,
                                   String xmlSearchString, String docSearchString,
                                   String implementedServiceTemplateSearchString) throws Exception {

        Design design = new Design();
        design.setDesignId(designId);
        design.setName(name);
        design.setVersion(version);
        design.setComment(comment);

        Set<Specification> specSet = new HashSet<>();
        Specification[] specs = restTemplate.getForObject(getHost() + "/api/_search/specifications?query=" + specificationSearchString, Specification[].class);
        for (Specification specification : specs) {
            specSet.add(specification);
        }
        design.setSpecifications(specSet);

        Xml[] xmls = restTemplate.getForObject(getHost() + "/api/_search/xmls?query=" + xmlSearchString, Xml[].class);
        design.setDesignAsXml(xmls[0]);

        Doc[] docs = restTemplate.getForObject(getHost() + "/api/_search/docs?query=" + docSearchString, Doc[].class);
        design.setDesignAsDoc(docs[0]);

        SpecificationTemplate[] templates = restTemplate.getForObject(getHost() + "/api/_search/specification-templates?query=" + implementedServiceTemplateSearchString, SpecificationTemplate[].class);
        design.setImplementedSpecificationVersion(templates[0]);

        HttpEntity<Design> request = new HttpEntity<>(design);
        restTemplate.postForLocation(getHost() + "/api/designs", request);
    }

    public void loadServiceSpecification(String specificationId, String name, String version, String comment,
                                          String xmlSearchString, String docSearchString,
                                          String implementedServiceTemplateSearchString) throws Exception {

        Specification spec = new Specification();
        spec.setName(name);
        spec.setSpecificationId(specificationId);
        spec.setVersion(version);
        spec.setComment(comment);

        Xml[] xmls = restTemplate.getForObject(getHost() + "/api/_search/xmls?query=" + xmlSearchString, Xml[].class);
        spec.setSpecAsXml(xmls[0]);

        Doc[] docs = restTemplate.getForObject(getHost() + "/api/_search/docs?query=" + docSearchString, Doc[].class);
        spec.setSpecAsDoc(docs[0]);

        SpecificationTemplate[] templates = restTemplate.getForObject(getHost() + "/api/_search/specification-templates?query=" + implementedServiceTemplateSearchString, SpecificationTemplate[].class);
        spec.setImplementedSpecificationVersion(templates[0]);

        HttpEntity<Specification> request = new HttpEntity<>(spec);
        restTemplate.postForLocation(getHost() + "/api/specifications", request);
    }

    public void loadXML(String name, String comment, String xmlFile) throws Exception {
        File file = new File(this.getClass().getClassLoader().getResource(xmlFile).toURI());
        loadXML(name, comment, file);
    }

    public void loadXML(String name, String comment, File file) throws Exception {
        Path path = file.toPath();
        String contentType = Files.probeContentType(path);
        if (name == null) {
            name = path.getFileName().toString();
        }

        Xml xml= new Xml();
        xml.setName(name);
        xml.setComment(comment);

        xml.setContentContentType(contentType);
        xml.setContent(new String(Files.readAllBytes(path)));

        HttpEntity<Xml> request = new HttpEntity<>(xml);
        restTemplate.postForLocation(getHost() + "/api/xmls", request);
    }

    public void loadXSD(String name, String comment, String xsdFile) throws Exception {
        File file = new File(this.getClass().getClassLoader().getResource(xsdFile).toURI());
        loadXSD(name, comment, file);
    }

    public void loadXSD(String name, String comment, File file) throws Exception {
        Path path = file.toPath();
        String contentType = Files.probeContentType(path);
        if (name == null) {
            name = path.getFileName().toString();
        }
        if (contentType == null && path.getFileName().toString().toLowerCase().endsWith(".xsd")) {
            contentType = "application/xml";
        }


        Xsd xsd= new Xsd();
        xsd.setName(name);
        xsd.setComment(comment);

        xsd.setContentContentType(contentType);
        xsd.setContent(Files.readAllBytes(path));

        HttpEntity<Xsd> request = new HttpEntity<>(xsd);
        restTemplate.postForLocation(getHost() + "/api/xsds", request);
    }

    public void loadSpecificationTemplateSet(
        String name, String version, String comment, String... templateSearchStrings
    ) throws Exception {
        SpecificationTemplateSet set = new SpecificationTemplateSet();
        set.setName(name);
        set.setVersion(version);
        set.setComment(comment);
        Set<SpecificationTemplate> templateSet = new HashSet<>();
        for (String searchString : templateSearchStrings) {
            SpecificationTemplate[] templates = restTemplate.getForObject(getHost() + "/api/_search/specification-templates?query=" + searchString, SpecificationTemplate[].class);
            templateSet.add(templates[0]);
        }
        set.setTemplates(templateSet);
        HttpEntity<SpecificationTemplateSet> request = new HttpEntity<>(set);
        restTemplate.postForLocation(getHost() + "/api/specification-template-sets", request);
    }

    public void loadSpecificationTemplate(
        String name,
        String version,
        SpecificationTemplateType specificationTemplateType,
        String comment,
        String xsdSearchString,
        String guidelineDocSearchString,
        String templateDocSearchString
    ) throws Exception {

        SpecificationTemplate template = new SpecificationTemplate();
        template.setName(name);
        template.setVersion(version);
        template.setType(specificationTemplateType);
        template.setComment(comment);

        Xsd[] xsds = restTemplate.getForObject(getHost() + "/api/_search/xsds?query=" + xsdSearchString, Xsd[].class);
        Set<Xsd> xsdSet = Arrays.stream(xsds).collect(Collectors.toSet());
        template.setXsds(xsdSet);

        Doc[] docs = restTemplate.getForObject(getHost() + "/api/_search/docs?query=" + guidelineDocSearchString, Doc[].class);
        template.setGuidelineDoc(docs[0]);

        docs = restTemplate.getForObject(getHost() + "/api/_search/docs?query=" + templateDocSearchString, Doc[].class);
        template.setTemplateDoc(docs[0]);

        HttpEntity<SpecificationTemplate> request = new HttpEntity<>(template);
        restTemplate.postForLocation(getHost() + "/api/specification-templates", request);

    }
    public void loadDoc(String name, String comment, String docFile) throws Exception {
        File file = new File(this.getClass().getClassLoader().getResource(docFile).toURI());
        loadDoc(name, comment, file);
    }

    public void loadDoc(String name, String comment, File file) throws Exception {
        Path path = file.toPath();
        String contentType = Files.probeContentType(path);
        if (name == null) {
            name = path.getFileName().toString();
        }

        Doc doc = new Doc();
        doc.setName(name);
        doc.setComment(comment);

        doc.setFilecontentContentType(contentType);
        doc.setMimetype(contentType);
        doc.setFilecontent(Files.readAllBytes(path));

        HttpEntity<Doc> request = new HttpEntity<>(doc);
        System.out.println("Putting file: " + getHost() + "/api/docs");
        restTemplate.postForLocation(getHost() + "/api/docs", request);

    }
}
