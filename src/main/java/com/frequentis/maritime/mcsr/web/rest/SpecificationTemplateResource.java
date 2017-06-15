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
import com.frequentis.maritime.mcsr.domain.SpecificationTemplate;
import com.frequentis.maritime.mcsr.service.SpecificationTemplateService;
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
 * REST controller for managing SpecificationTemplate.
 */
@RestController
@RequestMapping("/api")
public class SpecificationTemplateResource {

    private final Logger log = LoggerFactory.getLogger(SpecificationTemplateResource.class);

    @Inject
    private SpecificationTemplateService specificationTemplateService;

    /**
     * POST  /specification-templates : Create a new specificationTemplate.
     *
     * @param specificationTemplate the specificationTemplate to create
     * @return the ResponseEntity with status 201 (Created) and with body the new specificationTemplate, or with status 400 (Bad Request) if the specificationTemplate has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/specification-templates",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SpecificationTemplate> createSpecificationTemplate(@Valid @RequestBody SpecificationTemplate specificationTemplate) throws URISyntaxException {
        log.debug("REST request to save SpecificationTemplate : {}", specificationTemplate);
        if (specificationTemplate.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("specificationTemplate", "idexists", "A new specificationTemplate cannot already have an ID")).body(null);
        }
        SpecificationTemplate result = specificationTemplateService.save(specificationTemplate);
        return ResponseEntity.created(new URI("/api/specification-templates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("specificationTemplate", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /specification-templates : Updates an existing specificationTemplate.
     *
     * @param specificationTemplate the specificationTemplate to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated specificationTemplate,
     * or with status 400 (Bad Request) if the specificationTemplate is not valid,
     * or with status 500 (Internal Server Error) if the specificationTemplate couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/specification-templates",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SpecificationTemplate> updateSpecificationTemplate(@Valid @RequestBody SpecificationTemplate specificationTemplate) throws URISyntaxException {
        log.debug("REST request to update SpecificationTemplate : {}", specificationTemplate);
        if (specificationTemplate.getId() == null) {
            return createSpecificationTemplate(specificationTemplate);
        }
        SpecificationTemplate result = specificationTemplateService.save(specificationTemplate);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("specificationTemplate", specificationTemplate.getId().toString()))
            .body(result);
    }

    /**
     * GET  /specification-templates : get all the specificationTemplates.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of specificationTemplates in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/specification-templates",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SpecificationTemplate>> getAllSpecificationTemplates(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of SpecificationTemplates");
        Page<SpecificationTemplate> page = specificationTemplateService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/specification-templates");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /specification-templates/:id : get the "id" specificationTemplate.
     *
     * @param id the id of the specificationTemplate to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the specificationTemplate, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/specification-templates/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SpecificationTemplate> getSpecificationTemplate(@PathVariable Long id) {
        log.debug("REST request to get SpecificationTemplate : {}", id);
        SpecificationTemplate specificationTemplate = specificationTemplateService.findOne(id);
        return Optional.ofNullable(specificationTemplate)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /specification-templates/:id : delete the "id" specificationTemplate.
     *
     * @param id the id of the specificationTemplate to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/specification-templates/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSpecificationTemplate(@PathVariable Long id) {
        log.debug("REST request to delete SpecificationTemplate : {}", id);
        specificationTemplateService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("specificationTemplate", id.toString())).build();
    }

    /**
     * SEARCH  /_search/specification-templates?query=:query : search for the specificationTemplate corresponding
     * to the query.
     *
     * @param query the query of the specificationTemplate search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/specification-templates",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SpecificationTemplate>> searchSpecificationTemplates(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of SpecificationTemplates for query {}", query);
        Page<SpecificationTemplate> page = specificationTemplateService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/specification-templates");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
