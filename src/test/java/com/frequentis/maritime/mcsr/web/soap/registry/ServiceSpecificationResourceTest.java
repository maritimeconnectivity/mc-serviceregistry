package com.frequentis.maritime.mcsr.web.soap.registry;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlDTO;

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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@WithMockUser("test-user")
public class ServiceSpecificationResourceTest {
	Logger log = LoggerFactory.getLogger(ServiceSpecificationResourceTest.class);
	private static final String TOKEN = "";
	private static int specificationId = 0;

	@Autowired
	@Qualifier("serviceSpecificationResourceEndpoint")
	private Endpoint designResource;
	
	@Autowired
	private ServiceSpecificationResource serviceSpecificationResourceInternal;
	
	@LocalServerPort
	private int port;
	
	private ServiceSpecificationResource client;
	private static String xml;
	
	private static final String[] KEYWORDS = {"new", "neutral", "classical", "elemental", 
			"specialized", "broken", "fixed", "apropo", "manual", "France", "individual", 
			"steam", "stream", "soft", "hard", "critical"};
	
	private static final String[] STATUSES = {"new", "open", "closed"};
	
	@Test
	public void designResourceExist() {
		Assert.assertNotNull(designResource);
	}
	
	@BeforeClass
	public static void loadResources() throws IOException {
		DefaultResourceLoader rl = new DefaultResourceLoader();
		Resource resource = rl.getResource("classpath:dataload/xml/AddressForPersonLookupServiceSpecification.xml");
		xml = new String(Files.readAllBytes(resource.getFile().toPath()));
	}
	
	@Before
	public void setUp() throws MalformedURLException {
		URL wsdlUrl = new URL("http://localhost:" + port + "/services/ServiceSpecification?wsdl");
		Service s = Service.create(wsdlUrl, new QName("http://registry.soap.web.mcsr.maritime.frequentis.com/", "ServiceSpecificationResourceImplService"));
		
		client = s.getPort(ServiceSpecificationResource.class);
	}
	

	private SpecificationDTO prepareValidSpecificationDTO() {
		SpecificationDTO spec = new SpecificationDTO();
		int randNum = (int)(Math.random() * 1_000_000);
		
		spec.name = "Some Spec " + randNum;
		spec.name = "Name" + randNum;
		// Select four random words as keywords
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 4; i++) {
			int index = (int) (Math.random() * KEYWORDS.length);
			sb.append(KEYWORDS[index]);
			if(i != 3) {
				sb.append(", ");
			}
		}
		spec.keywords = sb.toString();
		spec.status = STATUSES[(int) Math.random() * STATUSES.length];
		spec.comment = "Comment for Name" + randNum;
		spec.version = "1.0.0";
		spec.specAsXml = new XmlDTO();
		spec.specAsXml.content = xml;
		spec.specAsXml.contentContentType = "application/xml";
		spec.specAsXml.name = "XML for Name " + randNum;
		spec.specAsXml.comment = "Some XML for some Specification";
		spec.specificationId = String.valueOf(++specificationId);
		
