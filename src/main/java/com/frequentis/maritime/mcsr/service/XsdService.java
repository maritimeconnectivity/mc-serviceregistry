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

import com.frequentis.maritime.mcsr.domain.Xsd;
import com.frequentis.maritime.mcsr.repository.XsdRepository;
import com.frequentis.maritime.mcsr.repository.search.XsdSearchRepository;
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
 * Service Implementation for managing Xsd.
 */
@Service
@Transactional
public class XsdService {

    private final Logger log = LoggerFactory.getLogger(XsdService.class);

    @Inject
    private XsdRepository xsdRepository;

    @Inject
    private XsdSearchRepository xsdSearchRepository;

    /**
     * Save a xsd.
     *
     * @param xsd the entity to save
     * @return the persisted entity
     */
    public Xsd save(Xsd xsd) {
        log.debug("Request to save Xsd : {}", xsd);
        Xsd result = xsdRepository.save(xsd);
        xsdSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the xsds.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Xsd> findAll(Pageable pageable) {
        log.debug("Request to get all Xsds");
        Page<Xsd> result = xsdRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one xsd by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Xsd findOne(Long id) {
        log.debug("Request to get Xsd : {}", id);
        Xsd xsd = xsdRepository.findById(id).orElse(null);
        return xsd;
    }

    /**
     *  Delete the  xsd by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Xsd : {}", id);
        Xsd xsd = xsdRepository.getOne(id);
        xsdRepository.delete(xsd);
        xsdSearchRepository.delete(xsd);
    }

    /**
     * Search for the xsd corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Xsd> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Xsds for query {}", query);
        return xsdSearchRepository.search(queryStringQuery(query), pageable);
    }
}
