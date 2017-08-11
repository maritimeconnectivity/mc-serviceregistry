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

import com.frequentis.maritime.mcsr.web.soap.dto.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.DesignDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.errors.AccessDeniedException;
import com.frequentis.maritime.mcsr.web.soap.errors.XmlValidateException;

/**
 * 
 * @author Lukas Vorisek
 *
 */
@WebService(targetNamespace = "com.frequentis.maritime.mcsr.web.soap.registry.TechnicalDesignResource", 
	name = "TechnicalDesignResource")
public interface TechnicalDesignResource {


    /**
     * Create a new design.
     *
     * @param design the design to create
     * @return the ResponseEntity with status 201 (Created) and with body the new design, or with status 400 (Bad Request) 
     * if the design has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
	@WebMethod
    public DesignDescriptorDTO createDesign(@WebParam(name = "design") @XmlElement(required = true) DesignDTO design, 
    		@WebParam(name = "bearerToken") @XmlElement(required = true) String bearerToken) throws Exception, XmlValidateException;

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
    		@WebParam(name = "design") @XmlElement(required = true) DesignDTO design, 
    		@WebParam(name = "bearerToken") @XmlElement(required = true) String bearerToken
    		) throws Exception, URISyntaxException;

    /**
     * Get all the designs.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of designs in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    public PageDTO<DesignDescriptorDTO> getAllDesigns(
    		@WebParam(name = "page") @XmlElement(required = false, defaultValue = "0") int page);

    /**
     * Get the "id" design with version "version".
     *
     * @param id the id of the design to retrieve
     * @param version the version of the design to retrieve, "latest" for the highest version number
     * @return the ResponseEntity with status 200 (OK) and with body the design, or with status 404 (Not Found)
     */
    public DesignDTO getDesign(
    		@WebParam(name="id") @XmlElement(required = true) String id, 
    		@WebParam(name="version") @XmlElement(required = false, defaultValue = "latest") String version);

    /**
     * Get all designs with id "id" across all versions.
     *
     * @param id the domain id of the design to retrieve
     * @return the result of the search
     */
    public PageDTO<DesignDescriptorDTO> getAllDesignsById(
    		@WebParam(name="id") @XmlElement(required = true) String id, 
    		@WebParam(name="page") @XmlElement(required = false, defaultValue = "0") int page);

    /**
     * Get all designs for specification id "id" across all versions.
     *
     * @param id the domain id of the specification for which all designs are to be retrieved
     * @return the result of the search
     */
    public PageDTO<DesignDescriptorDTO> getAllDesignsBySpecificationId(
    		@WebParam(name="id") @XmlElement(required = true) String id, 
    		@WebParam(name="page") @XmlElement(required = false, defaultValue = "0") int page);
    
    /**
     * Delete the "id" design of version "version".
     *
     * @param id the id of the design to delete
     * @param version the version of the design to delete
     * @return the ResponseEntity with status 200 (OK)
     * @throws AccessDeniedException 
     */
    public void deleteDesign(
    		@WebParam(name="id") @XmlElement(required = true) String id, 
    		@WebParam(name="version") @XmlElement(required = true) String version, 
    		@WebParam(name="bearerToken") @XmlElement(required = true) String bearerToken) throws AccessDeniedException;

    /**
     * Search for the design corresponding
     * to the query.
     *
     * @param query the query of the design search
     * @return the result of the search
     */
    public PageDTO<DesignDescriptorDTO> searchDesigns(
    		@WebParam(name="query") @XmlElement(required = true) String query,
    		@WebParam(name="page") @XmlElement(required = false, defaultValue = "0") int page);

    /**
     * Update status of the "id" design of version "version".
     *
     * @param id the id of the design to deprecate
     * @param version the version of the design to deprecate
     * @param status the new status
     * @return the ResponseEntity with status 200 (OK)
     */
    public void updateDesignStatus(
    		@WebParam(name="id") @XmlElement(required = true) String id, 
    		@WebParam(name="version") @XmlElement(required = true) String version, 
    		@WebParam(name="status") @XmlElement(required = true) String status, 
    		@WebParam(name="bearerToken") @XmlElement(required = true) String bearerToken) 
    		throws Exception, AccessDeniedException;


}
