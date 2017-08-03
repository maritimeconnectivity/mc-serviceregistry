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

import java.io.IOException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.elasticsearch.core.geo.GeoShape;
import org.springframework.data.elasticsearch.core.geo.GeoShapeModule;

@Converter(autoApply = true)
public class JsonNodeGeoShapeConverter implements AttributeConverter<JsonNode, GeoShape<?>> {

    @Override
    public GeoShape<?> convertToDatabaseColumn(JsonNode jsonNode){
        return convertToGeoShape(jsonNode);
    }

    @Override
    public JsonNode convertToEntityAttribute(GeoShape<?> s) {
        return convertFromGeoShape(s);
    }

    public static JsonNode convertFromGeoShape(GeoShape<?> s) {
        if (s == null) {
            return null;
        }

        ObjectMapper om = new ObjectMapper();
        om.registerModule(new GeoShapeModule());

        try {
            JsonNode node = om.readTree(om.writeValueAsString(s));
            // GeoJson should have first letter in uppercase
            // String type = node.get("type").asText();
            // String jsonType = type.substring(0, 1).toUpperCase() + type.substring(1);
            // ((ObjectNode) node).put("type", jsonType);
            return node;
        } catch (IOException e) {
            return null;
        }
    }

    public static GeoShape<?> convertToGeoShape(JsonNode jsonNode) {
        if (jsonNode == null  || jsonNode.asText() == null || jsonNode.asText() == "null") {
            return null;
        }

        ObjectMapper om = new ObjectMapper();
        om.registerModule(new GeoShapeModule());

        try {
            String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
            return om.readValue(json, GeoShape.class);
        } catch (IOException e) {
            return null;
        }
    }

}
