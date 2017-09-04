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
import com.frequentis.maritime.mcsr.web.soap.converters.doc.DocDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateSetDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecificationTemplateSetConverter extends AbstractConverter<SpecificationTemplateSet, SpecificationTemplateSetDTO>{

	@Autowired
	DocDTOConverter docConverter;

	@Autowired
	SpecificationTemplateDTOConverter templatesConverter;

	@Override
	public SpecificationTemplateSetDTO convert(SpecificationTemplateSet f) {
		if(f == null) {
			return null;
		}
		SpecificationTemplateSetDTO t = new SpecificationTemplateSetDTO();
		mapGeterWithSameName(f, t);
		t.docs = castToSet(docConverter.convert(f.getDocs()));
		t.templates = castToSet(templatesConverter.convert(f.getTemplates()));

		return t;
	}

}
