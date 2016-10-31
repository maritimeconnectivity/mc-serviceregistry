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

import com.frequentis.maritime.mcsr.domain.SpecificationTemplate;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the SpecificationTemplate entity.
 */
@SuppressWarnings("unused")
public interface SpecificationTemplateRepository extends JpaRepository<SpecificationTemplate,Long> {

    @Query("select distinct specificationTemplate from SpecificationTemplate specificationTemplate left join fetch specificationTemplate.docs left join fetch specificationTemplate.xsds")
    List<SpecificationTemplate> findAllWithEagerRelationships();

    @Query("select specificationTemplate from SpecificationTemplate specificationTemplate left join fetch specificationTemplate.docs left join fetch specificationTemplate.xsds where specificationTemplate.id =:id")
    SpecificationTemplate findOneWithEagerRelationships(@Param("id") Long id);

}
