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

import javax.jws.WebService;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frequentis.maritime.mcsr.domain.SpecificationTemplateSet;
import com.frequentis.maritime.mcsr.service.SpecificationTemplateSetService;
import com.frequentis.maritime.mcsr.web.soap.converters.specification.SpecificationTemplateSetConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.specification.SpecificationTemplateSetDescriptorConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.specification.SpecificationTemplateSetParameterConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateSetDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateSetDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateSetParameter;
import com.frequentis.maritime.mcsr.web.soap.errors.ProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

@Component
@Transactional
@WebService(endpointInterface = "com.frequentis.maritime.mcsr.web.soap.SpecificationTemplateSetResource")
@Secured("ROLE_USER")
public class SpecificationTemplateSetResourceImpl implements SpecificationTemplateSetResource {
	private static final Logger log = LoggerFactory.getLogger(SpecificationTemplateSetResourceImpl.class);

	private static final int ITEMS_PER_PAGE = 50;

	@Autowired
	SpecificationTemplateSetService specificationTemplateSetService;

	@Autowired
	SpecificationTemplateSetParameterConverter specTempSetParamConverter;

	@Autowired
	SpecificationTemplateSetDescriptorConverter specTempSetDescriptorConverter;;

	@Autowired
	SpecificationTemplateSetConverter specTempSetConverter;;

	/**
	 * {@inheritDoc}
	 * @throws ProcessingException
	 */
	@Override
	public SpecificationTemplateSetDescriptorDTO createSpecificationTemplateSet(
	        SpecificationTemplateSetParameter specificationTemplateSet) throws ProcessingException {

        log.debug("SOAP request to save SpecificationTemplateSet : {}", specificationTemplateSet);
        if (specificationTemplateSet.id != null) {
        	throw new ProcessingException("A new specificationTemplateSet cannot already have an ID");
        }
        SpecificationTemplateSet result = specificationTemplateSetService.save(specTempSetParamConverter.convert(specificationTemplateSet));

        return specTempSetDescriptorConverter.convert(result);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SpecificationTemplateSetDescriptorDTO updateSpecificationTemplateSet(
	        SpecificationTemplateSetParameter specificationTemplateSet) throws ProcessingException {

        log.debug("SOAP request to update SpecificationTemplateSet : {}", specificationTemplateSet);
        if (specificationTemplateSet.id == null) {
            return createSpecificationTemplateSet(specificationTemplateSet);
        }
        SpecificationTemplateSet result = specificationTemplateSetService.save(specTempSetParamConverter.convert(specificationTemplateSet));

        return specTempSetDescriptorConverter.convert(result);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<SpecificationTemplateSetDescriptorDTO> getAllSpecificationTemplateSets(int page) {
        log.debug("SOAP request to get a page of SpecificationTemplateSets");
        Page<SpecificationTemplateSet> pageResponse = specificationTemplateSetService.findAll(PageRequest.of(page, ITEMS_PER_PAGE));

        return PageResponse.buildFromPage(pageResponse, specTempSetDescriptorConverter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SpecificationTemplateSetDTO getSpecificationTemplateSet(Long id) {
        log.debug("SOAP request to get SpecificationTemplateSet : {}", id);
        SpecificationTemplateSet specificationTemplateSet = specificationTemplateSetService.findOne(id);

        return specTempSetConverter.convert(specificationTemplateSet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteSpecificationTemplateSet(Long id) {
        log.debug("SOAP request to delete SpecificationTemplateSet : {}", id);
        specificationTemplateSetService.delete(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<SpecificationTemplateSetDescriptorDTO> searchSpecificationTemplateSets(String query, int page) {
        log.debug("SOAP request to search for a page of SpecificationTemplateSets for query {}", query);
        Page<SpecificationTemplateSet> pageResponse = specificationTemplateSetService.search(query, PageRequest.of(page, ITEMS_PER_PAGE));

        return PageResponse.buildFromPage(pageResponse, specTempSetDescriptorConverter);
	}

}
