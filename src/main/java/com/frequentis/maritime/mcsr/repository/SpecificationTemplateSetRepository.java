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

import com.frequentis.maritime.mcsr.domain.SpecificationTemplateSet;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the SpecificationTemplateSet entity.
 */
@SuppressWarnings("unused")
public interface SpecificationTemplateSetRepository extends JpaRepository<SpecificationTemplateSet,Long> {

    @Query("select distinct specificationTemplateSet from SpecificationTemplateSet specificationTemplateSet left join fetch specificationTemplateSet.templates left join fetch specificationTemplateSet.docs")
    List<SpecificationTemplateSet> findAllWithEagerRelationships();

    @Query("select specificationTemplateSet from SpecificationTemplateSet specificationTemplateSet left join fetch specificationTemplateSet.templates left join fetch specificationTemplateSet.docs where specificationTemplateSet.id =:id")
    SpecificationTemplateSet findOneWithEagerRelationships(@Param("id") Long id);

}
