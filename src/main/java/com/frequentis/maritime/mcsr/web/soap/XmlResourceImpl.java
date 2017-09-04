package com.frequentis.maritime.mcsr.web.soap;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.service.XmlService;
import com.frequentis.maritime.mcsr.web.soap.converters.xml.XmlDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlDescriptorDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.errors.ProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

@Component
@WebService(endpointInterface = "com.frequentis.maritime.mcsr.web.soap.XmlResource")
@Secured("ROLE_USER")
public class XmlResourceImpl implements XmlResource {
	private static final int ITEMS_PER_PAGE = 10;

	Logger log = LoggerFactory.getLogger(XmlResourceImpl.class);

	@Autowired
	XmlDTOConverter xmlConverter;
	@Autowired
	XmlDescriptorDTOConverter xmlDConverter;

	@Autowired
	XmlService xmlService;

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public XmlDescriptorDTO createXml(XmlDTO xmlDto) throws ProcessingException {
        log.debug("SOAP request to save Xml : {}", xmlDto);
        if (xmlDto.id != null) {
        	throw new ProcessingException("A new xml cannot already have an ID");
        }
        Xml result = xmlService.save(xmlConverter.convertReverse(xmlDto));

        return xmlDConverter.convert(result);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public XmlDescriptorDTO updateXml(XmlDTO xmlDto) throws ProcessingException {
        log.debug("SOAP request to update Xml : {}", xmlDto);
        if (xmlDto.id == null) {
            return createXml(xmlDto);
        }
        Xml result = xmlService.save(xmlConverter.convertReverse(xmlDto));

        return xmlDConverter.convert(result);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public PageDTO<XmlDescriptorDTO> getAllXmls(int page) {
        log.debug("SOAP request to get a page of Xmls");
        Page<Xml> pageResponse = xmlService.findAll(PageRequest.of(page, ITEMS_PER_PAGE));

        return PageResponse.buildFromPage(pageResponse, xmlDConverter);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public XmlDTO getXml(Long id) {
        log.debug("SOAP request to get Xml : {}", id);
        Xml xml = xmlService.findOne(id);

        return xmlConverter.convert(xml);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void deleteXml(Long id) {
        log.debug("SOAP request to delete Xml : {}", id);
        xmlService.delete(id);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public PageDTO<XmlDescriptorDTO> searchXmls(String query, int page) throws ProcessingException {
        log.debug("SOAP request to search for a page of Xmls for query {}", query);
        Page<Xml> pageResponse = xmlService.search(query, PageRequest.of(page, ITEMS_PER_PAGE));

        return PageResponse.buildFromPage(pageResponse, xmlDConverter);
	}


}
