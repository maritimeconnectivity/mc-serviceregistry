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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
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
import org.springframework.util.Base64Utils;

import com.frequentis.maritime.mcsr.domain.Xsd;
import com.frequentis.maritime.mcsr.repository.XsdRepository;
import com.frequentis.maritime.mcsr.repository.search.XsdSearchRepository;
import com.frequentis.maritime.mcsr.service.XsdService;


/**
 * Test class for the XsdResource REST controller.
 *
 * @see XsdResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@WithMockUser("test-user")
public class XsdResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_COMMENT = "AAAAA";
    private static final String UPDATED_COMMENT = "BBBBB";

    private static final byte[] DEFAULT_CONTENT = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_CONTENT = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_CONTENT_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_CONTENT_CONTENT_TYPE = "image/png";

    @Inject
    private XsdRepository xsdRepository;

    @Inject
    private XsdService xsdService;

    @Inject
    private XsdSearchRepository xsdSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restXsdMockMvc;

    private Xsd xsd;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        XsdResource xsdResource = new XsdResource();
        ReflectionTestUtils.setField(xsdResource, "xsdService", xsdService);
        this.restXsdMockMvc = MockMvcBuilders.standaloneSetup(xsdResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        xsdSearchRepository.deleteAll();
        xsd = new Xsd();
        xsd.setName(DEFAULT_NAME);
        xsd.setComment(DEFAULT_COMMENT);
        xsd.setContent(DEFAULT_CONTENT);
        xsd.setContentContentType(DEFAULT_CONTENT_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createXsd() throws Exception {
        int databaseSizeBeforeCreate = xsdRepository.findAll().size();

        // Create the Xsd

        restXsdMockMvc.perform(post("/api/xsds")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(xsd)))
                .andExpect(status().isCreated());

        // Validate the Xsd in the database
        List<Xsd> xsds = xsdRepository.findAll();
        assertThat(xsds).hasSize(databaseSizeBeforeCreate + 1);
        Xsd testXsd = xsds.get(xsds.size() - 1);
        assertThat(testXsd.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testXsd.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testXsd.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testXsd.getContentContentType()).isEqualTo(DEFAULT_CONTENT_CONTENT_TYPE);

        // Validate the Xsd in ElasticSearch
        Xsd xsdEs = xsdSearchRepository.findById(testXsd.getId()).orElse(null);
        assertThat(xsdEs).isEqualToComparingFieldByField(testXsd);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = xsdRepository.findAll().size();
        // set the field null
        xsd.setName(null);

        // Create the Xsd, which fails.

        restXsdMockMvc.perform(post("/api/xsds")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(xsd)))
                .andExpect(status().isBadRequest());

        List<Xsd> xsds = xsdRepository.findAll();
        assertThat(xsds).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkContentIsRequired() throws Exception {
        int databaseSizeBeforeTest = xsdRepository.findAll().size();
        // set the field null
        xsd.setContent(null);

        // Create the Xsd, which fails.

        restXsdMockMvc.perform(post("/api/xsds")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(xsd)))
                .andExpect(status().isBadRequest());

        List<Xsd> xsds = xsdRepository.findAll();
        assertThat(xsds).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllXsds() throws Exception {
        // Initialize the database
        xsdRepository.saveAndFlush(xsd);

        // Get all the xsds
        restXsdMockMvc.perform(get("/api/xsds?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.[*].id").value(hasItem(xsd.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].contentContentType").value(hasItem(DEFAULT_CONTENT_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].content").value(hasItem(Base64Utils.encodeToString(DEFAULT_CONTENT))));
    }

    @Test
    @Transactional
    public void getXsd() throws Exception {
        // Initialize the database
        xsdRepository.saveAndFlush(xsd);

        // Get the xsd
        restXsdMockMvc.perform(get("/api/xsds/{id}", xsd.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.id").value(xsd.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.contentContentType").value(DEFAULT_CONTENT_CONTENT_TYPE))
            .andExpect(jsonPath("$.content").value(Base64Utils.encodeToString(DEFAULT_CONTENT)));
    }

    @Test
    @Transactional
    public void getNonExistingXsd() throws Exception {
        // Get the xsd
        restXsdMockMvc.perform(get("/api/xsds/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateXsd() throws Exception {
        // Initialize the database
        xsdService.save(xsd);

        int databaseSizeBeforeUpdate = xsdRepository.findAll().size();

        // Update the xsd
        Xsd updatedXsd = new Xsd();
        updatedXsd.setId(xsd.getId());
        updatedXsd.setName(UPDATED_NAME);
        updatedXsd.setComment(UPDATED_COMMENT);
        updatedXsd.setContent(UPDATED_CONTENT);
        updatedXsd.setContentContentType(UPDATED_CONTENT_CONTENT_TYPE);

        restXsdMockMvc.perform(put("/api/xsds")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedXsd)))
                .andExpect(status().isOk());

        // Validate the Xsd in the database
        List<Xsd> xsds = xsdRepository.findAll();
        assertThat(xsds).hasSize(databaseSizeBeforeUpdate);
        Xsd testXsd = xsds.get(xsds.size() - 1);
        assertThat(testXsd.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testXsd.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testXsd.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testXsd.getContentContentType()).isEqualTo(UPDATED_CONTENT_CONTENT_TYPE);

        // Validate the Xsd in ElasticSearch
        Xsd xsdEs = xsdSearchRepository.findById(testXsd.getId()).orElse(null);
        assertThat(xsdEs).isEqualToComparingFieldByField(testXsd);
    }

    @Test
    @Transactional
    public void deleteXsd() throws Exception {
        // Initialize the database
        xsdService.save(xsd);

        int databaseSizeBeforeDelete = xsdRepository.findAll().size();

        // Get the xsd
        restXsdMockMvc.perform(delete("/api/xsds/{id}", xsd.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        // FIXME HOTFIX! There is SD-ES bug DATAES-363
        //boolean xsdExistsInEs = xsdSearchRepository.existsById(xsd.getId());
        boolean xsdExistsInEs = xsdSearchRepository.findById(xsd.getId()).isPresent();
        assertThat(xsdExistsInEs).isFalse();

        // Validate the database is empty
        List<Xsd> xsds = xsdRepository.findAll();
        assertThat(xsds).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchXsd() throws Exception {
        // Initialize the database
        xsdService.save(xsd);

        // Search the xsd
        restXsdMockMvc.perform(get("/api/_search/xsds?query=id:" + xsd.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.[*].id").value(hasItem(xsd.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].contentContentType").value(hasItem(DEFAULT_CONTENT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(Base64Utils.encodeToString(DEFAULT_CONTENT))));
    }
}
