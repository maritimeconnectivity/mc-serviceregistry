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

import com.codahale.metrics.annotation.Timed;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.service.XmlService;
import com.frequentis.maritime.mcsr.web.rest.util.HeaderUtil;
import com.frequentis.maritime.mcsr.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Xml.
 */
@RestController
@RequestMapping("/api")
public class XmlResource {

    private final Logger log = LoggerFactory.getLogger(XmlResource.class);

    @Inject
    private XmlService xmlService;

    /**
     * POST  /xmls : Create a new xml.
     *
     * @param xml the xml to create
     * @return the ResponseEntity with status 201 (Created) and with body the new xml, or with status 400 (Bad Request) if the xml has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/xmls",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Xml> createXml(@Valid @RequestBody Xml xml) throws URISyntaxException {
        log.debug("REST request to save Xml : {}", xml);
        if (xml.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("xml", "idexists", "A new xml cannot already have an ID")).body(null);
        }
        Xml result = xmlService.save(xml);
        return ResponseEntity.created(new URI("/api/xmls/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("xml", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /xmls : Updates an existing xml.
     *
     * @param xml the xml to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated xml,
     * or with status 400 (Bad Request) if the xml is not valid,
     * or with status 500 (Internal Server Error) if the xml couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/xmls",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Xml> updateXml(@Valid @RequestBody Xml xml) throws URISyntaxException {
        log.debug("REST request to update Xml : {}", xml);
        if (xml.getId() == null) {
            return createXml(xml);
        }
        Xml result = xmlService.save(xml);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("xml", xml.getId().toString()))
            .body(result);
    }

    /**
     * GET  /xmls : get all the xmls.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of xmls in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/xmls",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Xml>> getAllXmls(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Xmls");
        Page<Xml> page = xmlService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/xmls");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /xmls/:id : get the "id" xml.
     *
     * @param id the id of the xml to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the xml, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/xmls/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Xml> getXml(@PathVariable Long id) {
        log.debug("REST request to get Xml : {}", id);
        Xml xml = xmlService.findOne(id);
        return Optional.ofNullable(xml)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /xmls/:id : delete the "id" xml.
     *
     * @param id the id of the xml to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/xmls/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteXml(@PathVariable Long id) {
        log.debug("REST request to delete Xml : {}", id);
        xmlService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("xml", id.toString())).build();
    }

    /**
     * SEARCH  /_search/xmls?query=:query : search for the xml corresponding
     * to the query.
     *
     * @param query the query of the xml search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/xmls",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Xml>> searchXmls(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Xmls for query {}", query);
        Page<Xml> page = xmlService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/xmls");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
