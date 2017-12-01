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

package com.frequentis.maritime.mcsr.repository;

import com.frequentis.maritime.mcsr.domain.Instance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Instance entity.
 */
@SuppressWarnings("unused")
public interface InstanceRepository extends JpaRepository<Instance,Long> {

    @Query("select distinct instance from Instance instance left join fetch instance.designs left join fetch instance.docs")
    List<Instance> findAllWithEagerRelationships();

    @Query("select instance from Instance instance left join fetch instance.designs left join fetch instance.docs where instance.id =:id")
    Instance findOneWithEagerRelationships(@Param("id") Long id);


    @Query("select distinct instance from Instance instance left join fetch instance.designs left join fetch instance.docs where instance.instanceId = :id and instance.status != 'simulated' and instance.compliant = true")
    List<Instance> findByDomainIdEagerRelationships(@Param("id") String id);

    @Query("select distinct instance from Instance instance left join fetch instance.designs left join fetch instance.docs where instance.instanceId = :id and instance.status = 'simulated' and instance.compliant = true")
    List<Instance> findSimulatedByDomainIdEagerRelationships(@Param("id") String id);


    @Query("select distinct instance from Instance instance left join fetch instance.designs left join fetch instance.docs where instance.instanceId = :id and instance.status != 'simulated'")
    List<Instance> findByDomainIdEagerRelationshipsWithNonCompliant(@Param("id") String id);

    @Query("select distinct instance from Instance instance left join fetch instance.designs left join fetch instance.docs where instance.instanceId = :id and instance.status = 'simulated'")
    List<Instance> findSimulatedByDomainIdEagerRelationshipsWithNonCompliant(@Param("id") String id);


    @Query("select distinct instance from Instance instance left join fetch instance.designs left join fetch instance.docs where instance.instanceId = :id and instance.version = :version and instance.compliant = true and instance.status != 'simulated'")
    List<Instance> findByDomainIdAndVersionEagerRelationships(@Param("id") String id, @Param("version") String version);

    @Query("select distinct instance from Instance instance left join fetch instance.designs left join fetch instance.docs where instance.instanceId = :id and instance.version = :version and instance.status != 'simulated'")
    List<Instance> findByDomainIdAndVersionEagerRelationshipsWithNonCompliant(@Param("id") String id, @Param("version") String version);

    @Query("select distinct instance from Instance instance left join fetch instance.designs left join fetch instance.docs where instance.instanceId = :id and instance.version = :version and instance.compliant = true and instance.status = 'simulated'")
    List<Instance> findSimulatedByDomainIdAndVersionEagerRelationships(@Param("id") String id, @Param("version") String version);

    @Query("select distinct instance from Instance instance left join fetch instance.designs left join fetch instance.docs where instance.instanceId = :id and instance.version = :version and instance.status = 'simulated'")
    List<Instance> findSimulatedByDomainIdAndVersionEagerRelationshipsWithNonCompliant(@Param("id") String id, @Param("version") String version);


    @Query("select distinct instance from Instance instance where instance.instanceId = :id")
    List<Instance> findByDomainId(@Param("id") String id);

    @Query("select distinct instance from Instance instance where instance.instanceId = :id and instance.version = :version")
    List<Instance> findByDomainIdAndVersion(@Param("id") String id, @Param("version") String version);

}
