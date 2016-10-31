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

import com.frequentis.maritime.mcsr.domain.Specification;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Specification entity.
 */
@SuppressWarnings("unused")
public interface SpecificationRepository extends JpaRepository<Specification,Long> {

    @Query("select distinct specification from Specification specification left join fetch specification.docs")
    List<Specification> findAllWithEagerRelationships();

    @Query("select specification from Specification specification left join fetch specification.docs where specification.id =:id")
    Specification findOneWithEagerRelationships(@Param("id") Long id);

    @Query("select distinct specification from Specification specification left join fetch specification.docs where specification.specificationId = :id")
    List<Specification> findByDomainId(@Param("id") String id);

    @Query("select distinct specification from Specification specification left join fetch specification.docs where specification.specificationId = :id and specification.version = :version")
    List<Specification> findByDomainIdAndVersion(@Param("id") String id, @Param("version") String version);

}
