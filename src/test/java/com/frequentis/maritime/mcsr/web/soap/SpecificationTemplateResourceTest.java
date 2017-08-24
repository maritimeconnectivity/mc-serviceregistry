package com.frequentis.maritime.mcsr.web.soap;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;
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

import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.domain.Xsd;
import com.frequentis.maritime.mcsr.domain.enumeration.SpecificationTemplateType;
import com.frequentis.maritime.mcsr.service.DocService;
import com.frequentis.maritime.mcsr.service.XsdService;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocReference;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateParameterDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XsdReference;
import com.frequentis.maritime.mcsr.web.soap.errors.ProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
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
public class SpecificationTemplateResourceTest {
	Logger log = LoggerFactory.getLogger(SpecificationTemplateResourceTest.class);
	private static final int RANDOM_NAME_LENGTH = 12;
	private static int specificationVersion = 1;

	@Autowired
	private SpecificationTemplateResource internal;

	@Autowired
	private DocService docService;

	@Autowired
	private XsdService xsdService;

	@LocalServerPort
	private int port;

	private SpecificationTemplateResource client;

	static List<String> xmls;

	private List<DocReference> docs = new ArrayList<>();
	private List<XsdReference> xsds = new ArrayList<>();

	@Before
	public void setUp() throws MalformedURLException {
		URL wsdlUrl = new URL("http://localhost:" + port + "/services/SpecificationTemplateResource?wsdl");
		Service s = Service.create(wsdlUrl, new QName("http://soap.web.mcsr.maritime.frequentis.com/", "SpecificationTemplateResourceImplService"));

		client = s.getPort(SpecificationTemplateResource.class);
		SoapTestUtils.addHttpBasicSecurity(client);

		// PrepareDocs
		prepareDocs();
		prepareXsds();
	}

	private void prepareDocs() {
		// TODO Auto-generated method stub
		for(int i = 0; i < 6; i++) {
			String dr = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
			Doc d = new Doc();
			d.setName(dr);
			d.setComment(dr);
			d.setFilecontent(RandomStringUtils.randomAlphabetic(1024).getBytes());
			d.setFilecontentContentType("text/plain");
			d.setMimetype("text/plain");

			docService.save(d);
			DocReference dRef = new DocReference();
			dRef.id = d.getId();
			docs.add(dRef);
		}
	}

	private void prepareXsds() {
		// TODO Auto-generated method stub
		for(int i = 0; i < 6; i++) {
			String dr = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
			Xsd d = new Xsd();
			d.setName(dr);
			d.setComment(dr);
			d.setContent(RandomStringUtils.randomAlphabetic(1024).getBytes());
			d.setContentContentType("text/plain");

			xsdService.save(d);
			XsdReference dRef = new XsdReference();
			dRef.id = d.getId();
			xsds.add(dRef);
		}
	}

	private DocReference randomDoc() {
		return this.docs.get((int) (Math.random() * this.docs.size()));
	}

	private XsdReference randomXsd() {
		return this.xsds.get((int) (Math.random() * this.xsds.size()));
	}

	private SpecificationTemplateParameterDTO createSpecificationTempalte() {
		SpecificationTemplateParameterDTO specificationTemplate = new SpecificationTemplateParameterDTO();
		specificationTemplate.name = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		specificationTemplate.comment = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);;
		specificationTemplate.version = "1." + (specificationVersion++) + "." + (int) (Math.random() * 9);
		specificationTemplate.guidelineDoc = randomDoc();
		specificationTemplate.templateDoc = randomDoc();
		specificationTemplate.docs = new ArrayList<>(Arrays.asList(randomDoc(), randomDoc()));
		specificationTemplate.xsds = new ArrayList<>(Arrays.asList(randomXsd(), randomXsd()));
		SpecificationTemplateType [] types = SpecificationTemplateType.values();
		specificationTemplate.type = types[(int) (Math.random() * types.length)];

