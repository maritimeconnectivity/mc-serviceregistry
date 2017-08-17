package com.frequentis.maritime.mcsr.web.soap.converters.instance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.domain.Instance;
import com.frequentis.maritime.mcsr.domain.SpecificationTemplate;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.Converter;
import com.frequentis.maritime.mcsr.web.soap.converters.design.DesignConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.design.DesignReferenceConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.doc.DocDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.doc.DocReferenceConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.specification.SpecificationTemplateDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.specification.SpecificationTemplateReferenceConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.xml.XmlDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.xml.XmlReferenceConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.design.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.instance.InstanceDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.instance.InstanceParameterDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlDTO;

@Component
public class InstanceParameterDTOToInstanceConverter extends AbstractConverter<InstanceParameterDTO, Instance> {

	@Autowired
	XmlReferenceConverter xmlConverter;
	
	@Autowired
	DocReferenceConverter docConverter;
	
	@Autowired
	DesignReferenceConverter designConverter;
	
	@Autowired
	SpecificationTemplateReferenceConverter templateConverter;

    @Override
    public Instance convert(InstanceParameterDTO f) {
        if(f == null) {
            return null;
        }
        Instance d = new Instance();
        d.setId(f.id);
        d.setUnlocode(f.unlocode);
        d.setInstanceAsXml(xmlConverter.convert(f.instanceAsXml));
        d.setInstanceAsDoc(docConverter.convert(f.instanceAsDoc));
        d.setImplementedSpecificationVersion(templateConverter.convert(f.implementedSpecificationVersion));
        d.setDocs(castToSet(docConverter.convert(f.docs)));
        d.setDesigns(castToSet(designConverter.convert(f.designs)));

        return d;
    }




}
