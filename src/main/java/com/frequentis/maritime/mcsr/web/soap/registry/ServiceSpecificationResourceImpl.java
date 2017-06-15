package com.frequentis.maritime.mcsr.web.soap.registry;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.service.SpecificationService;
import com.frequentis.maritime.mcsr.web.rest.util.HeaderUtil;
import com.frequentis.maritime.mcsr.web.rest.util.PaginationUtil;
import com.frequentis.maritime.mcsr.web.rest.util.XmlUtil;
import com.frequentis.maritime.mcsr.web.soap.PageResponse;
import com.frequentis.maritime.mcsr.web.soap.converters.Converter;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.SearchData;
import com.frequentis.maritime.mcsr.web.soap.dto.SpecificationDTO;


@Component("serviceSpecificationResourceSoap")
@Transactional
public class ServiceSpecificationResourceImpl implements ServiceSpecificationResource {
	public static final int ITEMS_PER_PAGE = 10;
	private static final Logger log = LoggerFactory.getLogger(ServiceSpecificationResourceImpl.class);
	@Autowired
	SpecificationService specificationService;
	@Autowired
	Converter<Specification, SpecificationDTO> specificationConverter;

	@Override
	public void createSpecification(Specification specification, String bearerToken) throws Exception {
        log.debug("REST request to save Specification : {}", specification);
        String organizationId = "";
        try {
            organizationId = HeaderUtil.extractOrganizationIdFromToken(bearerToken);
        } catch (Exception e) {
            log.warn("No organizationId could be parsed from the bearer token");
        }
        if (specification.getId() != null) {
            throw new IllegalArgumentException("A new specification cannot already have an ID");
        }
        String xml = specification.getSpecAsXml().getContent().toString();
        log.info("XML:" + xml);
        XmlUtil.validateXml(xml, "ServiceSpecificationSchema.xsd");

        specification.setOrganizationId(organizationId);
        specificationService.save(specification);	
	}

	@Override
	public void updateSpecification(Specification specification, String bearerToken) throws IllegalAccessException, Exception {
        log.debug("SOAP request to update Specification : {}", specification);
        if (specification.getId() == null) {
            createSpecification(specification, bearerToken);
        }

        String organizationId = "";
        try {
            organizationId = HeaderUtil.extractOrganizationIdFromToken(bearerToken);
        } catch (Exception e) {
            log.warn("No organizationId could be parsed from the bearer token");
        }
        if (specification.getOrganizationId() != null && specification.getOrganizationId().length() > 0 && !organizationId.equals(specification.getOrganizationId())) {
            log.warn("Cannot update entity, organization ID "+organizationId+" does not match that of entity: "+specification.getOrganizationId());
            throw new IllegalAccessException();
        }
        String xml = specification.getSpecAsXml().getContent().toString();
        log.info("XML:" + xml);
        XmlUtil.validateXml(xml, "ServiceSpecificationSchema.xsd");

        specificationService.save(specification);
	}

	@Override
	public PageDTO<SpecificationDTO> getAllSpecifications(int page) {
        log.debug("SOAP request to get a page of Specifications");
        Pageable pageable = new PageRequest(page, ITEMS_PER_PAGE);
        Page<Specification> pageRequest = specificationService.findAll(pageable);

		return PageResponse.buildFromPage(pageRequest, specificationConverter);
	}

	@Override
	public SpecificationDTO getSpecification(String id, String version) {
		log.debug("SOAP request to get Specification by id {} and version {}", id, version);
        Specification specification = null;
        if (version.equalsIgnoreCase("latest")) {
            specification = specificationService.findLatestVersionByDomainId(id);
        } else {
            specification = specificationService.findByDomainId(id, version);
        }
        
        return specificationConverter.convert(specification);
	}

	@Override
	public PageDTO<SpecificationDTO> getAllSpecificationsById(String id, int page) {
        log.debug("SOAP request to get a page of Specifications by id {}", id);
        Pageable pageable = new PageRequest(page, ITEMS_PER_PAGE);
        Page<Specification> pagedata = specificationService.findAllByDomainId(id, pageable);

		return PageResponse.buildFromPage(pagedata, specificationConverter);
	}

	@Override
	public void deleteSpecification(String id, String version, String bearerToken) throws IllegalAccessException {
		log.debug("SOAP request to delete a specification {} of version {}", id, version);
        Specification specification = specificationService.findByDomainId(id, version);

        String organizationId = "";
        try {
            organizationId = HeaderUtil.extractOrganizationIdFromToken(bearerToken);
        } catch (Exception e) {
            log.warn("No organizationId could be parsed from the bearer token");
        }
        if (specification.getOrganizationId() != null && specification.getOrganizationId().length() > 0 && !organizationId.equals(specification.getOrganizationId())) {
            log.warn("Cannot delete entity, organization ID "+organizationId+" does not match that of entity: "+specification.getOrganizationId());
            throw new IllegalAccessException();
        }

        specificationService.delete(specification.getId());
	}

	@Override
	public PageDTO<SpecificationDTO> searchSpecifications(SearchData searchData) {
        log.debug("SOAP request to search for a page {} of Specifications for query {}", searchData.page, searchData.query);
        Pageable pageable = new PageRequest(searchData.page, ITEMS_PER_PAGE);
        Page<Specification> page = specificationService.search(searchData.query, pageable);
        
        return PageResponse.buildFromPage(page, specificationConverter);
	}

	@Override
	public void updateSpecificationStatus(String id, String version, String status, String bearerToken) throws IllegalAccessException, Exception {
        log.debug("SOAP request to update status of Specification {} of version {}", id, version);
        Specification specification = specificationService.findByDomainId(id, version);

        String organizationId = "";
        try {
            organizationId = HeaderUtil.extractOrganizationIdFromToken(bearerToken);
        } catch (Exception e) {
            log.warn("No organizationId could be parsed from the bearer token");
        }
        if (specification.getOrganizationId() != null && specification.getOrganizationId().length() > 0 && !organizationId.equals(specification.getOrganizationId())) {
            log.warn("Cannot update entity, organization ID "+organizationId+" does not match that of entity: "+specification.getOrganizationId());
            throw new IllegalAccessException();
        }

        // @TODO This shoud be moved to the service layer
        Xml specificationXml = specification.getSpecAsXml();
        String xml = specificationXml.getContent().toString();
        //Update the status value inside the xml definition
        String resultXml = XmlUtil.updateXmlNode(status, xml, "/ServiceSpecificationSchema:serviceSpecification/status");
        specificationXml.setContent(resultXml);
        specification.setSpecAsXml(specificationXml);

        specificationService.updateStatus(specification.getId(), status);
	}

}
