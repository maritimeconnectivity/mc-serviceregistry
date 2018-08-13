package com.frequentis.maritime.mcsr.domain.util;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.web.rest.util.XmlUtil;

/**
 *
 * @author Lukas Vorisek
 *
 */
public final class DesignUtils {

	private DesignUtils() {

	}

	/**
	 *
	 * @param design Design where
	 * @param status
	 * @throws Exception
	 */
	public static void updateXmlStatusValue(Design design, String status) throws Exception {
		Xml designXml = design.getDesignAsXml();
		String xml = designXml.getContent().toString();
		String resultXml = XmlUtil.updateXmlNode(status, xml, "/ServiceDesignSchema:serviceDesign/status");
		designXml.setContent(resultXml);
		design.setDesignAsXml(designXml);
	}
}
