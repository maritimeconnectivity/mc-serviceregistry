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

import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.repository.SpecificationRepository;
import com.frequentis.maritime.mcsr.repository.search.SpecificationSearchRepository;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing Specification.
 */
@Service
@Transactional
public class SpecificationService {

    private final Logger log = LoggerFactory.getLogger(SpecificationService.class);

    @Inject
    private SpecificationRepository specificationRepository;

    @Inject
    private SpecificationSearchRepository specificationSearchRepository;

    /**
     * Save a specification.
     *
     * @param specification the entity to save
     * @return the persisted entity
     */
    public Specification save(Specification specification) {
        log.debug("Request to save Specification : {}", specification);
        Specification result = specificationRepository.save(specification);
        specificationSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the specifications.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Specification> findAll(Pageable pageable) {
        log.debug("Request to get all Specifications");
        Page<Specification> result = specificationRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one specification by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Specification findOne(Long id) {
        log.debug("Request to get Specification : {}", id);
        Specification specification = specificationRepository.findOneWithEagerRelationships(id);
        return specification;
    }

    /**
     *  Delete the  specification by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Specification : {}", id);
        specificationRepository.deleteById(id);
        specificationSearchRepository.deleteById(id);
    }

    /**
     *  update the status of a specification by id.
     *
     *  @param id the id of the entity
     *  @param status the status of the entity
     */
    public void updateStatus(Long id, String status) {
        log.debug("Request to update status of Specification : {}", id);
        Specification specification = specificationRepository.findOneWithEagerRelationships(id);
        specification.setStatus(status);
        save(specification);
    }

    /**
     * Search for the specification corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Specification> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Specifications for query {}", query);
        return specificationSearchRepository.search(queryStringQuery(query), pageable);
    }

    /**
     *  Get one specification by domain specific id (for example, maritime id) and version.
     *
     *  @param domainId the domain specific id of the specification
     *  @param version the version identifier of the specification
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Specification findByDomainId(String domainId, String version) {
        log.debug("Request to get Specification by domain id {} and version {}", domainId, version);
        Specification specification = null;
        try {
            Iterable<Specification> specifications = specificationRepository.findByDomainIdAndVersion(domainId, version);
            if (specifications.iterator().hasNext()) {
                specification = specifications.iterator().next();
            }
        } catch (Exception e) {
            log.debug("Could not find specification for domain id {} and version {}", domainId, version);
            e.printStackTrace();
        }
        return specification;
    }

    /**
     *  Get one specification by domain specific id (for example, maritime id), only return the latest version.
     *
     *  @param domainId the domain specific id of the specification
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Specification findLatestVersionByDomainId(String domainId) {
        log.debug("Request to get Specification by domain id {}", domainId);
        Specification specification = null;
        DefaultArtifactVersion latestVersion = new DefaultArtifactVersion("0.0");
        try {
            Iterable<Specification> specifications = specificationRepository.findByDomainId(domainId);
            if (specifications.iterator().hasNext()) {
                Specification i = specifications.iterator().next();
                //Compare version numbers, save the instance if it's a newer version
                DefaultArtifactVersion iv = new DefaultArtifactVersion(i.getVersion());
                if (iv.compareTo(latestVersion) > 0) {
                    specification = i;
                    latestVersion = iv;
                }
            }
        } catch (Exception e) {
            log.debug("Could not find specification for domain id {}", domainId);
            e.printStackTrace();
        }
        return specification;
    }

    /**
     *  Get all specifications by domain specific id (for example, maritime id).
     *
     *  @param domainId the domain specific id of the specification
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Specification> findAllByDomainId(String domainId, Pageable pageable) {
        log.debug("Request to get Specification by domain id {}", domainId);
        Page<Specification> specifications = null;
        try {
            specifications = specificationSearchRepository.search(queryStringQuery("specificationId:"+domainId), pageable);
        } catch (Exception e) {
            log.debug("Could not find specification for domain id {}", domainId);
            e.printStackTrace();
        }
        return specifications;
    }

}
