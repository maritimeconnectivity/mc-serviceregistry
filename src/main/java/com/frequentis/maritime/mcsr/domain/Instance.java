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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.frequentis.maritime.mcsr.domain.util.GeoShapeNodeConverter;
import com.frequentis.maritime.mcsr.domain.util.JsonNodeConverter;
import com.frequentis.maritime.mcsr.domain.util.JsonNodeGeoShapeConverter;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.geo.GeoShape;

import io.swagger.annotations.ApiModel;

/**
 * Holds a description of an service instance.An instance can be compatible to one or morespecification templates.It has at least a technical representation of thedescriptiion in form of an XML and a filled out templateas e.g. word document.
 *
 */
@ApiModel(description = ""
    + "Holds a description of an service instance.An instance can be compatible to one or morespecification templates.It has at least a technical representation of thedescriptiion in form of an XML and a filled out templateas e.g. word document."
    + "")
@Entity
@Table(name = "instance")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "instance")
public class Instance implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String SERVICESTATUS_LIVE = "live";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = true)
    @Field(type = FieldType.keyword, index = true)
    private String name;

    @NotNull
    @Column(name = "version", nullable = true)
    @Field(type = FieldType.keyword, index = true)
    private String version;

    @NotNull
    @Column(name = "comment", nullable = true)
    @Field(type = FieldType.text, index = true)
    private String comment;

    @Column(name = "geometry", columnDefinition = "LONGTEXT")
    @Convert(converter = GeoShapeNodeConverter.class)
    private GeoShape<?> geometry;

    @Column(name = "geometry_content_type")
    private String geometryContentType;

    @NotNull
    @Column(name = "instance_id", nullable = true)
    @JsonProperty("instanceId")
    @Field(type = FieldType.keyword, index = true)
    private String instanceId;

    @Column(name = "keywords")
    private String keywords;

    @Column(name = "status")
    @Field(type = FieldType.keyword, index = true)
    private String status;

    @Column(name = "organization_id")
    @JsonProperty("organization_id")
    @Field(type = FieldType.keyword, index = true)
    private String organizationId;

    @Column(name = "unlocode")
    @Field(type = FieldType.keyword, index = true)
    private String unlocode;

    @Column(name = "endpoint_uri")
    @JsonProperty("endpoint_uri")
    @Field(type = FieldType.keyword, index = true)
    private String endpointUri;

    @Column(name = "endpoint_type")
    @JsonProperty("endpoint_type")
    @Field(type = FieldType.keyword, index = true)
    private String endpointType;

    @Column(name = "mmsi")
    @Field(type = FieldType.keyword, index = true)
    private String mmsi;

    @Column(name = "imo")
    @Field(type = FieldType.keyword, index = true)
    private String imo;

    @Column(name = "service_type")
    @JsonProperty("service_type")
    @Field(type = FieldType.keyword, index = true)
    private String serviceType;

    @Column(name = "design_id")
    @JsonProperty("design_id")
    @Field(type = FieldType.keyword, index = true)
    private String designId;

    @Column(name = "specification_id")
    @JsonProperty("specification_id")
    @Field(type = FieldType.keyword, index = true)
    private String specificationId;

    @OneToOne
    @JoinColumn(unique = true)
    private Xml instanceAsXml;

    @OneToOne
    @JoinColumn(unique = true)
    private Doc instanceAsDoc;

    @ManyToOne
    private SpecificationTemplate implementedSpecificationVersion;

    @ManyToMany(fetch=FetchType.LAZY)
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "instance_designs",
               joinColumns = @JoinColumn(name="instances_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="designs_id", referencedColumnName="ID"))
    private Set<Design> designs = new HashSet<>();

    @ManyToMany(fetch=FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "instance_docs",
               joinColumns = @JoinColumn(name="instances_id", referencedColumnName="ID"),
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

    @Transient
    public GeoShape<?> getGeometryAsGeoShape() {
        return geometry;
    }

    public JsonNode getGeometry() {
        return JsonNodeGeoShapeConverter.convertFromGeoShape(getGeometryAsGeoShape());
    }

    public void setGeometryAsGeoShape(GeoShape<?> geometry) {
        this.geometry = geometry;
    }

    public void setGeometry(JsonNode geometry) {
        setGeometryAsGeoShape(JsonNodeGeoShapeConverter.convertToGeoShape(geometry));
    }

    public String getGeometryContentType() {
        return geometryContentType;
    }

    public void setGeometryContentType(String geometryContentType) {
        this.geometryContentType = geometryContentType;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
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

    public String getUnlocode() {
        return unlocode;
    }

    public void setUnlocode(String unlocode) {
        this.unlocode = unlocode;
    }

    public String getEndpointUri() {
        return endpointUri;
    }

    public void setEndpointUri(String endpointUri) {
        this.endpointUri = endpointUri;
    }

    public String getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(String endpointType) {
        this.endpointType = endpointType;
    }

    public String getMmsi() { return mmsi; }

    public void setMmsi(String mmsi) { this.mmsi = mmsi; }

    public String getImo() { return imo; }

    public void setImo(String imo) { this.imo = imo; }

    public String getServiceType() { return serviceType; }

    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getDesignId() { return designId; }

    public void setDesignId(String designId) { this.designId = designId; }

    public String getSpecificationId() { return specificationId; }

    public void setSpecificationId(String specificationId) { this.specificationId = specificationId; }

    public Xml getInstanceAsXml() {
        return instanceAsXml;
    }

    public void setInstanceAsXml(Xml xml) {
        this.instanceAsXml = xml;
    }

    public Doc getInstanceAsDoc() {
        return instanceAsDoc;
    }

    public void setInstanceAsDoc(Doc doc) {
        this.instanceAsDoc = doc;
    }

    public SpecificationTemplate getImplementedSpecificationVersion() {
        return implementedSpecificationVersion;
    }

    public void setImplementedSpecificationVersion(SpecificationTemplate specificationTemplate) {
        this.implementedSpecificationVersion = specificationTemplate;
    }

    public Set<Design> getDesigns() {
        return designs;
    }

    public void setDesigns(Set<Design> designs) {
        this.designs = designs;
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
        Instance instance = (Instance) o;
        if(instance.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, instance.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Instance{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", version='" + version + "'" +
            ", comment='" + comment + "'" +
            ", geometry='" + geometry + "'" +
            ", geometryContentType='" + geometryContentType + "'" +
            ", instanceId='" + instanceId + "'" +
            ", keywords='" + keywords + "'" +
            ", status='" + status + "'" +
            ", organizationId='" + organizationId + "'" +
            ", unlocode='" + unlocode + "'" +
            ", endpointUri='" + endpointUri + "'" +
            ", endpointType='" + endpointType + "'" +
            ", designId='" + designId + "'" +
            ", specificationId='" + specificationId + "'" +
            ", mmsi='" + mmsi + "'" +
            ", imo='" + imo+ "'" +
            ", serviceType='" + serviceType + "'" +
            '}';
    }
}
