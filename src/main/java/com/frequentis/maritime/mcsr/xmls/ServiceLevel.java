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
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceLevel {
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private float availability;
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private String name;
    @XmlElement(namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd")
    private String description;
    
    public float getAvailability() {
        return availability;
    }
    public void setAvailability(float availability) {
        this.availability = availability;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
}