		return spec;
	}
	
	@Test
	public void createSuccess() throws URISyntaxException, Exception {
		// Given
		SpecificationDTO specificationDTO = prepareValidSpecificationDTO();
		long countBefore = serviceSpecificationResourceInternal.getAllSpecifications(0).itemTotalCount;
		
		// When
		SpecificationDescriptorDTO spec = client.createSpecification(specificationDTO, TOKEN);
		
		// Then
		assertEquals(specificationDTO.name, spec.name);
		assertEquals(specificationDTO.keywords, spec.keywords);
		assertEquals(specificationDTO.status, spec.status);
		assertEquals(specificationDTO.comment, spec.comment);
		long countAfter = serviceSpecificationResourceInternal.getAllSpecifications(0).itemTotalCount;
		assertEquals(countBefore + 1, countAfter);
		
	}
	
	@Test
	public void specificationCount() throws Exception {
		// Given
		long countBefore = client.getAllSpecifications(0).itemTotalCount;
		long plusItems = 0;
		
		// When
		for(int i = 0; i < 5; i++) {
			serviceSpecificationResourceInternal.createSpecification(prepareValidSpecificationDTO(), TOKEN);
			plusItems++;
		}
		
		// Then
		long countAfter = client.getAllSpecifications(0).itemTotalCount;
		assertEquals(countBefore + plusItems, countAfter);

	}
	
	@Test
	public void getSpecification() throws Exception {
		// Given
		SpecificationDTO specDTO = prepareValidSpecificationDTO();
		SpecificationDTO otherSpecDTO = prepareValidSpecificationDTO();
		otherSpecDTO.version = "2.0.0";
		otherSpecDTO.specificationId = specDTO.specificationId;
		
		// When
		serviceSpecificationResourceInternal.createSpecification(specDTO, TOKEN);
		
		// Then
		SpecificationDTO saved = client.getSpecification(specDTO.specificationId, specDTO.version);
		assertNotNull(saved);
		assertEquals(specDTO.name, saved.name);
		assertEquals(specDTO.comment, saved.comment);
		assertEquals(specDTO.status, saved.status);
		assertEquals(specDTO.specificationId, saved.specificationId);
		assertEquals(specDTO.version, saved.version);
		
	}
	
	@Test
	public void getAllSpecificationsById() throws Exception {
		// Given
		int countOfSpecifications = 5;
		SpecificationDTO template = prepareValidSpecificationDTO();
		SpecificationDescriptorDTO[] specifications = new SpecificationDescriptorDTO[countOfSpecifications];
		for(int i = 0; i < countOfSpecifications; i++) {
			template.version = "1.0." + i;
			template.name = "Adam v1.0." + i;
			specifications[i] = serviceSpecificationResourceInternal.createSpecification(template, TOKEN);
		}
		
		// When
		PageDTO<SpecificationDescriptorDTO> resultPage = client.getAllSpecificationsById(template.specificationId, 0);
		
		// Then
		assertEquals(countOfSpecifications, resultPage.itemTotalCount);
		Iterable<SpecificationDescriptorDTO> results = resultPage.content;
		for(int i = 0; i < countOfSpecifications; i++) {
			assertThat(results, hasItem(hasSpecification(specifications[i])));
		}
	}
	
	@Test
	public void deleteSpecification() throws Exception {
		// Given
		SpecificationDescriptorDTO spec = serviceSpecificationResourceInternal.createSpecification(prepareValidSpecificationDTO(), TOKEN);
		
		
		// When
		client.deleteSpecification(spec.specificationId, spec.version, TOKEN);
		
		// Then
		SpecificationDescriptorDTO result = serviceSpecificationResourceInternal.getSpecification(spec.specificationId, spec.version);
		assertNull(result);
		
	}
	
	@Test
	public void updateSpecificationStatus() throws IllegalAccessException, Exception {
		// Given
		String newStatus = "provisional";
		SpecificationDescriptorDTO spec = serviceSpecificationResourceInternal.createSpecification(prepareValidSpecificationDTO(), TOKEN);
		
		// When
		client.updateSpecificationStatus(spec.specificationId, spec.version, newStatus, TOKEN);
		
		// Then
		SpecificationDescriptorDTO result = serviceSpecificationResourceInternal.getSpecification(spec.specificationId, spec.version);
		assertEquals(newStatus, result.status);
	}
	
	@Test
	public void updateSpecification() throws Exception {
		// Given
		SpecificationDTO specDTO = prepareValidSpecificationDTO();
		SpecificationDescriptorDTO specDesc = serviceSpecificationResourceInternal.createSpecification(specDTO, TOKEN);
		// We need xml id
		SpecificationDTO updateDTO = serviceSpecificationResourceInternal.getSpecification(specDesc.specificationId, specDesc.version);
		
		for(int i = 0; i < 4; i++) {
			// When
			String newName = "NewName" + Math.random() * 10000;
			updateDTO.name = newName;
			client.updateSpecification(updateDTO, TOKEN);
			
			// Then
			SpecificationDTO saved = serviceSpecificationResourceInternal.getSpecification(specDTO.specificationId, specDTO.version);
			assertEquals(newName, updateDTO.name);
		}
	}
	
	@Test
	public void searchSpecification() throws Exception {
		// Given
		int resultCount = ServiceSpecificationResourceImpl.ITEMS_PER_PAGE + 4;
		String searchSpecNamePrefix = "searchSpecName_dawdwdam" + (int)(Math.random() * 30000) +"9ag79e2qeda_";
		SpecificationDTO newSpec = prepareValidSpecificationDTO();
		for(int i = 0; i < resultCount; i++) {
			newSpec = prepareValidSpecificationDTO();
			newSpec.name = searchSpecNamePrefix + (int) (Math.random() * 1000);
			serviceSpecificationResourceInternal.createSpecification(newSpec, TOKEN);
		}
		for(int i = 0; i < 12; i++) {
			// Random name data
			newSpec = prepareValidSpecificationDTO();
			newSpec.name = "searchSpecName_" + (int) (Math.random() * 1000);
			serviceSpecificationResourceInternal.createSpecification(newSpec, TOKEN);
		}
		
		// When
		PageDTO<?> results = client.searchSpecifications("name:" + searchSpecNamePrefix + "*", 0);
		PageDTO<?> results2 = client.searchSpecifications("name:" + searchSpecNamePrefix + "*", 1);
		
		// Then
		assertEquals(resultCount, results.itemTotalCount);
		assertEquals(ServiceSpecificationResourceImpl.ITEMS_PER_PAGE, results.content.size());
		assertEquals(4, results2.content.size());
		
	}
	
	
	private static Matcher<SpecificationDescriptorDTO> hasSpecification(SpecificationDescriptorDTO spec) {
		return new BaseMatcher<SpecificationDescriptorDTO>() {

			private SpecificationDescriptorDTO actSpec;
			
			@Override
			public boolean matches(Object item) {
				SpecificationDescriptorDTO sp = (SpecificationDescriptorDTO) item;
				actSpec = sp;
				if(sp == null) {
					return false;
				}

				return spec.equals(sp);
			}

			@Override
			public void describeTo(Description description) {
				if(actSpec == null) {
					description.appendText("specification should not be null");
					return;
				}
				description.appendValue("specification " + actSpec.name + " is not same as " + spec.name);
				
			}
		};

	}


}
