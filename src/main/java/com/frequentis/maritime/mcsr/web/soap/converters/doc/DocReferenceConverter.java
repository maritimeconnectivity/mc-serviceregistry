package com.frequentis.maritime.mcsr.web.soap.converters.doc;

import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.repository.DocRepository;
import com.frequentis.maritime.mcsr.web.soap.converters.AbstractConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DocReferenceConverter extends AbstractConverter<DocReference, Doc> {

	@Autowired
	DocRepository docRepo;

	@Override
	public Doc convert(DocReference from) {
		if(from == null) {
			return null;
		}
		return docRepo.getOne(from.id);
	}

}
