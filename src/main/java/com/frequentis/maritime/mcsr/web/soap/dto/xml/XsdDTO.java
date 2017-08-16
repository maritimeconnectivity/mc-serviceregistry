package com.frequentis.maritime.mcsr.web.soap.dto.xml;

import javax.xml.bind.annotation.XmlElement;

public class XsdDTO extends XsdDescriptorDTO {
    @XmlElement(required = true)
    public byte[] content;
    @XmlElement(required = true)
    public String contentContentType;
}
