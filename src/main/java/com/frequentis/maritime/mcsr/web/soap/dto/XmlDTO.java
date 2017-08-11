package com.frequentis.maritime.mcsr.web.soap.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Xml")
public class XmlDTO {
	public Long id;
	@XmlElement(required = true)
	public String name;
	public String comment;
	@XmlElement(required = true)
	public String content;
	@XmlElement(required = true)
	public String contentContentType;
}
