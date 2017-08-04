package com.frequentis.maritime.mcsr.web.soap.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Doc")
public class DocDTO extends DocDescriptorDTO {
    @XmlElement(required = true)
    public byte[] filecontent;
    @XmlElement(required = true)
    public String filecontentContentType;


}
