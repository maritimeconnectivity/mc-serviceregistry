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

package com.frequentis.maritime.mcsr.domain.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.elasticsearch.core.geo.GeoShape;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Converter(autoApply = true)
public class GeoShapeNodeConverter implements AttributeConverter<GeoShape<?>, String> {

    @Override
    public String convertToDatabaseColumn(GeoShape<?> geoShape){
    	JsonNode jsonNode = JsonNodeGeoShapeConverter.convertFromGeoShape(geoShape);
        if (jsonNode == null  || jsonNode.asText() == null || jsonNode.asText() == "null") {
            return "";
        }
        return jsonNode.asText();

    }

    @Override
    public GeoShape<?> convertToEntityAttribute(String s) {
        if (s == null || s.length() == 0 || s.equals("null")) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.readTree(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JsonNodeGeoShapeConverter.convertToGeoShape(jsonNode);
    }

}
