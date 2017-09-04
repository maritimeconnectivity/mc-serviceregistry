package com.frequentis.maritime.mcsr.web.soap.dto.specification;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XsdDTO;

@XmlRootElement(name = "SpecificationTemplate")
public class SpecificationTemplateDTO extends SpecificationTemplateDescriptorDTO {
	@XmlElement(required = true)
    public DocDTO guidelineDoc;
	@XmlElement(required = true)
	public DocDTO templateDoc;
	@XmlElement(required = true)
	public Set<DocDTO> docs = new HashSet<>();
	@XmlElement(required = true)
	public Set<XsdDTO> xsds = new HashSet<>();
}
