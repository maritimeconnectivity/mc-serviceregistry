package com.frequentis.maritime.mcsr.web.soap.dto.specification;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.frequentis.maritime.mcsr.domain.enumeration.SpecificationTemplateType;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocReference;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XsdReference;

@XmlRootElement(name = "SpecificationTemplateParameter")
public class SpecificationTemplateParameterDTO {
    public Long id;
    @XmlElement(required = true)
    public String name;
    @XmlElement(required = true)
    public String version;
    @XmlElement(required = true)
    public SpecificationTemplateType type;
    @XmlElement(required = true)
    public String comment;
    @XmlElement(required = true)
    public DocReference guidelineDoc;
    @XmlElement(required = true)
    public DocReference templateDoc;
    @XmlElement(required = true)
    public List<DocReference> docs;
    @XmlElement(required = true)
    public List<XsdReference> xsds;
}
