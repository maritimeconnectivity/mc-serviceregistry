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

package com.frequentis.maritime.mcsr.builders;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.frequentis.maritime.mcsr.xmls.InstanceXML;

public class XMLInstanceBuilder {
    private static final Logger log = LoggerFactory.getLogger(XMLInstanceBuilder.class);

    private static final String XSI_SCHEMA_LOCATION = "http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd ServiceInstanceSchema.xsd";

    private Transformer tr;
    private InstanceXML iv;

    public XMLInstanceBuilder() {
        this(new InstanceXML());
    }

    public XMLInstanceBuilder(InstanceXML instance) {
        this.iv = instance;
        try {
            tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
        } catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
            log.error(e.getMessage(), e);
        }
    }

    public Document buildXml() {
        JAXBContext jaxContext;
        try {
            jaxContext = JAXBContext.newInstance(InstanceXML.class);
            Marshaller marshaller = jaxContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, XSI_SCHEMA_LOCATION);

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            marshaller.marshal(this.iv, doc);
            return doc;
        } catch (JAXBException | ParserConfigurationException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public String buildXmlString() throws TransformerException {
        Document doc = buildXml();

        DOMSource domSource = new DOMSource(doc);
        StringWriter sw = new StringWriter();
        tr.transform(domSource, new StreamResult(sw));

        return sw.getBuffer().toString();

    }

    public InstanceXML getBuilderXml() {
        return this.iv;
    }












}
