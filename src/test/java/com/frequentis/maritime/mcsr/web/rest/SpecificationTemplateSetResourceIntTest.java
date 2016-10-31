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
import com.frequentis.maritime.mcsr.domain.SpecificationTemplateSet;
import com.frequentis.maritime.mcsr.repository.SpecificationTemplateSetRepository;
import com.frequentis.maritime.mcsr.service.SpecificationTemplateSetService;
import com.frequentis.maritime.mcsr.repository.search.SpecificationTemplateSetSearchRepository;

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


/**
 * Test class for the SpecificationTemplateSetResource REST controller.
 *
 * @see SpecificationTemplateSetResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = McsrApp.class)
@WebAppConfiguration
@IntegrationTest
public class SpecificationTemplateSetResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_VERSION = "AAAAA";
    private static final String UPDATED_VERSION = "BBBBB";
    private static final String DEFAULT_COMMENT = "AAAAA";
    private static final String UPDATED_COMMENT = "BBBBB";

    @Inject
    private SpecificationTemplateSetRepository specificationTemplateSetRepository;

    @Inject
    private SpecificationTemplateSetService specificationTemplateSetService;

    @Inject
    private SpecificationTemplateSetSearchRepository specificationTemplateSetSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSpecificationTemplateSetMockMvc;

    private SpecificationTemplateSet specificationTemplateSet;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SpecificationTemplateSetResource specificationTemplateSetResource = new SpecificationTemplateSetResource();
        ReflectionTestUtils.setField(specificationTemplateSetResource, "specificationTemplateSetService", specificationTemplateSetService);
        this.restSpecificationTemplateSetMockMvc = MockMvcBuilders.standaloneSetup(specificationTemplateSetResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        specificationTemplateSetSearchRepository.deleteAll();
        specificationTemplateSet = new SpecificationTemplateSet();
        specificationTemplateSet.setName(DEFAULT_NAME);
        specificationTemplateSet.setVersion(DEFAULT_VERSION);
        specificationTemplateSet.setComment(DEFAULT_COMMENT);
    }

    @Test
    @Transactional
    public void createSpecificationTemplateSet() throws Exception {
        int databaseSizeBeforeCreate = specificationTemplateSetRepository.findAll().size();

        // Create the SpecificationTemplateSet

        restSpecificationTemplateSetMockMvc.perform(post("/api/specification-template-sets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(specificationTemplateSet)))
                .andExpect(status().isCreated());

        // Validate the SpecificationTemplateSet in the database
        List<SpecificationTemplateSet> specificationTemplateSets = specificationTemplateSetRepository.findAll();
        assertThat(specificationTemplateSets).hasSize(databaseSizeBeforeCreate + 1);
        SpecificationTemplateSet testSpecificationTemplateSet = specificationTemplateSets.get(specificationTemplateSets.size() - 1);
        assertThat(testSpecificationTemplateSet.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSpecificationTemplateSet.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testSpecificationTemplateSet.getComment()).isEqualTo(DEFAULT_COMMENT);

        // Validate the SpecificationTemplateSet in ElasticSearch
        SpecificationTemplateSet specificationTemplateSetEs = specificationTemplateSetSearchRepository.findOne(testSpecificationTemplateSet.getId());
        assertThat(specificationTemplateSetEs).isEqualToComparingFieldByField(testSpecificationTemplateSet);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = specificationTemplateSetRepository.findAll().size();
        // set the field null
        specificationTemplateSet.setName(null);

        // Create the SpecificationTemplateSet, which fails.

        restSpecificationTemplateSetMockMvc.perform(post("/api/specification-template-sets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(specificationTemplateSet)))
                .andExpect(status().isBadRequest());

        List<SpecificationTemplateSet> specificationTemplateSets = specificationTemplateSetRepository.findAll();
        assertThat(specificationTemplateSets).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkVersionIsRequired() throws Exception {
        int databaseSizeBeforeTest = specificationTemplateSetRepository.findAll().size();
        // set the field null
        specificationTemplateSet.setVersion(null);

        // Create the SpecificationTemplateSet, which fails.

        restSpecificationTemplateSetMockMvc.perform(post("/api/specification-template-sets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(specificationTemplateSet)))
                .andExpect(status().isBadRequest());

        List<SpecificationTemplateSet> specificationTemplateSets = specificationTemplateSetRepository.findAll();
        assertThat(specificationTemplateSets).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSpecificationTemplateSets() throws Exception {
        // Initialize the database
        specificationTemplateSetRepository.saveAndFlush(specificationTemplateSet);

        // Get all the specificationTemplateSets
        restSpecificationTemplateSetMockMvc.perform(get("/api/specification-template-sets?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(specificationTemplateSet.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())));
    }

    @Test
    @Transactional
    public void getSpecificationTemplateSet() throws Exception {
        // Initialize the database
        specificationTemplateSetRepository.saveAndFlush(specificationTemplateSet);

        // Get the specificationTemplateSet
        restSpecificationTemplateSetMockMvc.perform(get("/api/specification-template-sets/{id}", specificationTemplateSet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(specificationTemplateSet.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSpecificationTemplateSet() throws Exception {
        // Get the specificationTemplateSet
        restSpecificationTemplateSetMockMvc.perform(get("/api/specification-template-sets/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSpecificationTemplateSet() throws Exception {
        // Initialize the database
        specificationTemplateSetService.save(specificationTemplateSet);

        int databaseSizeBeforeUpdate = specificationTemplateSetRepository.findAll().size();

        // Update the specificationTemplateSet
        SpecificationTemplateSet updatedSpecificationTemplateSet = new SpecificationTemplateSet();
        updatedSpecificationTemplateSet.setId(specificationTemplateSet.getId());
        updatedSpecificationTemplateSet.setName(UPDATED_NAME);
        updatedSpecificationTemplateSet.setVersion(UPDATED_VERSION);
        updatedSpecificationTemplateSet.setComment(UPDATED_COMMENT);

        restSpecificationTemplateSetMockMvc.perform(put("/api/specification-template-sets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSpecificationTemplateSet)))
                .andExpect(status().isOk());

        // Validate the SpecificationTemplateSet in the database
        List<SpecificationTemplateSet> specificationTemplateSets = specificationTemplateSetRepository.findAll();
        assertThat(specificationTemplateSets).hasSize(databaseSizeBeforeUpdate);
        SpecificationTemplateSet testSpecificationTemplateSet = specificationTemplateSets.get(specificationTemplateSets.size() - 1);
        assertThat(testSpecificationTemplateSet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSpecificationTemplateSet.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testSpecificationTemplateSet.getComment()).isEqualTo(UPDATED_COMMENT);

        // Validate the SpecificationTemplateSet in ElasticSearch
        SpecificationTemplateSet specificationTemplateSetEs = specificationTemplateSetSearchRepository.findOne(testSpecificationTemplateSet.getId());
        assertThat(specificationTemplateSetEs).isEqualToComparingFieldByField(testSpecificationTemplateSet);
    }

    @Test
    @Transactional
    public void deleteSpecificationTemplateSet() throws Exception {
        // Initialize the database
        specificationTemplateSetService.save(specificationTemplateSet);

        int databaseSizeBeforeDelete = specificationTemplateSetRepository.findAll().size();

        // Get the specificationTemplateSet
        restSpecificationTemplateSetMockMvc.perform(delete("/api/specification-template-sets/{id}", specificationTemplateSet.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean specificationTemplateSetExistsInEs = specificationTemplateSetSearchRepository.exists(specificationTemplateSet.getId());
        assertThat(specificationTemplateSetExistsInEs).isFalse();

        // Validate the database is empty
        List<SpecificationTemplateSet> specificationTemplateSets = specificationTemplateSetRepository.findAll();
        assertThat(specificationTemplateSets).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSpecificationTemplateSet() throws Exception {
        // Initialize the database
        specificationTemplateSetService.save(specificationTemplateSet);

        // Search the specificationTemplateSet
        restSpecificationTemplateSetMockMvc.perform(get("/api/_search/specification-template-sets?query=id:" + specificationTemplateSet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(specificationTemplateSet.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())));
    }
}
