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
import com.frequentis.maritime.mcsr.domain.Xsd;
import com.frequentis.maritime.mcsr.service.XsdService;
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
 * REST controller for managing Xsd.
 */
@RestController
@RequestMapping("/api")
public class XsdResource {

    private final Logger log = LoggerFactory.getLogger(XsdResource.class);

    @Inject
    private XsdService xsdService;

    /**
     * POST  /xsds : Create a new xsd.
     *
     * @param xsd the xsd to create
     * @return the ResponseEntity with status 201 (Created) and with body the new xsd, or with status 400 (Bad Request) if the xsd has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/xsds",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Xsd> createXsd(@Valid @RequestBody Xsd xsd) throws URISyntaxException {
        log.debug("REST request to save Xsd : {}", xsd);
        if (xsd.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("xsd", "idexists", "A new xsd cannot already have an ID")).body(null);
        }
        Xsd result = xsdService.save(xsd);
        return ResponseEntity.created(new URI("/api/xsds/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("xsd", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /xsds : Updates an existing xsd.
     *
     * @param xsd the xsd to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated xsd,
     * or with status 400 (Bad Request) if the xsd is not valid,
     * or with status 500 (Internal Server Error) if the xsd couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/xsds",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Xsd> updateXsd(@Valid @RequestBody Xsd xsd) throws URISyntaxException {
        log.debug("REST request to update Xsd : {}", xsd);
        if (xsd.getId() == null) {
            return createXsd(xsd);
        }
        Xsd result = xsdService.save(xsd);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("xsd", xsd.getId().toString()))
            .body(result);
    }

    /**
     * GET  /xsds : get all the xsds.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of xsds in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/xsds",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Xsd>> getAllXsds(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Xsds");
        Page<Xsd> page = xsdService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/xsds");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /xsds/:id : get the "id" xsd.
     *
     * @param id the id of the xsd to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the xsd, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/xsds/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Xsd> getXsd(@PathVariable Long id) {
        log.debug("REST request to get Xsd : {}", id);
        Xsd xsd = xsdService.findOne(id);
        return Optional.ofNullable(xsd)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /xsds/:id : delete the "id" xsd.
     *
     * @param id the id of the xsd to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/xsds/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteXsd(@PathVariable Long id) {
        log.debug("REST request to delete Xsd : {}", id);
        xsdService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("xsd", id.toString())).build();
    }

    /**
     * SEARCH  /_search/xsds?query=:query : search for the xsd corresponding
     * to the query.
     *
     * @param query the query of the xsd search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/xsds",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Xsd>> searchXsds(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Xsds for query {}", query);
        Page<Xsd> page = xsdService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/xsds");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
