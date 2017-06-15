package com.frequentis.maritime.mcsr.web.soap.dto;

import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.domain.Xml;

public class DesignDTO {

    public Long id;

    public String name;

    public String version;

    public String comment;

    public String designId;

    public String status;

    public String organizationId;

    public Xml designAsXml;

    public Doc designAsDoc;

}
