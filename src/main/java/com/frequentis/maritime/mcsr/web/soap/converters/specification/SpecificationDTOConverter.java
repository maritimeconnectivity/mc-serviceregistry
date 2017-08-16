package com.frequentis.maritime.mcsr.web.soap.converters.specification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.domain.SpecificationTemplate;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractBidirectionalConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.Converter;
import com.frequentis.maritime.mcsr.web.soap.converters.doc.DocDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.xml.XmlDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.design.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlDTO;

@Component
public class SpecificationDTOConverter extends AbstractBidirectionalConverter<Specification, SpecificationDTO> {
    @Autowired
    DocDTOConverter docConverter;
    @Autowired
    XmlDTOConverter xmlConverter;
    @Autowired
    Converter<SpecificationTemplate, SpecificationTemplateDTO> templateConverter;


    @Override
    public SpecificationDTO convert(Specification from) {
        if(from == null) {
            return null;
        }
        SpecificationDTO t = new SpecificationDTO();
        mapGeterWithSameName(from, t);
        t.specAsDoc = docConverter.convert(from.getSpecAsDoc());
        t.specAsXml = xmlConverter.convert(from.getSpecAsXml());
        t.implementedSpecificationVersion = templateConverter.convert(from.getImplementedSpecificationVersion());


        return t;
    }
    
    public Specification convertReverse(SpecificationDTO f) {
        if(f == null) {
            return null;
        }
        Specification t = new Specification();
        t.setId(f.id);
        t.setComment(f.comment);
        t.setKeywords(f.keywords);
        t.setOrganizationId(f.organizationId);
        t.setStatus(f.status);
        t.setVersion(f.version);
        t.setName(f.name);
        t.setSpecificationId(f.specificationId);
        t.setSpecAsDoc(docConverter.convertReverse(f.specAsDoc));
        t.setSpecAsXml(xmlConverter.convertReverse(f.specAsXml));

        return t;
    }

}
