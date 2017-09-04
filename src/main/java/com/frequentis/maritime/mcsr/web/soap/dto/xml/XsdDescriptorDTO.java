package com.frequentis.maritime.mcsr.web.soap.dto.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Xsd")
public class XsdDescriptorDTO extends XsdReference {
    @XmlElement(required = true)
    public String name;
    @XmlElement(required = true)
    public String comment;
}
