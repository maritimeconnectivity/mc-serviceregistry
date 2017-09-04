package com.frequentis.maritime.mcsr.web.soap.dto.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Xml")
public class XmlDescriptorDTO extends XmlReference {
	@XmlElement(required = true)
	public String name;
	public String comment;
}
