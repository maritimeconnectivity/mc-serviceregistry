package com.frequentis.maritime.mcsr.web.soap.converters.design;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.repository.DesignRepository;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.design.DesignReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DesignReferenceConverter extends AbstractConverter<DesignReference, Design>{

	@Autowired
	DesignRepository designRepo;

	@Override
	public Design convert(DesignReference from) {
		if(from == null) {
			return null;
		}
		return designRepo.getOne(from.id);
	}

}
