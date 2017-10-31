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

package com.frequentis.maritime.mcsr.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.entity.ContentType;
import org.hibernate.validator.internal.util.privilegedactions.NewInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.frequentis.maritime.mcsr.builders.XMLInstanceBuilder;
import com.frequentis.maritime.mcsr.domain.Instance;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.repository.InstanceRepository;
import com.frequentis.maritime.mcsr.repository.XmlRepository;
import com.frequentis.maritime.mcsr.repository.search.InstanceSearchRepository;
import com.frequentis.maritime.mcsr.repository.search.XmlSearchRepository;
import com.frequentis.maritime.mcsr.service.DesignService;
import com.frequentis.maritime.mcsr.service.InstanceService;
import com.frequentis.maritime.mcsr.service.XmlService;
import com.frequentis.maritime.mcsr.web.soap.converters.instance.InstanceDTOConverter;
import com.frequentis.maritime.mcsr.xmls.CoversAreaType;
import com.frequentis.maritime.mcsr.xmls.CoverageArea;
import com.frequentis.maritime.mcsr.xmls.InstanceXML;
import com.frequentis.maritime.mcsr.xmls.InstanceXML.Status;
import com.frequentis.maritime.mcsr.xmls.ServiceDesignReference;
import com.frequentis.maritime.mcsr.xmls.ServiceLevel;
import com.frequentis.maritime.mcsr.xmls.VendorInfo;


