package com.frequentis.maritime.mcsr.web.soap.converters.specification;

import com.frequentis.maritime.mcsr.domain.SpecificationTemplate;
import com.frequentis.maritime.mcsr.repository.SpecificationTemplateRepository;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecificationTemplateReferenceConverter extends AbstractConverter<SpecificationTemplateReference, SpecificationTemplate> {

	@Autowired
	SpecificationTemplateRepository specTempRepo;

	@Override
	public SpecificationTemplate convert(SpecificationTemplateReference from) {
		if(from == null) {
			return null;
		}
		return specTempRepo.getOne(from.id);
	}

}
