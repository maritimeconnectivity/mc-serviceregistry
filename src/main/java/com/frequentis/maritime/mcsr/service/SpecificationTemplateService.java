/*
 * MaritimeCloud Service Registry
 * Copyright (c) 2016 Frequentis AG
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
package com.frequentis.maritime.mcsr.service;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frequentis.maritime.mcsr.domain.SpecificationTemplate;
import com.frequentis.maritime.mcsr.repository.SpecificationTemplateRepository;
import com.frequentis.maritime.mcsr.repository.search.SpecificationTemplateSearchRepository;

/**
 * Service Implementation for managing SpecificationTemplate.
 */
@Service
@Transactional
public class SpecificationTemplateService {

    private final Logger log = LoggerFactory.getLogger(SpecificationTemplateService.class);

    @Inject
    private SpecificationTemplateRepository specificationTemplateRepository;

    @Inject
    private SpecificationTemplateSearchRepository specificationTemplateSearchRepository;

    /**
     * Save a specificationTemplate.
     *
     * @param specificationTemplate the entity to save
     * @return the persisted entity
     */
    public SpecificationTemplate save(SpecificationTemplate specificationTemplate) {
        log.debug("Request to save SpecificationTemplate : {}", specificationTemplate);
        SpecificationTemplate result = specificationTemplateRepository.save(specificationTemplate);
        specificationTemplateSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the specificationTemplates.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<SpecificationTemplate> findAll(Pageable pageable) {
        log.debug("Request to get all SpecificationTemplates");
        Page<SpecificationTemplate> result = specificationTemplateRepository.findAll(pageable);
        return result;
    }

    /**
     * Get one specificationTemplate by id.
     *
     * @param id
     *            the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public SpecificationTemplate findOne(Long id) {
        log.debug("Request to get SpecificationTemplate : {}", id);
        SpecificationTemplate specificationTemplate = specificationTemplateRepository.findOneWithEagerRelationships(id);
        return specificationTemplate;
    }

    /**
     * Delete the specificationTemplate by id.
     *
     * @param id
     *            the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete SpecificationTemplate : {}", id);
        specificationTemplateRepository.deleteById(id);
        specificationTemplateSearchRepository.deleteById(id);
    }

    /**
     * Search for the specificationTemplate corresponding to the query.
     *
     * @param query
     *            the query of the search
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<SpecificationTemplate> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SpecificationTemplates for query {}", query);
        return specificationTemplateSearchRepository.search(queryStringQuery(query), pageable);
    }
}
