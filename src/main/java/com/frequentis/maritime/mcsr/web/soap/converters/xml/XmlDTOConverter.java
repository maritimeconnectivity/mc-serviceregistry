package com.frequentis.maritime.mcsr.web.soap.converters.xml;

import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractBidirectionalConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlDTO;

import org.springframework.stereotype.Component;

@Component
public class XmlDTOConverter extends AbstractBidirectionalConverter<Xml, XmlDTO>{

	@Override
	public XmlDTO convert(Xml f) {
		if(f == null) {
			return null;
		}
		XmlDTO r = new XmlDTO();
		mapGeterWithSameName(f, r);
		
		return r;
	}

	@Override
	public Xml convertReverse(XmlDTO f) {
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
