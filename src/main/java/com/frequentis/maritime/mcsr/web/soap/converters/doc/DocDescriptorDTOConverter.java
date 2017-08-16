package com.frequentis.maritime.mcsr.web.soap.converters.doc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.Converter;
import com.frequentis.maritime.mcsr.web.soap.converters.design.DesignConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.design.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDescriptorDTO;

@Component
public class DocDescriptorDTOConverter extends AbstractConverter<Doc, DocDescriptorDTO> {
    @Autowired
    DesignConverter docDesignConverter;


    @Override
    public DocDescriptorDTO convert(Doc from) {
        if(from == null) {
            return null;
        }
        DocDescriptorDTO ddto = new DocDTO();
        ddto.id = from.getId();
        ddto.name = from.getName();
        ddto.mimetype = from.getMimetype();
        ddto.comment = from.getComment();

        return ddto;
    }




}
