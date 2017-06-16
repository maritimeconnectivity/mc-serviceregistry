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

import java.lang.reflect.Method;
import java.util.List;

import javax.inject.Inject;

import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.annotation.Timed;
import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.domain.Instance;
import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.domain.SpecificationTemplate;
import com.frequentis.maritime.mcsr.domain.SpecificationTemplateSet;
import com.frequentis.maritime.mcsr.domain.User;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.domain.Xsd;
import com.frequentis.maritime.mcsr.repository.DesignRepository;
import com.frequentis.maritime.mcsr.repository.DocRepository;
import com.frequentis.maritime.mcsr.repository.InstanceRepository;
import com.frequentis.maritime.mcsr.repository.SpecificationRepository;
import com.frequentis.maritime.mcsr.repository.SpecificationTemplateRepository;
import com.frequentis.maritime.mcsr.repository.SpecificationTemplateSetRepository;
import com.frequentis.maritime.mcsr.repository.UserRepository;
import com.frequentis.maritime.mcsr.repository.XmlRepository;
import com.frequentis.maritime.mcsr.repository.XsdRepository;
import com.frequentis.maritime.mcsr.repository.search.DesignSearchRepository;
import com.frequentis.maritime.mcsr.repository.search.DocSearchRepository;
import com.frequentis.maritime.mcsr.repository.search.InstanceSearchRepository;
import com.frequentis.maritime.mcsr.repository.search.SpecificationSearchRepository;
import com.frequentis.maritime.mcsr.repository.search.SpecificationTemplateSearchRepository;
import com.frequentis.maritime.mcsr.repository.search.SpecificationTemplateSetSearchRepository;
import com.frequentis.maritime.mcsr.repository.search.UserSearchRepository;
import com.frequentis.maritime.mcsr.repository.search.XmlSearchRepository;
import com.frequentis.maritime.mcsr.repository.search.XsdSearchRepository;
import com.frequentis.maritime.mcsr.web.rest.registry.ServiceInstanceResource;
import com.frequentis.maritime.mcsr.web.rest.util.InstanceUtil;
import com.frequentis.maritime.mcsr.web.rest.util.XmlUtil;

@Service
public class ElasticsearchIndexService {

    private final Logger log = LoggerFactory.getLogger(ElasticsearchIndexService.class);

    @Inject
    private DesignRepository designRepository;

    @Inject
    private DesignSearchRepository designSearchRepository;

    @Inject
    private DocRepository docRepository;

    @Inject
    private DocSearchRepository docSearchRepository;

    @Inject
    private ServiceInstanceResource serviceInstanceResource;

    @Inject
    private InstanceRepository instanceRepository;

    @Inject
    private InstanceSearchRepository instanceSearchRepository;

    @Inject
    private SpecificationRepository specificationRepository;

    @Inject
    private SpecificationSearchRepository specificationSearchRepository;

    @Inject
    private SpecificationTemplateRepository specificationTemplateRepository;

    @Inject
    private SpecificationTemplateSearchRepository specificationTemplateSearchRepository;

    @Inject
    private SpecificationTemplateSetRepository specificationTemplateSetRepository;

    @Inject
    private SpecificationTemplateSetSearchRepository specificationTemplateSetSearchRepository;

    @Inject
    private XmlRepository xmlRepository;

    @Inject
    private XmlSearchRepository xmlSearchRepository;

    @Inject
    private XsdRepository xsdRepository;

    @Inject
    private XsdSearchRepository xsdSearchRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserSearchRepository userSearchRepository;

    @Inject
    private ElasticsearchTemplate elasticsearchTemplate;

    @Async
    @Timed
    public void reindexAll() {
        reindexForClass(Design.class, designRepository, designSearchRepository);
        reindexForClass(Doc.class, docRepository, docSearchRepository);
        reindexInstances(instanceRepository, instanceSearchRepository, serviceInstanceResource);
        reindexForClass(Specification.class, specificationRepository, specificationSearchRepository);
        reindexForClass(SpecificationTemplate.class, specificationTemplateRepository, specificationTemplateSearchRepository);
        reindexForClass(SpecificationTemplateSet.class, specificationTemplateSetRepository, specificationTemplateSetSearchRepository);
        reindexForClass(Xml.class, xmlRepository, xmlSearchRepository);
        reindexForClass(Xsd.class, xsdRepository, xsdSearchRepository);
        reindexForClass(User.class, userRepository, userSearchRepository);

        log.info("Elasticsearch: Successfully performed reindexing");
    }

    @Transactional
    @SuppressWarnings("unchecked")
    private <T> void reindexForClass(Class<T> entityClass, JpaRepository<T, Long> jpaRepository,
                                                          ElasticsearchRepository<T, Long> elasticsearchRepository) {
        elasticsearchTemplate.deleteIndex(entityClass);
        try {
            elasticsearchTemplate.createIndex(entityClass);
        } catch (IndexAlreadyExistsException e) {
            // Do nothing. Index was already concurrently recreated by some other service.
        }
        elasticsearchTemplate.putMapping(entityClass);
        if (jpaRepository.count() > 0) {
            try {
                Method m = jpaRepository.getClass().getMethod("findAllWithEagerRelationships");
                elasticsearchRepository.saveAll((List<T>) m.invoke(jpaRepository));
            } catch (Exception e) {
                elasticsearchRepository.saveAll(jpaRepository.findAll());
            }
        }
        log.info("Elasticsearch: Indexed all rows for " + entityClass.getSimpleName());
    }

    @Transactional
    @SuppressWarnings("unchecked")
    private <T> void reindexInstances(InstanceRepository instanceRepository,
                                      InstanceSearchRepository instanceSearchRepository,
                                      ServiceInstanceResource serviceInstanceResource) {
        //Don't delete the instance index because this index needs to be manually created
        //because spring-data can't handle geometry index creation
        //elasticsearchTemplate.deleteIndex(entityClass);
        try {
            elasticsearchTemplate.createIndex(Instance.class);
        } catch (IndexAlreadyExistsException e) {
            // Do nothing. Index was already concurrently recreated by some other service.
        }
        elasticsearchTemplate.putMapping(Instance.class);
        if (instanceRepository.count() > 0) {
            List<Instance> instanceList = null;
            try {
                instanceList = instanceRepository.findAllWithEagerRelationships();
            } catch (Exception e) {
                instanceList = instanceRepository.findAll();
            }
            for (Instance i : instanceList) {
                try {
                    String xml = i.getInstanceAsXml().getContent().toString();
                    XmlUtil.validateXml(xml, "ServiceInstanceSchema.xsd");
                    i = InstanceUtil.parseInstanceAttributesFromXML(i);
                    Instance result = instanceRepository.save(i);
                    result = InstanceUtil.parseInstanceGeometryFromXML(result);
                    instanceSearchRepository.save(result);
                } catch (Exception e) {
                    log.error("Error parsing XML of instance: " + i.getId(), e);
                }
            }
        }
        log.info("Elasticsearch: Indexed all rows for " + Instance.class.getSimpleName());
    }
}
