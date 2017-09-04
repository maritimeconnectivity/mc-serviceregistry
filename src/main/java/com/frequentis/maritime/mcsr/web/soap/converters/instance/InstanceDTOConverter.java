package com.frequentis.maritime.mcsr.web.soap.converters.instance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.frequentis.maritime.mcsr.domain.Instance;
import com.frequentis.maritime.mcsr.domain.SpecificationTemplate;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.Converter;
import com.frequentis.maritime.mcsr.web.soap.converters.design.DesignConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.doc.DocDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.xml.XmlDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.instance.InstanceDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateDTO;

@Component
public class InstanceDTOConverter extends AbstractConverter<Instance, InstanceDTO> {
    @Autowired
    DesignConverter docDesignConverter;
    @Autowired
    DocDTOConverter docConverter;
    @Autowired
    XmlDTOConverter xmlConverter;
    @Autowired
    Converter<SpecificationTemplate, SpecificationTemplateDTO> templateConverter;


    @Override
    public InstanceDTO convert(Instance f) {
        if(f == null) {
            return null;
        }
        InstanceDTO d = new InstanceDTO();
        mapGeterWithSameName(f, d);
        d.docs = castToList(docConverter.convert(f.getDocs()));
        d.designs = castToList(docDesignConverter.convert(f.getDesigns()));
        d.instanceAsDoc = docConverter.convert(f.getInstanceAsDoc());
        d.instanceAsXml = xmlConverter.convert(f.getInstanceAsXml());
        d.geometry = f.getGeometry() != null ? f.getGeometry().asText() : null;
        d.implementedSpecificationVersion = templateConverter.convert(f.getImplementedSpecificationVersion());

        return d;
    }




}
