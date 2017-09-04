package com.frequentis.maritime.mcsr.web.soap.converters.xml;

import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.repository.XmlRepository;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class XmlReferenceConverter extends AbstractConverter<XmlReference, Xml> {

	@Autowired
	XmlRepository xmlRepo;

	@Override
	public Xml convert(XmlReference from) {
		if(from == null) {
			return null;
		}
		return xmlRepo.getOne(from.id);
	}

}
