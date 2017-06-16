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

import com.frequentis.maritime.mcsr.domain.SpecificationTemplateSet;
import com.frequentis.maritime.mcsr.repository.SpecificationTemplateSetRepository;
import com.frequentis.maritime.mcsr.repository.search.SpecificationTemplateSetSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing SpecificationTemplateSet.
 */
@Service
@Transactional
public class SpecificationTemplateSetService {

    private final Logger log = LoggerFactory.getLogger(SpecificationTemplateSetService.class);

    @Inject
    private SpecificationTemplateSetRepository specificationTemplateSetRepository;

    @Inject
    private SpecificationTemplateSetSearchRepository specificationTemplateSetSearchRepository;

    /**
     * Save a specificationTemplateSet.
     *
     * @param specificationTemplateSet the entity to save
     * @return the persisted entity
     */
    public SpecificationTemplateSet save(SpecificationTemplateSet specificationTemplateSet) {
        log.debug("Request to save SpecificationTemplateSet : {}", specificationTemplateSet);
        SpecificationTemplateSet result = specificationTemplateSetRepository.save(specificationTemplateSet);
        specificationTemplateSetSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the specificationTemplateSets.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<SpecificationTemplateSet> findAll(Pageable pageable) {
        log.debug("Request to get all SpecificationTemplateSets");
        Page<SpecificationTemplateSet> result = specificationTemplateSetRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one specificationTemplateSet by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public SpecificationTemplateSet findOne(Long id) {
        log.debug("Request to get SpecificationTemplateSet : {}", id);
        SpecificationTemplateSet specificationTemplateSet = specificationTemplateSetRepository.findOneWithEagerRelationships(id);
        return specificationTemplateSet;
    }

    /**
     *  Delete the  specificationTemplateSet by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete SpecificationTemplateSet : {}", id);
        //specificationTemplateSetRepository.delete(id);
        //specificationTemplateSetSearchRepository.delete(id);
    }

    /**
     * Search for the specificationTemplateSet corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<SpecificationTemplateSet> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of SpecificationTemplateSets for query {}", query);
        return specificationTemplateSetSearchRepository.search(queryStringQuery(query), pageable);
    }
}
