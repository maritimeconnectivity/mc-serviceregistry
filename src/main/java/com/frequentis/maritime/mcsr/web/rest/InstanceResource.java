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
import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Instance;
import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.service.InstanceService;
import com.frequentis.maritime.mcsr.web.rest.util.HeaderUtil;
import com.frequentis.maritime.mcsr.web.rest.util.InstanceUtil;
import com.frequentis.maritime.mcsr.web.rest.util.PaginationUtil;
import com.frequentis.maritime.mcsr.web.rest.util.XmlUtil;
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
    public ResponseEntity<Instance> createInstance(@Valid @RequestBody Instance instance) throws Exception, URISyntaxException {
        log.debug("REST request to save Instance : {}", instance);
        if (instance.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("instance", "idexists", "A new instance cannot already have an ID")).body(null);
        }
        try {
            String xml = instance.getInstanceAsXml().getContent().toString();
            log.info("XML:" + xml);
            XmlUtil.validateXml(xml, "ServiceInstanceSchema.xsd");
            instance = InstanceUtil.parseInstanceAttributesFromXML(instance);
        } catch (Exception e) {
            log.error("Error parsing xml: ", e);
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("instance", e.getMessage(), e.toString()))
                .body(instance);
        }
        if (instance.getDesigns() != null && instance.getDesigns().size() > 0) {
            Design design = instance.getDesigns().iterator().next();
            if (design != null) {
                instance.setDesignId(design.getDesignId());
                if (design.getSpecifications() != null && design.getSpecifications().size()> 0) {
                    Specification specification = design.getSpecifications().iterator().next();
                    if (specification != null) {
                        instance.setSpecificationId(specification.getSpecificationId());
                    }
                }
            }
        }
        Instance result = instanceService.save(instance);
        try {
            result = InstanceUtil.parseInstanceGeometryFromXML(result);
        } catch (Exception e) {
            log.error("Error parsing geometry: ", e);
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("instance", e.getMessage(), e.toString()))
                .body(instance);
        }
        instanceService.saveGeometry(result);
        return ResponseEntity.created(new URI("/api/instances/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("instance", result.getId().toString()))
            .body(result);
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
        try {
            instance = InstanceUtil.parseInstanceAttributesFromXML(instance);
        } catch (Exception e) {
            log.debug("Error parsing xml: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (instance.getDesigns() != null && instance.getDesigns().size() > 0) {
            Design design = instance.getDesigns().iterator().next();
            if (design != null) {
                instance.setDesignId(design.getDesignId());
                if (design.getSpecifications() != null && design.getSpecifications().size()> 0) {
                    Specification specification = design.getSpecifications().iterator().next();
                    if (specification != null) {
                        instance.setSpecificationId(specification.getSpecificationId());
                    }
                }
            }
        }
        try {
            String xml = instance.getInstanceAsXml().getContent().toString();
            log.info("XML:" + xml);
            XmlUtil.validateXml(xml, "ServiceInstanceSchema.xsd");
            instance = InstanceUtil.parseInstanceAttributesFromXML(instance);
        } catch (Exception e) {
            log.error("Error parsing xml: ", e);
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("instance", e.getMessage(), e.toString()))
                .body(instance);
        }

        Instance result = instanceService.save(instance);
        try {
            result = InstanceUtil.parseInstanceGeometryFromXML(result);
        } catch (Exception e) {
            log.debug("Error parsing geometry: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        instanceService.saveGeometry(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("instance", instance.getId().toString()))
            .body(result);
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
    public ResponseEntity<Instance> getInstance(@PathVariable Long id) {
        log.debug("REST request to get Instance : {}", id);
        Instance instance = instanceService.findOne(id);
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
        Page<Instance> page = instanceService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/instances");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
