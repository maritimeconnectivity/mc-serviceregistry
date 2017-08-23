/*
 * MaritimeCloud Service Registry
 * Copyright (c) 2017 Frequentis AG
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

package com.frequentis.maritime.mcsr.web.soap.converters.specification;

import com.frequentis.maritime.mcsr.domain.SpecificationTemplateSet;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateSetDescriptorDTO;

import org.springframework.stereotype.Component;

@Component
public class SpecificationTemplateSetDescriptorConverter extends AbstractConverter<SpecificationTemplateSet, SpecificationTemplateSetDescriptorDTO>{

	@Override
	public SpecificationTemplateSetDescriptorDTO convert(SpecificationTemplateSet f) {
		if(f == null) {
			return null;
		}
		SpecificationTemplateSetDescriptorDTO t = new SpecificationTemplateSetDescriptorDTO();
		mapGeterWithSameName(f, t);

		return t;
	}

}
