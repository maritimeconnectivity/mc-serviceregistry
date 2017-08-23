package com.frequentis.maritime.mcsr.web.soap;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.design.DesignDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.errors.ProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
public class XmlResourceTest {
	Logger log = LoggerFactory.getLogger(XmlResourceTest.class);
	private static final int RANDOM_NAME_LENGTH = 12;

	@Autowired
	@Qualifier("technicalInstanceResource")
	private Endpoint instanceResource;

	@Autowired
	private XmlResource internal;

	@LocalServerPort
	private int port;

	private XmlResource client;

	static List<String> xmls;

	@BeforeClass
	public static void loadResources() throws IOException {
		DefaultResourceLoader rl = new DefaultResourceLoader();
		String [] xmlNames = {"AddressForPersonLookupServiceDesignREST", "AddressForPersonLookupServiceInstance", "AddressForPersonLookupServiceSpecification"};
		xmls = new ArrayList<String>(xmlNames.length);

		Resource resource;
		for(String xml : xmlNames) {
			resource = rl.getResource("classpath:dataload/xml/" + xml + ".xml");
			xmls.add(new String(Files.readAllBytes(resource.getFile().toPath())));
		}
	}

	@Before
	public void setUp() throws MalformedURLException {
		URL wsdlUrl = new URL("http://localhost:" + port + "/services/XmlResource?wsdl");
		Service s = Service.create(wsdlUrl, new QName("http://soap.web.mcsr.maritime.frequentis.com/", "XmlResourceImplService"));

		client = s.getPort(XmlResource.class);
		SoapTestUtils.addHttpBasicSecurity(client);
	}

	private XmlDTO createXml() {
		XmlDTO xml = new XmlDTO();
		xml.name = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		xml.comment = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);;
		xml.contentContentType = "application/xml";
		xml.content = xmls.get((int)(Math.random() * xmls.size()));

		return xml;
	}

	@Test
	public void create() throws ProcessingException {
		// Given
		XmlDTO newXml = createXml();

		// When
		XmlDescriptorDTO savedDocument = client.createXml(newXml);

		// Then
		assertNotNull(savedDocument);
		assertEquals(newXml.name, savedDocument.name);
		assertEquals(newXml.comment, savedDocument.comment);
	}

	@Test
	public void getXml() throws ProcessingException {
		// Given
		XmlDTO newXml = createXml();
		newXml.id = internal.createXml(newXml).id;
		// some other xmls
		for(int i = 0; i < 3; i++) {
			internal.createXml(createXml());
		}

		// When
		XmlDTO resultXml = client.getXml(newXml.id);

		// Then
		assertNotNull(resultXml);
		assertEquals(newXml.name, resultXml.name);
		assertEquals(newXml.comment, resultXml.comment);
		assertEquals(newXml.contentContentType, resultXml.contentContentType);
		assertEquals(newXml.content, resultXml.content);

	}

	@Test
	public void getAllXmls() throws ProcessingException {
		// Given
		int instanceCount = 5;
		XmlDescriptorDTO [] xmls = new XmlDescriptorDTO[instanceCount];
		for(int i = 0; i < instanceCount; i++) {
			xmls[i] = internal.createXml(createXml());
		}

		// When
		List<XmlDescriptorDTO> storedXmls = new ArrayList<>();
		int page = 0;
		PageDTO<XmlDescriptorDTO> resultPage;
		do {
			resultPage = client.getAllXmls(page++);
			if(resultPage.content != null) {
				storedXmls.addAll(resultPage.content);
			}
		} while (resultPage.content != null && resultPage.page < resultPage.pageCount);

		// Then
		assertNotNull(resultPage.content);
		assertEquals(resultPage.itemTotalCount, storedXmls.size());
		for(XmlDescriptorDTO xml : xmls) {
			assertThat(storedXmls, hasItem(hasXml(xml)));
		}
	}

	@Test
	public void searchXml() throws ProcessingException {
		// Given
		XmlDTO template = createXml();
		String prefix = RandomStringUtils.randomAlphabetic(15);
		template.name = prefix + "_" + RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		internal.createXml(template);
		template.name = prefix + "_" + RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		internal.createXml(template);
		for(int i = 0; i < 4; i++) {
			internal.createXml(createXml());
		}

		// When
		PageDTO<XmlDescriptorDTO> resultPage = client.searchXmls("name:" + prefix + "*", 0);

		// Then
		assertNotNull(resultPage.content);
		assertEquals(2, resultPage.itemTotalCount);
		assertThat(resultPage.content, hasItem(hasXml(template)));
	}

	@Test
	public void updateXml() throws ProcessingException {
		// Given
		String oldName = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		String newName = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		XmlDTO newXml = createXml();
		newXml.name = oldName;
		newXml.id = internal.createXml(newXml).id;

		// When
		newXml.name = newName;
		client.updateXml(newXml);

		// Then
		XmlDTO storedXml = internal.getXml(newXml.id);
		assertEquals(newName, storedXml.name);
	}

	@Test
	public void deleteXml() throws ProcessingException {
		// Given
		XmlDescriptorDTO newXml = internal.createXml(createXml());

		// When
		client.deleteXml(newXml.id);

		// Then
		XmlDTO result = internal.getXml(newXml.id);
		assertNull(result);
	}


	private static Matcher<XmlDescriptorDTO> hasXml(XmlDescriptorDTO doc) {
		return new BaseMatcher<XmlDescriptorDTO>() {

			private XmlDescriptorDTO actDoc;

			@Override
			public boolean matches(Object item) {
				XmlDescriptorDTO d = (XmlDescriptorDTO) item;
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
				description.appendValue("Xml " + actDoc.name + " should be " + doc.name);

			}
		};

	}

}
