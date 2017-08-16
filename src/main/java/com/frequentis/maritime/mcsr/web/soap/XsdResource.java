package com.frequentis.maritime.mcsr.web.soap;

import java.net.URISyntaxException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;

import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XsdDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XsdDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.errors.ProcessingException;


@WebService(name = "XsdResource")
public interface XsdResource {
    /**
     * Create a new xsd.
     *
     * @param xsd the xsd to create
     * @return the ResponseEntity with status 201 (Created) and with body the new xsd, or with status 400 (Bad Request) if the xsd has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
	@WebMethod
    public XsdDescriptorDTO createXsd(@Valid @WebParam(name = "xsd") @XmlElement(required = true) XsdDTO xsd) throws ProcessingException;

    /**
     * Updates an existing xsd.
     *
     * @param xsd the xsd to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated xsd,
     * or with status 400 (Bad Request) if the xsd is not valid,
     * or with status 500 (Internal Server Error) if the xsd couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
	@WebMethod
    public XsdDescriptorDTO updateXsd(@Valid @WebParam(name = "xsd") @XmlElement(required = true) XsdDTO xsd) throws ProcessingException;

    /**
     * Get all the xsds.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of xsds in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
	@WebMethod
    public PageDTO<XsdDescriptorDTO> getAllXsds(@Valid @WebParam(name = "page") @XmlElement(required = true) int page);

    /**
     * Get the "id" xsd.
     *
     * @param id the id of the xsd to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the xsd, or with status 404 (Not Found)
     */
	@WebMethod
    public XsdDTO getXsd(@Valid @WebParam(name = "xsdId") @XmlElement(required = true) Long id);

    /**
     * DELETE  /xsds/:id : delete the "id" xsd.
     *
     * @param id the id of the xsd to delete
     * @return the ResponseEntity with status 200 (OK)
     */
	@WebMethod
    public void deleteXsd(@Valid @WebParam(name = "xsdId") @XmlElement(required = true) Long id);

    /**
     * Search for the xsd corresponding
     * to the query.
     *
     * @param query the query of the xsd search
     * @return the result of the search
     */
	@WebMethod
    public PageDTO<XsdDescriptorDTO> searchXsds(@Valid @WebParam(name = "query") @XmlElement(required = true) String query, 
    		@Valid @WebParam(name = "page") @XmlElement(required = true) int page);
}
