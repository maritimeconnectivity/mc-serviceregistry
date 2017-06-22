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

import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.repository.DocRepository;
import com.frequentis.maritime.mcsr.repository.search.DocSearchRepository;
import com.frequentis.maritime.mcsr.service.DocService;


/**
 * Test class for the DocResource REST controller.
 *
 * @see DocResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@WithMockUser("test-user")
public class DocResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_COMMENT = "AAAAA";
    private static final String UPDATED_COMMENT = "BBBBB";
    private static final String DEFAULT_MIMETYPE = "AAAAA";
    private static final String UPDATED_MIMETYPE = "BBBBB";

    private static final byte[] DEFAULT_FILECONTENT = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILECONTENT = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_FILECONTENT_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILECONTENT_CONTENT_TYPE = "image/png";

    @Inject
    private DocRepository docRepository;

    @Inject
    private DocService docService;

    @Inject
    private DocSearchRepository docSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restDocMockMvc;

    private Doc doc;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DocResource docResource = new DocResource();
        ReflectionTestUtils.setField(docResource, "docService", docService);
        this.restDocMockMvc = MockMvcBuilders.standaloneSetup(docResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        docSearchRepository.deleteAll();
        doc = new Doc();
        doc.setName(DEFAULT_NAME);
        doc.setComment(DEFAULT_COMMENT);
        doc.setMimetype(DEFAULT_MIMETYPE);
        doc.setFilecontent(DEFAULT_FILECONTENT);
        doc.setFilecontentContentType(DEFAULT_FILECONTENT_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createDoc() throws Exception {
        int databaseSizeBeforeCreate = docRepository.findAll().size();

        // Create the Doc

        restDocMockMvc.perform(post("/api/docs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(doc)))
                .andExpect(status().isCreated());

        // Validate the Doc in the database
        List<Doc> docs = docRepository.findAll();
        assertThat(docs).hasSize(databaseSizeBeforeCreate + 1);
        Doc testDoc = docs.get(docs.size() - 1);
        assertThat(testDoc.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDoc.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testDoc.getMimetype()).isEqualTo(DEFAULT_MIMETYPE);
        assertThat(testDoc.getFilecontent()).isEqualTo(DEFAULT_FILECONTENT);
        assertThat(testDoc.getFilecontentContentType()).isEqualTo(DEFAULT_FILECONTENT_CONTENT_TYPE);

        // Validate the Doc in ElasticSearch
        Doc docEs = docSearchRepository.findById(testDoc.getId()).get();
        assertThat(docEs).isEqualToComparingFieldByField(testDoc);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = docRepository.findAll().size();
        // set the field null
        doc.setName(null);

        // Create the Doc, which fails.

        restDocMockMvc.perform(post("/api/docs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(doc)))
                .andExpect(status().isBadRequest());

        List<Doc> docs = docRepository.findAll();
        assertThat(docs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMimetypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = docRepository.findAll().size();
        // set the field null
        doc.setMimetype(null);

        // Create the Doc, which fails.

        restDocMockMvc.perform(post("/api/docs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(doc)))
                .andExpect(status().isBadRequest());

        List<Doc> docs = docRepository.findAll();
        assertThat(docs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkFilecontentIsRequired() throws Exception {
        int databaseSizeBeforeTest = docRepository.findAll().size();
        // set the field null
        doc.setFilecontent(null);

        // Create the Doc, which fails.

        restDocMockMvc.perform(post("/api/docs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(doc)))
                .andExpect(status().isBadRequest());

        List<Doc> docs = docRepository.findAll();
        assertThat(docs).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDocs() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get all the docs
        restDocMockMvc.perform(get("/api/docs?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(doc.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].mimetype").value(hasItem(DEFAULT_MIMETYPE.toString())))
                .andExpect(jsonPath("$.[*].filecontentContentType").value(hasItem(DEFAULT_FILECONTENT_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].filecontent").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILECONTENT))));
    }

    @Test
    @Transactional
    public void getDoc() throws Exception {
        // Initialize the database
        docRepository.saveAndFlush(doc);

        // Get the doc
        restDocMockMvc.perform(get("/api/docs/{id}", doc.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(doc.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.mimetype").value(DEFAULT_MIMETYPE.toString()))
            .andExpect(jsonPath("$.filecontentContentType").value(DEFAULT_FILECONTENT_CONTENT_TYPE))
            .andExpect(jsonPath("$.filecontent").value(Base64Utils.encodeToString(DEFAULT_FILECONTENT)));
    }

    @Test
    @Transactional
    public void getNonExistingDoc() throws Exception {
        // Get the doc
        restDocMockMvc.perform(get("/api/docs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDoc() throws Exception {
        // Initialize the database
        docService.save(doc);

        int databaseSizeBeforeUpdate = docRepository.findAll().size();

        // Update the doc
        Doc updatedDoc = new Doc();
        updatedDoc.setId(doc.getId());
        updatedDoc.setName(UPDATED_NAME);
        updatedDoc.setComment(UPDATED_COMMENT);
        updatedDoc.setMimetype(UPDATED_MIMETYPE);
        updatedDoc.setFilecontent(UPDATED_FILECONTENT);
        updatedDoc.setFilecontentContentType(UPDATED_FILECONTENT_CONTENT_TYPE);

        restDocMockMvc.perform(put("/api/docs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedDoc)))
                .andExpect(status().isOk());

        // Validate the Doc in the database
        List<Doc> docs = docRepository.findAll();
        assertThat(docs).hasSize(databaseSizeBeforeUpdate);
        Doc testDoc = docs.get(docs.size() - 1);
        assertThat(testDoc.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDoc.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testDoc.getMimetype()).isEqualTo(UPDATED_MIMETYPE);
        assertThat(testDoc.getFilecontent()).isEqualTo(UPDATED_FILECONTENT);
        assertThat(testDoc.getFilecontentContentType()).isEqualTo(UPDATED_FILECONTENT_CONTENT_TYPE);

        // Validate the Doc in ElasticSearch
        Doc docEs = docSearchRepository.findById(testDoc.getId()).get();
        assertThat(docEs).isEqualToComparingFieldByField(testDoc);
    }

    @Test
    @Transactional
    public void deleteDoc() throws Exception {
        // Initialize the database
        docService.save(doc);

        int databaseSizeBeforeDelete = docRepository.findAll().size();

        // Get the doc
        restDocMockMvc.perform(delete("/api/docs/{id}", doc.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean docExistsInEs = docSearchRepository.existsById(doc.getId());
        assertThat(docExistsInEs).isFalse();

        // Validate the database is empty
        List<Doc> docs = docRepository.findAll();
        assertThat(docs).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchDoc() throws Exception {
        // Initialize the database
        docService.save(doc);

        // Search the doc
        restDocMockMvc.perform(get("/api/_search/docs?query=id:" + doc.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(doc.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].mimetype").value(hasItem(DEFAULT_MIMETYPE.toString())))
            .andExpect(jsonPath("$.[*].filecontentContentType").value(hasItem(DEFAULT_FILECONTENT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].filecontent").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILECONTENT))));
    }
}