/**
 * Test class for the InstanceResource REST controller.
 *
 * @see InstanceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@WithMockUser("test-user")
public class InstanceResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_VERSION = "AAAAA";
    private static final String UPDATED_VERSION = "BBBBB";
    private static final String DEFAULT_COMMENT = "AAAAA";
    private static final String UPDATED_COMMENT = "BBBBB";

    private static final String DEFAULT_GEOMETRY =
        "{\n" +
            "  \"type\": \"Polygon\",\n" +
            "  \"coordinates\": [\n" +
            "    [\n" +
            "      [0, 0], [10, 10], [10, 0], [0, 0]\n" +
            "    ]\n" +
            "  ]\n" +
            "}";
    private static final String UPDATED_GEOMETRY =
        "{\n" +
            "  \"type\": \"Polygon\",\n" +
            "  \"coordinates\": [\n" +
            "    [\n" +
            "      [0, 0], [20, 20], [20, 0], [0, 0]\n" +
            "    ]\n" +
            "  ]\n" +
            "}";
    private static final String DEFAULT_GEOMETRY_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_GEOMETRY_CONTENT_TYPE = "image/png";
    private static final String DEFAULT_INSTANCE_ID = "AAAAA";
    private static final String UPDATED_INSTANCE_ID = "BBBBB";
    private static final String DEFAULT_KEYWORDS = "AAAAA";
    private static final String UPDATED_KEYWORDS = "BBBBB";
    private static final String DEFAULT_STATUS = Status.provisional.name();
    private static final String UPDATED_STATUS = Status.deleted.name();
    private static final String DEFAULT_ORGANIZATION_ID = "AAAAA";
    private static final String UPDATED_ORGANIZATION_ID = "BBBBB";
    private static final String DEFAULT_UNLOCODE = "AAAAA";
    private static final String UPDATED_UNLOCODE = "BBBBB";
    private static final String DEFAULT_IMO = "AAAAA";
    private static final String UPDATED_IMO = "BBBBB";
    private static final String DEFAULT_ENDPOINT_URI = "AAAAA";
    private static final String UPDATED_ENDPOINT_URI = "BBBBB";
    private static final String DEFAULT_ENDPOINT_TYPE = "AAAAA";
    private static final String UPDATED_ENDPOINT_TYPE = "BBBBB";

    @Inject
    private InstanceRepository instanceRepository;

    @Inject
    private InstanceService instanceService;

    @Inject
    private XmlService xmlService;

    @Inject
    private XmlSearchRepository xmlSearchRepository;

    @Inject
    private InstanceSearchRepository instanceSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private DesignService designService;

    @Inject
    private InstanceDTOConverter instanceConverter;

    private MockMvc restInstanceMockMvc;

    private Instance instance;

    @Inject
    private InstanceResource instanceResource;

    @Autowired
    private ApplicationContext appContext;

    private String addressForPersonLookupServiceInstanceXmlContent;

    @PostConstruct
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        InstanceResource instanceResource = new InstanceResource();
        ReflectionTestUtils.setField(instanceResource, "instanceService", instanceService);
        ReflectionTestUtils.setField(instanceResource, "designService", designService);
        ReflectionTestUtils.setField(instanceResource, "instanceConverter", instanceConverter);
        this.restInstanceMockMvc = MockMvcBuilders.standaloneSetup(instanceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
        // Load XML
        Path path = appContext.getResource("classpath:dataload/xml/AddressForPersonLookupServiceInstance.xml").getFile().toPath();
        addressForPersonLookupServiceInstanceXmlContent = new String(Files.readAllBytes(path));
    }

    @Before
    public void initTest() throws TransformerException {
        instanceSearchRepository.deleteAll();
        instance = new Instance();
        // Common
        instance.setEndpointType(DEFAULT_ENDPOINT_TYPE);
        instance.setGeometryContentType(DEFAULT_GEOMETRY_CONTENT_TYPE);
        instance.setOrganizationId(DEFAULT_ORGANIZATION_ID);

        // All these field are overridden by XML (XML is required)
        instance.setName(DEFAULT_NAME);
        instance.setVersion(DEFAULT_VERSION);
        instance.setComment(DEFAULT_COMMENT);
        instance.setInstanceId(DEFAULT_INSTANCE_ID);
        instance.setKeywords(DEFAULT_KEYWORDS);
        instance.setStatus(DEFAULT_STATUS);
        instance.setUnlocode(DEFAULT_UNLOCODE);
        instance.setEndpointUri(DEFAULT_ENDPOINT_URI);

        // Create XML
        xmlSearchRepository.deleteAll();

        Xml xml = new Xml();
        xml.setName(DEFAULT_NAME);
        xml.setComment(DEFAULT_COMMENT);
        xml.setContentContentType(MediaType.APPLICATION_XML_VALUE);

        XMLInstanceBuilder xmlBuilder = new XMLInstanceBuilder();
        InstanceXML x = xmlBuilder.getBuilderXml();
        x.setName(DEFAULT_NAME);
        x.setDescription(DEFAULT_COMMENT);
        x.setURL(DEFAULT_ENDPOINT_URI);
        x.setKeywords(DEFAULT_KEYWORDS);
        x.setId(DEFAULT_INSTANCE_ID);
        x.setStatus(Status.valueOf(DEFAULT_STATUS));
        x.setServiceType(DEFAULT_ENDPOINT_TYPE);
        x.setIMO(DEFAULT_IMO);
        x.setVersion(DEFAULT_VERSION);
        x.setMMSI(RandomStringUtils.randomAlphabetic(5));
        x.setRequiresAuthorization("false");

        VendorInfo producedBy = x.getProducedBy();
        producedBy.setContactInfo(RandomStringUtils.randomAlphabetic(15));
        producedBy.setName(RandomStringUtils.randomAlphabetic(15));
        producedBy.setId(RandomStringUtils.randomAlphabetic(15));
        producedBy.setOrganizationId(RandomStringUtils.randomAlphabetic(15));
        producedBy.setDescription(RandomStringUtils.randomAlphabetic(15));

        VendorInfo providedBy = x.getProvidedBy();
        providedBy.setContactInfo(RandomStringUtils.randomAlphabetic(15));
        providedBy.setName(RandomStringUtils.randomAlphabetic(15));
        providedBy.setId(RandomStringUtils.randomAlphabetic(15));
        providedBy.setOrganizationId(RandomStringUtils.randomAlphabetic(15));
        providedBy.setDescription(RandomStringUtils.randomAlphabetic(15));

        ServiceLevel offersServiceLevel = x.getOffersServiceLevel();
        offersServiceLevel.setName(RandomStringUtils.randomAlphabetic(15));
        offersServiceLevel.setAvailability(1);
        offersServiceLevel.setDescription(RandomStringUtils.randomAlphabetic(15));

        CoversAreaType conversAreas = x.getCoversAreas();
        // Requires plenty of heap (parsing JSON with unls)
        // conversAreas.setUnLoCode(DEFAULT_UNLOCODE);

        List<CoverageArea> coversArea = new ArrayList<>();
        CoverageArea ca = new CoverageArea();
        ca.setName("Bermuda Triangle");
        ca.setDescription("Loosely defined region in the western part of the North Atlantic Ocean.");
        ca.setGeometryAsWKT("POLYGON((-80.190 25.774, -66.118 18.466, -64.757 32.321, -80.190 25.774))");
        coversArea.add(ca);
        conversAreas.setCoversArea(coversArea);


        ServiceDesignReference implementsServiceDesign = x.getImplementsServiceDesign();
        implementsServiceDesign.setId("awdwad");
        implementsServiceDesign.setVersion("dwadwdwad");

        xml.setContent(xmlBuilder.buildXmlString());
        xmlService.save(xml);

        // Set XML

        instance.setInstanceAsXml(xml);
    }

    // TODO This test can't work. XML is required argument.
    @Test
    @Transactional
    public void createInstance() throws Exception {
        int databaseSizeBeforeCreate = instanceRepository.findAll().size();

        // Create the Instance

        restInstanceMockMvc.perform(post("/api/instances")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(instance)))
                .andExpect(status().isCreated());

        // Validate the Instance in the database
        List<Instance> instances = instanceRepository.findAll();
        assertThat(instances).hasSize(databaseSizeBeforeCreate + 1);
        Instance testInstance = instances.get(instances.size() - 1);
        assertThat(testInstance.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testInstance.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testInstance.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testInstance.getGeometryContentType()).isEqualTo(DEFAULT_GEOMETRY_CONTENT_TYPE);
        assertThat(testInstance.getInstanceId()).isEqualTo(DEFAULT_INSTANCE_ID);
        assertThat(testInstance.getKeywords()).isEqualTo(DEFAULT_KEYWORDS);
        assertThat(testInstance.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testInstance.getOrganizationId()).isEqualTo(DEFAULT_ORGANIZATION_ID);
//        assertThat(testInstance.getUnlocode()).isEqualTo(DEFAULT_UNLOCODE);
        assertThat(testInstance.getEndpointUri()).isEqualTo(DEFAULT_ENDPOINT_URI);
        assertThat(testInstance.getEndpointType()).isEqualTo(DEFAULT_ENDPOINT_TYPE);

        // Validate the Instance in ElasticSearch
        Instance instanceEs = instanceSearchRepository.findById(testInstance.getId()).get();
        assertThat(instanceEs.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(instanceEs.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(instanceEs.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(instanceEs.getGeometryContentType()).isEqualTo(DEFAULT_GEOMETRY_CONTENT_TYPE);
        assertThat(instanceEs.getInstanceId()).isEqualTo(DEFAULT_INSTANCE_ID);
        assertThat(instanceEs.getKeywords()).isEqualTo(DEFAULT_KEYWORDS);
        assertThat(instanceEs.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(instanceEs.getOrganizationId()).isEqualTo(DEFAULT_ORGANIZATION_ID);
//        assertThat(instanceEs.getUnlocode()).isEqualTo(DEFAULT_UNLOCODE);
        assertThat(instanceEs.getEndpointUri()).isEqualTo(DEFAULT_ENDPOINT_URI);
        assertThat(instanceEs.getEndpointType()).isEqualTo(DEFAULT_ENDPOINT_TYPE);
    }

    // Deprecated this parameter is parsed from XML
    //@Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = instanceRepository.findAll().size();
        // set the field null
        instance.setName(null);

        // Create the Instance, which fails.

        restInstanceMockMvc.perform(post("/api/instances")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(instance)))
                .andExpect(status().isBadRequest());

        List<Instance> instances = instanceRepository.findAll();
        assertThat(instances).hasSize(databaseSizeBeforeTest);
    }

    // Deprecated this parameter is parsed from XML
    //@Test
    @Transactional
    public void checkVersionIsRequired() throws Exception {
        int databaseSizeBeforeTest = instanceRepository.findAll().size();
        // set the field null
        instance.setVersion(null);

        // Create the Instance, which fails.

        restInstanceMockMvc.perform(post("/api/instances")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(instance)))
                .andExpect(status().isBadRequest());

        List<Instance> instances = instanceRepository.findAll();
        assertThat(instances).hasSize(databaseSizeBeforeTest);
    }

    // Deprecated this parameter is parsed from XML
    //@Test
    @Transactional
    public void checkCommentIsRequired() throws Exception {
        int databaseSizeBeforeTest = instanceRepository.findAll().size();
        // set the field null
        instance.setComment(null);

        // Create the Instance, which fails.

        restInstanceMockMvc.perform(post("/api/instances")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(instance)))
                .andExpect(status().isBadRequest());

        List<Instance> instances = instanceRepository.findAll();
        assertThat(instances).hasSize(databaseSizeBeforeTest);
    }

    // Deprecated this parameter is parsed from XML
    //@Test
    @Transactional
    public void checkInstanceIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = instanceRepository.findAll().size();
        // set the field null
        instance.setInstanceId(null);

        // Create the Instance, which fails.

        restInstanceMockMvc.perform(post("/api/instances")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(instance)))
                .andExpect(status().isBadRequest());

        List<Instance> instances = instanceRepository.findAll();
        assertThat(instances).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllInstances() throws Exception {
        // Initialize the database
        instanceRepository.saveAndFlush(instance);

        // Get all the instances
        restInstanceMockMvc.perform(get("/api/instances?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(instance.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].geometryContentType").value(hasItem(DEFAULT_GEOMETRY_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].instanceId").value(hasItem(DEFAULT_INSTANCE_ID.toString())))
                .andExpect(jsonPath("$.[*].keywords").value(hasItem(DEFAULT_KEYWORDS.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].organizationId").value(hasItem(DEFAULT_ORGANIZATION_ID.toString())))
                .andExpect(jsonPath("$.[*].unlocode").value(hasItem(DEFAULT_UNLOCODE.toString())))
                .andExpect(jsonPath("$.[*].endpointUri").value(hasItem(DEFAULT_ENDPOINT_URI.toString())))
                .andExpect(jsonPath("$.[*].endpointType").value(hasItem(DEFAULT_ENDPOINT_TYPE.toString())));
    }

    @Test
    @Transactional
    public void getInstance() throws Exception {
        // Initialize the database
        instanceRepository.saveAndFlush(instance);

        // Get the instance
        restInstanceMockMvc.perform(get("/api/instances/{id}", instance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(instance.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.geometryContentType").value(DEFAULT_GEOMETRY_CONTENT_TYPE))
            .andExpect(jsonPath("$.instanceId").value(DEFAULT_INSTANCE_ID.toString()))
            .andExpect(jsonPath("$.keywords").value(DEFAULT_KEYWORDS.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.organizationId").value(DEFAULT_ORGANIZATION_ID.toString()))
            .andExpect(jsonPath("$.unlocode").value(DEFAULT_UNLOCODE.toString()))
            .andExpect(jsonPath("$.endpointUri").value(DEFAULT_ENDPOINT_URI.toString()))
            .andExpect(jsonPath("$.endpointType").value(DEFAULT_ENDPOINT_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingInstance() throws Exception {
        // Get the instance
        restInstanceMockMvc.perform(get("/api/instances/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    // TODO This test can't work. XML is required argument.
    @Test
    @Transactional
    public void updateInstance() throws Exception {
        // Initialize the database
        instanceService.save(instance);

        int databaseSizeBeforeUpdate = instanceRepository.findAll().size();

        // Update the instance
        Instance updatedInstance = new Instance();
        updatedInstance.setId(instance.getId());
        updatedInstance.setGeometryContentType(UPDATED_GEOMETRY_CONTENT_TYPE);
        updatedInstance.setOrganizationId(UPDATED_ORGANIZATION_ID);
        updatedInstance.setEndpointType(UPDATED_ENDPOINT_TYPE);

        // Required by validation in controller
        updatedInstance.setName(DEFAULT_NAME);
        updatedInstance.setVersion(DEFAULT_VERSION);
        updatedInstance.setComment(DEFAULT_COMMENT);
        updatedInstance.setInstanceId(DEFAULT_INSTANCE_ID);
        updatedInstance.setKeywords(DEFAULT_KEYWORDS);
        updatedInstance.setStatus(DEFAULT_STATUS);
        updatedInstance.setUnlocode(DEFAULT_UNLOCODE);
        updatedInstance.setEndpointUri(DEFAULT_ENDPOINT_URI);

        Xml xml = new Xml();
        xml.setName(DEFAULT_NAME);
        xml.setComment(DEFAULT_COMMENT);
        xml.setContentContentType(MediaType.APPLICATION_XML_VALUE);

        XMLInstanceBuilder xmlBuilder = new XMLInstanceBuilder();
        InstanceXML x = xmlBuilder.getBuilderXml();
        x.setName(UPDATED_VERSION);
        x.setDescription(UPDATED_COMMENT);
        x.setURL(UPDATED_ENDPOINT_URI);
        x.setKeywords(UPDATED_KEYWORDS);
        x.setId(UPDATED_INSTANCE_ID);
        x.setStatus(Status.valueOf(UPDATED_STATUS));
        x.setServiceType(UPDATED_ENDPOINT_TYPE);
        x.setIMO(UPDATED_IMO);
        x.setVersion(UPDATED_VERSION);
        x.setMMSI(RandomStringUtils.randomAlphabetic(5));
        x.setRequiresAuthorization("false");

        VendorInfo producedBy = x.getProducedBy();
        producedBy.setContactInfo(RandomStringUtils.randomAlphabetic(15));
        producedBy.setName(UPDATED_NAME);
        producedBy.setId(RandomStringUtils.randomAlphabetic(15));
        producedBy.setOrganizationId(RandomStringUtils.randomAlphabetic(15));
        producedBy.setDescription(RandomStringUtils.randomAlphabetic(15));

        VendorInfo providedBy = x.getProvidedBy();
        providedBy.setContactInfo(RandomStringUtils.randomAlphabetic(15));
        providedBy.setName(RandomStringUtils.randomAlphabetic(15));
        providedBy.setId(RandomStringUtils.randomAlphabetic(15));
        providedBy.setOrganizationId(RandomStringUtils.randomAlphabetic(15));
        providedBy.setDescription(RandomStringUtils.randomAlphabetic(15));

        ServiceLevel offersServiceLevel = x.getOffersServiceLevel();
        offersServiceLevel.setName(RandomStringUtils.randomAlphabetic(15));
        offersServiceLevel.setAvailability(1);
        offersServiceLevel.setDescription(RandomStringUtils.randomAlphabetic(15));

        CoversAreaType conversAreas = x.getCoversAreas();
        // Requires plenty of heap (parsing JSON with unls)
        // conversAreas.setUnLoCode(DEFAULT_UNLOCODE);

        List<CoverageArea> coversArea = new ArrayList<>();
        CoverageArea ca = new CoverageArea();
        ca.setName("Bermuda Triangle");
        ca.setDescription("Loosely defined region in the western part of the North Atlantic Ocean.");
        ca.setGeometryAsWKT("POLYGON((-80.190 25.774, -66.118 18.466, -64.757 32.321, -80.190 25.774))");
        coversArea.add(ca);
        conversAreas.setCoversArea(coversArea);


        ServiceDesignReference implementsServiceDesign = x.getImplementsServiceDesign();
        implementsServiceDesign.setId("awdwad");
        implementsServiceDesign.setVersion("dwadwdwad");

        xml.setContent(xmlBuilder.buildXmlString());
        xmlService.save(xml);

        updatedInstance.setInstanceAsXml(xml);

        restInstanceMockMvc.perform(put("/api/instances")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedInstance)))
                .andExpect(status().isOk());

        // Validate the Instance in the database
        List<Instance> instances = instanceRepository.findAll();
        assertThat(instances).hasSize(databaseSizeBeforeUpdate);
        Instance testInstance = instances.get(instances.size() - 1);
        assertThat(testInstance.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testInstance.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testInstance.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testInstance.getGeometryContentType()).isEqualTo(UPDATED_GEOMETRY_CONTENT_TYPE);
        assertThat(testInstance.getInstanceId()).isEqualTo(UPDATED_INSTANCE_ID);
        assertThat(testInstance.getKeywords()).isEqualTo(UPDATED_KEYWORDS);
        assertThat(testInstance.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testInstance.getOrganizationId()).isEqualTo(UPDATED_ORGANIZATION_ID);
//        assertThat(testInstance.getUnlocode()).isEqualTo(UPDATED_UNLOCODE);
        assertThat(testInstance.getEndpointUri()).isEqualTo(UPDATED_ENDPOINT_URI);
        assertThat(testInstance.getEndpointType()).isEqualTo(UPDATED_ENDPOINT_TYPE);

        // Validate the Instance in ElasticSearch
        Instance instanceEs = instanceSearchRepository.findById(testInstance.getId()).get();
        assertThat(instanceEs.getName()).isEqualTo(UPDATED_NAME);
        assertThat(instanceEs.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(instanceEs.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(instanceEs.getGeometryContentType()).isEqualTo(UPDATED_GEOMETRY_CONTENT_TYPE);
        assertThat(instanceEs.getInstanceId()).isEqualTo(UPDATED_INSTANCE_ID);
        assertThat(instanceEs.getKeywords()).isEqualTo(UPDATED_KEYWORDS);
        assertThat(instanceEs.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(instanceEs.getOrganizationId()).isEqualTo(UPDATED_ORGANIZATION_ID);
//        assertThat(instanceEs.getUnlocode()).isEqualTo(UPDATED_UNLOCODE);
        assertThat(instanceEs.getEndpointUri()).isEqualTo(UPDATED_ENDPOINT_URI);
        assertThat(instanceEs.getEndpointType()).isEqualTo(UPDATED_ENDPOINT_TYPE);
    }

    @Test
    @Transactional
    public void deleteInstance() throws Exception {
        // Initialize the database
        instanceService.save(instance);

        int databaseSizeBeforeDelete = instanceRepository.findAll().size();

        // Get the instance
        restInstanceMockMvc.perform(delete("/api/instances/{id}", instance.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        //boolean instanceExistsInEs = instanceSearchRepository.existsById(instance.getId());
        boolean instanceExistsInEs = instanceSearchRepository.findById(instance.getId()).isPresent();
        assertThat(instanceExistsInEs).isFalse();

        // Validate the database is empty
        List<Instance> instances = instanceRepository.findAll();
        assertThat(instances).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchInstance() throws Exception {
        // Initialize the database
        instanceService.save(instance);
        // saveGeometry have to be called
        instanceService.saveGeometry(instance);

        // Search the instance
        restInstanceMockMvc.perform(get("/api/_search/instances?query=id:" + instance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(instance.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].geometryContentType").value(hasItem(DEFAULT_GEOMETRY_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].instanceId").value(hasItem(DEFAULT_INSTANCE_ID.toString())))
            .andExpect(jsonPath("$.[*].keywords").value(hasItem(DEFAULT_KEYWORDS.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].organizationId").value(hasItem(DEFAULT_ORGANIZATION_ID.toString())))
//            .andExpect(jsonPath("$.[*].unlocode").value(hasItem(DEFAULT_UNLOCODE.toString())))
            .andExpect(jsonPath("$.[*].endpointUri").value(hasItem(DEFAULT_ENDPOINT_URI.toString())))
            .andExpect(jsonPath("$.[*].endpointType").value(hasItem(DEFAULT_ENDPOINT_TYPE.toString())));
    }
}
