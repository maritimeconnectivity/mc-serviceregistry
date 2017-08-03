package com.frequentis.maritime.mcsr.web.soap.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.web.soap.dto.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.SpecificationDTO;

@Component
public class SpecificationDTOConverter extends AbstractConverter<Specification, SpecificationDTO> {
    @Autowired
    Converter<Design, DesignDTO> docDesignConverter;


    @Override
    public SpecificationDTO convert(Specification from) {
        if(from == null) {
            return null;
        }
        SpecificationDTO t = new SpecificationDTO();
        t.comment = from.getComment();
        t.id = from.getId();
        t.keywords = from.getKeywords();
        t.organizationId = from.getOrganizationId();
        t.status = from.getStatus();
        t.version = from.getVersion();
        t.specificationId = from.getSpecificationId();

        return t;
    }




}
