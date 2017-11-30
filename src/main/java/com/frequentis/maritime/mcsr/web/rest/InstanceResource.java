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

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.JsonNode;
import com.frequentis.maritime.mcsr.domain.Instance;
import com.frequentis.maritime.mcsr.service.DesignService;
import com.frequentis.maritime.mcsr.service.InstanceService;
import com.frequentis.maritime.mcsr.web.exceptions.GeometryParseException;
import com.frequentis.maritime.mcsr.web.exceptions.XMLValidationException;
import com.frequentis.maritime.mcsr.web.rest.util.HeaderUtil;
import com.frequentis.maritime.mcsr.web.rest.util.InstanceUtil;
import com.frequentis.maritime.mcsr.web.rest.util.PaginationUtil;
import com.frequentis.maritime.mcsr.web.soap.converters.instance.InstanceDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.instance.InstanceDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.annotations.ApiIgnore;

/**
 * REST controller for managing Instance.
 */
@RestController
@RequestMapping("/api")
@ApiIgnore
public class InstanceResource {

    private final Logger log = LoggerFactory.getLogger(InstanceResource.class);

    @Inject
    private InstanceService instanceService;

    @Inject
    private DesignService designService;

    @Inject
    private InstanceDTOConverter instanceConverter;

    /**
     * POST  /instances : Create a new instance.
     *
     * @param instance the instance to create
     * @return the ResponseEntity with status 201 (Created) and with body the new instance, or with status 400 (Bad Request) if the instance has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/instances",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Instance> createInstance(@RequestBody Instance instance) throws Exception, URISyntaxException {
        log.debug("REST request to save Instance : {}", instance);
        if (instance.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("instance", "idexists", "A new instance cannot already have an ID")).body(null);
        }

        return saveInstance(instance, true);

    }

    /**
     * PUT  /instances : Updates an existing instance.
     *
     * @param instance the instance to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated instance,
     * or with status 400 (Bad Request) if the instance is not valid,
     * or with status 500 (Internal Server Error) if the instance couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/instances",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Instance> updateInstance(@Valid @RequestBody Instance instance) throws Exception, URISyntaxException {
        log.debug("REST request to update Instance : {}", instance);
        if (instance.getId() == null) {
            return createInstance(instance);
        }

        return saveInstance(instance, false);
    }

    private ResponseEntity<Instance> saveInstance(Instance instance, boolean newInstance) throws URISyntaxException {
        try {
            InstanceUtil.prepareInstanceForSave(instance, designService);
            JsonNode geometry = instance.getGeometry();
            instanceService.save(instance);
            instance.setGeometry(geometry);
            instanceService.saveGeometry(instance);

        } catch (XMLValidationException e) {
            log.error("Error parsing xml: ", e);
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("instance", e.getMessage(), e.toString()))
                .body(instance);
        } catch (GeometryParseException e) {
            instanceService.save(instance);
            log.error("Error parsing geometry: ", e);
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("instance", e.getMessage(), e.toString()))
                .body(instance);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("instance", e.getMessage(), e.toString()))
                .body(instance);
        }

        BodyBuilder entity = null;
        if(newInstance) {
            entity = ResponseEntity.created(new URI("/api/instances/" + instance.getId()));
        } else {
            entity = ResponseEntity.ok();
        }
        entity
            .headers(HeaderUtil.createEntityUpdateAlert("instance", instance.getId().toString()))
            .body(instance);

        return entity.build();
    }

    /**
     * GET  /instances : get all the instances.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of instances in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/instances",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Instance>> getAllInstances(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Instances");
        Page<Instance> page = instanceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/instances");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /instances/:id : get the "id" instance.
     *
     * @param id the id of the instance to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the instance, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/instances/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<InstanceDTO> getInstance(@PathVariable Long id) {
        log.debug("REST request to get Instance : {}", id);
        InstanceDTO instance = instanceConverter.convert(instanceService.findOne(id));
        return Optional.ofNullable(instance)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /instances/:id : delete the "id" instance.
     *
     * @param id the id of the instance to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/instances/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteInstance(@PathVariable Long id) {
        log.debug("REST request to delete Instance : {}", id);
        instanceService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("instance", id.toString())).build();
    }

    /**
     * SEARCH  /_search/instances?query=:query : search for the instance corresponding
     * to the query.
     *
     * @param query the query of the instance search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/instances",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Instance>> searchInstances(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Instances for query {}", query);
        //Page<Instance> page = instanceService.search(query, true, false, pageable);
        Page<Instance> page = instanceService.searchAll(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/instances");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
