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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import io.swagger.annotations.ApiModel;

/**
 * Holds a logical description of a service. A specification can be compatible to one or more
 * specification templates. It has at least a technical representation of the service
 * descriptiion in form of an XML and a filled out service templateas e.g. word document.
 *
 */
@ApiModel(description = ""
    + "Holds a logical description of a service. A specification can be compatible to one or "
    + "morespecification templates.It has at least a technical representation of the servicedescriptiion "
    + "in form of an XML and a filled out service templateas e.g. word document.")
@Entity
@Table(name = "specification")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "specification")
@Setting(settingPath = "analyzer-settings.json")
public class Specification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Field(type = FieldType.text, index = true, fielddata = true)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    @Field(type = FieldType.text, index = true, fielddata = true)
    private String name;

    @NotNull
    @Column(name = "version", nullable = false)
    @Field(type = FieldType.text, index = true, fielddata = true)
    private String version;

    @Column(name = "published_at", nullable = true)
    @Field(type = FieldType.text, index = true, fielddata = true)
    private String publishedAt;

    @Column(name = "last_updated_at", nullable = true)
    @Field(type = FieldType.text, index = true, fielddata = true)
    private String lastUpdatedAt;

    @NotNull
    @Column(name = "comment", nullable = false)
    @Field(type = FieldType.text, index = true, fielddata = true)
    private String comment;

    @Column(name = "keywords")
    @Field(type = FieldType.text, index = true, fielddata = true)
    private String keywords;

    @NotNull
    @Column(name = "specification_id", nullable = false)
    @JsonProperty("specificationId")
    @Field(type = FieldType.keyword, index = true)
    @Mapping(mappingPath = "keyword-mapping.json")
    private String specificationId;

    @Column(name = "status")
    @Field(type = FieldType.keyword, index = true)
    private String status;

    @Column(name = "organization_id")
    @JsonProperty("organizationId")
    @Field(type = FieldType.keyword, index = true)
    @Mapping(mappingPath = "keyword-mapping.json")
    private String organizationId;

    @OneToOne
    @JoinColumn(unique = true)
    private Xml specAsXml;

    @OneToOne
    @JoinColumn(unique = true)
    private Doc specAsDoc;

    @ManyToOne
    private SpecificationTemplate implementedSpecificationVersion;

    @ManyToMany(cascade = CascadeType.PERSIST)
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

    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(String lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
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
            ", publishedAt='" + publishedAt + "'" +
            ", lastUpdatedAt='" + lastUpdatedAt + "'" +
            ", comment='" + comment + "'" +
            ", keywords='" + keywords + "'" +
            ", specificationId='" + specificationId + "'" +
            ", status='" + status + "'" +
            ", organizationId='" + organizationId + "'" +
            '}';
    }
}
