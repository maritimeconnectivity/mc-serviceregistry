package com.frequentis.maritime.mcsr.web.soap.converters;

import javax.inject.Inject;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.web.soap.dto.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.XmlDTO;

import org.springframework.stereotype.Component;

@Component
public class DesignDTOToDesignConverter extends AbstractConverter<DesignDTO, Design> {
	
	@Inject
	private Converter<DocDTO, Doc> docConverter;
	
	@Inject
	private Converter<XmlDTO, Xml> xmlConverter;

	@Override
	public Design convert(DesignDTO f) {
		if(f == null) {
			return null;
		}
		Design d = new Design();
		d.setId(f.id);
		d.setName(f.name);
		d.setComment(f.comment);
		d.setDesignAsDoc(docConverter.convert(f.designAsDoc));
		d.setDesignAsXml(xmlConverter.convert(f.designAsXml));
		d.setDesignId(f.designId);
		d.setStatus(f.status);
		d.setVersion(f.version);
		return d;
	}

}
