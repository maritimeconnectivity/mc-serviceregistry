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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.web.soap.dto.DesignDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;

/**
 * 
 * @author Lukas Vorisek
 *
 */
@WebService(targetNamespace = "com.frequentis.maritime.mcsr.web.soap.registry.TechnicalDesignResource", name = "TechnicalDesignResource")
public interface TechnicalDesignResource {


    /**
     * Create a new design.
     *
     * @param design the design to create
     * @return the ResponseEntity with status 201 (Created) and with body the new design, or with status 400 (Bad Request) if the design has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
	@WebMethod
    public DesignDescriptorDTO createDesign(
    		@WebParam(name = "design") @XmlElement(required = true) Design design, 
    		@WebParam(name = "bearerToken") @XmlElement(required = true) String bearerToken) throws Exception, URISyntaxException;

    /**
     * Updates an existing design.
     *
     * @param design the design to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated design,
     * or with status 400 (Bad Request) if the design is not valid,
     * or with status 500 (Internal Server Error) if the design couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    public DesignDescriptorDTO updateDesign(
    		@WebParam(name = "design") @XmlElement(required = true) Design design, 
    		@RequestHeader String bearerToken) throws Exception, URISyntaxException;

    /**
     * Get all the designs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of designs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    public PageDTO<DesignDescriptorDTO> getAllDesigns(@WebParam(name = "page") @XmlElement(required = false, defaultValue = "0") int page);

    /**
     * Get the "id" design with version "version".
     *
     * @param id the id of the design to retrieve
     * @param version the version of the design to retrieve, "latest" for the highest version number
     * @return the ResponseEntity with status 200 (OK) and with body the design, or with status 404 (Not Found)
     */
    public ResponseEntity<Design> getDesign(@WebParam String id, @PathVariable String version);

    /**
     * Get all designs with id "id" across all versions.
     *
     * @param id the domain id of the design to retrieve
     * @return the result of the search
     */
    public PageDTO<DesignDescriptorDTO> getAllDesignsById(@WebParam String id, @WebParam int page);

    /**
     * Get all designs for specification id "id" across all versions.
     *
     * @param id the domain id of the specification for which all designs are to be retrieved
     * @return the result of the search
     */
    public PageDTO<DesignDescriptorDTO> getAllDesignsBySpecificationId(@WebParam String id, @WebParam int page);
    
    /**
     * Delete the "id" design of version "version".
     *
     * @param id the id of the design to delete
     * @param version the version of the design to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    public void deleteDesign(@WebParam String id, @WebParam String version, @RequestHeader("Authorization") String bearerToken);

    /**
     * Search for the design corresponding
     * to the query.
     *
     * @param query the query of the design search
     * @return the result of the search
     */
    public PageDTO<DesignDescriptorDTO> searchDesigns(@RequestParam String query, @WebParam int page);

    /**
     * Update status of the "id" design of version "version".
     *
     * @param id the id of the design to deprecate
     * @param version the version of the design to deprecate
     * @param status the new status
     * @return the ResponseEntity with status 200 (OK)
     */
    public void updateDesignStatus(@WebParam String id, @WebParam String version, @WebParam String status, @RequestHeader("Authorization") String bearerToken) throws Exception;


}
