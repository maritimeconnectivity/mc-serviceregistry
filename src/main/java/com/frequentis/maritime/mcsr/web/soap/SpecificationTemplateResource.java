package com.frequentis.maritime.mcsr.web.soap;

import java.net.URISyntaxException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;

import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateParameterDTO;
import com.frequentis.maritime.mcsr.web.soap.errors.ProcessingException;

@WebService(name = "SpecificationTemplate")
public interface SpecificationTemplateResource {
	
	/**
     * Create a new specificationTemplate.
     *
     * @param specificationTemplate the specificationTemplate to create
     * @return the ResponseEntity with status 201 (Created) and with body the new specificationTemplate, or with status 400 (Bad Request) if the specificationTemplate has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
	@WebMethod
    public SpecificationTemplateDescriptorDTO createSpecificationTemplate(
    		@Valid @WebParam(name = "specificationTemplate") @XmlElement(required = true) SpecificationTemplateParameterDTO specificationTemplate) throws ProcessingException;

    /**
     * Updates an existing specificationTemplate.
     *
     * @param specificationTemplate the specificationTemplate to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated specificationTemplate,
     * or with status 400 (Bad Request) if the specificationTemplate is not valid,
     * or with status 500 (Internal Server Error) if the specificationTemplate couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
	@WebMethod
    public SpecificationTemplateDescriptorDTO updateSpecificationTemplate(
    		@Valid @WebParam(name = "specificationTemplate") @XmlElement(required = true) SpecificationTemplateParameterDTO specificationTemplate) throws ProcessingException;

    /**
     * Get all the specificationTemplates.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of specificationTemplates in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
	@WebMethod
    public PageDTO<SpecificationTemplateDescriptorDTO> getAllSpecificationTemplates(
    		@WebParam(name = "page") @XmlElement(required = true) int page);

    /**
     * Get the "id" specificationTemplate.
     *
     * @param id the id of the specificationTemplate to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the specificationTemplate, or with status 404 (Not Found)
     */
	@WebMethod
    public SpecificationTemplateDTO getSpecificationTemplate(
    		@WebParam(name = "specificationTemplateId") @XmlElement(required = true) Long id);

    /**
     * Delete the "id" specificationTemplate.
     *
     * @param id the id of the specificationTemplate to delete
     * @return the ResponseEntity with status 200 (OK)
     */
	@WebMethod
    public void deleteSpecificationTemplate(
    		@WebParam(name = "specificationTemplateId") @XmlElement(required = true) Long id);

    /**
     * Search for the specificationTemplate corresponding
     * to the query.
     *
     * @param query the query of the specificationTemplate search
     * @return the result of the search
     */
	@WebMethod
    public PageDTO<SpecificationTemplateDescriptorDTO> searchSpecificationTemplates(
    		@WebParam(name = "query") @XmlElement(required = true) String query, 
    		@WebParam(name = "page") @XmlElement(required = true) int page);
}
