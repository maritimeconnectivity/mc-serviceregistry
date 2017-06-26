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

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.context.WebApplicationContext;

import com.frequentis.maritime.mcsr.dataload.DataLoader;
import com.frequentis.maritime.mcsr.dataload.TestDataLoader;
import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.repository.DesignRepository;
import com.frequentis.maritime.mcsr.repository.search.DesignSearchRepository;
import com.frequentis.maritime.mcsr.service.DesignService;


/**
 * Test class for the DesignResource REST controller.
 *
 * @see DesignResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@WithMockUser("test-user")
public class DesignResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_VERSION = "AAAAA";
    private static final String UPDATED_VERSION = "BBBBB";
    private static final String DEFAULT_COMMENT = "AAAAA";
    private static final String UPDATED_COMMENT = "BBBBB";
    private static final String DEFAULT_DESIGN_ID = "AAAAA";
    private static final String UPDATED_DESIGN_ID = "BBBBB";
    private static final String DEFAULT_STATUS = "AAAAA";
    private static final String UPDATED_STATUS = "BBBBB";
    private static final String DEFAULT_ORGANIZATION_ID = "AAAAA";
    private static final String UPDATED_ORGANIZATION_ID = "BBBBB";

    @Inject
    private DesignRepository designRepository;

    @Inject
    private DesignService designService;

    @Inject
    private DesignSearchRepository designSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;
    
    @Inject
    private ApplicationContext context;
    
    @Inject
    private XmlResource xmlResource;
    
    @Inject
    Environment environment;
    
    @Inject
    TestDataLoader testDataLoader;

    private MockMvc restDesignMockMvc;

    private Design design;

    @PostConstruct
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        DesignResource designResource = new DesignResource();
        ReflectionTestUtils.setField(designResource, "designService", designService);
        this.restDesignMockMvc = MockMvcBuilders.standaloneSetup(designResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
        
        
        testDataLoader.run();
    }

    @Before
    public void initTest() throws URISyntaxException {
        designSearchRepository.deleteAll();
        design = new Design();
        design.setName(DEFAULT_NAME);
        design.setVersion(DEFAULT_VERSION);
        design.setComment(DEFAULT_COMMENT);
        design.setDesignId(DEFAULT_DESIGN_ID);
        design.setStatus(DEFAULT_STATUS);
        design.setOrganizationId(DEFAULT_ORGANIZATION_ID);
    }

    @Test
    @Transactional
    public void createDesign() throws Exception {
        int databaseSizeBeforeCreate = designRepository.findAll().size();

        // Create the Design

        restDesignMockMvc.perform(post("/api/designs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(design)))
                .andExpect(status().isCreated());

        // Validate the Design in the database
        List<Design> designs = designRepository.findAll();
        assertThat(designs).hasSize(databaseSizeBeforeCreate + 1);
        Design testDesign = designs.get(designs.size() - 1);
        assertThat(testDesign.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDesign.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testDesign.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testDesign.getDesignId()).isEqualTo(DEFAULT_DESIGN_ID);
        assertThat(testDesign.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testDesign.getOrganizationId()).isEqualTo(DEFAULT_ORGANIZATION_ID);

        // Validate the Design in ElasticSearch
        Design designEs = designSearchRepository.findById(testDesign.getId()).get();
        assertThat(designEs).isEqualToComparingFieldByField(testDesign);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = designRepository.findAll().size();
        // set the field null
        design.setName(null);

        // Create the Design, which fails.

        restDesignMockMvc.perform(post("/api/designs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(design)))
                .andExpect(status().isBadRequest());

        List<Design> designs = designRepository.findAll();
        assertThat(designs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkVersionIsRequired() throws Exception {
        int databaseSizeBeforeTest = designRepository.findAll().size();
        // set the field null
        design.setVersion(null);

        // Create the Design, which fails.

        restDesignMockMvc.perform(post("/api/designs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(design)))
                .andExpect(status().isBadRequest());

        List<Design> designs = designRepository.findAll();
        assertThat(designs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCommentIsRequired() throws Exception {
        int databaseSizeBeforeTest = designRepository.findAll().size();
        // set the field null
        design.setComment(null);

        // Create the Design, which fails.

        restDesignMockMvc.perform(post("/api/designs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(design)))
                .andExpect(status().isBadRequest());

        List<Design> designs = designRepository.findAll();
        assertThat(designs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDesignIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = designRepository.findAll().size();
        // set the field null
        design.setDesignId(null);

        // Create the Design, which fails.

        restDesignMockMvc.perform(post("/api/designs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(design)))
                .andExpect(status().isBadRequest());

        List<Design> designs = designRepository.findAll();
        assertThat(designs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDesigns() throws Exception {
        // Initialize the database
        designRepository.saveAndFlush(design);

        // Get all the designs
        restDesignMockMvc.perform(get("/api/designs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.[*].id").value(hasItem(design.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].designId").value(hasItem(DEFAULT_DESIGN_ID.toString())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].organizationId").value(hasItem(DEFAULT_ORGANIZATION_ID.toString())));
    }

    @Test
    @Transactional
    public void getDesign() throws Exception {
        // Initialize the database
        designRepository.saveAndFlush(design);

        // Get the design
        restDesignMockMvc.perform(get("/api/designs/{id}", design.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.id").value(design.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.designId").value(DEFAULT_DESIGN_ID.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.organizationId").value(DEFAULT_ORGANIZATION_ID.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDesign() throws Exception {
        // Get the design
        restDesignMockMvc.perform(get("/api/designs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDesign() throws Exception {
        // Initialize the database
        designService.save(design);

        int databaseSizeBeforeUpdate = designRepository.findAll().size();

        // Update the design
        Design updatedDesign = new Design();
        updatedDesign.setId(design.getId());
        updatedDesign.setName(UPDATED_NAME);
        updatedDesign.setVersion(UPDATED_VERSION);
        updatedDesign.setComment(UPDATED_COMMENT);
        updatedDesign.setDesignId(UPDATED_DESIGN_ID);
        updatedDesign.setStatus(UPDATED_STATUS);
        updatedDesign.setOrganizationId(UPDATED_ORGANIZATION_ID);

        restDesignMockMvc.perform(put("/api/designs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedDesign)))
                .andExpect(status().isOk());

        // Validate the Design in the database
        List<Design> designs = designRepository.findAll();
        assertThat(designs).hasSize(databaseSizeBeforeUpdate);
        Design testDesign = designs.get(designs.size() - 1);
        assertThat(testDesign.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDesign.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testDesign.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testDesign.getDesignId()).isEqualTo(UPDATED_DESIGN_ID);
        assertThat(testDesign.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testDesign.getOrganizationId()).isEqualTo(UPDATED_ORGANIZATION_ID);

        // Validate the Design in ElasticSearch
        Design designEs = designSearchRepository.findById(testDesign.getId()).get();
        assertThat(designEs).isEqualToComparingFieldByField(testDesign);
    }

    @Test
    @Transactional
    public void deleteDesign() throws Exception {
        // Initialize the database
        designService.save(design);

        int databaseSizeBeforeDelete = designRepository.findAll().size();

        // Get the design
        restDesignMockMvc.perform(delete("/api/designs/{id}", design.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean designExistsInEs = designSearchRepository.existsById(design.getId());
        assertThat(designExistsInEs).isFalse();

        // Validate the database is empty
        List<Design> designs = designRepository.findAll();
        assertThat(designs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchDesign() throws Exception {
        // Initialize the database
        designService.save(design);

        // Search the design
        restDesignMockMvc.perform(get("/api/_search/designs?query=id:" + design.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(design.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].designId").value(hasItem(DEFAULT_DESIGN_ID.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].organizationId").value(hasItem(DEFAULT_ORGANIZATION_ID.toString())));
    }
}
