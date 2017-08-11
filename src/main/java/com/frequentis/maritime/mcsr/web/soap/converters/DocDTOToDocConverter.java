package com.frequentis.maritime.mcsr.web.soap.converters;

import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.web.soap.dto.DocDTO;

import org.springframework.stereotype.Component;

@Component
public class DocDTOToDocConverter extends AbstractConverter<DocDTO, Doc> {

	@Override
	public Doc convert(DocDTO f) {
		if(f == null) {
			return null;
		}
		Doc d = new Doc();
		d.setId(f.id);
		d.setName(f.name);
		d.setComment(f.comment);
		d.setFilecontent(f.filecontent);
		d.setFilecontentContentType(f.filecontentContentType);
		d.setMimetype(f.mimetype);
		
		return d;
	}

}
