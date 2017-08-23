/*
 * MaritimeCloud Service Registry
 * Copyright (c) 2017 Frequentis AG
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

package com.frequentis.maritime.mcsr.web.soap;

import java.net.URISyntaxException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;

import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateSetDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateSetDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateSetParameter;
import com.frequentis.maritime.mcsr.web.soap.errors.ProcessingException;

@WebService(name = "SpecificationTemplateSetResource")
public interface SpecificationTemplateSetResource {

    /**
     * POST  /specification-template-sets : Create a new specificationTemplateSet.
     *
     * @param specificationTemplateSet the specificationTemplateSet to create
     * @return the ResponseEntity with status 201 (Created) and with body the new specificationTemplateSet, or with status 400 (Bad Request) if the specificationTemplateSet has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
	@WebMethod
    public SpecificationTemplateSetDescriptorDTO createSpecificationTemplateSet(
    		@Valid @WebParam(name = "specificationTemplateSet") @XmlElement(required = true) SpecificationTemplateSetParameter specificationTemplateSet)
    				 throws ProcessingException;

    /**
     * PUT  /specification-template-sets : Updates an existing specificationTemplateSet.
     *
     * @param specificationTemplateSet the specificationTemplateSet to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated specificationTemplateSet,
     * or with status 400 (Bad Request) if the specificationTemplateSet is not valid,
     * or with status 500 (Internal Server Error) if the specificationTemplateSet couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
	@WebMethod
    public SpecificationTemplateSetDescriptorDTO updateSpecificationTemplateSet
    (@Valid @WebParam(name = "specificationTemplateSet") @XmlElement(required = true) SpecificationTemplateSetParameter specificationTemplateSet)
    		 throws ProcessingException;

    /**
     * GET  /specification-template-sets : get all the specificationTemplateSets.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of specificationTemplateSets in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
	@WebMethod
    public PageDTO<SpecificationTemplateSetDescriptorDTO> getAllSpecificationTemplateSets(@WebParam(name = "page") @XmlElement(required = true) int page);

    /**
     * GET  /specification-template-sets/:id : get the "id" specificationTemplateSet.
     *
     * @param id the id of the specificationTemplateSet to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the specificationTemplateSet, or with status 404 (Not Found)
     */
	@WebMethod
    public SpecificationTemplateSetDTO getSpecificationTemplateSet(@WebParam(name = "specificationTemplateSetId") @XmlElement(required = true) Long id);

    /**
     * DELETE  /specification-template-sets/:id : delete the "id" specificationTemplateSet.
     *
     * @param id the id of the specificationTemplateSet to delete
     * @return the ResponseEntity with status 200 (OK)
     */
	@WebMethod
    public void deleteSpecificationTemplateSet(@WebParam(name = "specificationTemplateSetId") @XmlElement(required = true) Long id);

    /**
     * SEARCH  /_search/specification-template-sets?query=:query : search for the specificationTemplateSet corresponding
     * to the query.
     *
     * @param query the query of the specificationTemplateSet search
     * @return the result of the search
     */
	@WebMethod
    public PageDTO<SpecificationTemplateSetDescriptorDTO> searchSpecificationTemplateSets(
    		@WebParam(name = "query") @XmlElement(required = true) String query,
    		@WebParam(name = "page") @XmlElement(required = true) int page);



}
