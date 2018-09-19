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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.service.DocService;
import com.frequentis.maritime.mcsr.web.rest.util.HeaderUtil;
import com.frequentis.maritime.mcsr.web.rest.util.PaginationUtil;

/**
 * REST controller for managing Doc.
 */
@RestController
@RequestMapping("/api")
public class DocResource {

    private final Logger log = LoggerFactory.getLogger(DocResource.class);

    @Inject
    private DocService docService;

    /**
     * POST  /docs : Create a new doc.
     *
     * @param doc the doc to create
     * @return the ResponseEntity with status 201 (Created) and with body the new doc, or with status 400 (Bad Request) if the doc has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/docs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Doc> createDoc(@Valid @RequestBody Doc doc) throws URISyntaxException {
        log.debug("REST request to save Doc : {}", doc);
        if (doc.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("doc", "idexists", "A new doc cannot already have an ID")).body(null);
        }
        if (doc.getFilecontentContentType() == null ||
	    (doc.getFilecontentContentType().equalsIgnoreCase("application/pdf") &&
	    doc.getFilecontentContentType().equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document") &&
	    doc.getFilecontentContentType().equalsIgnoreCase("application/vnd.oasis.opendocument.text")
	    )
	) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("doc", "formaterror", "Unsupported document format. Only PDF, ODT or DOCX are allowed.")).body(null);
        }
        Doc result = docService.save(doc);
        return ResponseEntity.created(new URI("/api/docs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("doc", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /docs : Updates an existing doc.
     *
     * @param doc the doc to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated doc,
     * or with status 400 (Bad Request) if the doc is not valid,
     * or with status 500 (Internal Server Error) if the doc couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/docs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Doc> updateDoc(@Valid @RequestBody Doc doc) throws URISyntaxException {
        log.debug("REST request to update Doc : {}", doc);
        if (doc.getId() == null) {
            return createDoc(doc);
        }
        Doc result = docService.save(doc);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("doc", doc.getId().toString()))
            .body(result);
    }

    /**
     * GET  /docs : get all the docs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of docs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/docs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Doc>> getAllDocs(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Docs");
        Page<Doc> page = docService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/docs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /docs/:id : get the "id" doc.
     *
     * @param id the id of the doc to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the doc, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/docs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Doc> getDoc(@PathVariable Long id) {
        log.debug("REST request to get Doc : {}", id);
        Doc doc = docService.findOne(id);
        return Optional.ofNullable(doc)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /docs/:id : delete the "id" doc.
     *
     * @param id the id of the doc to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/docs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteDoc(@PathVariable Long id) {
        log.debug("REST request to delete Doc : {}", id);
        docService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("doc", id.toString())).build();
    }

    /**
     * SEARCH  /_search/docs?query=:query : search for the doc corresponding
     * to the query.
     *
     * @param query the query of the doc search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/docs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Doc>> searchDocs(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Docs for query {}", query);
        Page<Doc> page = docService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/docs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
