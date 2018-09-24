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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Instance;
import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.repository.DocRepository;
import com.frequentis.maritime.mcsr.repository.DesignRepository;
import com.frequentis.maritime.mcsr.repository.SpecificationRepository;
import com.frequentis.maritime.mcsr.repository.InstanceRepository;
import com.frequentis.maritime.mcsr.repository.search.DocSearchRepository;
import com.frequentis.maritime.mcsr.repository.search.DesignSearchRepository;
import com.frequentis.maritime.mcsr.repository.search.SpecificationSearchRepository;
import com.frequentis.maritime.mcsr.repository.search.InstanceSearchRepository;

/**
 * Service Implementation for managing Doc.
 */
@Service
@Transactional
public class DocService {

    private final Logger log = LoggerFactory.getLogger(DocService.class);

    @Inject
    private DocRepository docRepository;

    @Inject
    private DocSearchRepository docSearchRepository;

    @Inject
    private DesignRepository designRepository;

    @Inject
    private DesignSearchRepository designSearchRepository;

    @Inject
    private SpecificationRepository specificationRepository;

    @Inject
    private SpecificationSearchRepository specificationSearchRepository;

    @Inject
    private InstanceRepository instanceRepository;

    @Inject
    private InstanceSearchRepository instanceSearchRepository;

    /**
     * Save a doc.
     *
     * @param doc the entity to save
     * @return the persisted entity
     */
    public Doc save(Doc doc) {
        log.debug("Request to save Doc : {}", doc);
        Doc result = docRepository.save(doc);
        docSearchRepository.save(result);
	Doc searchBaseDoc = docRepository.findById(doc.getId()).orElse(null);

	List<Design> designs = designRepository.findAllWithEagerRelationships();
	if (designs != null && designs.size() > 0) {
	    for (Design d : designs) {
		if (d.getDesignAsDoc() != null && d.getDesignAsDoc().getId() == doc.getId()) {
	            log.debug("Updating Linked Design: {}", d);
                    Design resultDesign = designRepository.save(d);
	            designSearchRepository.save(resultDesign);
		}
	    }
	}
	List<Specification> specs = specificationRepository.findAllWithEagerRelationships();
	if (specs != null && specs.size() > 0) {
	    for (Specification s : specs) {
		if (s.getSpecAsDoc() != null && s.getSpecAsDoc().getId() == doc.getId()) {
	            log.debug("Updating Linked Specification: {}", s);
            	    Specification resultSpecification = specificationRepository.save(s);
	    	    specificationSearchRepository.save(resultSpecification);
		}
	    }
	}
	List<Instance> instances = instanceRepository.findAllWithEagerRelationships();
	if (instances != null && instances.size() > 0) {
	    for (Instance i : instances) {
		if (i.getInstanceAsDoc() != null && i.getInstanceAsDoc().getId() == doc.getId()) {
	            log.debug("Updating Linked Instance: {}", i);
            	    Instance resultInstance = instanceRepository.save(i);
	    	    instanceSearchRepository.save(resultInstance);
		}
	    }
	}
	
        return result;
    }

    /**
     *  Get all the docs.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Doc> findAll(Pageable pageable) {
        log.debug("Request to get all Docs");
        Page<Doc> result = docRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one doc by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Doc findOne(Long id) {
        log.debug("Request to get Doc : {}", id);
        Doc doc = docRepository.findById(id).orElse(null);
        return doc;
    }

    /**
     *  Delete the  doc by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Doc : {}", id);
        docRepository.deleteById(id);
        docSearchRepository.deleteById(id);
    }

    /**
     * Search for the doc corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Doc> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Docs for query {}", query);
        log.debug("Doc search regusets query {}", queryStringQuery(query));
        return docSearchRepository.search(queryStringQuery(query), pageable);
    }
}
