package com.frequentis.maritime.mcsr.web.soap.dto.instance;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.frequentis.maritime.mcsr.web.soap.dto.design.DesignReference;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocReference;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateReference;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlReference;

@XmlRootElement(name = "Instance")
public class InstanceParameterDTO {
	public Long id;

	public XmlReference instanceAsXml;

	public DocReference instanceAsDoc;

	public SpecificationTemplateReference implementedSpecificationVersion;

	public List<DesignReference> designs;

	public List<DocReference> docs;

	public String unlocode;

}
