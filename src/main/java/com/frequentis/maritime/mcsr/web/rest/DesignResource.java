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
import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.service.DesignService;
import com.frequentis.maritime.mcsr.web.rest.util.HeaderUtil;
import com.frequentis.maritime.mcsr.web.rest.util.PaginationUtil;
import com.frequentis.maritime.mcsr.web.rest.util.XmlUtil;

import springfox.documentation.annotations.ApiIgnore;

/**
 * REST controller for managing Design.
 */
@RestController
@RequestMapping("/api")
@ApiIgnore
public class DesignResource {

    private final Logger log = LoggerFactory.getLogger(DesignResource.class);

    @Inject
    private DesignService designService;

    /**
     * POST  /designs : Create a new design.
     *
     * @param design the design to create
     * @return the ResponseEntity with status 201 (Created) and with body the new design, or with status 400 (Bad Request) if the design has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/designs",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Design> createDesign(@Valid @RequestBody Design design) throws URISyntaxException {
        log.debug("REST request to save Design : {}", design);
        if (design.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("design", "idexists", "A new design cannot already have an ID")).body(null);
        }
        if(design.getDesignAsXml() != null) {
            try {
                String xml = design.getDesignAsXml().getContent().toString();
                log.info("XML:" + xml);
                XmlUtil.validateXml(xml, "ServiceDesignSchema.xsd");
            } catch (Exception e) {
                log.error("Error parsing xml: ", e);
                return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert("design", e.getMessage(), e.toString()))
                    .body(design);
            }
        }
        Design result = designService.save(design);
        return ResponseEntity.created(new URI("/api/designs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("design", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /designs : Updates an existing design.
     *
     * @param design the design to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated design,
     * or with status 400 (Bad Request) if the design is not valid,
     * or with status 500 (Internal Server Error) if the design couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/designs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Design> updateDesign(@Valid @RequestBody Design design) throws URISyntaxException {
        log.debug("REST request to update Design : {}", design);
        if (design.getId() == null) {
            return createDesign(design);
        }
        if(design.getDesignAsXml() != null) {
            try {
                String xml = design.getDesignAsXml().getContent().toString();
                log.info("XML:" + xml);
                XmlUtil.validateXml(xml, "ServiceDesignSchema.xsd");
            } catch (Exception e) {
                log.error("Error parsing xml: ", e);
                return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert("design", e.getMessage(), e.toString()))
                    .body(design);
            }
        }

        Design result = designService.save(design);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("design", design.getId().toString()))
            .body(result);
    }

    /**
     * GET  /designs : get all the designs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of designs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/designs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Design>> getAllDesigns(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Designs");
        Page<Design> page = designService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/designs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /designs/:id : get the "id" design.
     *
     * @param id the id of the design to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the design, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/designs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Design> getDesign(@PathVariable Long id) {
        log.debug("REST request to get Design : {}", id);
        Design design = designService.findOne(id);
        return Optional.ofNullable(design)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /designs/:id : delete the "id" design.
     *
     * @param id the id of the design to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/designs/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteDesign(@PathVariable Long id) {
        log.debug("REST request to delete Design : {}", id);
        designService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("design", id.toString())).build();
    }

    /**
     * SEARCH  /_search/designs?query=:query : search for the design corresponding
     * to the query.
     *
     * @param query the query of the design search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/designs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Design>> searchDesigns(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Designs for query {}", query);
        Page<Design> page = designService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/designs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
