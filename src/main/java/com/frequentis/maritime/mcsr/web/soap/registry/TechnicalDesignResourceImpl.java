package com.frequentis.maritime.mcsr.web.soap.registry;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.jws.WebService;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.util.DesignUtils;
import com.frequentis.maritime.mcsr.service.DesignService;
import com.frequentis.maritime.mcsr.service.XmlService;
import com.frequentis.maritime.mcsr.web.rest.util.XmlUtil;
import com.frequentis.maritime.mcsr.web.soap.PageResponse;
import com.frequentis.maritime.mcsr.web.soap.converters.design.DesignConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.design.DesignDescriptorConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.design.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.design.DesignDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.errors.AccessDeniedException;
import com.frequentis.maritime.mcsr.web.soap.errors.XmlValidateException;
import com.frequentis.maritime.mcsr.web.util.WebUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

@Component("technicalDesignResourceSoap")
@WebService(endpointInterface = "com.frequentis.maritime.mcsr.web.soap.registry.TechnicalDesignResource")
@Secured("ROLE_USER")
public class TechnicalDesignResourceImpl implements TechnicalDesignResource {
	private static final Logger log = LoggerFactory.getLogger(TechnicalDesignResourceImpl.class);
	private static final int ITEMS_PER_PAGE = 10;
	private static final String SCHEMA_SERVICE_DESIGN = "ServiceDesignSchema.xsd";

	@Inject
    private DesignService designService;

	@Inject
	private XmlService xmlService;

	@Inject
	private DesignConverter designConverter;
	@Inject
	private DesignDescriptorConverter designDescriptorConverter;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public DesignDescriptorDTO createDesign(DesignDTO designDto, String bearerToken) throws XmlValidateException, Exception {
		log.debug("SOAP request to create design");
		String organizationId = WebUtils.extractOrganizationIdFromToken(bearerToken, log);
		Design design = designConverter.convertReverse(designDto);


		if(design.getId() != null) {
			throw new Exception("A new design cannot already have an ID");
		}
		if(design.getDesignAsXml() == null || design.getDesignAsXml().getContent() == null) {
			throw new Exception("XML must be present!");
		}

		String xml = design.getDesignAsXml().getContent().toString();
		log.info("XML: " + xml);
		try {
			XmlUtil.validateXml(xml, SCHEMA_SERVICE_DESIGN);
		} catch (Exception e) {
			throw new XmlValidateException("Design XML validation failed.", e);
		}
		xmlService.save(design.getDesignAsXml());

		design.setOrganizationId(organizationId);
		Design result = designService.save(design);
		return designDescriptorConverter.convert(result);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DesignDescriptorDTO updateDesign(DesignDTO designDto, String bearerToken) throws Exception, URISyntaxException {
		log.debug("SOAP request to update Design with id {}", designDto.id);
		if(designDto.id == null) {
			return createDesign(designDto, bearerToken);
		}
		Design design = designConverter.convertReverse(designDto);

		String organizationId = WebUtils.extractOrganizationIdFromToken(bearerToken, log);
		if(!DesignUtils.matchOrganizationId(design, organizationId)) {
			log.warn("");
			throw new AccessDeniedException("");
		}

		String xml = design.getDesignAsXml().getContent().toString();
		log.info("XML: " + xml);
		XmlUtil.validateXml(xml, SCHEMA_SERVICE_DESIGN);

		Design result = designService.save(design);
		return designDescriptorConverter.convert(result);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<DesignDescriptorDTO> getAllDesigns(int page) {
		log.debug("SOAP request to get a page of Designs");
		Page<Design> pageOfDesigns = designService.findAll(PageRequest.of(page, ITEMS_PER_PAGE));
		return PageResponse.buildFromPage(pageOfDesigns, designDescriptorConverter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DesignDTO getDesign(String id, String version) {
		log.debug("SOAP request to get Design {} of version {}", id, version);
		Design design = null;
		if(version == null || version.equals("latest")) {
			design = designService.findLatestVersionByDomainId(id);
		} else {
			design = designService.findByDomainId(id, version);
		}

		return designConverter.convert(design);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<DesignDescriptorDTO> getAllDesignsById(String id, int page) {
		log.debug("SOAP request to get a page of Design by domain id {}", id);
		Page<Design> resultPage = designService.findAllByDomainId(id, PageRequest.of(page, ITEMS_PER_PAGE));
		return PageResponse.buildFromPage(resultPage, designDescriptorConverter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<DesignDescriptorDTO> getAllDesignsBySpecificationId(String id, int page) {
		log.debug("SOAP request to get a page of Design by specification id {}", id);
		List<Design> listOfDesigns = designService.findAllBySpecificationId(id);
		Page<Design> resultPage = new PageImpl<>(listOfDesigns);
		return PageResponse.buildFromPage(resultPage, designDescriptorConverter);
	}

	/**
	 * {@inheritDoc}
	 * @throws Exception
	 */
	@Override
	public void deleteDesign(String id, String version, String bearerToken) throws AccessDeniedException {
		log.debug("SOAP request to delete Design by id {} and version {}", id, version);
		Design design = designService.findByDomainId(id, version);
		if(design == null) {
			log.warn("Request for delete nonexisted design wit id {}", id);
			return;
		}

		String organizationId = WebUtils.extractOrganizationIdFromToken(bearerToken, log);

		if(!DesignUtils.matchOrganizationId(design, organizationId)) {
			log.warn("Cannot delete entity, organization ID "+organizationId+" does not match that of entity: "+design.getOrganizationId());
			throw new AccessDeniedException("Cannot delete entity, organization ID "+organizationId+" does not match that of entity: "+design.getOrganizationId());
		}
		designService.delete(design.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<DesignDescriptorDTO> searchDesigns(String query, int page) {
		log.debug("SOAP request to search for a page of Designs for query {}", query);
		Page<Design> pageOfDesigns = designService.search(query, PageRequest.of(page, ITEMS_PER_PAGE));
		return PageResponse.buildFromPage(pageOfDesigns, designDescriptorConverter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateDesignStatus(String id, String version, String status, String bearerToken) throws Exception, AccessDeniedException {
		log.debug("SOAP request to update design status to {} by design id {} and version {}", status, id, version);
		Design design = designService.findByDomainId(id, version);

		String organizationId = WebUtils.extractOrganizationIdFromToken(bearerToken, log);
		if(!DesignUtils.matchOrganizationId(design, organizationId)) {
			log.warn("Cannot update entity, organization ID "+organizationId+" does not match that of entity: "+design.getOrganizationId());
			throw new AccessDeniedException("Cannot update entity, organization ID "+organizationId+" does not match that of entity: "+design.getOrganizationId());
		}

		// Ommited - no effect
        // DesignUtils.updateXmlStatusValue(design, status);

		designService.updateStatus(design.getId(), status);
	}

}
