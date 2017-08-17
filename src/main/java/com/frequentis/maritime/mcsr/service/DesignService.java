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

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.repository.DesignRepository;
import com.frequentis.maritime.mcsr.repository.search.DesignSearchRepository;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing Design.
 */
@Service
@Transactional
public class DesignService {

    private final Logger log = LoggerFactory.getLogger(DesignService.class);

    @Inject
    private DesignRepository designRepository;

    @Inject
    private DesignSearchRepository designSearchRepository;

    /**
     * Save a design.
     *
     * @param design the entity to save
     * @return the persisted entity
     */
    public Design save(Design design) {
        log.debug("Request to save Design : {}", design);
        Design result = designRepository.save(design);
        designSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the designs.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Design> findAll(Pageable pageable) {
        log.debug("Request to get all Designs");
        Page<Design> result = designRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one design by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Design findOne(Long id) {
        log.debug("Request to get Design : {}", id);
        Design design = designRepository.findOneWithEagerRelationships(id);
        return design;
    }

    /**
     *  Delete the  design by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Design : {}", id);
        designRepository.deleteById(id);
        designSearchRepository.deleteById(id);
    }

    /**
     *  Update the status of a design by id.
     *
     *  @param id the id of the entity
     *  @param status the status of the entity
     */
    public void updateStatus(Long id, String status) {
        log.debug("Request to update status of Design : {}", id);
        Design design = designRepository.findOneWithEagerRelationships(id);
        design.setStatus(status);
        save(design);
    }

    /**
     * Search for the design corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Design> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Designs for query {}", query);
        return designSearchRepository.search(queryStringQuery(query), pageable);
    }

    /**
     *  Get one design by domain specific id (for example, maritime id) and version.
     *
     *  @param domainId the domain specific id of the design
     *  @param version the version identifier of the design
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Design findByDomainId(String domainId, String version) {
        log.debug("Request to get Design by domain id {} and version {}", domainId, version);
        Design design = null;
        try {
            Iterable<Design> designs = designRepository.findByDomainIdAndVersion(domainId, version);
            if (designs.iterator().hasNext()) {
                design = designs.iterator().next();
            }
        } catch (Exception e) {
            log.debug("Could not find design for domain id {} and version {}", domainId, version);
            e.printStackTrace();
        }
        return design;
    }

    /**
     *  Get one design by domain specific id (for example, maritime id), only return the latest version.
     *
     *  @param domainId the domain specific id of the design
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Design findLatestVersionByDomainId(String domainId) {
        log.debug("Request to get Design by domain id {}", domainId);
        Design design = null;
        DefaultArtifactVersion latestVersion = new DefaultArtifactVersion("0.0");
        try {
            Iterable<Design> designs = designRepository.findByDomainId(domainId);
            if (designs.iterator().hasNext()) {
                Design i = designs.iterator().next();
                //Compare version numbers, save the instance if it's a newer version
                DefaultArtifactVersion iv = new DefaultArtifactVersion(i.getVersion());
                if (iv.compareTo(latestVersion) > 0) {
                    design = i;
                    latestVersion = iv;
                }
            }
        } catch (Exception e) {
            log.debug("Could not find specification for domain id {}", domainId);
            e.printStackTrace();
        }
        return design;
    }

    /**
     *  Get all designs by domain specific id (for example, maritime id).
     *
     *  @param domainId the domain specific id of the design
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Design> findAllByDomainId(String domainId, Pageable pageable) {
        log.debug("Request to get Design by domain id {}", domainId);
        Page<Design> designs = null;
        try {
        	// I think that this doesn't make sense.
            //designs = designSearchRepository.search(queryStringQuery("design_id=\""+domainId), pageable);
        	designs = designSearchRepository.search(queryStringQuery("designId:=" + domainId), pageable);
        } catch (Exception e) {
            log.debug("Could not find design for domain id {}", domainId);
            e.printStackTrace();
        }
        return designs;
    }

    /**
     *  Get all designs by specification id (for example, maritime id).
     *
     *  @param specificationId the domain specific id of the specification this design is for
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Design> findAllBySpecificationId(String specificationId) {
        log.debug("Request to get Design by specification id {}", specificationId);
        List<Design> designs = null;
        try {
            designs = designRepository.findBySpecificationId(specificationId);
        } catch (Exception e) {
            log.debug("Could not find design for domain id {}", specificationId);
            e.printStackTrace();
        }
        return designs;
    }
}
