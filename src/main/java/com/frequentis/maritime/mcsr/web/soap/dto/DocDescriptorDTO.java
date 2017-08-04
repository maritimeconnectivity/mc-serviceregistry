package com.frequentis.maritime.mcsr.web.soap.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DocDescriptor")
public class DocDescriptorDTO {
    @XmlElement(required = false)
    public Long id;
    @XmlElement(required = true)
    public String name;
    @XmlElement(required = true)
    public String comment;
    @XmlElement(required = true)
    public String mimetype;

}
