package com.frequentis.maritime.mcsr.web.soap;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.domain.SpecificationTemplate;
import com.frequentis.maritime.mcsr.domain.enumeration.SpecificationTemplateType;
import com.frequentis.maritime.mcsr.service.DocService;
import com.frequentis.maritime.mcsr.service.SpecificationTemplateService;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocReference;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateReference;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateSetDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateSetDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateSetParameter;
import com.frequentis.maritime.mcsr.web.soap.errors.ProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
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
@WithMockUser("test-user")
public class SpecificationTemplateSetResourceTest {
	Logger log = LoggerFactory.getLogger(SpecificationTemplateSetResourceTest.class);
	private static final int RANDOM_NAME_LENGTH = 12;
	
	@Autowired
	private SpecificationTemplateSetResource internal;
	
	@Autowired
	DocService docService;
	
	@Autowired
	SpecificationTemplateService specificationTemplateService;
	
	@LocalServerPort
	private int port;
	
	private SpecificationTemplateSetResource client;
	
	static List<String> xmls;
	static List<DocReference> docs = new ArrayList<DocReference>();
	static List<SpecificationTemplateReference> templates = new ArrayList<SpecificationTemplateReference>();
	
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
		URL wsdlUrl = new URL("http://localhost:" + port + "/services/SpecificationTemplateSetResource?wsdl");
		Service s = Service.create(wsdlUrl, new QName("http://soap.web.mcsr.maritime.frequentis.com/", "SpecificationTemplateSetResourceImplService"));
		
		client = s.getPort(SpecificationTemplateSetResource.class);
		
