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

import com.codahale.metrics.annotation.Timed;
import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Instance;
import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.service.InstanceService;
import com.frequentis.maritime.mcsr.web.rest.util.HeaderUtil;
import com.frequentis.maritime.mcsr.web.rest.util.InstanceUtil;
import com.frequentis.maritime.mcsr.web.rest.util.PaginationUtil;
import com.frequentis.maritime.mcsr.web.rest.util.XmlUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

@RestController
@RequestMapping("/api")
@Api
public class ServiceInstanceResource {

    private final Logger log = LoggerFactory.getLogger(ServiceInstanceResource.class);

    @Inject
    private InstanceService instanceService;

    /**
     * POST  /serviceInstance : Create a new instance.
     *
     * @param instance the instance to create
     * @return the ResponseEntity with status 201 (Created) and with body the new instance, or with status 400 (Bad Request) if the instance has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/serviceInstance",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Instance> createInstance(@Valid @RequestBody Instance instance, @RequestHeader(value = "Authorization", required=false) String bearerToken) throws URISyntaxException {
        log.debug("REST request to save Instance : {}", instance);
        String organizationId = "";
        try {
            organizationId = HeaderUtil.extractOrganizationIdFromToken(bearerToken);
        } catch (Exception e) {
            log.warn("No organizationId could be parsed from the bearer token");
        }
        if (instance.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("instance", "idexists", "A new instance cannot already have an ID")).body(null);
        }

        try {
            String xml = instance.getInstanceAsXml().getContent().toString();
            log.info("XML:" + xml);
            XmlUtil.validateXml(xml, "ServiceInstanceSchema.xsd");
            instance = InstanceUtil.parseInstanceAttributesFromXML(instance);
            instance.setOrganizationId(organizationId);
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
        return ResponseEntity.created(new URI("/api/serviceInstance/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("instance", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /serviceInstance : Updates an existing instance.
     *
     * @param instance the instance to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated instance,
     * or with status 400 (Bad Request) if the instance is not valid,
     * or with status 500 (Internal Server Error) if the instance couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/serviceInstance",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Instance> updateInstance(@Valid @RequestBody Instance instance, @RequestHeader(value = "Authorization", required=false) String bearerToken) throws URISyntaxException {
        log.debug("REST request to update Instance : {}", instance);

        try {
            instance = InstanceUtil.parseInstanceAttributesFromXML(instance);
        } catch (Exception e) {
            log.error("Error parsing xml: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (instance.getId() == null) {
            return createInstance(instance, bearerToken);
        }

        String organizationId = "";
        try {
            organizationId = HeaderUtil.extractOrganizationIdFromToken(bearerToken);
        } catch (Exception e) {
            log.warn("No organizationId could be parsed from the bearer token");
        }
        if (instance.getOrganizationId() != null && instance.getOrganizationId().length() > 0 && !organizationId.equals(instance.getOrganizationId())) {
            log.warn("Cannot update entity, organization ID "+organizationId+" does not match that of entity: "+instance.getOrganizationId());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
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
     * GET  /serviceInstance : get all the instances.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of instances in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/serviceInstance",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Instance>> getAllInstances(@RequestParam(defaultValue = "false") String includeDoc, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Instances");
        Page<Instance> page = instanceService.findAll(pageable);
        if (page != null && page.getContent() != null && "true".equalsIgnoreCase(includeDoc) == false) {
            for(Instance instance:page.getContent()) {
                instance.setDocs(null);
                instance.setInstanceAsDoc(null);
            }
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/serviceInstance");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /serviceInstance/:id/:version : get the "id" instance with version "version".
     *
     * @param id the domain id of the instance to retrieve
     * @param version the version of the instance to retrieve, "latest" for the highest version number
     * @return the ResponseEntity with status 200 (OK) and with body the instance, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/serviceInstance/{id}/{version}/",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "getInstance", notes = "Returns the service instance with the specified id and version. Use version 'latest' to get the newest one.")
    public ResponseEntity<Instance> getInstance(@PathVariable String id, @PathVariable String version, @RequestParam(defaultValue = "false") String includeDoc) {
        log.debug("REST request to get Instance via domain id {} and version {}", id, version);
        Instance instance = null;
        if (version.equalsIgnoreCase("latest")) {
            instance = instanceService.findLatestVersionByDomainId(id);
        } else {
            instance = instanceService.findByDomainId(id, version);
        }
        if (instance != null && "true".equalsIgnoreCase(includeDoc) == false) {
            instance.setDocs(null);
            instance.setInstanceAsDoc(null);
        }
        return Optional.ofNullable(instance)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /serviceInstance/:id : get all instances with id "id" across all versions.
     *
     * @param id the domain id of the instance to retrieve
     * @return the result of the search
     */
    @RequestMapping(value = "/serviceInstance/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Instance>> getAllInstancesById(@PathVariable String id, @RequestParam(defaultValue = "false") String includeDoc, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Instances by id {}", id);
        Page<Instance> page = instanceService.findAllByDomainId(id, pageable);
        if (page != null && page.getContent() != null && "true".equalsIgnoreCase(includeDoc) == false) {
            for(Instance instance:page.getContent()) {
                instance.setDocs(null);
                instance.setInstanceAsDoc(null);
            }
        }
        HttpHeaders headers = PaginationUtil.generateSearchPaginationNoQueryHttpHeaders(page, "/api/serviceInstance/"+id);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * DELETE  /serviceInstance/:id/:version : delete the "id" instance of version "version".
     *
     * @param id the domain id of the instance to delete
     * @param version the version of the instance to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/serviceInstance/{id}/{version}/",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteInstance(@PathVariable String id, @PathVariable String version, @RequestHeader(value = "Authorization", required=false) String bearerToken) {
        log.debug("REST request to delete Instance id {} version {}", id, version);
        Instance instance = instanceService.findByDomainId(id, version);

