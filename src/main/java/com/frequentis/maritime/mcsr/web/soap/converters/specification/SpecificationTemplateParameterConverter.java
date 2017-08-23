package com.frequentis.maritime.mcsr.web.soap.converters.specification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.domain.SpecificationTemplate;
import com.frequentis.maritime.mcsr.domain.Xsd;
import com.frequentis.maritime.mcsr.service.DocService;
import com.frequentis.maritime.mcsr.service.XsdService;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocReference;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateParameterDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XsdReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecificationTemplateParameterConverter extends AbstractConverter<SpecificationTemplateParameterDTO, SpecificationTemplate> {

	@Autowired
	DocService docService;

	@Autowired
	XsdService xsdService;

	@Override
	public SpecificationTemplate convert(SpecificationTemplateParameterDTO f) {
		if(f == null) {
			return null;
		}
		SpecificationTemplate r = new SpecificationTemplate();
		mapSetterWithSameName(f, r);
		// Documents
		Set<Doc> documents = new HashSet<Doc>();
		if(f.docs != null) {
			for(DocReference docRef : f.docs) {
				documents.add(docService.findOne(docRef.id));
			}
		}
		r.setDocs(documents);

		if(f.guidelineDoc != null) {
			Doc s = docService.findOne(f.guidelineDoc.id);
			r.setGuidelineDoc(s);
		}

		if(f.templateDoc != null) {
			Doc s = docService.findOne(f.templateDoc.id);
			r.setTemplateDoc(s);
		}

		Set<Xsd> xsds = new HashSet<Xsd>();
		if(f.xsds != null) {
			for(XsdReference xsdRef : f.xsds) {
				xsds.add(xsdService.findOne(xsdRef.id));
			}
		}
		r.setXsds(xsds);


		return r;
	}

}
