package com.frequentis.maritime.mcsr.web.soap.converters.instance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Instance;
import com.frequentis.maritime.mcsr.domain.SpecificationTemplate;
import com.frequentis.maritime.mcsr.service.DesignService;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.Converter;
import com.frequentis.maritime.mcsr.web.soap.converters.design.DesignConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.doc.DocDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.xml.XmlDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.instance.InstanceDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateDTO;

@Component
public class InstanceDTOConverter extends AbstractConverter<Instance, InstanceDTO> {
    private final Logger log = LoggerFactory.getLogger(InstanceDTOConverter.class);

    @Autowired
    DesignService designService;

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
        // Should not be in response
        //d.designs = castToList(docDesignConverter.convert(f.getDesigns()));
        Design cd = null;
        if(f.getDesigns() != null) {
            for(Design dd : f.getDesigns()) {
                if(dd.getDesignId().equals(f.getDesignId())) {
                    cd = dd;
                }
            }
        }

        d.design = docDesignConverter.convert(cd);
        d.instanceAsDoc = docConverter.convert(f.getInstanceAsDoc());
        d.instanceAsXml = xmlConverter.convert(f.getInstanceAsXml());
        d.compliant = f.isCompliant();


        if(f.getGeometry() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = null;
            try {
                json = objectMapper.writeValueAsString(f.getGeometry());
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
            d.geometry = json;
        }
        d.implementedSpecificationVersion = templateConverter.convert(f.getImplementedSpecificationVersion());

        return d;
    }




}
