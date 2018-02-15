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

import java.io.IOException;
import java.util.List;

//import static org.elasticsearch.common.geo.builders.ShapeBuilder.newPoint;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.elasticsearch.common.geo.builders.ShapeBuilders;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frequentis.maritime.mcsr.domain.Instance;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.repository.InstanceRepository;
import com.frequentis.maritime.mcsr.repository.search.InstanceSearchRepository;
import com.frequentis.maritime.mcsr.web.rest.util.XmlUtil;
import com.vividsolutions.jts.geom.Geometry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.geo.GeoShapeModule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;
/**
 * Service Implementation for managing Instance.
 */
@Service
@Transactional
public class InstanceService {
    private final Logger log = LoggerFactory.getLogger(InstanceService.class);
    private static final boolean SEARCH_INCLUDE_NONCOMPLIANT_BY_DEFAULT = false;

    @Inject
    private InstanceRepository instanceRepository;

    @Inject
    private InstanceSearchRepository instanceSearchRepository;
    @Inject
    private ElasticsearchOperations elasticsearchOperations;
    
    @Inject
    private XmlService xmlService;
    
    

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

        // set compliant flag if is instance compliant
        setCompliantFlag(instance);

        Instance result = instanceRepository.save(instance);
        return result;
    }

    public Instance saveGeometry(Instance instance) throws Exception{
        //Save instance to DB
    	log.debug("saveGeometry for instance {}", instance);
        JsonNode geometry = instance.getGeometry();
        if (instance.getGeometry() == null || instance.getGeometry().asText() == null || instance.getGeometry().asText() == "null") {
            log.debug("Setting whole-earth coverage");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode wholeEarth = null;
            wholeEarth = mapper.readTree(wholeWorldGeoJson);
            geometry = wholeEarth;
        }

        // set compliant flag if is instance compliant
        setCompliantFlag(instance);

        //Save to ES
        instance.setGeometry(geometry);
        instanceSearchRepository.save(instance);
        return instance;
    }

    /**
     * Sets compliant flag to service instance to true if service has design and
     * specification, to false if service has not these documents.
     *
     * @param instance
     */
    private void setCompliantFlag(Instance instance) {
        if(instance == null) {
            return;
        }

        if(instance.getDesigns() != null
                && instance.getDesigns().size() > 0
                && instance.getSpecificationId() != null
                && !instance.getSpecificationId().isEmpty()) {

            instance.setCompliant(true);
        } else {
            instance.setCompliant(false);
        }
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
     * @throws Exception 
     */
    public void updateStatus(Long id, String status) throws Exception {
        log.debug("Request to update status of Instance : {}", id);
        Instance instance = instanceRepository.findOneWithEagerRelationships(id);
        
        Xml instanceXml = instance.getInstanceAsXml();
        if(instanceXml != null && instanceXml.getContent() != null) {
            String xml = instanceXml.getContent().toString();
            //Update the status value inside the xml definition
            String resultXml = XmlUtil.updateXmlNode(status, xml, "/*[local-name()='serviceInstance']/*[local-name()='status']");
            instanceXml.setContent(resultXml);
            // Save XML
            xmlService.save(instanceXml);
            instance.setInstanceAsXml(instanceXml);
        }
        
        instance.setStatus(status);
        instance.setInstanceAsXml(instanceXml);
        // Update also ES record
        Instance esInstance = instanceSearchRepository.findOneByInstanceIdAndVersion(QueryParser.escape(instance.getInstanceId()), instance.getVersion());
        esInstance.setStatus(status);
        esInstance.setInstanceAsXml(instanceXml);

        instanceSearchRepository.save(esInstance);
        save(instance);
    }

    /**
     * Search for the instance corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable
     *  @return the list of entities
     */
    public Page<Instance> search(String query, Pageable pageable) {
        return search(query, SEARCH_INCLUDE_NONCOMPLIANT_BY_DEFAULT, pageable);
    }

    /**
     * Search fo the instance corresponding to the query.
     *
     * <p>
     * Difference between searchAll and {@link #search(String, Pageable)} is that searchAll
     * includes non-compliant services
     * </p>
     *
     * @param query
     * @param pageable
     * @return
     */
    public Page<Instance> searchAll(String query, Pageable pageable) {
        log.debug("Request to search for a page of Instances for query {}", query);

        QueryStringQueryBuilder queryStringQuery2 = queryStringQuery(query);
        log.error(queryStringQuery2.toString());

        return instanceSearchRepository.search(queryStringQuery2, pageable);
    }

    /**
     * Search for the instance corresponding to the query.
     *
     *  @param query the query of the search
     *  @param includeNonCompliant include also non-compliant services
     *  @param pageable
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Instance> search(String query, boolean includeNonCompliant, Pageable pageable) {
        log.debug("Request to search for a page of Instances for query {}", query);
        BoolQueryBuilder qb =  QueryBuilders.boolQuery();
        qb.must(queryStringQuery(query));

        if(!includeNonCompliant) {
            qb.must(boolQuery().filter(QueryBuilders.termQuery("compliant", "true")));
        }

        return instanceSearchRepository.search(qb, pageable);
    }

    /**
     * Search for the instance by keyword.
     *
     *  @param keywords the keywords of the search
     *  @return the list of entities
     */
    public Page<Instance> searchKeywords(String keywords, Pageable pageable) {
        return searchKeywords(keywords, SEARCH_INCLUDE_NONCOMPLIANT_BY_DEFAULT, pageable);
    }

    /**
     * Search for the instance by keyword.
     *
     *  @param keywords the keywords of the search
     *  @param includeNonCompliant include also non-compliant services
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Instance> searchKeywords(String keywords, boolean includeNonCompliant, Pageable pageable) {
        log.debug("Request to search for a page of Instances for keywords  {}", keywords);

        if(includeNonCompliant) {
            return instanceSearchRepository.findByKeywords(keywords, pageable);
        } else {
            return instanceSearchRepository.findByKeywordsAndCompliantTrue(keywords, pageable);
        }
    }

    /**
     * Search for the instance by unlocode.
     *
     *  @param unlocode the unlocode of the search
     *  @return the list of entities
     */
    public Page<Instance> searchUnlocode(String unlocode, Pageable pageable) {
        return searchUnlocode(unlocode, SEARCH_INCLUDE_NONCOMPLIANT_BY_DEFAULT, pageable);
    }

    /**
     * Search for the instance by unlocode.
     *
     *  @param unlocode the unlocode of the search
     *  @param includeNonCompliant include also non-compliant services
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Instance> searchUnlocode(String unlocode, boolean includeNonCompliant, Pageable pageable) {
        log.debug("Request to search for a page of Instances for unlocode {}", unlocode);

        if(includeNonCompliant ) {
            return instanceSearchRepository.findByUnlocode(unlocode, pageable);
        } else {
            return instanceSearchRepository.findByUnlocodeAndCompliantTrue(unlocode, pageable);
        }

    }

    /**
     *  Get one instance by domain specific id (for example, maritime id) and version.
     *
     *  @param domainId the domain specific id of the instance
     *  @param version the version identifier of the instance
     *  @return the entity
     */
    public Instance findByDomainId(String domainId, String version) {
        return findByDomainId(domainId, version, SEARCH_INCLUDE_NONCOMPLIANT_BY_DEFAULT);
    }

    public Instance findAllByDomainId(String domainId, String version) {
        log.debug("Request to get Instance by domain id {} and version {} without restriction");
        List<Instance> findByDomainIdAndVersion = instanceRepository.findByDomainIdAndVersion(domainId, version);
        if(findByDomainIdAndVersion != null && !findByDomainIdAndVersion.isEmpty()) {
            return findByDomainIdAndVersion.get(0);
        }
        return null;
    }

    /**
     *  Get one instance by domain specific id (for example, maritime id) and version.
     *
     *  @param domainId the domain specific id of the instance
     *  @param version the version identifier of the instance
     *  @param includeNonCompliant include also non-compliant services
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Instance findByDomainId(String domainId, String version, boolean includeNonCompliant) {
        log.debug("Request to get Instance by domain id {} and version {}", domainId, version);
        Instance instance = null;
        try {
            Iterable<Instance> instances;
            if(includeNonCompliant) {
                instances = instanceRepository.findByDomainIdAndVersionEagerRelationshipsWithNonCompliant(domainId, version);
            } else {
                instances = instanceRepository.findByDomainIdAndVersionEagerRelationships(domainId, version);
            }

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
    public Instance findLatestVersionByDomainId(String domainId) {
        return findLatestVersionByDomainId(domainId, SEARCH_INCLUDE_NONCOMPLIANT_BY_DEFAULT);
    }

    /**
     *  Get one instance by domain specific id (for example, maritime id), only return the latest version.
     *
     *  @param domainId the domain specific id of the instance
     *  @param includeNonCompliant include also non-compliant services
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Instance findLatestVersionByDomainId(String domainId, boolean includeNonCompliant) {
        log.debug("Request to get Instance by domain id {}", domainId);
        Instance instance = null;
        DefaultArtifactVersion latestVersion = new DefaultArtifactVersion("0.0");
        try {

            Iterable<Instance> instances;
            if(includeNonCompliant) {
                instances = instanceRepository.findByDomainIdEagerRelationshipsWithNonCompliant(domainId);
            } else {
                instances = instanceRepository.findByDomainIdEagerRelationships(domainId);
            }

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
    public Page<Instance> findAllByDomainId(String domainId, Pageable pageable) {
        return findAllByDomainId(domainId, SEARCH_INCLUDE_NONCOMPLIANT_BY_DEFAULT, pageable);
    }

    /**
     *  Get all matching instances by domain specific id (for example, maritime id)
     *
     *  @param domainId the domain specific id of the instance
     *  @param includeNonCompliant include also non-compliant services
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Instance> findAllByDomainId(String domainId, boolean includeNonCompliant, Pageable pageable) {
        log.debug("Request to get Instance by domain id {}", QueryParser.escape(domainId));
        Page<Instance> instances = null;

        BoolQueryBuilder qb = QueryBuilders.boolQuery();
        qb.must(queryStringQuery("instanceId:" + QueryParser.escape(domainId)));
        if(!includeNonCompliant) {
            qb.must(boolQuery().filter(QueryBuilders.termQuery("compliant", true)));
        }

        try {
            instances = instanceSearchRepository.search(qb, pageable);
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
    public Page<Instance> findByLocation(double latitude, double longitude, String query, Pageable pageable) throws Exception {
        return findByLocation(latitude, longitude, query, SEARCH_INCLUDE_NONCOMPLIANT_BY_DEFAULT, pageable);
    }

    /**
     *  Get service instances by location
     *
     *  @param latitude search latitude
     *  @param longitude search longitude
     *  @param includeNonCompliant include also non-compliant services
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Page<Instance> findByLocation(double latitude, double longitude, String query, boolean includeNonCompliant, Pageable pageable) throws Exception {
        log.debug("Request to get Instance by lat {} long {} and query {}", latitude, longitude, query);
        Page<Instance> instances = null;
        Geometry g;
        ShapeBuilder sb = ShapeBuilders.newPoint(longitude, latitude);
        if (query == null || query.trim().length() == 0) {
            query = "*";
        }
        BoolQueryBuilder qb = boolQuery()
            .must(geoShapeQuery("geometry", sb))
            .must(queryStringQuery(query));
        if(!includeNonCompliant) {
            qb.must(boolQuery().filter(QueryBuilders.termQuery("compliant", true)));
        }

        instances = instanceSearchRepository.search(qb, pageable);
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
        return findByGeoshape(geoJson, query, SEARCH_INCLUDE_NONCOMPLIANT_BY_DEFAULT, pageable);
    }

    /**
     *  Get service instances by geoshape query
     *
     *  @param geoJson search geometry in geojson format
     *  @param includeNonCompliant include also non-compliant services
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public Page<Instance> findByGeoshape(String geoJson, String query, boolean includeNonCompliant, Pageable pageable) throws Exception {
        log.debug("Request to get Instance by query {} and geojson {}", query, geoJson);
        Page<Instance> instances = null;

        ObjectMapper om = new ObjectMapper();
        om.registerModule(new GeoShapeModule());

        XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, geoJson);
        parser.nextToken();
        ShapeBuilder sb = ShapeBuilder.parse(parser);
        if (query == null || query.trim().length() == 0) {
            query = "*";
        }
        BoolQueryBuilder qb = boolQuery()
            .must(geoShapeQuery("geometry", sb))
            .must(queryStringQuery(query));
        if(!includeNonCompliant) {
            qb.must(boolQuery().filter(QueryBuilders.termQuery("compliant", true)));
        }
        instances = instanceSearchRepository.search(qb, pageable);

        return instances;
    }


}
