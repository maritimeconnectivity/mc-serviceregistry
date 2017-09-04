package com.frequentis.maritime.mcsr.web.soap.dto.design;

import javax.xml.bind.annotation.XmlElement;

import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlDTO;

public class DesignDTO extends DesignDescriptorDTO {

	@XmlElement(required = true)
    public XmlDTO designAsXml;

    public DocDTO designAsDoc;

}
