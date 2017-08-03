package com.frequentis.maritime.mcsr.web.soap.converters;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.web.soap.dto.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.DocDTO;

@Component
public class DocDTOConverter extends AbstractConverter<Doc, DocDTO> {
    @Autowired
    Converter<Design, DesignDTO> docDesignConverter;


    @Override
    public DocDTO convert(Doc from) {
        if(from == null) {
            return null;
        }
        DocDTO ddto = new DocDTO();
        ddto.id = from.getId();
        ddto.name = from.getName();
        ddto.filecontentContentType = from.getFilecontentContentType();
        ddto.filecontent = from.getFilecontent();
        ddto.mimetype = from.getMimetype();
        ddto.comment = from.getComment();

        return ddto;
    }




}
