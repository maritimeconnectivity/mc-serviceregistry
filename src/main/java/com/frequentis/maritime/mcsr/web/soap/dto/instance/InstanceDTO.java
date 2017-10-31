package com.frequentis.maritime.mcsr.web.soap.dto.instance;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.frequentis.maritime.mcsr.web.soap.dto.design.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlDTO;

@XmlRootElement(name = "Instance")
public class InstanceDTO {
	public Long id;

	@XmlElement(required = true)
	public String name;

	@XmlElement(required = true)
	public String version;

	public String comment;

	public String geometry;

	public String geometryContentType;

	public String instanceId;

	public String keywords;

	public String status;

	public String organizationId;

	public String unlocode;

	public String endpointUri;

	public String endpointType;

	public String mmsi;

	public String imo;

	public String serviceType;

	public String designId;

	public DesignDTO design;

	public String specificationId;

	public XmlDTO instanceAsXml;

	public DocDTO instanceAsDoc;

	public SpecificationTemplateDTO implementedSpecificationVersion;

	public List<DesignDTO> designs;

	public List<DocDTO> docs;

	public Boolean compliant;

    @Override
    public String toString() {
        return "InstanceDTO [id=" + id + ", name=" + name + ", version=" + version + ", geometry=" + geometry
                + ", status=" + status + ", unlocode=" + unlocode + "]";
    }

}
