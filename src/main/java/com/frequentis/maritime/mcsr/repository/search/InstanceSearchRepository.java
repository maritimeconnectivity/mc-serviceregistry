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

package com.frequentis.maritime.mcsr.repository.search;

import com.frequentis.maritime.mcsr.domain.Instance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Instance entity.
 */
public interface InstanceSearchRepository extends ElasticsearchRepository<Instance, Long> {

    Instance findOneByInstanceIdAndVersion(String id, String version);

    Page<Instance> findByKeywords(String keywords, Pageable pageable);
    Page<Instance> findByKeywordsAndCompliantTrue(String keywords, Pageable pageable);
    Page<Instance> findByKeywordsAndCompliantTrueAndStatus(String keywords, String status, Pageable pageable);
    Page<Instance> findByKeywordsAndCompliantTrueAndStatusNot(String keywords, String status, Pageable pageable);
    Page<Instance> findByKeywordsAndStatus(String keywords, String status, Pageable pageable);
    Page<Instance> findByKeywordsAndStatusNot(String keywords, String status, Pageable pageable);

    Page<Instance> findByUnlocode(String unlocode, Pageable pageable);
    Page<Instance> findByUnlocodeAndCompliantTrue(String unlocode, Pageable pageable);
    Page<Instance> findByUnlocodeAndCompliantTrueAndStatus(String unlocode, String status, Pageable pageable);
    Page<Instance> findByUnlocodeAndCompliantTrueAndStatusNot(String unlocode, String status, Pageable pageable);
    Page<Instance> findByUnlocodeAndStatus(String unlocode, String status, Pageable pageable);
    Page<Instance> findByUnlocodeAndStatusNot(String unlocode, String status, Pageable pageable);

}
