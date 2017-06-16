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
package com.frequentis.maritime.mcsr.web.rest.registry;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.codahale.metrics.annotation.Timed;
import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.service.SpecificationService;
import com.frequentis.maritime.mcsr.web.rest.SpecificationResource;
import com.frequentis.maritime.mcsr.web.rest.util.HeaderUtil;
import com.frequentis.maritime.mcsr.web.rest.util.PaginationUtil;
import com.frequentis.maritime.mcsr.web.rest.util.XmlUtil;

@RestController
@RequestMapping("/api")
public class ServiceSpecificationResource {

    private final Logger log = LoggerFactory.getLogger(SpecificationResource.class);

    @Inject
    private SpecificationService specificationService;

    /**
     * POST  /serviceSpecification : Create a new specification.
     *
     * @param specification the specification to create
     * @return the ResponseEntity with status 201 (Created) and with body the new specification, or with status 400 (Bad Request) if the specification has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/serviceSpecification",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Specification> createSpecification(@Valid @RequestBody Specification specification, @RequestHeader("Authorization") String bearerToken) throws Exception, URISyntaxException {
        log.debug("REST request to save Specification : {}", specification);
        String organizationId = "";
        try {
            organizationId = HeaderUtil.extractOrganizationIdFromToken(bearerToken);
        } catch (Exception e) {
            log.warn("No organizationId could be parsed from the bearer token");
        }
        if (specification.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("specification", "idexists", "A new specification cannot already have an ID")).body(null);
        }
        String xml = specification.getSpecAsXml().getContent().toString();
        log.info("XML:" + xml);
        XmlUtil.validateXml(xml, "ServiceSpecificationSchema.xsd");

        specification.setOrganizationId(organizationId);
        Specification result = specificationService.save(specification);
        return ResponseEntity.created(new URI("/api/specifications/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("specification", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /serviceSpecification : Updates an existing specification.
     *
     * @param specification the specification to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated specification,
     * or with status 400 (Bad Request) if the specification is not valid,
     * or with status 500 (Internal Server Error) if the specification couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/serviceSpecification",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Specification> updateSpecification(@Valid @RequestBody Specification specification, @RequestHeader("Authorization") String bearerToken) throws Exception, URISyntaxException {
        log.debug("REST request to update Specification : {}", specification);
        if (specification.getId() == null) {
            return createSpecification(specification, bearerToken);
        }

        String organizationId = "";
        try {
            organizationId = HeaderUtil.extractOrganizationIdFromToken(bearerToken);
        } catch (Exception e) {
            log.warn("No organizationId could be parsed from the bearer token");
        }
        if (specification.getOrganizationId() != null && specification.getOrganizationId().length() > 0 && !organizationId.equals(specification.getOrganizationId())) {
            log.warn("Cannot update entity, organization ID "+organizationId+" does not match that of entity: "+specification.getOrganizationId());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        String xml = specification.getSpecAsXml().getContent().toString();
        log.info("XML:" + xml);
        XmlUtil.validateXml(xml, "ServiceSpecificationSchema.xsd");

        Specification result = specificationService.save(specification);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("specification", specification.getId().toString()))
            .body(result);
    }

    /**
     * GET  /serviceSpecification : get all the specifications.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of specifications in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/serviceSpecification",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Specification>> getAllSpecifications(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Specifications");
        Page<Specification> page = specificationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/serviceSpecification");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /serviceSpecification/:id/:version : get the "id" specification of version "version".
     *
     * @param id the id of the specification to retrieve
     * @param version the version of the specification to retrieve, "latest" for the highest version number
     * @return the ResponseEntity with status 200 (OK) and with body the specification, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/serviceSpecification/{id}/{version}/",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
        public ResponseEntity<Specification> getSpecification(@PathVariable String id, @PathVariable String version) {
        log.debug("REST request to get Specification {} of version {}", id, version);
        Specification specification = null;
        if (version.equalsIgnoreCase("latest")) {
            specification = specificationService.findLatestVersionByDomainId(id);
        } else {
            specification = specificationService.findByDomainId(id, version);
        }
        return Optional.ofNullable(specification)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /serviceSpecification/:id : get all specifications with id "id" across all versions.
     *
     * @param id the domain id of the specification to retrieve
     * @return the result of the search
     */
    @RequestMapping(value = "/serviceSpecification/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Specification>> getAllSpecificationsById(@PathVariable String id, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Instances by id {}", id);
        Page<Specification> page = specificationService.findAllByDomainId(id, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationNoQueryHttpHeaders(page, "/api/serviceSpecification/"+id);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * DELETE  /serviceSpecification/:id/:version : delete the "id" specification of version "version".
     *
     * @param id the id of the specification to delete
     * @param version the version of the specification to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/serviceSpecification/{id}/{version}/",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSpecification(@PathVariable String id, @PathVariable String version, @RequestHeader("Authorization") String bearerToken) {
        log.debug("REST request to delete Specification {} of version {}", id, version);
        Specification specification = specificationService.findByDomainId(id, version);

        String organizationId = "";
        try {
            organizationId = HeaderUtil.extractOrganizationIdFromToken(bearerToken);
        } catch (Exception e) {
            log.warn("No organizationId could be parsed from the bearer token");
        }
        if (specification.getOrganizationId() != null && specification.getOrganizationId().length() > 0 && !organizationId.equals(specification.getOrganizationId())) {
            log.warn("Cannot delete entity, organization ID "+organizationId+" does not match that of entity: "+specification.getOrganizationId());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        specificationService.delete(specification.getId());
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("specification", id.toString())).build();
    }

