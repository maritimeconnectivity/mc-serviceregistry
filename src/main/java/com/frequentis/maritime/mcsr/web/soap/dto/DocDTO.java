package com.frequentis.maritime.mcsr.web.soap.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Doc")
public class DocDTO {
	@XmlElement(required = false)
	public Long id;
	@XmlElement(required = true)
    public String name;
	@XmlElement(required = true)
    public String comment;
	@XmlElement(required = true)
    public String mimetype;
	@XmlElement(required = true)
    public byte[] filecontent;
	@XmlElement(required = true)
    public String filecontentContentType;
    

}
