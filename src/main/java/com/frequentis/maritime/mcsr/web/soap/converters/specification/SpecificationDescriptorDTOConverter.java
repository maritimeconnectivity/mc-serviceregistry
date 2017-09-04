package com.frequentis.maritime.mcsr.web.soap.converters.specification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.Converter;
import com.frequentis.maritime.mcsr.web.soap.converters.doc.DocDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.design.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationDescriptorDTO;

@Component
public class SpecificationDescriptorDTOConverter extends AbstractConverter<Specification, SpecificationDescriptorDTO> {
    @Autowired
    DocDTOConverter docDesignConverter;


    @Override
    public SpecificationDescriptorDTO convert(Specification from) {
        if(from == null) {
            return null;
        }
        SpecificationDescriptorDTO t = new SpecificationDescriptorDTO();
        mapGeterWithSameName(from, t);

        return t;
    }




}
