/*
 * MaritimeCloud Service Registry
 * Copyright (c) 2016 Frequentis AG
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.frequentis.maritime.mcsr.domain;

import io.swagger.annotations.ApiModel;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * Holds a logical description of a service.A specification can be compatible to one or morespecification templates.It has at least a technical representation of the servicedescriptiion in form of an XML and a filled out service templateas e.g. word document.
 *
 */
@ApiModel(description = ""
    + "Holds a logical description of a service.A specification can be compatible to one or morespecification templates.It has at least a technical representation of the servicedescriptiion in form of an XML and a filled out service templateas e.g. word document."
    + "")
@Entity
@Table(name = "specification")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "specification")
public class Specification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "version", nullable = false)
    private String version;

    @NotNull
    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "keywords")
    private String keywords;

    @NotNull
    @Column(name = "specification_id", nullable = false)
    private String specificationId;

    @Column(name = "status")
    private String status;

    @Column(name = "organization_id")
    private String organizationId;

    @OneToOne
    @JoinColumn(unique = true)
    private Xml specAsXml;

    @OneToOne
    @JoinColumn(unique = true)
    private Doc specAsDoc;

    @ManyToOne
    private SpecificationTemplate implementedSpecificationVersion;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "specification_docs",
               joinColumns = @JoinColumn(name="specifications_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="docs_id", referencedColumnName="ID"))
    private Set<Doc> docs = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getSpecificationId() {
        return specificationId;
    }

    public void setSpecificationId(String specificationId) {
        this.specificationId = specificationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Xml getSpecAsXml() {
        return specAsXml;
    }

    public void setSpecAsXml(Xml xml) {
        this.specAsXml = xml;
    }

    public Doc getSpecAsDoc() {
        return specAsDoc;
    }

    public void setSpecAsDoc(Doc doc) {
        this.specAsDoc = doc;
    }

    public SpecificationTemplate getImplementedSpecificationVersion() {
        return implementedSpecificationVersion;
    }

    public void setImplementedSpecificationVersion(SpecificationTemplate specificationTemplate) {
        this.implementedSpecificationVersion = specificationTemplate;
    }

    public Set<Doc> getDocs() {
        return docs;
    }

    public void setDocs(Set<Doc> docs) {
        this.docs = docs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Specification specification = (Specification) o;
        if(specification.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, specification.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Specification{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", version='" + version + "'" +
            ", comment='" + comment + "'" +
            ", keywords='" + keywords + "'" +
            ", specificationId='" + specificationId + "'" +
            ", status='" + status + "'" +
            ", organizationId='" + organizationId + "'" +
            '}';
    }
}