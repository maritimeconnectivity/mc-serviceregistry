package com.frequentis.maritime.mcsr.web.soap.converters.specification;

import com.frequentis.maritime.mcsr.domain.SpecificationTemplate;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateDescriptorDTO;

import org.springframework.stereotype.Component;

@Component
public class SpecificationTemplateDescriptorConverter extends AbstractConverter<SpecificationTemplate, SpecificationTemplateDescriptorDTO> {

	@Override
	public SpecificationTemplateDescriptorDTO convert(SpecificationTemplate f) {
		if(f == null) {
			return null;
		}
		SpecificationTemplateDescriptorDTO t = new SpecificationTemplateDescriptorDTO();
		mapGeterWithSameName(f, t);
		
		return t;
	}

}
