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

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.geoShapeQuery;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

//import static org.elasticsearch.common.geo.builders.ShapeBuilder.newPoint;

import javax.inject.Inject;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frequentis.maritime.mcsr.domain.Instance;
import com.frequentis.maritime.mcsr.repository.InstanceRepository;
import com.frequentis.maritime.mcsr.repository.search.InstanceSearchRepository;
import com.vividsolutions.jts.geom.Geometry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.geo.GeoShape;
import org.springframework.data.elasticsearch.core.geo.GeoShapeModule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * Service Implementation for managing Instance.
 */
@Service
@Transactional
public class InstanceService {

    private final Logger log = LoggerFactory.getLogger(InstanceService.class);

    @Inject
    private InstanceRepository instanceRepository;

    @Inject
    private InstanceSearchRepository instanceSearchRepository;
    @Inject
    private ElasticsearchOperations elasticsearchOperations;

    private String wholeWorldGeoJson = "{\n" +
        "  \"type\": \"Polygon\",\n" +
        "  \"coordinates\": [\n" +
        "    [[-180, -90], [-180, 90], [180, 90], [180, -90], [-180, -90]]\n" +
        "  ]\n" +
        "}";

    /**
     * Save a instance.
     *
     * @param instance the entity to save
     * @return the persisted entity
     */
    @Transactional
    public Instance save(Instance instance) {
        log.debug("Request to save Instance : {}", instance);
        instance.setGeometry(null);
        Instance result = instanceRepository.save(instance);
        return result;
    }

    public Instance saveGeometry(Instance instance) throws Exception{
        //Save instance to DB
        JsonNode geometry = instance.getGeometry();
        if (instance.getGeometry() == null || instance.getGeometry().asText() == null || instance.getGeometry().asText() == "null") {
            log.debug("Setting whole-earth coverage");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode wholeEarth = null;
            wholeEarth = mapper.readTree(wholeWorldGeoJson);
            geometry = wholeEarth;
        }
        //Save to ES
        instance.setGeometry(geometry);
        instanceSearchRepository.save(instance);
        return instance;
    }


    /**
     *  Get all the instances.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Instance> findAll(Pageable pageable) {
        log.debug("Request to get all Instances");
        Page<Instance> result = null;
        result = instanceRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one instance by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Instance findOne(Long id) {
        log.debug("Request to get Instance : {}", id);
        Instance instance = instanceRepository.findOneWithEagerRelationships(id);
        return instance;
    }

    /**
     *  Delete the  instance by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Instance : {}", id);
        instanceRepository.deleteById(id);
        instanceSearchRepository.deleteById(id);
    }

    /**
     *  Update the status of an instance by id.
     *
     *  @param id the id of the entity
     *  @param status the status of the entity
     */
    public void updateStatus(Long id, String status) {
        log.debug("Request to update status of Instance : {}", id);
        Instance instance = instanceRepository.findOneWithEagerRelationships(id);
        instance.setStatus(status);
        save(instance);
    }

