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
import com.frequentis.maritime.mcsr.domain.SpecificationTemplateSet;
import com.frequentis.maritime.mcsr.service.SpecificationTemplateSetService;
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
import springfox.documentation.annotations.ApiIgnore;

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
 * REST controller for managing SpecificationTemplateSet.
 */
@RestController
@RequestMapping("/api")
@ApiIgnore
public class SpecificationTemplateSetResource {

    private final Logger log = LoggerFactory.getLogger(SpecificationTemplateSetResource.class);

    @Inject
    private SpecificationTemplateSetService specificationTemplateSetService;

    /**
     * POST  /specification-template-sets : Create a new specificationTemplateSet.
     *
     * @param specificationTemplateSet the specificationTemplateSet to create
     * @return the ResponseEntity with status 201 (Created) and with body the new specificationTemplateSet, or with status 400 (Bad Request) if the specificationTemplateSet has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/specification-template-sets",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SpecificationTemplateSet> createSpecificationTemplateSet(@Valid @RequestBody SpecificationTemplateSet specificationTemplateSet) throws URISyntaxException {
        log.debug("REST request to save SpecificationTemplateSet : {}", specificationTemplateSet);
        if (specificationTemplateSet.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("specificationTemplateSet", "idexists", "A new specificationTemplateSet cannot already have an ID")).body(null);
        }
        SpecificationTemplateSet result = specificationTemplateSetService.save(specificationTemplateSet);
        return ResponseEntity.created(new URI("/api/specification-template-sets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("specificationTemplateSet", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /specification-template-sets : Updates an existing specificationTemplateSet.
     *
     * @param specificationTemplateSet the specificationTemplateSet to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated specificationTemplateSet,
     * or with status 400 (Bad Request) if the specificationTemplateSet is not valid,
     * or with status 500 (Internal Server Error) if the specificationTemplateSet couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/specification-template-sets",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SpecificationTemplateSet> updateSpecificationTemplateSet(@Valid @RequestBody SpecificationTemplateSet specificationTemplateSet) throws URISyntaxException {
        log.debug("REST request to update SpecificationTemplateSet : {}", specificationTemplateSet);
        if (specificationTemplateSet.getId() == null) {
            return createSpecificationTemplateSet(specificationTemplateSet);
        }
        SpecificationTemplateSet result = specificationTemplateSetService.save(specificationTemplateSet);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("specificationTemplateSet", specificationTemplateSet.getId().toString()))
            .body(result);
    }

    /**
     * GET  /specification-template-sets : get all the specificationTemplateSets.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of specificationTemplateSets in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/specification-template-sets",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SpecificationTemplateSet>> getAllSpecificationTemplateSets(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of SpecificationTemplateSets");
        Page<SpecificationTemplateSet> page = specificationTemplateSetService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/specification-template-sets");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /specification-template-sets/:id : get the "id" specificationTemplateSet.
     *
     * @param id the id of the specificationTemplateSet to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the specificationTemplateSet, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/specification-template-sets/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SpecificationTemplateSet> getSpecificationTemplateSet(@PathVariable Long id) {
        log.debug("REST request to get SpecificationTemplateSet : {}", id);
        SpecificationTemplateSet specificationTemplateSet = specificationTemplateSetService.findOne(id);
        return Optional.ofNullable(specificationTemplateSet)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /specification-template-sets/:id : delete the "id" specificationTemplateSet.
     *
     * @param id the id of the specificationTemplateSet to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/specification-template-sets/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSpecificationTemplateSet(@PathVariable Long id) {
        log.debug("REST request to delete SpecificationTemplateSet : {}", id);
        specificationTemplateSetService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("specificationTemplateSet", id.toString())).build();
    }

    /**
     * SEARCH  /_search/specification-template-sets?query=:query : search for the specificationTemplateSet corresponding
     * to the query.
     *
     * @param query the query of the specificationTemplateSet search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/specification-template-sets",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SpecificationTemplateSet>> searchSpecificationTemplateSets(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of SpecificationTemplateSets for query {}", query);
        Page<SpecificationTemplateSet> page = specificationTemplateSetService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/specification-template-sets");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
