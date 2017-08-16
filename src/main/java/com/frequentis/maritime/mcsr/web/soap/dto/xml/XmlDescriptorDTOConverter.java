package com.frequentis.maritime.mcsr.web.soap.dto.xml;

import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractConverter;

import org.springframework.stereotype.Component;

@Component
public class XmlDescriptorDTOConverter extends AbstractConverter<Xml, XmlDescriptorDTO> {

	@Override
	public XmlDescriptorDTO convert(Xml f) {
		if(f == null) {
			return null;
		}
		XmlDescriptorDTO r = new XmlDescriptorDTO();
		mapGeterWithSameName(f, r);
		
		return r;
	}

}
