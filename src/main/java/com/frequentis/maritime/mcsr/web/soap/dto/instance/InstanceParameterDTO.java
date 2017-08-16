package com.frequentis.maritime.mcsr.web.soap.dto.instance;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.frequentis.maritime.mcsr.web.soap.dto.design.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.design.DesignReference;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocReference;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateParameterDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlDTO;

@XmlRootElement(name = "Instance")
public class InstanceParameterDTO {
	public Long id;
	
	public XmlDTO instanceAsXml;
	
	public DocDTO instanceAsDoc;
	
	public SpecificationTemplateParameterDTO implementedSpecificationVersion;
	
	public List<DesignReference> designs;
	
	public List<DocReference> docs;
	
	public String unlocode;

}
