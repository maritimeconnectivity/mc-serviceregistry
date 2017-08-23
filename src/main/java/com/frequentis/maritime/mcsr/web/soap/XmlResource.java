package com.frequentis.maritime.mcsr.web.soap;

import java.net.URISyntaxException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;

import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.errors.ProcessingException;


@WebService(name = "Xml")
public interface XmlResource {
	  /**
     * Create a new xml.
     *
     * @param xml the xml to create
     * @return the ResponseEntity with status 201 (Created) and with body the new xml, or with status 400 (Bad Request) if the xml has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
	@WebMethod
    public XmlDescriptorDTO createXml(@Valid @WebParam(name = "xml") @XmlElement(required = true) XmlDTO xml) throws ProcessingException;

    /**
     * Updates an existing xml.
     *
     * @param xml the xml to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated xml,
     * or with status 400 (Bad Request) if the xml is not valid,
     * or with status 500 (Internal Server Error) if the xml couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
	@WebMethod
    public XmlDescriptorDTO updateXml(@Valid @WebParam(name = "xml") @XmlElement(required = true) XmlDTO xml) throws ProcessingException;

    /**
     * get all the xmls.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of xmls in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
	@WebMethod
    public PageDTO<XmlDescriptorDTO> getAllXmls(@WebParam(name = "page") @XmlElement(required = true) int page);

    /**
     * get the "id" xml.
     *
     * @param id the id of the xml to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the xml, or with status 404 (Not Found)
     */
	@WebMethod
    public XmlDTO getXml(@WebParam(name = "xmlId") @XmlElement(required = true) Long id);

    /**
     * delete the "id" xml.
     *
     * @param id the id of the xml to delete
     * @return the ResponseEntity with status 200 (OK)
     */
	@WebMethod
    public void deleteXml(@WebParam(name = "xmlId") @XmlElement(required = true) Long id);

    /**
     * search for the xml corresponding
     * to the query.
     *
     * @param query the query of the xml search
     * @return the result of the search
     */
	@WebMethod
    public PageDTO<XmlDescriptorDTO> searchXmls(
    		@WebParam(name = "query") @XmlElement(required = true) String query,
    		@WebParam(name = "page") @XmlElement(required = true) int page) throws ProcessingException;
}