    /**
     * SEARCH  /_search/serviceSpecification?query=:query : search for the specification corresponding
     * to the query.
     *
     * @param query the query of the specification search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/serviceSpecification",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Specification>> searchSpecifications(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Specifications for query {}", query);
        Page<Specification> page = specificationService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/serviceSpecification");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * STATUS UPDATE  /serviceSpecification/:id/:version/status: update status of the "id" service specification of version "version"
     *
     * @param id the id of the specification to deprecate
     * @param version the version of the specification to deprecate
     * @param status the new status
     * @return the result of the operation
     */
    @RequestMapping(value = "/serviceSpecification/{id}/{version}/status",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> updateSpecificationStatus(@PathVariable String id, @PathVariable String version, @RequestParam String status, @RequestHeader("Authorization") String bearerToken)
        throws Exception, URISyntaxException {
        log.debug("REST request to update status of Specification {} of version {}", id, version);
        Specification specification = specificationService.findByDomainId(id, version);

        String organizationId = "";
        try {
            organizationId = HeaderUtil.extractOrganizationIdFromToken(bearerToken);
        } catch (Exception e) {
            log.warn("No organizationId could be parsed from the bearer token");
        }
        if (specification.getOrganizationId() != null && specification.getOrganizationId().length() > 0 && !organizationId.equals(specification.getOrganizationId())) {
            log.warn("Cannot update entity, organization ID "+organizationId+" does not match that of entity: "+specification.getOrganizationId());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Xml specificationXml = specification.getSpecAsXml();
        String xml = specificationXml.getContent().toString();
        //Update the status value inside the xml definition
        String resultXml = XmlUtil.updateXmlNode(status, xml, "/ServiceSpecificationSchema:serviceSpecification/status");
        specificationXml.setContent(resultXml);
        specification.setSpecAsXml(specificationXml);

        specificationService.updateStatus(specification.getId(), status);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityStatusUpdateAlert("specification", id.toString())).build();
    }

    @ExceptionHandler({Exception.class, URISyntaxException.class})
    public ResponseEntity<?> handleException(Exception e, WebRequest webRequest) {
        Map<String, String> errorMap = new HashMap<String, String>();
        errorMap.put("status", "error");
        errorMap.put("timestamp", ""+System.currentTimeMillis());
        errorMap.put("error", "Bad Request");
        errorMap.put("message", e.getMessage());
        errorMap.put("status", "400");
        return ResponseEntity.badRequest().body(errorMap);
    }



}
