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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class XmlUtil {

    /**
     * update an xml node with a new value
     *
     * @param newValue the updated value to set
     * @param xml the XML as string
     * @param xPathExpression the xpath expression to the element to be changed
     * @return the resulting XML as string
     */
    public static String updateXmlNode(String newValue, String xml, String xPathExpression) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();

        Node result = (Node) xPath.evaluate(xPathExpression, doc, XPathConstants.NODE);
        if(result == null) {
            throw new TransformerException("Missing element");
        } else {
            result.getFirstChild().setNodeValue(newValue);
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.getBuffer().toString();
    }

    /**
     * Validate xml against a schema on classpath
     *
     * @param xml the XML as string
     * @param schemaFileName the name of the XSD on the classpath
     * @return true if successful, throws SAXEXception if xml invalid
     */
    public static boolean validateXml(String xml, String schemaFileName) throws SAXException, IOException {
        //File schemaFile = new File(schemaFileName);
        Source xmlSource = new StreamSource(new StringReader(xml));
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver(new ResourceResolver());
        InputStream schemaInputStream = XmlUtil.class.getClassLoader().getResourceAsStream(schemaFileName);
        StreamSource schemaStreamSource = new StreamSource(schemaInputStream);
        Schema schema = schemaFactory.newSchema(schemaStreamSource);
        Validator validator = schema.newValidator();
        validator.validate(xmlSource);
        return true;
    }

}
