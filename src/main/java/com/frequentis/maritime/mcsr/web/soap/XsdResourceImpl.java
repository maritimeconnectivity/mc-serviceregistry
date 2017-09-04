package com.frequentis.maritime.mcsr.web.soap;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frequentis.maritime.mcsr.domain.Xsd;
import com.frequentis.maritime.mcsr.service.XsdService;
import com.frequentis.maritime.mcsr.web.soap.converters.xml.XsdDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.xml.XsdDescriptorConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XsdDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XsdDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.errors.ProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

@Component
@WebService(endpointInterface = "com.frequentis.maritime.mcsr.web.soap.XsdResource")
@Secured("ROLE_USER")
public class XsdResourceImpl implements XsdResource {
	public static final int ITEMS_PER_PAGE = 50;

	Logger log = LoggerFactory.getLogger(XsdResourceImpl.class);

	@Autowired
	XsdDTOConverter xsdConverter;

	@Autowired
	XsdDescriptorConverter xsdDCovnerter;

	@Autowired
	XsdService xsdService;

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public XsdDescriptorDTO createXsd(XsdDTO xsdDto) throws ProcessingException {
        log.debug("SOAP request to save Xsd : {}", xsdDto);
        if (xsdDto.id != null) {
            throw new ProcessingException("A new xsd cannot already have an ID");
        }
        Xsd result = xsdService.save(xsdConverter.convertReverse(xsdDto));

        return xsdConverter.convert(result);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public XsdDescriptorDTO updateXsd(XsdDTO xsd) throws ProcessingException {
        log.debug("SOAP request to update Xsd : {}", xsd);
        if (xsd.id == null) {
            return createXsd(xsd);
        }
        Xsd result = xsdService.save(xsdConverter.convertReverse(xsd));

        return xsdConverter.convert(result);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public PageDTO<XsdDescriptorDTO> getAllXsds(int page) {
        log.debug("SOAP request to get a page of Xsds");
        Page<Xsd> pageResult = xsdService.findAll(PageRequest.of(page, ITEMS_PER_PAGE));

        return PageResponse.buildFromPage(pageResult, xsdDCovnerter);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public XsdDTO getXsd(Long id) {
        log.debug("SOAP request to get Xsd : {}", id);
        Xsd xsd = xsdService.findOne(id);

        return xsdConverter.convert(xsd);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void deleteXsd(Long id) {
        log.debug("SOAP request to delete Xsd : {}", id);
        xsdService.delete(id);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public PageDTO<XsdDescriptorDTO> searchXsds(String query, int page) {
        log.debug("SOAP request to search for a page of Xsds for query {}", query);
        Page<Xsd> pageResponse = xsdService.search(query, PageRequest.of(page, ITEMS_PER_PAGE));

        return PageResponse.buildFromPage(pageResponse, xsdDCovnerter);
	}



}