        String organizationId = "";
        try {
            organizationId = HeaderUtil.extractOrganizationIdFromToken(bearerToken);
        } catch (Exception e) {
            log.warn("No organizationId could be parsed from the bearer token");
        }
        if (instance.getOrganizationId() != null && instance.getOrganizationId().length() > 0 && !organizationId.equals(instance.getOrganizationId())) {
            log.warn("Cannot delete entity, organization ID "+organizationId+" does not match that of entity: "+instance.getOrganizationId());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        instanceService.delete(instance.getId());
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("instance", id)).build();
    }

    /**
     * SEARCH  /_search/serviceInstance?query=:query : search for the instance corresponding
     * to the query.
     *
     * @param query the query of the instance search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/serviceInstance",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Instance>> searchInstances(@RequestParam String query, @RequestParam(defaultValue = "false") String includeDoc, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Instances for query {}", query);
        Page<Instance> page = instanceService.search(query, pageable);
        if (page != null && page.getContent() != null && "true".equalsIgnoreCase(includeDoc) == false) {
            for(Instance instance:page.getContent()) {
                instance.setDocs(null);
                instance.setInstanceAsDoc(null);
            }
        }
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/serviceInstance");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * SEARCH  /_searchKeywords/serviceInstance?query=:query : search for the instance corresponding
     * to the supplied keywords.
     *
     * @param query the query of the instance keyword search
     * @return the result of the search
     */
    @RequestMapping(value = "/_searchKeywords/serviceInstance",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Instance>> searchInstancesByKeywords(@RequestParam String query, @RequestParam(defaultValue = "false") String includeDoc, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Instances for keywords {}", query);
        Page<Instance> page = instanceService.searchKeywords(query, pageable);
        if (page != null && page.getContent() != null && "true".equalsIgnoreCase(includeDoc) == false) {
            for(Instance instance:page.getContent()) {
                instance.setDocs(null);
                instance.setInstanceAsDoc(null);
            }
        }
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_searchKeywords/serviceInstance");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * SEARCH  /_searchUnlocode/serviceInstance?query=:query : search for the instance corresponding
     * to the supplied unlocode.
     *
     * @param query the query of the instance keyword search
     * @return the result of the search
     */
    @RequestMapping(value = "/_searchUnlocode/serviceInstance",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "searchInstancesByUnlocode", notes = "Returns all service instances matching the specified UnLoCode.")
    public ResponseEntity<List<Instance>> searchInstancesByUnlocode(@RequestParam String query, @RequestParam(defaultValue = "false") String includeDoc, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Instances for unlocode {}", query);
        Page<Instance> page = instanceService.searchUnlocode(query, pageable);
        if (page != null && page.getContent() != null && "true".equalsIgnoreCase(includeDoc) == false) {
            for(Instance instance:page.getContent()) {
                instance.setDocs(null);
                instance.setInstanceAsDoc(null);
            }
        }
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_searchUnlocode/serviceInstance");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * SEARCH  /_searchLocation/serviceInstance?lat=:latitude&lon=:longitude : search for the instance corresponding
     * to the supplied position
     *
     * @param latitude the latitude of the search position
     * @param longitude the longitude of the search position
     * @return the result of the search
     */
    @RequestMapping(value = "/_searchLocation/serviceInstance",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "searchInstancesByLocation", notes = "Returns all service instances matching the specified Lat/Lon coordinates.")
    public ResponseEntity<List<Instance>> searchInstancesByLocation(@RequestParam String latitude, @RequestParam(defaultValue = "false") String includeDoc, @RequestParam String longitude, @RequestParam(defaultValue = "", required=false) String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get Instance by lat {} long {}", latitude, longitude);
        Page<Instance> page = instanceService.findByLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), query, pageable);
        if (page != null && page.getContent() != null && "true".equalsIgnoreCase(includeDoc) == false) {
            for(Instance instance:page.getContent()) {
                instance.setDocs(null);
                instance.setInstanceAsDoc(null);
            }
        }
//TODO: pagination headers only support one query parameter, need to find out if we even need this for the API
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(latitude, page, "/api/_searchLocation/serviceInstance");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * SEARCH  /_searchGeometryGeoJSON/serviceInstance?geometry=:geometry : search for the instance corresponding
     * to the supplied position
     *
     * @param geometry the search geometry in geojson format
     * @param query additional query filters in elasticsearch queryString syntax
     * @return the result of the search
     */
    @RequestMapping(value = "/_searchGeometryGeoJSON/serviceInstance",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "searchInstancesByGeometryGeojson", notes = "Returns all service instances matching the specified GeoJson shape.")
    public ResponseEntity<List<Instance>> searchInstancesByGeometryGeojson(@RequestParam String geometry, @RequestParam(defaultValue = "false") String includeDoc, @RequestParam(defaultValue = "", required=false) String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get Instance by geojson ", geometry);
        Page<Instance> page = instanceService.findByGeoshape(geometry, query, pageable);
        if (page != null && page.getContent() != null && "true".equalsIgnoreCase(includeDoc) == false) {
            for(Instance instance:page.getContent()) {
                instance.setDocs(null);
                instance.setInstanceAsDoc(null);
            }
        }
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(geometry, page, "/api/_searchGeometry/serviceInstance");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * SEARCH  /_searchGeometryWKT/serviceInstance?geometry=:geometry : search for the instance corresponding
     * to the supplied position
     *
     * @param geometry the search geometry in WKT format
     * @param query additional query filters in elasticsearch queryString syntax
     * @return the result of the search
     */
    @RequestMapping(value = "/_searchGeometryWKT/serviceInstance",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "searchInstancesByGeometryWKT", notes = "Returns all service instances matching the specified WKT shape.")
    public ResponseEntity<List<Instance>> searchInstancesByGeometryWKT(@RequestParam String geometry, @RequestParam(defaultValue = "", required=false) String query, @RequestParam(defaultValue = "false") String includeDoc, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get Instance by wkt ", geometry);
        String geoJson = null;
        try {
            geoJson = InstanceUtil.convertWKTtoGeoJson(geometry).toString();
            log.debug("Converted Geojson: " + geoJson);
        } catch (Exception e) {
            log.error("Error parsing wkt: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Page<Instance> page = instanceService.findByGeoshape(geoJson, query, pageable);
        if (page != null && page.getContent() != null && "true".equalsIgnoreCase(includeDoc) == false) {
            for(Instance instance:page.getContent()) {
                instance.setDocs(null);
                instance.setInstanceAsDoc(null);
            }
        }
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(geoJson, page, "/api/_searchGeometry/serviceInstance");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * STATUS UPDATE  /serviceInstance/:id/:version/status : changes status of the "id" instance of version "version".
     *
     * @param id the domain id of the instance to deprecate/activate/etc.
     * @param version the id of the instance to deprecate/activate/etc.
     * @param status the new status
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/serviceInstance/{id}/{version}/status",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> updateInstanceStatus(@PathVariable String id, @PathVariable String version, @RequestParam String status, @RequestHeader(value = "Authorization", required=false) String bearerToken) {
        log.debug("REST request to update status of Instance {} version {}", id, version);
        try {
            Instance instance = instanceService.findByDomainId(id, version);

            String organizationId = "";
            try {
                organizationId = HeaderUtil.extractOrganizationIdFromToken(bearerToken);
            } catch (Exception e) {
                log.warn("No organizationId could be parsed from the bearer token");
            }
            if (instance.getOrganizationId() != null && instance.getOrganizationId().length() > 0 && !organizationId.equals(instance.getOrganizationId())) {
                log.warn("Cannot update entity, organization ID "+organizationId+" does not match that of entity: "+instance.getOrganizationId());
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            Xml instanceXml = instance.getInstanceAsXml();
            String xml = instanceXml.getContent().toString();
            //Update the status value inside the xml definition
            String resultXml = XmlUtil.updateXmlNode(status, xml, "/ServiceInstanceSchema:serviceInstance/status");
            instanceXml.setContent(resultXml);
            instance.setInstanceAsXml(instanceXml);
            instanceService.updateStatus(instance.getId(), status);
        }
        catch (Exception e) {
            log.debug("Error updating instance xml: ", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().headers(HeaderUtil.createEntityStatusUpdateAlert("instance", id.toString())).build();
    }


}