		generateRandomDocs();
		generateRandomTemplates();
	}
	
	private void generateRandomDocs() {
		if(!docs.isEmpty()) {
			return;
		}
		for(int i = 0; i < 5; i++) {
			Doc d = new Doc();
			String rn = RandomStringUtils.randomAlphabetic(15);
			d.setName(rn);
			d.setComment(rn);
			d.setMimetype("text/plain");
			d.setFilecontentContentType("text/plain");
			d.setFilecontent(RandomStringUtils.randomAlphanumeric(1024).getBytes());
			
			docService.save(d);
			DocReference dr = new DocReference();
			dr.id = d.getId();
			docs.add(dr);
		}
		
	}
	
	private void generateRandomTemplates() {
		if(!templates.isEmpty()) {
			return;
		}
		for(int i = 0; i < 5; i++) {
			SpecificationTemplate d = new SpecificationTemplate();
			String rn = RandomStringUtils.randomAlphabetic(15);
			d.setName(rn);
			d.setComment(rn);
			d.setType(SpecificationTemplateType.DESIGN);
			d.setVersion(randomVersion());
			
			specificationTemplateService.save(d);
			SpecificationTemplateReference dr = new SpecificationTemplateReference();
			dr.id = d.getId();
			templates.add(dr);
		}
		
	}
	
	private SpecificationTemplateSetParameter createSpecificationTemplateSetParameter() {
		SpecificationTemplateSetParameter xml = new SpecificationTemplateSetParameter();
		xml.name = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		xml.comment = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);;
		xml.version = randomVersion();
	
		xml.docs.add(randomDoc());
		xml.docs.add(randomDoc());
		
		xml.templates.add(randomTemplate());
		
		return xml;
	}
	
	private SpecificationTemplateReference randomTemplate() {
		return templates.get((int) (Math.random() * templates.size()));
	}

	private DocReference randomDoc() {
		return docs.get((int) (Math.random() * docs.size()));
	}
	
	private String randomVersion() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 3; i++) {
				sb.append(randomVersionNumber());
			if(i != 2) {
				sb.append('.');
			}
		}
		return sb.toString();
	}
	
	private int randomVersionNumber() {
		return (int) (Math.random() * 10);
	}

	@Test
	public void create() throws ProcessingException {
		// Given
		SpecificationTemplateSetParameter newXml = createSpecificationTemplateSetParameter();
		
		// When
		SpecificationTemplateSetDescriptorDTO savedDocument = client.createSpecificationTemplateSet(newXml);
		
		// Then
		assertNotNull(savedDocument);
		assertEquals(newXml.name, savedDocument.name);
		assertEquals(newXml.comment, savedDocument.comment);
	}
	
	@Test
	public void getXml() throws ProcessingException {
		// Given
		SpecificationTemplateSetParameter newXml = createSpecificationTemplateSetParameter();
		newXml.id = internal.createSpecificationTemplateSet(newXml).id;
		// some other xmls
		for(int i = 0; i < 3; i++) {
			internal.createSpecificationTemplateSet(createSpecificationTemplateSetParameter());
		}
		
		// When
		SpecificationTemplateSetDTO resultXml = client.getSpecificationTemplateSet(newXml.id);
		
		// Then
		assertNotNull(resultXml);
		assertEquals(newXml.name, resultXml.name);
		assertEquals(newXml.comment, resultXml.comment);
		
	}
	
	@Test
	public void getAllXmls() throws ProcessingException {
		// Given
		int instanceCount = 5;
		SpecificationTemplateSetDescriptorDTO [] xmls = new SpecificationTemplateSetDescriptorDTO[instanceCount];
		for(int i = 0; i < instanceCount; i++) {
			xmls[i] = internal.createSpecificationTemplateSet(createSpecificationTemplateSetParameter());
		}
		
		// When
		List<SpecificationTemplateSetDescriptorDTO> storedXmls = new ArrayList<>();
		int page = 0;
		PageDTO<SpecificationTemplateSetDescriptorDTO> resultPage;
		do {
			resultPage = client.getAllSpecificationTemplateSets(page++);
			if(resultPage.content != null) {
				storedXmls.addAll(resultPage.content);
			}
		} while (resultPage.content != null && resultPage.page < resultPage.pageCount);
		
		// Then
		assertNotNull(resultPage.content);
		assertEquals(resultPage.itemTotalCount, storedXmls.size());
		for(SpecificationTemplateSetDescriptorDTO xml : xmls) {
			assertThat(storedXmls, hasItem(hasSpecificationTemplateSet(xml)));
		}
	}
	
	@Test
	public void searchXml() throws ProcessingException {
		// Given
		SpecificationTemplateSetParameter template = createSpecificationTemplateSetParameter();
		String prefix = RandomStringUtils.randomAlphabetic(15);
		template.name = prefix + "_" + RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		internal.createSpecificationTemplateSet(template);
		template.name = prefix + "_" + RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		internal.createSpecificationTemplateSet(template);
		for(int i = 0; i < 4; i++) {
			internal.createSpecificationTemplateSet(createSpecificationTemplateSetParameter());
		}
		
		// When
		PageDTO<SpecificationTemplateSetDescriptorDTO> resultPage = client.searchSpecificationTemplateSets("name:" + prefix + "*", 0);
		
		// Then
		assertNotNull(resultPage.content);
		assertEquals(2, resultPage.itemTotalCount);
	}
	
	@Test
	public void updateXml() throws ProcessingException {
		// Given
		String oldName = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		String newName = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		SpecificationTemplateSetParameter newXml = createSpecificationTemplateSetParameter();
		newXml.name = oldName;
		newXml.id = internal.createSpecificationTemplateSet(newXml).id;
		
		// When
		newXml.name = newName;
		client.updateSpecificationTemplateSet(newXml);
		
		// Then
		SpecificationTemplateSetDescriptorDTO storedXml = internal. getSpecificationTemplateSet(newXml.id);
		assertEquals(newName, storedXml.name);
	}
	
	@Test
	public void deleteXml() throws ProcessingException {
		// Given
		SpecificationTemplateSetDescriptorDTO newXml = internal.createSpecificationTemplateSet(createSpecificationTemplateSetParameter());
		
		// When
		client.deleteSpecificationTemplateSet(newXml.id);
		
		// Then
		SpecificationTemplateSetDescriptorDTO result = internal.getSpecificationTemplateSet(newXml.id);
		assertNull(result);
	}
	
	
	private static Matcher<SpecificationTemplateSetDescriptorDTO> hasSpecificationTemplateSet(SpecificationTemplateSetDescriptorDTO doc) {
		return new BaseMatcher<SpecificationTemplateSetDescriptorDTO>() {

			private SpecificationTemplateSetDescriptorDTO actDoc;
			
			@Override
			public boolean matches(Object item) {
				SpecificationTemplateSetDescriptorDTO d = (SpecificationTemplateSetDescriptorDTO) item;
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
				description.appendValue("SpecificationTemplateSet " + actDoc.name + " should be " + doc.name);
				
			}
		};

	}

}
