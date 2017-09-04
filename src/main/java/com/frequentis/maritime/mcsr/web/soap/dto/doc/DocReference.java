package com.frequentis.maritime.mcsr.web.soap.dto.doc;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DocReference")
public class DocReference {
    @XmlElement(required = true)
    public Long id;
}
