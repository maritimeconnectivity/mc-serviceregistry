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
import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.service.DesignService;
import com.frequentis.maritime.mcsr.web.rest.DesignResource;
import com.frequentis.maritime.mcsr.web.rest.util.HeaderUtil;
import com.frequentis.maritime.mcsr.web.rest.util.PaginationUtil;
import com.frequentis.maritime.mcsr.web.rest.util.XmlUtil;

@RestController
@RequestMapping("/api")
public class TechnicalDesignResource {
    private final Logger log = LoggerFactory.getLogger(DesignResource.class);

    @Inject
    private DesignService designService;

    /**
     * POST  /technicalDesign : Create a new design.
     *
     * @param design the design to create
     * @return the ResponseEntity with status 201 (Created) and with body the new design, or with status 400 (Bad Request) if the design has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/technicalDesign",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Design> createDesign(@Valid @RequestBody Design design, @RequestHeader("Authorization") String bearerToken) throws Exception, URISyntaxException {
        log.debug("REST request to save Design : {}", design);
        String organizationId = "";
        try {
            organizationId = HeaderUtil.extractOrganizationIdFromToken(bearerToken);
        } catch (Exception e) {
            log.warn("No organizationId could be parsed from the bearer token");
        }
        if (design.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("design", "idexists", "A new design cannot already have an ID")).body(null);
        }
        String xml = design.getDesignAsXml().getContent().toString();
        log.info("XML:" + xml);
        XmlUtil.validateXml(xml, "ServiceDesignSchema.xsd");
        design.setOrganizationId(organizationId);
        Design result = designService.save(design);
        return ResponseEntity.created(new URI("/api/technicalDesign/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("design", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /technicalDesign : Updates an existing design.
     *
     * @param design the design to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated design,
     * or with status 400 (Bad Request) if the design is not valid,
     * or with status 500 (Internal Server Error) if the design couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/technicalDesign",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Design> updateDesign(@Valid @RequestBody Design design, @RequestHeader("Authorization") String bearerToken) throws Exception, URISyntaxException {
        log.debug("REST request to update Design : {}", design);
        if (design.getId() == null) {
            return createDesign(design, bearerToken);
        }

        String organizationId = "";
        try {
            organizationId = HeaderUtil.extractOrganizationIdFromToken(bearerToken);
        } catch (Exception e) {
            log.warn("No organizationId could be parsed from the bearer token");
        }
        if (design.getOrganizationId() != null && design.getOrganizationId().length() > 0 && !organizationId.equals(design.getOrganizationId())) {
            log.warn("Cannot update entity, organization ID "+organizationId+" does not match that of entity: "+design.getOrganizationId());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        String xml = design.getDesignAsXml().getContent().toString();
        log.info("XML:" + xml);
        XmlUtil.validateXml(xml, "ServiceDesignSchema.xsd");

        Design result = designService.save(design);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("design", design.getId().toString()))
            .body(result);
    }

    /**
     * GET  /technicalDesign : get all the designs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of designs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/technicalDesign",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Design>> getAllDesigns(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Designs");
        Page<Design> page = designService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/technicalDesign");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /technicalDesign/:id/:version : get the "id" design with version "version".
     *
     * @param id the id of the design to retrieve
     * @param version the version of the design to retrieve, "latest" for the highest version number
     * @return the ResponseEntity with status 200 (OK) and with body the design, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/technicalDesign/{id}/{version}/",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
        @Timed
    public ResponseEntity<Design> getDesign(@PathVariable String id, @PathVariable String version) {
        log.debug("REST request to get Design {} of version {}", id, version);
        Design design = null;
        if (version.equalsIgnoreCase("latest")) {
            design = designService.findLatestVersionByDomainId(id);
        } else {
            design = designService.findByDomainId(id, version);
        }
        return Optional.ofNullable(design)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    /**
     * GET  /technicalDesign/:id : get all designs with id "id" across all versions.
     *
     * @param id the domain id of the design to retrieve
     * @return the result of the search
     */
    @RequestMapping(value = "/technicalDesign/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Design>> getAllDesignsById(@PathVariable String id, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Instances by id {}", id);
        Page<Design> page = designService.findAllByDomainId(id, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationNoQueryHttpHeaders(page, "/api/technicalDesign/"+id);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /_searchSpecificationId/technicalDesign?id=:id : get all designs for specification id "id" across all versions.
     *
     * @param id the domain id of the specification for which all designs are to be retrieved
     * @return the result of the search
     */
    @RequestMapping(value = "/_searchSpecificationId/technicalDesign",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Design>> getAllDesignsBySpecificationId(@RequestParam String id, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Instances by id {}", id);
        List<Design> designs = designService.findAllBySpecificationId(id);
        return new ResponseEntity<>(designs, new HttpHeaders(), HttpStatus.OK);
    }

    /**
     * DELETE  /technicalDesign/:id/:version : delete the "id" design of version "version".
     *
     * @param id the id of the design to delete
     * @param version the version of the design to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/technicalDesign/{id}/{version}/",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteDesign(@PathVariable String id, @PathVariable String version, @RequestHeader("Authorization") String bearerToken) {
        log.debug("REST request to delete Design {}", id, version);
        Design design = designService.findByDomainId(id, version);

        String organizationId = "";
        try {
            organizationId = HeaderUtil.extractOrganizationIdFromToken(bearerToken);
        } catch (Exception e) {
            log.warn("No organizationId could be parsed from the bearer token");
        }
        if (design.getOrganizationId() != null && design.getOrganizationId().length() > 0 && !organizationId.equals(design.getOrganizationId())) {
            log.warn("Cannot delete entity, organization ID "+organizationId+" does not match that of entity: "+design.getOrganizationId());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        designService.delete(design.getId());
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("design", id.toString())).build();
    }

    /**
     * SEARCH  /_search/technicalDesign?query=:query : search for the design corresponding
     * to the query.
     *
     * @param query the query of the design search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/technicalDesign",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Design>> searchDesigns(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Designs for query {}", query);
        Page<Design> page = designService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/technicalDesign");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * UPDATE STATUS  /technicalDesign/:id/:version/status : update status of the "id" design of version "version".
     *
     * @param id the id of the design to deprecate
     * @param version the version of the design to deprecate
     * @param status the new status
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/technicalDesign/{id}/{version}/status",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> updateDesignStatus(@PathVariable String id, @PathVariable String version, @RequestParam String status, @RequestHeader("Authorization") String bearerToken) throws Exception {
        log.debug("REST request to update status of Design {} of version {}", id, version);
        Design design = designService.findByDomainId(id, version);

        String organizationId = "";
        try {
            organizationId = HeaderUtil.extractOrganizationIdFromToken(bearerToken);
        } catch (Exception e) {
            log.warn("No organizationId could be parsed from the bearer token");
        }
        if (design.getOrganizationId() != null && design.getOrganizationId().length() > 0 && !organizationId.equals(design.getOrganizationId())) {
            log.warn("Cannot update entity, organization ID "+organizationId+" does not match that of entity: "+design.getOrganizationId());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Xml designXml = design.getDesignAsXml();
        String xml = designXml.getContent().toString();
        //Update the status value inside the xml definition
        String resultXml = XmlUtil.updateXmlNode(status, xml, "/ServiceDesignSchema:serviceDesign/status");
        designXml.setContent(resultXml);
        design.setDesignAsXml(designXml);

        designService.updateStatus(design.getId(), status);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("design", id.toString())).build();
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
