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
import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.service.SpecificationService;
import com.frequentis.maritime.mcsr.web.rest.util.HeaderUtil;
import com.frequentis.maritime.mcsr.web.rest.util.PaginationUtil;
import com.frequentis.maritime.mcsr.web.rest.util.XmlUtil;

/**
 * REST controller for managing Specification.
 */
@RestController
@RequestMapping("/api")
public class SpecificationResource {

    private final Logger log = LoggerFactory.getLogger(SpecificationResource.class);

    @Inject
    private SpecificationService specificationService;

    /**
     * POST  /specifications : Create a new specification.
     *
     * @param specification the specification to create
     * @return the ResponseEntity with status 201 (Created) and with body the new specification, or with status 400 (Bad Request) if the specification has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/specifications",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Specification> createSpecification(@Valid @RequestBody Specification specification) throws URISyntaxException {
        log.debug("REST request to save Specification : {}", specification);
        if (specification.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("specification", "idexists", "A new specification cannot already have an ID")).body(null);
        }
        try {
            String xml = specification.getSpecAsXml().getContent().toString();
            log.info("XML:" + xml);
            XmlUtil.validateXml(xml, "ServiceSpecificationSchema.xsd");
        } catch (Exception e) {
            log.error("Error parsing xml: ", e);
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("specification", e.getMessage(), e.toString()))
                .body(specification);
        }
        Specification result = specificationService.save(specification);
        return ResponseEntity.created(new URI("/api/specifications/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("specification", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /specifications : Updates an existing specification.
     *
     * @param specification the specification to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated specification,
     * or with status 400 (Bad Request) if the specification is not valid,
     * or with status 500 (Internal Server Error) if the specification couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/specifications",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Specification> updateSpecification(@Valid @RequestBody Specification specification) throws URISyntaxException {
        log.debug("REST request to update Specification : {}", specification);
        if (specification.getId() == null) {
            return createSpecification(specification);
        }
        try {
            String xml = specification.getSpecAsXml().getContent().toString();
            log.info("XML:" + xml);
            XmlUtil.validateXml(xml, "ServiceSpecificationSchema.xsd");
        } catch (Exception e) {
            log.error("Error parsing xml: ", e);
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("specification", e.getMessage(), e.toString()))
                .body(specification);
        }
        Specification result = specificationService.save(specification);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("specification", specification.getId().toString()))
            .body(result);
    }

    /**
     * GET  /specifications : get all the specifications.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of specifications in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/specifications",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Specification>> getAllSpecifications(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Specifications");
        Page<Specification> page = specificationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/specifications");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /specifications/:id : get the "id" specification.
     *
     * @param id the id of the specification to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the specification, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/specifications/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Specification> getSpecification(@PathVariable Long id) {
        log.debug("REST request to get Specification : {}", id);
        Specification specification = specificationService.findOne(id);
        return Optional.ofNullable(specification)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /specifications/:id : delete the "id" specification.
     *
     * @param id the id of the specification to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/specifications/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSpecification(@PathVariable Long id) {
        log.debug("REST request to delete Specification : {}", id);
        specificationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("specification", id.toString())).build();
    }

    /**
     * SEARCH  /_search/specifications?query=:query : search for the specification corresponding
     * to the query.
     *
     * @param query the query of the specification search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/specifications",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Specification>> searchSpecifications(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Specifications for query {}", query);
        Page<Specification> page = specificationService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/specifications");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