		return specificationTemplate;
	}

	@Test
	public void create() throws ProcessingException {
		// Given
		SpecificationTemplateParameterDTO newXsd = createSpecificationTempalte();

		// When
		SpecificationTemplateDescriptorDTO savedDocument = internal.createSpecificationTemplate(newXsd);

		// Then
		assertNotNull(savedDocument);
		assertEquals(newXsd.name, savedDocument.name);
		assertEquals(newXsd.comment, savedDocument.comment);
	}

	@Test
	public void getSpecificationTemplate() throws ProcessingException {
		// Given
		SpecificationTemplateParameterDTO newXsd = createSpecificationTempalte();
		newXsd.id = internal.createSpecificationTemplate(newXsd).id;
		// some other xsds
		for(int i = 0; i < 3; i++) {
			internal.createSpecificationTemplate(createSpecificationTempalte());
		}

		// When
		SpecificationTemplateDTO resultXsd = client.getSpecificationTemplate(newXsd.id);

		// Then
		assertNotNull(resultXsd);
		assertEquals(newXsd.name, resultXsd.name);
		assertEquals(newXsd.comment, resultXsd.comment);
		assertEquals(newXsd.docs.size(), resultXsd.docs.size());
		assertEquals(newXsd.guidelineDoc.id, resultXsd.guidelineDoc.id);

	}

	@Test
	public void getAllXsds() throws ProcessingException {
		// Given
		int instanceCount = 5;
		SpecificationTemplateDescriptorDTO [] xmls = new SpecificationTemplateDescriptorDTO[instanceCount];
		for(int i = 0; i < instanceCount; i++) {
			xmls[i] = internal.createSpecificationTemplate(createSpecificationTempalte());
		}

		// When
		List<SpecificationTemplateDescriptorDTO> storedXmls = new ArrayList<>();
		int page = 0;
		PageDTO<SpecificationTemplateDescriptorDTO> resultPage;
		do {
			resultPage = client.getAllSpecificationTemplates(page++);
			if(resultPage.content != null) {
				storedXmls.addAll(resultPage.content);
			}
		} while (resultPage.content != null && resultPage.page < resultPage.pageCount);

		// Then
		assertNotNull(resultPage.content);
		assertEquals(resultPage.itemTotalCount, storedXmls.size());
		for(SpecificationTemplateDescriptorDTO xml : xmls) {
			assertThat(storedXmls, hasItem(hasTemplate(xml)));
		}
	}

	@Test
	public void searchXml() throws ProcessingException {
		// Given
		SpecificationTemplateParameterDTO template = createSpecificationTempalte();
		String prefix = RandomStringUtils.randomAlphabetic(15);
		template.name = prefix + "_" + RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		internal.createSpecificationTemplate(template);
		template.name = prefix + "_" + RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		internal.createSpecificationTemplate(template);
		for(int i = 0; i < 4; i++) {
			internal.createSpecificationTemplate(createSpecificationTempalte());
		}

		// When
		PageDTO<SpecificationTemplateDescriptorDTO> resultPage = client.searchSpecificationTemplates("name:" + prefix + "*", 0);

		// Then
		assertNotNull(resultPage.content);
		assertEquals(2, resultPage.itemTotalCount);
	}

	@Test
	public void updateXsd() throws ProcessingException {
		// Given
		String oldName = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		String newName = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		SpecificationTemplateParameterDTO newXsd = createSpecificationTempalte();
		newXsd.name = oldName;
		newXsd.id = internal.createSpecificationTemplate(newXsd).id;

		// When
		newXsd.name = newName;
		client.updateSpecificationTemplate(newXsd);

		// Then
		SpecificationTemplateDTO storedXml = internal.getSpecificationTemplate(newXsd.id);
		assertEquals(newName, storedXml.name);
	}

	@Test
	public void deleteSpecificationTemplate() throws ProcessingException {
		// Given
		SpecificationTemplateDescriptorDTO newTemplate = internal.createSpecificationTemplate(createSpecificationTempalte());

		// When
		client.deleteSpecificationTemplate(newTemplate.id);

		// Then
		SpecificationTemplateDTO result = internal.getSpecificationTemplate(newTemplate.id);
		assertNull(result);
	}

	private static Matcher<SpecificationTemplateDescriptorDTO> hasTemplate(SpecificationTemplateDescriptorDTO doc) {
		return new BaseMatcher<SpecificationTemplateDescriptorDTO>() {

			private SpecificationTemplateDescriptorDTO actDoc;

			@Override
			public boolean matches(Object item) {
				SpecificationTemplateDescriptorDTO d = (SpecificationTemplateDescriptorDTO) item;
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
				description.appendValue("SpecificationTemplateDescriptor " + actDoc.name + " should be " + doc.name);

			}
		};

	}


	private static Matcher<SpecificationTemplateParameterDTO> hasTemplate(SpecificationTemplateParameterDTO doc) {
		return new BaseMatcher<SpecificationTemplateParameterDTO>() {

			private SpecificationTemplateDescriptorDTO actDoc;

			@Override
			public boolean matches(Object item) {
				SpecificationTemplateDescriptorDTO d = (SpecificationTemplateDescriptorDTO) item;
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
				description.appendValue("SpecificationTemplateDescriptor " + actDoc.name + " should be " + doc.name);

			}
		};

	}

}
