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
package com.frequentis.maritime.mcsr.web.soap.registry;

import java.net.URISyntaxException;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import com.frequentis.maritime.mcsr.domain.Instance;
import com.frequentis.maritime.mcsr.web.soap.dto.InstanceDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.errors.AccessDeniedException;
import com.frequentis.maritime.mcsr.web.soap.errors.InstanceAlreadyExistException;
import com.frequentis.maritime.mcsr.web.soap.errors.ProcessingException;
import com.frequentis.maritime.mcsr.web.soap.errors.XmlValidateException;

@WebService
public interface ServiceInstanceResource {
    /**
     * POST  /serviceInstance : Create a new instance.
     *
     * @param instance the instance to create
     * @return the ResponseEntity with status 201 (Created) and with body the new instance, or with status 400 (Bad Request) if the instance has already an ID
     * @throws Exception 
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    public InstanceDTO createInstance(
    		@WebParam(name = "instance") @XmlElement(required = true) InstanceDTO instance, 
    		@WebParam(name = "bearerToken") @XmlElement(required = true) String bearerToken) 
    				throws AccessDeniedException, XmlValidateException, InstanceAlreadyExistException;

    /**
     * PUT  /serviceInstance : Updates an existing instance.
     *
     * @param instance the instance to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated instance,
     * or with status 400 (Bad Request) if the instance is not valid,
     * or with status 500 (Internal Server Error) if the instance couldnt be updated
     * @throws Exception 
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    public InstanceDTO updateInstance(
    		@WebParam(name = "instance") @XmlElement(required = true) InstanceDTO instance, 
    		@WebParam(name = "bearerToken") @XmlElement(required = true) String bearerToken) 
    				throws AccessDeniedException, XmlValidateException, InstanceAlreadyExistException;

    /**
     * GET  /serviceInstance : get all the instances.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of instances in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    public PageDTO<InstanceDTO> getAllInstances(
    		@WebParam(name = "includeDoc") @XmlElement(required = true) boolean includeDoc, 
    		@WebParam(name = "page") @XmlElement(required = true) int page);
    /**
     * GET  /serviceInstance/:id/:version : get the "id" instance with version "version".
     * @param id the domain id of the instance to retrieve
     * @param version the version of the instance to retrieve, "latest" for the highest version number
     * @return the ResponseEntity with status 200 (OK) and with body the instance, or with status 404 (Not Found)
     */
    public Instance getInstance(
    		@WebParam(name = "id") @XmlElement(required = true) String id, 
    		@WebParam(name = "version") @XmlElement(required = true) String version, 
    		@WebParam(name = "includeDoc") @XmlElement(required = true) boolean includeDoc);

    /**
     * GET  /serviceInstance/:id : get all instances with id "id" across all versions.
     *
     * @param id the domain id of the instance to retrieve
     * @return the result of the search
     */
    public PageDTO<InstanceDTO> getAllInstancesById(
    		@WebParam(name = "id") @XmlElement(required = true) String id, 
    		@WebParam(name = "includeDoc") @XmlElement(required = true) boolean includeDoc, 
    		@WebParam(name = "page") @XmlElement(required = true) int page);

    /**
     * DELETE  /serviceInstance/:id/:version : delete the "id" instance of version "version".
     *
     * @param id the domain id of the instance to delete
     * @param version the version of the instance to delete
     * @return the ResponseEntity with status 200 (OK)
     * @throws Exception 
     */
    public void deleteInstance(
    		@WebParam(name = "id") @XmlElement(required = true) String id, 
    		@WebParam(name = "version") @XmlElement(required = true) String version, 
    		@WebParam(name = "bearerToken") @XmlElement(required = true) String bearerToken) throws AccessDeniedException;

    /**
     * SEARCH  /_search/serviceInstance?query=:query : search for the instance corresponding
     * to the query.
     *
     * @param query the query of the instance search
     * @return the result of the search
     */
    public PageDTO<InstanceDTO> searchInstances(
    		@WebParam(name = "query") @XmlElement(required = true) String query, 
    		@WebParam(name = "includeDoc") @XmlElement(required = true) boolean includeDoc, 
    		@WebParam(name = "page") @XmlElement(required = true) int page);

