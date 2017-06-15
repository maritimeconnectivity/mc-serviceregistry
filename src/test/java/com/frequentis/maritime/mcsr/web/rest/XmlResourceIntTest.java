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
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.repository.XmlRepository;
import com.frequentis.maritime.mcsr.service.XmlService;
import com.frequentis.maritime.mcsr.repository.search.XmlSearchRepository;

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
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the XmlResource REST controller.
 *
 * @see XmlResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = McsrApp.class)
@WebAppConfiguration
@IntegrationTest
public class XmlResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_COMMENT = "AAAAA";
    private static final String UPDATED_COMMENT = "BBBBB";

    private static final String DEFAULT_CONTENT = "AAAAA";
    private static final String UPDATED_CONTENT = "BBBBB";
    private static final String DEFAULT_CONTENT_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_CONTENT_CONTENT_TYPE = "image/png";

    @Inject
    private XmlRepository xmlRepository;

    @Inject
    private XmlService xmlService;

    @Inject
    private XmlSearchRepository xmlSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restXmlMockMvc;

    private Xml xml;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        XmlResource xmlResource = new XmlResource();
        ReflectionTestUtils.setField(xmlResource, "xmlService", xmlService);
        this.restXmlMockMvc = MockMvcBuilders.standaloneSetup(xmlResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        xmlSearchRepository.deleteAll();
        xml = new Xml();
        xml.setName(DEFAULT_NAME);
        xml.setComment(DEFAULT_COMMENT);
        xml.setContent(DEFAULT_CONTENT);
        xml.setContentContentType(DEFAULT_CONTENT_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createXml() throws Exception {
        int databaseSizeBeforeCreate = xmlRepository.findAll().size();

        // Create the Xml

        restXmlMockMvc.perform(post("/api/xmls")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(xml)))
            .andExpect(status().isCreated());

        // Validate the Xml in the database
        List<Xml> xmls = xmlRepository.findAll();
        assertThat(xmls).hasSize(databaseSizeBeforeCreate + 1);
        Xml testXml = xmls.get(xmls.size() - 1);
        assertThat(testXml.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testXml.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testXml.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testXml.getContentContentType()).isEqualTo(DEFAULT_CONTENT_CONTENT_TYPE);

        // Validate the Xml in ElasticSearch
        Xml xmlEs = xmlSearchRepository.findOne(testXml.getId());
        assertThat(xmlEs).isEqualToComparingFieldByField(testXml);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = xmlRepository.findAll().size();
        // set the field null
        xml.setName(null);

        // Create the Xml, which fails.

        restXmlMockMvc.perform(post("/api/xmls")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(xml)))
            .andExpect(status().isBadRequest());

        List<Xml> xmls = xmlRepository.findAll();
        assertThat(xmls).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkContentIsRequired() throws Exception {
        int databaseSizeBeforeTest = xmlRepository.findAll().size();
        // set the field null
        xml.setContent(null);

        // Create the Xml, which fails.

        restXmlMockMvc.perform(post("/api/xmls")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(xml)))
            .andExpect(status().isBadRequest());

        List<Xml> xmls = xmlRepository.findAll();
        assertThat(xmls).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllXmls() throws Exception {
        // Initialize the database
        xmlRepository.saveAndFlush(xml);

        // Get all the xmls
        restXmlMockMvc.perform(get("/api/xmls?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(xml.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].contentContentType").value(hasItem(DEFAULT_CONTENT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())));
    }

    @Test
    @Transactional
    public void getXml() throws Exception {
        // Initialize the database
        xmlRepository.saveAndFlush(xml);

        // Get the xml
        restXmlMockMvc.perform(get("/api/xmls/{id}", xml.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(xml.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.contentContentType").value(DEFAULT_CONTENT_CONTENT_TYPE))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingXml() throws Exception {
        // Get the xml
        restXmlMockMvc.perform(get("/api/xmls/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateXml() throws Exception {
        // Initialize the database
        xmlService.save(xml);

        int databaseSizeBeforeUpdate = xmlRepository.findAll().size();

        // Update the xml
        Xml updatedXml = new Xml();
        updatedXml.setId(xml.getId());
        updatedXml.setName(UPDATED_NAME);
        updatedXml.setComment(UPDATED_COMMENT);
        updatedXml.setContent(UPDATED_CONTENT);
        updatedXml.setContentContentType(UPDATED_CONTENT_CONTENT_TYPE);

        restXmlMockMvc.perform(put("/api/xmls")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedXml)))
                .andExpect(status().isOk());

        // Validate the Xml in the database
        List<Xml> xmls = xmlRepository.findAll();
        assertThat(xmls).hasSize(databaseSizeBeforeUpdate);
        Xml testXml = xmls.get(xmls.size() - 1);
        assertThat(testXml.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testXml.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testXml.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testXml.getContentContentType()).isEqualTo(UPDATED_CONTENT_CONTENT_TYPE);

        // Validate the Xml in ElasticSearch
        Xml xmlEs = xmlSearchRepository.findOne(testXml.getId());
        assertThat(xmlEs).isEqualToComparingFieldByField(testXml);
    }

    @Test
    @Transactional
    public void deleteXml() throws Exception {
        // Initialize the database
        xmlService.save(xml);

        int databaseSizeBeforeDelete = xmlRepository.findAll().size();

        // Get the xml
        restXmlMockMvc.perform(delete("/api/xmls/{id}", xml.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean xmlExistsInEs = xmlSearchRepository.exists(xml.getId());
        assertThat(xmlExistsInEs).isFalse();

        // Validate the database is empty
        List<Xml> xmls = xmlRepository.findAll();
        assertThat(xmls).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchXml() throws Exception {
        // Initialize the database
        xmlService.save(xml);

        // Search the xml
        restXmlMockMvc.perform(get("/api/_search/xmls?query=id:" + xml.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(xml.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].contentContentType").value(hasItem(DEFAULT_CONTENT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())));
    }
}
