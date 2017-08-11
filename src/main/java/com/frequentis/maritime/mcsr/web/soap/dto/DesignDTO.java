package com.frequentis.maritime.mcsr.web.soap.dto;

import javax.xml.bind.annotation.XmlElement;

import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.domain.Xml;

public class DesignDTO extends DesignDescriptorDTO {

	@XmlElement(required = true)
    public XmlDTO designAsXml;

    public DocDTO designAsDoc;

}
