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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
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

import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.repository.SpecificationRepository;
import com.frequentis.maritime.mcsr.repository.search.SpecificationSearchRepository;
import com.frequentis.maritime.mcsr.service.SpecificationService;
import com.frequentis.maritime.mcsr.service.XmlService;


/**
 * Test class for the SpecificationResource REST controller.
 *
 * @see SpecificationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@WithMockUser("test-user")
public class SpecificationResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_VERSION = "AAAAA";
    private static final String UPDATED_VERSION = "BBBBB";
    private static final String DEFAULT_COMMENT = "AAAAA";
    private static final String UPDATED_COMMENT = "BBBBB";
    private static final String DEFAULT_KEYWORDS = "AAAAA";
    private static final String UPDATED_KEYWORDS = "BBBBB";
    private static final String DEFAULT_SPECIFICATION_ID = "AAAAA";
    private static final String UPDATED_SPECIFICATION_ID = "BBBBB";
    private static final String DEFAULT_STATUS = "AAAAA";
    private static final String UPDATED_STATUS = "BBBBB";
    private static final String DEFAULT_ORGANIZATION_ID = "AAAAA";
    private static final String UPDATED_ORGANIZATION_ID = "BBBBB";
	private static final String DEFAULT_XML_COMMENT = "LLLL";
	private static final String DEFAULT_XML_CONTENTYPE = "application/xml";
	private static final String DEFAULT_XML_NAME = "SpecTestXML";

    @Inject
    private SpecificationRepository specificationRepository;

    @Inject
    private SpecificationService specificationService;

    @Inject
    private SpecificationSearchRepository specificationSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;
    
    @Inject
    private ApplicationContext appContext;
    
    @Inject
    private XmlService xmlService;

    private MockMvc restSpecificationMockMvc;

    private Specification specification;
    
    private Xml specificationXML;

    @PostConstruct
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        SpecificationResource specificationResource = new SpecificationResource();
        ReflectionTestUtils.setField(specificationResource, "specificationService", specificationService);
        this.restSpecificationMockMvc = MockMvcBuilders.standaloneSetup(specificationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
        
        Path xmlPath = appContext.getResource("classpath:dataload/xml/AddressForPersonLookupServiceSpecification.xml").getFile().toPath();
        String specificationXML = new String(Files.readAllBytes(xmlPath));
        
        // XML for test
        Xml xml = new Xml();
        xml.setComment(DEFAULT_XML_COMMENT);
        xml.setName(DEFAULT_XML_NAME);
        xml.setContentContentType(DEFAULT_XML_CONTENTYPE);
        xml.setContent(specificationXML);
        xmlService.save(xml);
        this.specificationXML = xml;
        
    }

    @Before
    public void initTest() {
        specificationSearchRepository.deleteAll();
        specification = new Specification();
        specification.setName(DEFAULT_NAME);
        specification.setVersion(DEFAULT_VERSION);
        specification.setComment(DEFAULT_COMMENT);
        specification.setKeywords(DEFAULT_KEYWORDS);
        specification.setSpecificationId(DEFAULT_SPECIFICATION_ID);
        specification.setStatus(DEFAULT_STATUS);
        specification.setOrganizationId(DEFAULT_ORGANIZATION_ID);
        

    }

    @Test
    @Transactional
    public void createSpecification() throws Exception {
        int databaseSizeBeforeCreate = specificationRepository.findAll().size();
        
        // XML is required in resource
        specification.setSpecAsXml(specificationXML);

        // Create the Specification
        restSpecificationMockMvc.perform(post("/api/specifications")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(specification)))
                .andExpect(status().isCreated());

        // Validate the Specification in the database
        List<Specification> specifications = specificationRepository.findAll();
        assertThat(specifications).hasSize(databaseSizeBeforeCreate + 1);
        Specification testSpecification = specifications.get(specifications.size() - 1);
        assertThat(testSpecification.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSpecification.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testSpecification.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testSpecification.getKeywords()).isEqualTo(DEFAULT_KEYWORDS);
        assertThat(testSpecification.getSpecificationId()).isEqualTo(DEFAULT_SPECIFICATION_ID);
        assertThat(testSpecification.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testSpecification.getOrganizationId()).isEqualTo(DEFAULT_ORGANIZATION_ID);

        // Validate the Specification in ElasticSearch
        Specification specificationEs = specificationSearchRepository.findById(testSpecification.getId()).get();
        assertThat(specificationEs).isEqualToComparingFieldByField(testSpecification);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = specificationRepository.findAll().size();
        // set the field null
        specification.setName(null);

        // Create the Specification, which fails.

        restSpecificationMockMvc.perform(post("/api/specifications")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(specification)))
                .andExpect(status().isBadRequest());

        List<Specification> specifications = specificationRepository.findAll();
        assertThat(specifications).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkVersionIsRequired() throws Exception {
        int databaseSizeBeforeTest = specificationRepository.findAll().size();
        // set the field null
        specification.setVersion(null);

        // Create the Specification, which fails.

        restSpecificationMockMvc.perform(post("/api/specifications")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(specification)))
                .andExpect(status().isBadRequest());

        List<Specification> specifications = specificationRepository.findAll();
        assertThat(specifications).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCommentIsRequired() throws Exception {
        int databaseSizeBeforeTest = specificationRepository.findAll().size();
        // set the field null
        specification.setComment(null);

        // Create the Specification, which fails.

        restSpecificationMockMvc.perform(post("/api/specifications")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(specification)))
                .andExpect(status().isBadRequest());

        List<Specification> specifications = specificationRepository.findAll();
        assertThat(specifications).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSpecificationIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = specificationRepository.findAll().size();
        // set the field null
        specification.setSpecificationId(null);

        // Create the Specification, which fails.

        restSpecificationMockMvc.perform(post("/api/specifications")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(specification)))
                .andExpect(status().isBadRequest());

        List<Specification> specifications = specificationRepository.findAll();
        assertThat(specifications).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSpecifications() throws Exception {
        // Initialize the database
        specificationRepository.saveAndFlush(specification);

        // Get all the specifications
        restSpecificationMockMvc.perform(get("/api/specifications?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.[*].id").value(hasItem(specification.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].keywords").value(hasItem(DEFAULT_KEYWORDS.toString())))
                .andExpect(jsonPath("$.[*].specificationId").value(hasItem(DEFAULT_SPECIFICATION_ID.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].organizationId").value(hasItem(DEFAULT_ORGANIZATION_ID.toString())));
    }

    @Test
    @Transactional
    public void getSpecification() throws Exception {
        // Initialize the database
        specificationRepository.saveAndFlush(specification);

        // Get the specification
        restSpecificationMockMvc.perform(get("/api/specifications/{id}", specification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.id").value(specification.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.keywords").value(DEFAULT_KEYWORDS.toString()))
            .andExpect(jsonPath("$.specificationId").value(DEFAULT_SPECIFICATION_ID.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.organizationId").value(DEFAULT_ORGANIZATION_ID.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSpecification() throws Exception {
        // Get the specification
        restSpecificationMockMvc.perform(get("/api/specifications/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSpecification() throws Exception {
        // XML is required in resource
        specification.setSpecAsXml(specificationXML);
        // Initialize the database
        specificationService.save(specification);

        int databaseSizeBeforeUpdate = specificationRepository.findAll().size();

        // Update the specification
        Specification updatedSpecification = new Specification();
        updatedSpecification.setId(specification.getId());
        updatedSpecification.setName(UPDATED_NAME);
        updatedSpecification.setVersion(UPDATED_VERSION);
        updatedSpecification.setComment(UPDATED_COMMENT);
        updatedSpecification.setKeywords(UPDATED_KEYWORDS);
        updatedSpecification.setSpecificationId(UPDATED_SPECIFICATION_ID);
        updatedSpecification.setStatus(UPDATED_STATUS);
        updatedSpecification.setOrganizationId(UPDATED_ORGANIZATION_ID);
        updatedSpecification.setSpecAsXml(specificationXML);

        restSpecificationMockMvc.perform(put("/api/specifications")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSpecification)))
                .andExpect(status().isOk());

        // Validate the Specification in the database
        List<Specification> specifications = specificationRepository.findAll();
        assertThat(specifications).hasSize(databaseSizeBeforeUpdate);
        Specification testSpecification = specifications.get(specifications.size() - 1);
        assertThat(testSpecification.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSpecification.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testSpecification.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testSpecification.getKeywords()).isEqualTo(UPDATED_KEYWORDS);
        assertThat(testSpecification.getSpecificationId()).isEqualTo(UPDATED_SPECIFICATION_ID);
        assertThat(testSpecification.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testSpecification.getOrganizationId()).isEqualTo(UPDATED_ORGANIZATION_ID);

        // Validate the Specification in ElasticSearch
        Specification specificationEs = specificationSearchRepository.findById(testSpecification.getId()).get();
        assertThat(specificationEs).isEqualToComparingFieldByField(testSpecification);
    }

    @Test
    @javax.transaction.Transactional
    public void deleteSpecification() throws Exception {
        // Initialize the database
        specificationService.save(specification);

        int databaseSizeBeforeDelete = specificationRepository.findAll().size();

        // Get the specification
        restSpecificationMockMvc.perform(delete("/api/specifications/{id}", specification.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        specificationSearchRepository.refresh();
        
        // FIXME HOTFIX! There is SD-ES bug DATAES-363
        //boolean specificationExistsInEs = specificationSearchRepository.existsById(specification.getId());
        boolean specificationExistsInEs = specificationSearchRepository.findById(specification.getId()).isPresent();
        assertThat(specificationExistsInEs).isFalse();

        // Validate the database is empty
        List<Specification> specifications = specificationRepository.findAll();
        assertThat(specifications).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSpecification() throws Exception {
        // Initialize the database
        specificationService.save(specification);

        // Search the specification
        restSpecificationMockMvc.perform(get("/api/_search/specifications?query=id:" + specification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.[*].id").value(hasItem(specification.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].keywords").value(hasItem(DEFAULT_KEYWORDS.toString())))
            .andExpect(jsonPath("$.[*].specificationId").value(hasItem(DEFAULT_SPECIFICATION_ID.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].organizationId").value(hasItem(DEFAULT_ORGANIZATION_ID.toString())));
    }
}
