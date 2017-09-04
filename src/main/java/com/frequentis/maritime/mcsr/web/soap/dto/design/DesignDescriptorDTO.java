package com.frequentis.maritime.mcsr.web.soap.dto.design;

import javax.xml.bind.annotation.XmlElement;

public class DesignDescriptorDTO extends DesignReference {

    @XmlElement(required = true)
    public String name;

    @XmlElement(required = true)
    public String version;

    public String comment;

    @XmlElement(required = true)
    public String designId;

    @XmlElement(required = true)
    public String status;

    @XmlElement(required = true)
    public String organizationId;

}
