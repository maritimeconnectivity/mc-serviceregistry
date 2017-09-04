package com.frequentis.maritime.mcsr.web.soap.dto.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Xsd")
public class XsdReference {
	@XmlElement(required = true)
    public Long id;
}