    /**
     * SEARCH  /_searchKeywords/serviceInstance?query=:query : search for the instance corresponding
     * to the supplied keywords.
     *
     * @param query the query of the instance keyword search
     * @return the result of the search
     */
    public PageDTO<InstanceDTO> searchInstancesByKeywords(
    		@WebParam(name = "query") @XmlElement(required = true) String query, 
    		@WebParam(name = "includeDoc") @XmlElement(required = true) boolean includeDoc, 
    		@WebParam(name = "page") @XmlElement(required = true) int page);

    /**
     * SEARCH  /_searchUnlocode/serviceInstance?query=:query : search for the instance corresponding
     * to the supplied unlocode.
     *
     * @param query the query of the instance keyword search
     * @return the result of the search
     */
    public PageDTO<InstanceDTO> searchInstancesByUnlocode(
    		@WebParam(name = "query") @XmlElement(required = true) String query, 
    		@WebParam(name = "includeDoc") @XmlElement(required = true) boolean includeDoc, 
    		@WebParam(name = "page") @XmlElement(required = true) int page);

    /**
     * SEARCH  /_searchLocation/serviceInstance?lat=:latitude&lon=:longitude : search for the instance corresponding
     * to the supplied position
     *
     * @param latitude the latitude of the search position
     * @param longitude the longitude of the search position
     * @return the result of the search
     * @throws ProcessingException 
     * @throws Exception 
     * @throws NumberFormatException 
     */
    public PageDTO<InstanceDTO> searchInstancesByLocation(
    		@WebParam(name = "latitude") @XmlElement(required = true) String latitude, 
    		@WebParam(name = "includeDoc") @XmlElement(required = true) boolean includeDoc, 
    		@WebParam(name = "longitude") @XmlElement(required = true) String longitude, 
    		@WebParam(name = "query") @XmlElement(required = true) String query, 
    		@WebParam(name = "page") @XmlElement(required = true) int page)
    				throws AccessDeniedException, ProcessingException;

    /**
     * SEARCH  /_searchGeometryGeoJSON/serviceInstance?geometry=:geometry : search for the instance corresponding
     * to the supplied position
     *
     * @param geometry the search geometry in geojson format
     * @param query additional query filters in elasticsearch queryString syntax
     * @return the result of the search
     * @throws Exception 
     */
    public PageDTO<InstanceDTO> searchInstancesByGeometryGeojson(
    		@WebParam(name = "geometry") @XmlElement(required = true) String geometry, 
    		@WebParam(name = "includeDoc") @XmlElement(required = true) boolean includeDoc, 
    		@WebParam(name = "query") @XmlElement(required = true) String query, 
    		@WebParam(name = "page") @XmlElement(required = true) int page) throws Exception;

    /**
     * SEARCH  /_searchGeometryWKT/serviceInstance?geometry=:geometry : search for the instance corresponding
     * to the supplied position
     *
     * @param geometry the search geometry in WKT format
     * @param query additional query filters in elasticsearch queryString syntax
     * @return the result of the search
     * @throws Exception 
     */
    public PageDTO<InstanceDTO> searchInstancesByGeometryWKT(
    		@WebParam(name = "geometry") @XmlElement(required = true) String geometry,
    		@WebParam(name = "query") @XmlElement(required = true) String query, 
    		@WebParam(name = "includeDoc") @XmlElement(required = true) String includeDoc, 
    		@WebParam(name = "page") @XmlElement(required = true) int page) throws Exception;

    /**
     * STATUS UPDATE  /serviceInstance/:id/:version/status : changes status of the "id" instance of version "version".
     *
     * @param id the domain id of the instance to deprecate/activate/etc.
     * @param version the id of the instance to deprecate/activate/etc.
     * @param status the new status
     * @return the ResponseEntity with status 200 (OK)
     * @throws Exception 
     */
    public void updateInstanceStatus(
    		@WebParam(name = "id") @XmlElement(required = true) String id, 
    		@WebParam(name = "version") @XmlElement(required = true) String version, 
    		@WebParam(name = "status") @XmlElement(required = true) String status, 
    		@WebParam(name = "bearerToken") @XmlElement(required = true)  String bearerToken) throws Exception;




}
