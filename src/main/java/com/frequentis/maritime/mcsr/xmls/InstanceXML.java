/*
 * MaritimeCloud Service Registry
 * Copyright (c) 2017 Frequentis AG
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

package com.frequentis.maritime.mcsr.xmls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "serviceInstance", namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
@XmlAccessorType(XmlAccessType.FIELD)
public class InstanceXML {
    public enum Status {
        provisional, relased, deprecated, deleted;
    }
    
    
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private String id;
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private String version;
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private String name;
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private Status status;
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private String description;
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private String keywords;
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private String URL;
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private String MMSI;
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private String IMO;
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private String serviceType;
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private String requiresAuthorization;
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private ServiceLevel offersServiceLevel = new ServiceLevel();
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private CoversAreaType coversAreas = new CoversAreaType();
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private ServiceDesignReference implementsServiceDesign = new ServiceDesignReference();
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private VendorInfo producedBy = new VendorInfo();
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private VendorInfo providedBy = new VendorInfo();
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getKeywords() {
        return keywords;
    }
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
    public String getURL() {
        return URL;
    }
    public void setURL(String uRL) {
        URL = uRL;
    }
    public String getMMSI() {
        return MMSI;
    }
    public void setMMSI(String mMSI) {
        MMSI = mMSI;
    }
    public String getIMO() {
        return IMO;
    }
    public void setIMO(String iMO) {
        IMO = iMO;
    }
    public String getServiceType() {
        return serviceType;
    }
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
    public String getRequiresAuthorization() {
        return requiresAuthorization;
    }
    public void setRequiresAuthorization(String requiredAuthorization) {
        this.requiresAuthorization = requiredAuthorization;
    }
    public ServiceLevel getOffersServiceLevel() {
        return offersServiceLevel;
    }
    public void setOffersServiceLevel(ServiceLevel offersServiceLevel) {
        this.offersServiceLevel = offersServiceLevel;
    }
    public CoversAreaType getCoversAreas() {
        return coversAreas;
    }
    public void setCoversAreas(CoversAreaType coversAreas) {
        this.coversAreas = coversAreas;
    }
    public ServiceDesignReference getImplementsServiceDesign() {
        return implementsServiceDesign;
    }
    public void setImplementsServiceDesign(ServiceDesignReference implementsServiceDesign) {
        this.implementsServiceDesign = implementsServiceDesign;
    }
    public VendorInfo getProducedBy() {
        return producedBy;
    }
    public void setProducedBy(VendorInfo producedBy) {
        this.producedBy = producedBy;
    }
    public VendorInfo getProvidedBy() {
        return providedBy;
    }
    public void setProvidedBy(VendorInfo providedBy) {
        this.providedBy = providedBy;
    }
    
}