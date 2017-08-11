package com.frequentis.maritime.mcsr.web.soap.converters;

import org.springframework.stereotype.Component;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.web.soap.dto.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.DesignDescriptorDTO;

@Component
public class DesignDescriptorConverter extends AbstractConverter<Design, DesignDescriptorDTO> {

    @Override
    public DesignDescriptorDTO convert(Design from) {
        if(from == null) {
            return null;
        }
        DesignDescriptorDTO ddto = new DesignDescriptorDTO();
        mapGeterWithSameName(from, ddto);

        return ddto;
    }




}
