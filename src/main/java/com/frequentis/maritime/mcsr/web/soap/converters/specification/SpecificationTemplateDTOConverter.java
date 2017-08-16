package com.frequentis.maritime.mcsr.web.soap.converters.specification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.domain.SpecificationTemplate;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.domain.Xsd;
import com.frequentis.maritime.mcsr.service.DocService;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractBidirectionalConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.doc.DocDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.xml.XsdDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.design.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XsdDTO;

@Component
public class SpecificationTemplateDTOConverter extends AbstractBidirectionalConverter<SpecificationTemplate, SpecificationTemplateDTO> {

	@Autowired
	DocDTOConverter docConverter;
	@Autowired
	XsdDTOConverter xsdConverter;

	@Autowired
	DocService docService;

    @Override
    public SpecificationTemplateDTO convert(SpecificationTemplate f) {
        if(f == null) {
            return null;
        }
        SpecificationTemplateDTO t = new SpecificationTemplateDTO();
        mapGeterWithSameName(f, t);
        t.docs = castToSet(docConverter.convert(f.getDocs()));
        t.templateDoc = docConverter.convert(f.getTemplateDoc());
        t.xsds = castToSet(xsdConverter.convert(f.getXsds()));
        t.guidelineDoc = docConverter.convert(f.getGuidelineDoc());
        return t;
    }

    @Override
    public SpecificationTemplate convertReverse(SpecificationTemplateDTO f) {
        if(f == null) {
            return null;
        }
        SpecificationTemplate d = new SpecificationTemplate();
        d.setName(f.name);
        d.setComment(f.comment);
        d.setId(f.id);
        d.setType(f.type);
        d.setGuidelineDoc(docConverter.convertReverse(f.guidelineDoc));
        d.setDocs(castToSet(docConverter.convertReverse(f.docs)));
        d.setVersion(f.version);
        d.setXsds(castToSet(xsdConverter.convertReverse(f.xsds)));
        return d;
    }

}
