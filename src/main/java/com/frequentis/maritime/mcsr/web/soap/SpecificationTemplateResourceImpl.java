package com.frequentis.maritime.mcsr.web.soap;

import javax.jws.WebService;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frequentis.maritime.mcsr.domain.SpecificationTemplate;
import com.frequentis.maritime.mcsr.service.SpecificationTemplateService;
import com.frequentis.maritime.mcsr.web.soap.converters.specification.SpecificationTemplateDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.specification.SpecificationTemplateDescriptorConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.specification.SpecificationTemplateParameterConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateParameterDTO;
import com.frequentis.maritime.mcsr.web.soap.errors.ProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

@Component
@WebService(endpointInterface = "com.frequentis.maritime.mcsr.web.soap.SpecificationTemplateResource")
@Secured("ROLE_USER")
public class SpecificationTemplateResourceImpl implements SpecificationTemplateResource {
	private static final int ITEMS_PER_PAGE = 50;

	Logger log = LoggerFactory.getLogger(SpecificationTemplateResourceImpl.class);

	@Autowired
	SpecificationTemplateService specificationTemplateService;

	@Autowired
	SpecificationTemplateParameterConverter spc;

	@Autowired
	SpecificationTemplateDTOConverter stc;

	@Autowired
	SpecificationTemplateDescriptorConverter stdc;

	/**
	 * {@inheritDoc}}
	 * @throws ProcessingException
	 */
	@Override
	@Transactional
	public SpecificationTemplateDescriptorDTO createSpecificationTemplate(
			SpecificationTemplateParameterDTO specificationTemplate) throws ProcessingException {

        log.debug("SOAP request to save SpecificationTemplate : {}", specificationTemplate);
        if (specificationTemplate.id != null) {
            throw new ProcessingException("A new specificationTemplate cannot already have an ID");
        }
        SpecificationTemplate result = specificationTemplateService.save(spc.convert(specificationTemplate));

		return stc.convert(result);
	}

	/**
	 * {@inheritDoc}}
	 * @throws ProcessingException
	 */
	@Override
	@Transactional
	public SpecificationTemplateDescriptorDTO updateSpecificationTemplate(
			SpecificationTemplateParameterDTO specificationTemplate) throws ProcessingException {

        log.debug("SOAP request to update SpecificationTemplate : {}", specificationTemplate);
        if (specificationTemplate.id == null) {
            return createSpecificationTemplate(specificationTemplate);
        }
        SpecificationTemplate result = specificationTemplateService.save(spc.convert(specificationTemplate));

        return stc.convert(result);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public PageDTO<SpecificationTemplateDescriptorDTO> getAllSpecificationTemplates(int page) {
        log.debug("SOAP request to get a page of SpecificationTemplates");
        Page<SpecificationTemplate> pageResult = specificationTemplateService.findAll(PageRequest.of(page, ITEMS_PER_PAGE));

        return PageResponse.buildFromPage(pageResult, stdc);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public SpecificationTemplateDTO getSpecificationTemplate(Long id) {
        log.debug("SOAP request to get SpecificationTemplate : {}", id);
        SpecificationTemplate specificationTemplate = specificationTemplateService.findOne(id);

        return stc.convert(specificationTemplate);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void deleteSpecificationTemplate(Long id) {
        log.debug("REST request to delete SpecificationTemplate : {}", id);
        specificationTemplateService.delete(id);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public PageDTO<SpecificationTemplateDescriptorDTO> searchSpecificationTemplates(String query, int page) {
        log.debug("SOAP request to search for a page of SpecificationTemplates for query {}", query);
        Page<SpecificationTemplate> responsePage = specificationTemplateService.search(query, PageRequest.of(page, ITEMS_PER_PAGE));

        return PageResponse.buildFromPage(responsePage, stdc);
	}

}
