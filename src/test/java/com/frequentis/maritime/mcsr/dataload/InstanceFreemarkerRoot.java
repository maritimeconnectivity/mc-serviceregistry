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
package com.frequentis.maritime.mcsr.dataload;

import com.opencsv.CSVReader;
import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.shape.Point;
import com.spatial4j.core.shape.impl.PointImpl;
// @todo ShapeBuilder and RandomShapeGenerator
//import org.elasticsearch.common.geo.builders.ShapeBuilder;
//import org.elasticsearch.test.geo.RandomShapeGenerator;

import org.elasticsearch.common.geo.builders.PointBuilder;
import org.elasticsearch.common.geo.builders.ShapeBuilder;

import java.io.FileReader;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by TLutz on 05.08.2016.
 */
public class InstanceFreemarkerRoot {
    private String uuid = UUID.randomUUID().toString();
    private String instanceId = "urn:mrn:mcl:service:instance:example:" + uuid;
    private String personMrnID = "urn:mrn:mcl:user:example:johndoe";
    private String personName = "John Doe";
    private String personEmail = "johndoe@example.com";

    private String specificationName = "Address Lokup Service";
    private String serviceDesignId = "urn:mrn:mcl:service:design:example:32c11e45-1fa0-42db-bbff-cfe687382fee";

    private String coversAreaName = "Bermuda Triangle";
    private String coversAreaDescription = "Loosely defined region in the western part of the North Atlantic Ocean.";
    private String coversAreaGeometryWKT = "POLYGON(-80.190 25.774, -66.118 18.466, -64.757 32.321, -80.190 25.774";

    private String providerMrnID = "urn:mrn:mcl:user:example:janedoe";
    private String providerName = "Jane Doe";
    private String providerEmail = "janedoe@example.com";
    private String providerDomain = "jane.doe.example.com";

    private static Random random;
    private static List<String[]> personList;
    private static List<String[]> portList;

    public InstanceFreemarkerRoot() throws Exception  {
        if (random == null) {
            random = new Random();
            // person and domain created with http://www.convertcsv.com/generate-test-data.htm
            CSVReader reader = new CSVReader(new FileReader(this.getClass().getResource("/dataload/instance-generator/random-persons.csv").getFile()));
            personList = reader.readAll();

            // port data from http://msi.nga.mil/NGAPortal/MSI.portal?_nfpb=true&_pageLabel=msi_portal_page_62&pubCode=0015, converted to csv with qgis
            reader = new CSVReader(new FileReader(this.getClass().getResource("/dataload/instance-generator/WPI.csv").getFile()));
            portList = reader.readAll();

        }
    }

    public void randomize() throws Exception {
        randomizePerson();
        randomizeProvider();
        randomizeCoverageArea();
    }

    public void randomizePerson() throws Exception {
        String[] csvEntry = personList.get(randInt(0,personList.size()));
        setPersonName(csvEntry[0] + " " + csvEntry[1]);
        setPersonEmail(csvEntry[2]);
        setPersonMrnID("urn:mrn:mcl:user:example:" + csvEntry[0].toLowerCase() + csvEntry[1].toLowerCase());
    }

    public void randomizeProvider() throws Exception {
        String[] csvEntry = personList.get(randInt(0,personList.size()));
        setProviderName(csvEntry[0] + " " + csvEntry[1]);
        setProviderEmail(csvEntry[2]);
        setProviderEmail("urn:mrn:mcl:user:example:" + csvEntry[0].toLowerCase() + csvEntry[1].toLowerCase());
        setProviderDomain(csvEntry[3]);
    }

    public void randomizeCoverageArea() throws Exception {
        String[] csvEntry = portList.get(randInt(0,portList.size()));
        Point point = new PointImpl(Double.valueOf(csvEntry[0]), Double.valueOf(csvEntry[1]), SpatialContext.GEO);
        ShapeBuilder shapeBuilder = null;
        //while (shapeBuilder == null) {

            //shapeBuilder = RandomShapeGenerator.createShapeNear(random, point, RandomShapeGenerator.ShapeType.POLYGON);
        //}
        //setCoversAreaGeometryWKT(shapeBuilder.build().toString());
        setCoversAreaName(csvEntry[4]);
        setCoversAreaDescription(csvEntry[4] + " " + csvEntry[5]);
    }

    public static int randInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPersonMrnID() {
        return personMrnID;
    }

    public void setPersonMrnID(String personMrnID) {
        this.personMrnID = personMrnID;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonEmail() {
        return personEmail;
    }

    public void setPersonEmail(String personEmail) {
        this.personEmail = personEmail;
    }

    public String getSpecificationName() {
        return specificationName;
    }

    public void setSpecificationName(String specificationName) {
        this.specificationName = specificationName;
    }

    public String getServiceDesignId() {
        return serviceDesignId;
    }

    public void setServiceDesignId(String serviceDesignId) {
        this.serviceDesignId = serviceDesignId;
    }

    public String getCoversAreaName() {
        return coversAreaName;
    }

    public void setCoversAreaName(String coversAreaName) {
        this.coversAreaName = coversAreaName;
    }

    public String getCoversAreaDescription() {
        return coversAreaDescription;
    }

    public void setCoversAreaDescription(String coversAreaDescription) {
        this.coversAreaDescription = coversAreaDescription;
    }

    public String getCoversAreaGeometryWKT() {
        return coversAreaGeometryWKT;
    }

    public void setCoversAreaGeometryWKT(String coversAreaGeometryWKT) {
        this.coversAreaGeometryWKT = coversAreaGeometryWKT;
    }

    public String getProviderMrnID() {
        return providerMrnID;
    }

    public void setProviderMrnID(String providerMrnID) {
        this.providerMrnID = providerMrnID;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderEmail() {
        return providerEmail;
    }

    public void setProviderEmail(String providerEmail) {
        this.providerEmail = providerEmail;
    }

    public String getProviderDomain() {
        return providerDomain;
    }

    public void setProviderDomain(String providerDomain) {
        this.providerDomain = providerDomain;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
