package com.frequentis.maritime.mcsr.web.soap.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.web.soap.dto.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.DocDescriptorDTO;

@Component
public class DocDTOConverter extends AbstractConverter<Doc, DocDTO>  {
    @Autowired
    Converter<Doc, DocDescriptorDTO> descriptorDTO;


    @Override
    public DocDTO convert(Doc from) {
    	if(from == null) {
    		return null;
    	}
    	DocDTO ddto = (DocDTO) descriptorDTO.convert(from);
        ddto.filecontentContentType = from.getFilecontentContentType();
        ddto.filecontent = from.getFilecontent();

        return ddto;
    }




}
