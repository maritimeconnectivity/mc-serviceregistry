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

import com.frequentis.maritime.mcsr.McsrApp;
import com.frequentis.maritime.mcsr.domain.SpecificationTemplate;
import com.frequentis.maritime.mcsr.repository.SpecificationTemplateRepository;
import com.frequentis.maritime.mcsr.service.SpecificationTemplateService;
import com.frequentis.maritime.mcsr.repository.search.SpecificationTemplateSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.frequentis.maritime.mcsr.domain.enumeration.SpecificationTemplateType;

/**
 * Test class for the SpecificationTemplateResource REST controller.
 *
 * @see SpecificationTemplateResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = McsrApp.class)
@WebAppConfiguration
@IntegrationTest
public class SpecificationTemplateResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_VERSION = "AAAAA";
    private static final String UPDATED_VERSION = "BBBBB";

    private static final SpecificationTemplateType DEFAULT_TYPE = SpecificationTemplateType.SPECIFICATION;
    private static final SpecificationTemplateType UPDATED_TYPE = SpecificationTemplateType.DESIGN;
    private static final String DEFAULT_COMMENT = "AAAAA";
    private static final String UPDATED_COMMENT = "BBBBB";

    @Inject
    private SpecificationTemplateRepository specificationTemplateRepository;

    @Inject
    private SpecificationTemplateService specificationTemplateService;

    @Inject
    private SpecificationTemplateSearchRepository specificationTemplateSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSpecificationTemplateMockMvc;

    private SpecificationTemplate specificationTemplate;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SpecificationTemplateResource specificationTemplateResource = new SpecificationTemplateResource();
        ReflectionTestUtils.setField(specificationTemplateResource, "specificationTemplateService", specificationTemplateService);
        this.restSpecificationTemplateMockMvc = MockMvcBuilders.standaloneSetup(specificationTemplateResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        specificationTemplateSearchRepository.deleteAll();
        specificationTemplate = new SpecificationTemplate();
        specificationTemplate.setName(DEFAULT_NAME);
        specificationTemplate.setVersion(DEFAULT_VERSION);
        specificationTemplate.setType(DEFAULT_TYPE);
        specificationTemplate.setComment(DEFAULT_COMMENT);
    }

    @Test
    @Transactional
    public void createSpecificationTemplate() throws Exception {
        int databaseSizeBeforeCreate = specificationTemplateRepository.findAll().size();

        // Create the SpecificationTemplate

        restSpecificationTemplateMockMvc.perform(post("/api/specification-templates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(specificationTemplate)))
                .andExpect(status().isCreated());

        // Validate the SpecificationTemplate in the database
        List<SpecificationTemplate> specificationTemplates = specificationTemplateRepository.findAll();
        assertThat(specificationTemplates).hasSize(databaseSizeBeforeCreate + 1);
        SpecificationTemplate testSpecificationTemplate = specificationTemplates.get(specificationTemplates.size() - 1);
        assertThat(testSpecificationTemplate.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSpecificationTemplate.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testSpecificationTemplate.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testSpecificationTemplate.getComment()).isEqualTo(DEFAULT_COMMENT);

        // Validate the SpecificationTemplate in ElasticSearch
        SpecificationTemplate specificationTemplateEs = specificationTemplateSearchRepository.findOne(testSpecificationTemplate.getId());
        assertThat(specificationTemplateEs).isEqualToComparingFieldByField(testSpecificationTemplate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = specificationTemplateRepository.findAll().size();
        // set the field null
        specificationTemplate.setName(null);

        // Create the SpecificationTemplate, which fails.

        restSpecificationTemplateMockMvc.perform(post("/api/specification-templates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(specificationTemplate)))
                .andExpect(status().isBadRequest());

        List<SpecificationTemplate> specificationTemplates = specificationTemplateRepository.findAll();
        assertThat(specificationTemplates).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkVersionIsRequired() throws Exception {
        int databaseSizeBeforeTest = specificationTemplateRepository.findAll().size();
        // set the field null
        specificationTemplate.setVersion(null);

        // Create the SpecificationTemplate, which fails.

        restSpecificationTemplateMockMvc.perform(post("/api/specification-templates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(specificationTemplate)))
                .andExpect(status().isBadRequest());

        List<SpecificationTemplate> specificationTemplates = specificationTemplateRepository.findAll();
        assertThat(specificationTemplates).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = specificationTemplateRepository.findAll().size();
        // set the field null
        specificationTemplate.setType(null);

        // Create the SpecificationTemplate, which fails.

        restSpecificationTemplateMockMvc.perform(post("/api/specification-templates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(specificationTemplate)))
                .andExpect(status().isBadRequest());

        List<SpecificationTemplate> specificationTemplates = specificationTemplateRepository.findAll();
        assertThat(specificationTemplates).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSpecificationTemplates() throws Exception {
        // Initialize the database
        specificationTemplateRepository.saveAndFlush(specificationTemplate);

        // Get all the specificationTemplates
        restSpecificationTemplateMockMvc.perform(get("/api/specification-templates?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(specificationTemplate.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())));
    }

    @Test
    @Transactional
    public void getSpecificationTemplate() throws Exception {
        // Initialize the database
        specificationTemplateRepository.saveAndFlush(specificationTemplate);

        // Get the specificationTemplate
        restSpecificationTemplateMockMvc.perform(get("/api/specification-templates/{id}", specificationTemplate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(specificationTemplate.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSpecificationTemplate() throws Exception {
        // Get the specificationTemplate
        restSpecificationTemplateMockMvc.perform(get("/api/specification-templates/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSpecificationTemplate() throws Exception {
        // Initialize the database
        specificationTemplateService.save(specificationTemplate);

        int databaseSizeBeforeUpdate = specificationTemplateRepository.findAll().size();

        // Update the specificationTemplate
        SpecificationTemplate updatedSpecificationTemplate = new SpecificationTemplate();
        updatedSpecificationTemplate.setId(specificationTemplate.getId());
        updatedSpecificationTemplate.setName(UPDATED_NAME);
        updatedSpecificationTemplate.setVersion(UPDATED_VERSION);
        updatedSpecificationTemplate.setType(UPDATED_TYPE);
        updatedSpecificationTemplate.setComment(UPDATED_COMMENT);

        restSpecificationTemplateMockMvc.perform(put("/api/specification-templates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSpecificationTemplate)))
                .andExpect(status().isOk());

        // Validate the SpecificationTemplate in the database
        List<SpecificationTemplate> specificationTemplates = specificationTemplateRepository.findAll();
        assertThat(specificationTemplates).hasSize(databaseSizeBeforeUpdate);
        SpecificationTemplate testSpecificationTemplate = specificationTemplates.get(specificationTemplates.size() - 1);
        assertThat(testSpecificationTemplate.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSpecificationTemplate.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testSpecificationTemplate.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSpecificationTemplate.getComment()).isEqualTo(UPDATED_COMMENT);

        // Validate the SpecificationTemplate in ElasticSearch
        SpecificationTemplate specificationTemplateEs = specificationTemplateSearchRepository.findOne(testSpecificationTemplate.getId());
        assertThat(specificationTemplateEs).isEqualToComparingFieldByField(testSpecificationTemplate);
    }

    @Test
    @Transactional
    public void deleteSpecificationTemplate() throws Exception {
        // Initialize the database
        specificationTemplateService.save(specificationTemplate);

        int databaseSizeBeforeDelete = specificationTemplateRepository.findAll().size();

        // Get the specificationTemplate
        restSpecificationTemplateMockMvc.perform(delete("/api/specification-templates/{id}", specificationTemplate.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean specificationTemplateExistsInEs = specificationTemplateSearchRepository.exists(specificationTemplate.getId());
        assertThat(specificationTemplateExistsInEs).isFalse();

        // Validate the database is empty
        List<SpecificationTemplate> specificationTemplates = specificationTemplateRepository.findAll();
        assertThat(specificationTemplates).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSpecificationTemplate() throws Exception {
        // Initialize the database
        specificationTemplateService.save(specificationTemplate);

        // Search the specificationTemplate
        restSpecificationTemplateMockMvc.perform(get("/api/_search/specification-templates?query=id:" + specificationTemplate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(specificationTemplate.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())));
    }
}
