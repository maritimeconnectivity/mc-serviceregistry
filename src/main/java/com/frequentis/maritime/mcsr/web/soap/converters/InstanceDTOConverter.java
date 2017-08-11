package com.frequentis.maritime.mcsr.web.soap.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Instance;
import com.frequentis.maritime.mcsr.web.soap.dto.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.InstanceDTO;

@Component
public class InstanceDTOConverter extends AbstractConverter<Instance, InstanceDTO> {
    @Autowired
    Converter<Design, DesignDTO> docDesignConverter;


    @Override
    public InstanceDTO convert(Instance f) {
        if(f == null) {
            return null;
        }
        InstanceDTO d = new InstanceDTO();
        mapGeterWithSameName(f, d);

        return d;
    }




}
