package com.frequentis.maritime.mcsr.web.soap.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PageDTO<E> {
    @XmlElement(required = true)
    public int page;
    @XmlElement(required = true)
    public int pageCount;
    @XmlElement(required = true)
    public List<E> content;
}
