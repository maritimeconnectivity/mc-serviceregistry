package com.frequentis.maritime.mcsr.web.soap.converters.doc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractBidirectionalConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.Converter;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDescriptorDTO;

@Component
public class DocDTOConverter extends AbstractBidirectionalConverter<Doc, DocDTO>  {
    @Autowired
    DocDTOConverter descriptorDTO;


    @Override
    public DocDTO convert(Doc from) {
    	if(from == null) {
    		return null;
    	}
    	DocDTO ddto = new DocDTO();
    	mapGeterWithSameName(from, ddto);
        ddto.filecontentContentType = from.getFilecontentContentType();
        ddto.filecontent = from.getFilecontent();

        return ddto;
    }


	@Override
	public Doc convertReverse(DocDTO f) {
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
