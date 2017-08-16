package com.frequentis.maritime.mcsr.web.soap.dto.specification;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.frequentis.maritime.mcsr.domain.enumeration.SpecificationTemplateType;

@XmlRootElement(name = "SpecificationTemplate")
public class SpecificationTemplateDescriptorDTO {
    public Long id;
    @XmlElement(required = true)
    public String name;
    @XmlElement(required = true)
    public String version;
    @XmlElement(required = true)
    public SpecificationTemplateType type;
    @XmlElement(required = true)
    public String comment;


}
