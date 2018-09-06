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
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import com.frequentis.maritime.mcsr.domain.Xml;

import io.swagger.annotations.ApiModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Holds a description of a technical design.A design can be compatible to one or morespecification templates.It has at least a technical representation of thedescriptiion in form of an XML and a filled out templateas e.g. word document.
 *
 */
@ApiModel(description = ""
    + "Holds a description of a technical design.A design can be compatible to one or morespecification templates.It has at least a technical representation of thedescriptiion in form of an XML and a filled out templateas e.g. word document."
    + "")
@Entity
@Table(name = "design")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "design")
@Setting(settingPath = "analyzer-settings.json")
public class Design implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @NotNull
    @Column(name = "design_id", nullable = false)
    @Field(type = FieldType.keyword, index = true)
    @JsonProperty("designId")
    @Mapping(mappingPath = "keyword-mapping.json")
    private String designId;

    @Column(name = "status")
    @Field(type = FieldType.keyword, index = true)
    private String status;

    @Column(name = "organization_id")
    @Field(type = FieldType.keyword, index = true)
    @JsonProperty("organizationId")
    @Mapping(mappingPath = "keyword-mapping.json")
    private String organizationId;

    @OneToOne
    @JoinColumn(unique = true)
    private Xml designAsXml;

    @OneToOne
    @JoinColumn(unique = true)
    private Doc designAsDoc;

    @ManyToOne
    private SpecificationTemplate implementedSpecificationVersion;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "design_specifications",
               joinColumns = @JoinColumn(name="designs_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="specifications_id", referencedColumnName="ID"))
    private Set<Specification> specifications = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "design_docs",
               joinColumns = @JoinColumn(name="designs_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="docs_id", referencedColumnName="ID"))
    private Set<Doc> docs = new HashSet<>();

    @ManyToMany(mappedBy = "designs")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Instance> instances = new HashSet<>();

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

    public String getDesignId() {
        return designId;
    }

    public void setDesignId(String designId) {
        this.designId = designId;
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

    public Xml getDesignAsXml() {
        return designAsXml;
    }

    public void setDesignAsXml(Xml xml) {
        this.designAsXml = xml;
    }

    public Doc getDesignAsDoc() {
        return designAsDoc;
    }

    public void setDesignAsDoc(Doc doc) {
        this.designAsDoc = doc;
    }

    public SpecificationTemplate getImplementedSpecificationVersion() {
        return implementedSpecificationVersion;
    }

    public void setImplementedSpecificationVersion(SpecificationTemplate specificationTemplate) {
        this.implementedSpecificationVersion = specificationTemplate;
    }

    public Set<Specification> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(Set<Specification> specifications) {
        this.specifications = specifications;
    }

    public Set<Doc> getDocs() {
        return docs;
    }

    public void setDocs(Set<Doc> docs) {
        this.docs = docs;
    }

    public Set<Instance> getInstances() {
        return instances;
    }

    public void setInstances(Set<Instance> instances) {
        this.instances = instances;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Design design = (Design) o;
        if(design.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, design.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Design{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", version='" + version + "'" +
            ", publishedAt='" + publishedAt + "'" +
            ", lastUpdatedAt='" + lastUpdatedAt + "'" +
            ", comment='" + comment + "'" +
            ", designId='" + designId + "'" +
            ", status='" + status + "'" +
            ", organizationId='" + organizationId + "'" +
            '}';
    }
}
