package com.frequentis.maritime.mcsr.web.soap.converters;

import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.web.soap.dto.XmlDTO;

import org.springframework.stereotype.Component;

@Component
public class XmlDTOToXmlConverter extends AbstractConverter<XmlDTO, Xml> {

	@Override
	public Xml convert(XmlDTO f) {
		if(f == null) {
			return null;
		}
		Xml d = new Xml();
		d.setComment(f.comment);
		d.setId(f.id);
		d.setContent(f.content);
		d.setContentContentType(f.contentContentType);
		d.setName(f.name);
		
		return d;
	}

}
