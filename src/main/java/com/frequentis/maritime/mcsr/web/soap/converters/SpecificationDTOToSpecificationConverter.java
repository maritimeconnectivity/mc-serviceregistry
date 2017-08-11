package com.frequentis.maritime.mcsr.web.soap.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.web.soap.dto.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.SpecificationDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.XmlDTO;

@Component
public class SpecificationDTOToSpecificationConverter extends AbstractConverter<SpecificationDTO, Specification> {

	@Autowired
	Converter<DocDTO, Doc> docConverter;
	
	@Autowired
	Converter<XmlDTO, Xml> xmlConverter;

    @Override
    public Specification convert(SpecificationDTO f) {
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
        t.setSpecAsDoc(docConverter.convert(f.specAsDoc));
        t.setSpecAsXml(xmlConverter.convert(f.specAsXml));

        return t;
    }




}
