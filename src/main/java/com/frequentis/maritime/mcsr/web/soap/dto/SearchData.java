package com.frequentis.maritime.mcsr.web.soap.dto;

import javax.xml.bind.annotation.XmlElement;

public class SearchData {
	@XmlElement(required = true)
    public String query;
	@XmlElement(required = true)
    public int page;
}
