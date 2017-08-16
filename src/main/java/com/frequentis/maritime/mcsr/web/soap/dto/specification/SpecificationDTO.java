package com.frequentis.maritime.mcsr.web.soap.dto.specification;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlDTO;

@XmlRootElement(name = "Specification")
public class SpecificationDTO extends SpecificationDescriptorDTO {
    @XmlElement(required = true)
    public XmlDTO specAsXml;
    @XmlElement(required = false)
    public DocDTO specAsDoc;
    @XmlElement(required = false)
    public DocDTO docs;
    @XmlElement(required = true)
    public SpecificationTemplateDTO implementedSpecificationVersion;
    
}
