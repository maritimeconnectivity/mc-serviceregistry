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

import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.repository.XmlRepository;
import com.frequentis.maritime.mcsr.repository.search.XmlSearchRepository;
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
 * Service Implementation for managing Xml.
 */
@Service
@Transactional
public class XmlService {

    private final Logger log = LoggerFactory.getLogger(XmlService.class);

    @Inject
    private XmlRepository xmlRepository;

    @Inject
    private XmlSearchRepository xmlSearchRepository;

    /**
     * Save a xml.
     *
     * @param xml the entity to save
     * @return the persisted entity
     */
    public Xml save(Xml xml) {
        log.debug("Request to save Xml : {}", xml);
        Xml result = xmlRepository.save(xml);
        xmlSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the xmls.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Xml> findAll(Pageable pageable) {
        log.debug("Request to get all Xmls");
        Page<Xml> result = xmlRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one xml by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Xml findOne(Long id) {
        log.debug("Request to get Xml : {}", id);
        Xml xml = xmlRepository.findOne(id);
        return xml;
    }

    /**
     *  Delete the  xml by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Xml : {}", id);
        xmlRepository.delete(id);
        xmlSearchRepository.delete(id);
    }

    /**
     * Search for the xml corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Xml> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Xmls for query {}", query);
        return xmlSearchRepository.search(queryStringQuery(query), pageable);
    }
}
