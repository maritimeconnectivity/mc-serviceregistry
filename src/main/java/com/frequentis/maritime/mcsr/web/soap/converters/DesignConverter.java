package com.frequentis.maritime.mcsr.web.soap.converters;

import org.springframework.stereotype.Component;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.web.soap.dto.DesignDTO;

@Component
public class DesignConverter extends AbstractConverter<Design, DesignDTO> {


    @Override
    public DesignDTO convert(Design from) {
        if(from == null) {
            return null;
        }
        DesignDTO ddto = new DesignDTO();
        ddto.name = from.getName();

        return ddto;
    }




}
