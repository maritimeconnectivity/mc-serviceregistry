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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

@XmlAccessorType(XmlAccessType.FIELD)
public class CoversAreaType {
    @XmlElements({
            @XmlElement(name = "unLoCode", namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd", type = String.class),
            @XmlElement(name = "coversArea", namespace = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd", type = CoverageArea.class)
    })
    private List<?> area;
    
    public boolean isCoversArea() {
        return !area.isEmpty() && area.get(0) instanceof CoverageArea;
    }
    
    public boolean isUnLoCode() {
        return !area.isEmpty() && area.get(0) instanceof String;
    }
    
    @SuppressWarnings("unchecked")
    public List<CoverageArea> getCoversArea() {
        if(isCoversArea()) {
            return (List<CoverageArea>) area;
        }
        
        return null;
    }
    
    public void setCoversArea(List<CoverageArea> coversArea) {
        this.area = coversArea;
    }
    
    public String getUnLoCode() {
        if(isUnLoCode()) {
            return (String) area.get(0);
        }
        
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public void setUnLoCode(String unLoCode) {
        this.area = new ArrayList<String>();
        ((ArrayList<String>) this.area).add(unLoCode);
    }
    
    
}