    /**
     * Search for the instance corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Instance> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Instances for query {}", query);
        return instanceSearchRepository.search(queryStringQuery(query), pageable);
    }

    /**
     * Search for the instance by keyword.
     *
     *  @param keywords the keywords of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Instance> searchKeywords(String keywords, Pageable pageable) {
        log.debug("Request to search for a page of Instances for keywords  {}", keywords);
        return instanceSearchRepository.findByKeywords(keywords, pageable);
    }

    /**
     * Search for the instance by unlocode.
     *
     *  @param unlocode the unlocode of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Instance> searchUnlocode(String unlocode, Pageable pageable) {
        log.debug("Request to search for a page of Instances for unlocode {}", unlocode);
        return instanceSearchRepository.findByUnlocode(unlocode, pageable);
    }

    /**
     *  Get one instance by domain specific id (for example, maritime id) and version.
     *
     *  @param domainId the domain specific id of the instance
     *  @param version the version identifier of the instance
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Instance findByDomainId(String domainId, String version) {
        log.debug("Request to get Instance by domain id {} and version {}", domainId, version);
        Instance instance = null;
        try {
            Iterable<Instance> instances = instanceRepository.findByDomainIdAndVersionEagerRelationships(domainId, version);
            if (instances.iterator().hasNext()) {
                instance = instances.iterator().next();
            }
        } catch (Exception e) {
            log.debug("Could not find instance for domain id {} and version {}", domainId, version);
            e.printStackTrace();
        }
        return instance;
    }

    /**
     *  Get one instance by domain specific id (for example, maritime id), only return the latest version.
     *
     *  @param domainId the domain specific id of the instance
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Instance findLatestVersionByDomainId(String domainId) {
        log.debug("Request to get Instance by domain id {}", domainId);
        Instance instance = null;
        DefaultArtifactVersion latestVersion = new DefaultArtifactVersion("0.0");
        try {
            Iterable<Instance> instances = instanceRepository.findByDomainIdEagerRelationships(domainId);
            if (instances.iterator().hasNext()) {
                Instance i = instances.iterator().next();
                //Compare version numbers, save the instance if it's a newer version
                DefaultArtifactVersion iv = new DefaultArtifactVersion(i.getVersion());
                if (iv.compareTo(latestVersion) > 0 && i.getStatus().equalsIgnoreCase(Instance.SERVICESTATUS_LIVE)) {
                    instance = i;
                    latestVersion = iv;
                }
            }
        } catch (Exception e) {
            log.debug("Could not find a live instance for domain id {}", domainId);
            e.printStackTrace();
        }
        return instance;
    }

    /**
     *  Get all matching instances by domain specific id (for example, maritime id)
     *
     *  @param domainId the domain specific id of the instance
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Instance> findAllByDomainId(String domainId, Pageable pageable) {
        log.debug("Request to get Instance by domain id {}", QueryParser.escape(domainId));
        Page<Instance> instances = null;
        try {
            instances = instanceSearchRepository.search(queryStringQuery("instanceId:" + QueryParser.escape(domainId)), pageable);
        } catch (Exception e) {
            log.debug("Could not find instance for domain id {}", domainId);
            e.printStackTrace();
        }
        return instances;
    }

    /**
     *  Get service instances by location
     *
     *  @param latitude search latitude
     *  @param longitude search longitude
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Page<Instance> findByLocation(double latitude, double longitude, String query, Pageable pageable) throws Exception {
        log.debug("Request to get Instance by lat {} long {} and query {}", latitude, longitude, query);
        Page<Instance> instances = null;
        Geometry g;
        //ShapeBuilder sb = newPoint(longitude, latitude);
        if (query == null || query.trim().length() == 0) {
            query = "*";
        }
//        QueryBuilder qb = boolQuery()
//            .must(geoShapeQuery("geometry", sb))
//            .must(queryStringQuery(query));
//        instances = instanceSearchRepository.search(qb, pageable);
        return instances;
    }

    /**
     *  Get service instances by geoshape query
     *
     *  @param geoJson search geometry in geojson format
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Page<Instance> findByGeoshape(String geoJson, String query, Pageable pageable) throws Exception {
        log.debug("Request to get Instance by query {} and geojson {}", query, geoJson);
        Page<Instance> instances = null;

        ObjectMapper om = new ObjectMapper();
        om.registerModule(new GeoShapeModule());

        GeoShape shape = om.readValue(geoJson, GeoShape.class);

        XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, geoJson);
        parser.nextToken();
        ShapeBuilder sb = ShapeBuilder.parse(parser);
        if (query == null || query.trim().length() == 0) {
            query = "*";
        }
        QueryBuilder qb = boolQuery()
            .must(geoShapeQuery("geometry", sb))
            .must(queryStringQuery(query));
        instances = instanceSearchRepository.search(qb, pageable);

        return instances;
    }


}
