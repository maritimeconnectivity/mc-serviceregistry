package com.frequentis.maritime.mcsr.web.soap.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.service.SpecificationService;
import com.frequentis.maritime.mcsr.service.XmlService;
import com.frequentis.maritime.mcsr.web.rest.util.HeaderUtil;
import com.frequentis.maritime.mcsr.web.rest.util.XmlUtil;
import com.frequentis.maritime.mcsr.web.soap.PageResponse;
import com.frequentis.maritime.mcsr.web.soap.converters.Converter;
import com.frequentis.maritime.mcsr.web.soap.converters.specification.SpecificationDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.specification.SpecificationDescriptorDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.errors.ProcessingException;
import com.frequentis.maritime.mcsr.web.soap.errors.XmlValidateException;
import com.frequentis.maritime.mcsr.web.util.WebUtils;


@Component("serviceSpecificationResourceSoap")
@Transactional
public class ServiceSpecificationResourceImpl implements ServiceSpecificationResource {
    public static final int ITEMS_PER_PAGE = 10;
    private static final Logger log = LoggerFactory.getLogger(ServiceSpecificationResourceImpl.class);
    @Autowired
    SpecificationService specificationService;
    @Autowired
    XmlService xmlService;
    @Autowired
    SpecificationDTOConverter specificationConverter;
    @Autowired
    SpecificationDescriptorDTOConverter specificationDescriptorConverter;

    @Override
    public SpecificationDescriptorDTO createSpecification(SpecificationDTO specificationDTO, String bearerToken) throws Exception {
        log.debug("REST request to save Specification : {}", specificationDTO);
        String organizationId = WebUtils.extractOrganizationIdFromToken(bearerToken, log);
        if (specificationDTO.id != null) {
            throw new IllegalArgumentException("A new specification cannot already have an ID");
        }
        
        Specification specification = specificationConverter.convertReverse(specificationDTO);
        if (specification == null || specification.getSpecAsXml() == null || specification.getSpecAsXml().getContent() == null) {
        	throw new XmlValidateException("Specification must have a valid XML body");
        }
        String xml = specification.getSpecAsXml().getContent().toString();
        log.info("XML:" + xml);
        try {
        	XmlUtil.validateXml(xml, "ServiceSpecificationSchema.xsd");
        } catch (Exception e) {
        	throw new XmlValidateException("Specification must have a valid XML body", e);
        }

        specification.setOrganizationId(organizationId);
        xmlService.save(specification.getSpecAsXml());
        specificationService.save(specification);
        return specificationDescriptorConverter.convert(specification);
    }

    @Override
    public SpecificationDescriptorDTO updateSpecification(SpecificationDTO specificationDTO, String bearerToken) throws IllegalAccessException, Exception {
        log.debug("SOAP request to update Specification : {}", specificationDTO);
        if (specificationDTO.id == null) {
            createSpecification(specificationDTO, bearerToken);
        }

        Specification specification = specificationConverter.convertReverse(specificationDTO);
        String organizationId = WebUtils.extractOrganizationIdFromToken(bearerToken, log);
        if (specification.getOrganizationId() != null && specification.getOrganizationId().length() > 0 && !organizationId.equals(specification.getOrganizationId())) {
            log.warn("Cannot update entity, organization ID "+organizationId+" does not match that of entity: "+specification.getOrganizationId());
            throw new IllegalAccessException();
        }
        String xml = specification.getSpecAsXml().getContent().toString();
        log.info("XML:" + xml);
        XmlUtil.validateXml(xml, "ServiceSpecificationSchema.xsd");

        specificationService.save(specification);
        return specificationDescriptorConverter.convert(specification);
    }

    @Override
    public PageDTO<SpecificationDescriptorDTO> getAllSpecifications(int page) {
        log.debug("SOAP request to get a page of Specifications");
        Page<Specification> pageRequest = specificationService.findAll(PageRequest.of(page, ITEMS_PER_PAGE));

        return PageResponse.buildFromPage(pageRequest, specificationDescriptorConverter);
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
    public PageDTO<SpecificationDescriptorDTO> getAllSpecificationsById(String id, int page) {
        log.debug("SOAP request to get a page of Specifications by id {}", id);
        Page<Specification> pagedata = specificationService.findAllByDomainId(id, PageRequest.of(page, ITEMS_PER_PAGE));

        return PageResponse.buildFromPage(pagedata, specificationDescriptorConverter);
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
    public PageDTO<SpecificationDescriptorDTO> searchSpecifications(String query, int page) {
        log.debug("SOAP request to search for a page {} of Specifications for query {}", page, query);
        Page<Specification> pageOfSpecification = specificationService.search(query, PageRequest.of(page, ITEMS_PER_PAGE));

        return PageResponse.buildFromPage(pageOfSpecification, specificationDescriptorConverter);
    }

    @Override
    public void updateSpecificationStatus(String id, String version, String status, String bearerToken) throws IllegalAccessException, ProcessingException {
        log.debug("SOAP request to update status of Specification {} of version {}", id, version);
        Specification specification = specificationService.findByDomainId(id, version);

        String organizationId = WebUtils.extractOrganizationIdFromToken(bearerToken, log);
        if (specification.getOrganizationId() != null && specification.getOrganizationId().length() > 0 && !organizationId.equals(specification.getOrganizationId())) {
            log.warn("Cannot update entity, organization ID "+organizationId+" does not match that of entity: "+specification.getOrganizationId());
            throw new IllegalAccessException();
        }

        // @TODO This shoud be moved to the service layer
        Xml specificationXml = specification.getSpecAsXml();
        String xml = specificationXml.getContent().toString();
        //Update the status value inside the xml definition
        String resultXml;
        try {
        	resultXml = XmlUtil.updateXmlNode(status, xml, "/serviceSpecification/status");
        } catch (Exception e) {
        	throw new ProcessingException(e.getMessage());
        }
        specificationXml.setContent(resultXml);
        specification.setSpecAsXml(specificationXml);

        specificationService.updateStatus(specification.getId(), status);
    }

}
