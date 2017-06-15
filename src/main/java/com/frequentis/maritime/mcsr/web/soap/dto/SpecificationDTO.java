package com.frequentis.maritime.mcsr.web.soap.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Specification")
public class SpecificationDTO {
	public Long id;
	@XmlElement(required = true)
	public String name;
	@XmlElement(required = true)
	public String version;
	@XmlElement(required = true)
	public String comment;
	@XmlElement(required = true)
	public String keywords;
	@XmlElement(required = true)
	public String specificationId;
	@XmlElement(required = true)
	public String status;
	@XmlElement(required = true)
	public String organizationId;
}
