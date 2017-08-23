package com.frequentis.maritime.mcsr.web.soap;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XsdDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XsdDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.errors.ProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
public class XsdResourceTest {
	Logger log = LoggerFactory.getLogger(XsdResourceTest.class);
	private static final int RANDOM_NAME_LENGTH = 12;

	@Autowired
	@Qualifier("technicalInstanceResource")
	private Endpoint instanceResource;
	
	@Autowired
	private XsdResource internal;
	
	@LocalServerPort
	private int port;
	
	private XsdResource client;
	
	static List<String> xmls;
	
	@Before
	public void setUp() throws MalformedURLException {
		URL wsdlUrl = new URL("http://localhost:" + port + "/services/XsdResource?wsdl");
		Service s = Service.create(wsdlUrl, new QName("http://soap.web.mcsr.maritime.frequentis.com/", "XsdResourceImplService"));
		
		client = s.getPort(XsdResource.class);
		SoapTestUtils.addHttpBasicSecurity(client);
	}
	
	private XsdDTO createXsd() {
		XsdDTO xsd = new XsdDTO();
		xsd.name = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		xsd.comment = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);;
		xsd.contentContentType = "application/xml";
		xsd.content = RandomStringUtils.randomAlphabetic(1024).getBytes();
		
		return xsd;
	}

	@Test
	public void create() throws ProcessingException {
		// Given
		XsdDTO newXsd = createXsd();
		
		// When
		XsdDescriptorDTO savedDocument = client.createXsd(newXsd);
		
		// Then
		assertNotNull(savedDocument);
		assertEquals(newXsd.name, savedDocument.name);
		assertEquals(newXsd.comment, savedDocument.comment);
	}
	
	@Test
	public void getXsd() throws ProcessingException {
		// Given
		XsdDTO newXsd = createXsd();
		newXsd.id = internal.createXsd(newXsd).id;
		// some other xsds
		for(int i = 0; i < 3; i++) {
			internal.createXsd(createXsd());
		}
		
		// When
		XsdDTO resultXsd = client.getXsd(newXsd.id);
		
		// Then
		assertNotNull(resultXsd);
		assertEquals(newXsd.name, resultXsd.name);
		assertEquals(newXsd.comment, resultXsd.comment);
		assertEquals(newXsd.contentContentType, resultXsd.contentContentType);
		assertEquals(new String(newXsd.content), new String(resultXsd.content));
		
	}
	
	@Test
	public void getAllXsds() throws ProcessingException {
		// Given
		int instanceCount = 5;
		XsdDescriptorDTO [] xmls = new XsdDescriptorDTO[instanceCount];
		for(int i = 0; i < instanceCount; i++) {
			xmls[i] = internal.createXsd(createXsd());
		}
		
		// When
		List<XsdDescriptorDTO> storedXmls = new ArrayList<>();
		int page = 0;
		PageDTO<XsdDescriptorDTO> resultPage;
		do {
			resultPage = client.getAllXsds(page++);
			if(resultPage.content != null) {
				storedXmls.addAll(resultPage.content);
			}
		} while (resultPage.content != null && resultPage.page < resultPage.pageCount);
		
		// Then
		assertNotNull(resultPage.content);
		assertEquals(resultPage.itemTotalCount, storedXmls.size());
		for(XsdDescriptorDTO xml : xmls) {
			assertThat(storedXmls, hasItem(hasXsd(xml)));
		}
	}
	
	@Test
	public void searchXml() throws ProcessingException {
		// Given
		XsdDTO template = createXsd();
		String prefix = RandomStringUtils.randomAlphabetic(15);
		template.name = prefix + "_" + RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		internal.createXsd(template);
		template.name = prefix + "_" + RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		internal.createXsd(template);
		for(int i = 0; i < 4; i++) {
			internal.createXsd(createXsd());
		}
		
		// When
		PageDTO<XsdDescriptorDTO> resultPage = client.searchXsds("name:" + prefix + "*", 0);
		
		// Then
		assertNotNull(resultPage.content);
		assertEquals(2, resultPage.itemTotalCount);
		assertThat(resultPage.content, hasItem(hasXsd(template)));
	}
	
	@Test
	public void updateXsd() throws ProcessingException {
		// Given
		String oldName = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		String newName = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		XsdDTO newXsd = createXsd();
		newXsd.name = oldName;
		newXsd.id = internal.createXsd(newXsd).id;
		
		// When
		newXsd.name = newName;
		client.updateXsd(newXsd);
		
		// Then
		XsdDTO storedXml = internal.getXsd(newXsd.id);
		assertEquals(newName, storedXml.name);
	}
	
	@Test
	public void deleteXsd() throws ProcessingException {
		// Given
		XsdDescriptorDTO newXsd = internal.createXsd(createXsd());
		
		// When
		client.deleteXsd(newXsd.id);
		
		// Then
		XsdDTO result = internal.getXsd(newXsd.id);
		assertNull(result);
	}
	
	
	private static Matcher<XsdDescriptorDTO> hasXsd(XsdDescriptorDTO doc) {
		return new BaseMatcher<XsdDescriptorDTO>() {

			private XsdDescriptorDTO actDoc;
			
			@Override
			public boolean matches(Object item) {
				XsdDescriptorDTO d = (XsdDescriptorDTO) item;
				actDoc = d;
				if (!d.name.equals(doc.name)) {
					return false;
				}
				if (!d.comment.equals(doc.comment)) {
					return false;
				}
				return true;
			}

			@Override
			public void describeTo(Description description) {
				description.appendValue("Xsd " + actDoc.name + " should be " + doc.name);
				
			}
		};

	}

}
