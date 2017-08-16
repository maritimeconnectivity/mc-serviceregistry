package com.frequentis.maritime.mcsr.web.soap.dto.design;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DesignReference")
public class DesignReference {
	@XmlElement(required = true)
    public Long id;
}
