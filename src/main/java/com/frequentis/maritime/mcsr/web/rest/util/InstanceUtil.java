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

package com.frequentis.maritime.mcsr.web.rest.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frequentis.maritime.mcsr.domain.Instance;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.wololo.geojson.GeoJSON;
import org.wololo.jts2geojson.GeoJSONWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class InstanceUtil {
    private static final Logger log = LoggerFactory.getLogger(InstanceUtil.class);
    private static HashMap<String, UnLoCodeMapEntry> UnLoCodeMap = null;
    private static final String JSON_KEY_COUNTRY = "Country";
    private static final String JSON_KEY_LOCATION = "Location";
    private static final String JSON_KEY_COORDINATES = "Coordinates";


    /**
     * Parse instance attributes from the xml payload for search/filtering
     *
     * @param instance the instance to parse
     * @return an instance with its attributes set
     * @throws Exception if the XML is invalid or attributes not present
     */
    public static Instance parseInstanceAttributesFromXML(Instance instance) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        builder = factory.newDocumentBuilder();
        log.info("Parsing XML: " + instance.getInstanceAsXml().getContent().toString());
        Document doc = builder.parse(new ByteArrayInputStream(instance.getInstanceAsXml().getContent().toString().getBytes(StandardCharsets.UTF_8)));
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();

        instance.setName(xPath.compile("/*[local-name()='serviceInstance']/*[local-name()='name']").evaluate(doc, XPathConstants.STRING).toString());
        instance.setVersion(xPath.compile("/*[local-name()='serviceInstance']/*[local-name()='version']").evaluate(doc, XPathConstants.STRING).toString());
        instance.setInstanceId(xPath.compile("/*[local-name()='serviceInstance']/*[local-name()='id']").evaluate(doc, XPathConstants.STRING).toString());
        instance.setKeywords(xPath.compile("/*[local-name()='serviceInstance']/*[local-name()='keywords']").evaluate(doc, XPathConstants.STRING).toString());
        instance.setStatus(xPath.compile("/*[local-name()='serviceInstance']/*[local-name()='status']").evaluate(doc, XPathConstants.STRING).toString());
        instance.setComment(xPath.compile("/*[local-name()='serviceInstance']/*[local-name()='description']").evaluate(doc, XPathConstants.STRING).toString());
        instance.setEndpointUri(xPath.compile("/*[local-name()='serviceInstance']/*[local-name()='URL']").evaluate(doc, XPathConstants.STRING).toString());
        instance.setMmsi(xPath.compile("/*[local-name()='serviceInstance']/*[local-name()='MMSI']").evaluate(doc, XPathConstants.STRING).toString());
        instance.setImo(xPath.compile("/*[local-name()='serviceInstance']/*[local-name()='IMO']").evaluate(doc, XPathConstants.STRING).toString());
        instance.setServiceType(xPath.compile("/*[local-name()='serviceInstance']/*[local-name()='serviceType']").evaluate(doc, XPathConstants.STRING).toString());

        String unLoCode = xPath.compile("/*[local-name()='serviceInstance']/*[local-name()='unLoCode']").evaluate(doc, XPathConstants.STRING).toString();
        if (unLoCode != null && unLoCode.length() > 0) {
            instance.setUnlocode(unLoCode);
        }
        return instance;
    }

    /**
     * Parse instance geometry from the xml payload for search/filtering
     *
     * @param instance the instance to parse
     * @return an instance with its attributes set
     * @throws Exception if the XML is invalid or attributes not present
     */
    public static Instance parseInstanceGeometryFromXML(Instance instance) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        builder = factory.newDocumentBuilder();
        log.info("Parsing XML: " + instance.getInstanceAsXml().getContent().toString());
        Document doc = builder.parse(new ByteArrayInputStream(instance.getInstanceAsXml().getContent().toString().getBytes(StandardCharsets.UTF_8)));
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();

        String unLoCode = xPath.compile("/*[local-name()='serviceInstance']/*[local-name()='coversAreas']/*[local-name()='unLoCode']").evaluate(doc, XPathConstants.STRING).toString();
        String geometryAsWKT = xPath.compile("/*[local-name()='serviceInstance']/*[local-name()='coversAreas']/*[local-name()='coversArea']/*[local-name()='geometryAsWKT']").evaluate(doc);
        if (unLoCode != null && unLoCode.length() > 0) {
            instance.setUnlocode(unLoCode);
            mapUnLoCodeToLocation(instance, unLoCode);
        } else if (geometryAsWKT != null && geometryAsWKT.length() > 0) {
            JsonNode geometryAsGeoJson = convertWKTtoGeoJson(geometryAsWKT);
            instance.setGeometry(geometryAsGeoJson);
        }
        return instance;
    }



    /**
     * Converts a WKT geometry into GeoJson format, via JTS geometry
     *
     * @param geometryAsWKT The geometry in WKT format
     * @return JsonNode with the geometry expressed in GeoJson format
     * @throws ParseException if the WKT geometry was invalid
     * @throws IOException if the geoJson string could not be read by the Json parser
     */
    public static JsonNode convertWKTtoGeoJson(String geometryAsWKT) throws ParseException, IOException {
        WKTReader wktReader = new WKTReader();
        Geometry geometry = wktReader.read(geometryAsWKT);
        if (geometry == null) {
            log.debug("WKT geometry parsing error");
        }
        JsonNode jsonNode = null;
        GeoJSONWriter writer = new GeoJSONWriter();
        GeoJSON geoJson = writer.write(geometry);
        ObjectMapper mapper = new ObjectMapper();
        jsonNode = mapper.readTree(geoJson.toString());
        return jsonNode;
    }


    /**
     * Gets the location data for an unlocode and populates an instance with the lat/lon point geometry
     *
     * @param instance The instance object to modify
     * @param unLoCode The unlocode whose lat/lon position has to be inserted into the instance
     * @throws IOException if the UnLoCode mapping file could not be loaded
     */
    public static void mapUnLoCodeToLocation(Instance instance, String unLoCode) throws IOException {
        if (UnLoCodeMap == null) {
            InputStream s = InstanceUtil.class.getClassLoader().getResourceAsStream("UnLoCodeLists.json");
            loadUnLoCodeMapping(s);
        }

        applyUnLoCodeMapping(instance, unLoCode);
    }

    public static void applyUnLoCodeMapping(Instance instance, String unLoCode) {
        UnLoCodeMapEntry e = UnLoCodeMap.get(unLoCode);
        String pointWKT = "";
        try {
            if (e != null) {
                pointWKT = "POINT (" + e.longitude + " " + e.latitude + ")";
                JsonNode pointJson = convertWKTtoGeoJson(pointWKT);
                //Update the json geometry so E2 can find it
                instance.setGeometry(pointJson);
                //insert the WKT geometry into the XML
//TODO: Check if this is actually neccessary, ATM this doesn't create missing xml nodes and it might be better to not modify the XML anyway
//                Xml instanceXml = instance.getInstanceAsXml();
//                String xml = instanceXml.getContent().toString();
//                String resultXml = updateXmlNode(pointWKT, xml, "/ServiceInstanceSchema:serviceInstance/coversArea/coversArea/geometryAsWKT");
//                instanceXml.setContent(resultXml);
//                instance.setInstanceAsXml(instanceXml);
            }
        } catch (Exception ex) {
            log.error("Error parsing point geometry generated from UnLoCode mapping " + pointWKT + ": ", ex);
        }
    }

    /**
     * Fetch lat/lon from json mapping file and populate coverage geometry with it as point
     *
     * @throws Exception if the unLoCode mapping file could not be found
     */
    public static void loadUnLoCodeMapping(InputStream inStream) throws IOException {
        UnLoCodeMap = new HashMap<String, UnLoCodeMapEntry>();
        ObjectMapper mapper = new ObjectMapper();
        double invalid = -99999;

        JsonNode unLoCodeJson = null;
        unLoCodeJson = mapper.readTree(inStream);
        for (JsonNode entry : unLoCodeJson) {
            try {
                UnLoCodeMapEntry unLoCode = new UnLoCodeMapEntry();
                String country = entry.get(JSON_KEY_COUNTRY).textValue();
                String location = entry.get(JSON_KEY_LOCATION).textValue();
                String coordinatesCombined = entry.get(JSON_KEY_COORDINATES).textValue();
                unLoCode.latitude = invalid;
                unLoCode.longitude = invalid;
                //coordinates are given in the form of "DDMM[N/S] DDDMM[W/E]"
                if (coordinatesCombined != null) {
                    coordinatesCombined = coordinatesCombined.trim();
                    if (coordinatesCombined.length() > 0) {
                        String[] c = coordinatesCombined.split("\\s");
                        String latDegrees = c[0].substring(0, 2);
                        String latMinutes = c[0].substring(2, 4);
                        String latDirection = c[0].substring(4, 5);
                        unLoCode.latitude = Double.parseDouble(latDegrees + "." + latMinutes);
                        if (latDirection == "S") {
                            unLoCode.latitude = -unLoCode.latitude;
                        }
                        String lonDegrees = c[1].substring(0, 3);
                        String lonMinutes = c[1].substring(3, 5);
                        String lonDirection = c[1].substring(5, 6);
                        unLoCode.longitude = Double.parseDouble(lonDegrees + "." + lonMinutes);
                        if (lonDirection == "W") {
                            unLoCode.longitude = -unLoCode.longitude;
                        }
                    }
                }
                String status = entry.get("Status").textValue();

                unLoCode.status = status;
                if (unLoCode.latitude != invalid && unLoCode.longitude != invalid) {
                    UnLoCodeMap.put(country + " " + location, unLoCode);
                }
            } catch (Exception e) {
                log.error("Error parsing UnLoCode mapping file: ", e);
            }
        }
    }

    public static boolean checkOrganizationId(Instance instance, String organizationId) {
        if (instance.getOrganizationId() != null && instance.getOrganizationId().length() > 0 && !organizationId.equals(instance.getOrganizationId())) {
            return false;
        }
		return true;

    }


}
