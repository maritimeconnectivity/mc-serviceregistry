package com.frequentis.maritime.mcsr.web.soap.converters;

import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.web.soap.dto.XmlDTO;

import org.springframework.stereotype.Component;

@Component
public class XMLConverter extends AbstractConverter<Xml, XmlDTO>{

	@Override
	public XmlDTO convert(Xml f) {
		if(f == null) {
			return null;
		}
		XmlDTO r = new XmlDTO();
		mapGeterWithSameName(f, r);
		
		return r;
	}

}
