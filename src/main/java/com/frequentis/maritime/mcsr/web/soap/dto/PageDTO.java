package com.frequentis.maritime.mcsr.web.soap.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PageDTO<E> {
    @XmlElement(required = true)
    public int page;
    @XmlElement(required = true)
    public int pageCount;
    @XmlElement(required = true)
    public long itemTotalCount;
    @XmlElementWrapper(name = "content")
    @XmlElement(required = true, name="item")
    public List<E> content;
}
