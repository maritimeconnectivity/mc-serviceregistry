package com.frequentis.maritime.mcsr.web.soap.converters;

import org.springframework.stereotype.Component;

import com.frequentis.maritime.mcsr.domain.Instance;
import com.frequentis.maritime.mcsr.web.soap.dto.InstanceDTO;

@Component
public class InstanceDTOToInstanceConverter extends AbstractConverter<InstanceDTO, Instance> {



    @Override
    public Instance convert(InstanceDTO from) {
        if(from == null) {
            return null;
        }
        Instance ddto = new Instance();

        return ddto;
    }




}
