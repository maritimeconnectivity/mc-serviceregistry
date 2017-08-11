package com.frequentis.maritime.mcsr.web.soap.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
	
	public String specificationId;
	
	public String instanceAsXml;
	
	public String instanceAsDoc;
	
	public String SpecificationTemplate;
	
	public List<DesignDTO> designs;
	
	public List<DocDTO> docs;

}
