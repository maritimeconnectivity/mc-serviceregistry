package com.frequentis.maritime.mcsr.web.soap.registry;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frequentis.maritime.mcsr.web.soap.SoapTestUtils;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.design.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.design.DesignDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlDTO;
import com.frequentis.maritime.mcsr.web.soap.errors.XmlValidateException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
public class TechnicalDesignResourceTest {
	Logger log = LoggerFactory.getLogger(TechnicalDesignResourceTest.class);
	private static final String TOKEN = "";
	private static int usedDesignId = 0;

	@Autowired
	private TechnicalDesignResource designResourceInternal;

	@Autowired
	ElasticsearchTemplate est;

	@LocalServerPort
	private int port;

	private TechnicalDesignResource client;
	private static String xml;

	@BeforeClass
	public static void loadResources() throws IOException {
		DefaultResourceLoader rl = new DefaultResourceLoader();
		Resource resource = rl.getResource("classpath:dataload/xml/AddressForPersonLookupServiceDesignREST.xml");
		xml = new String(Files.readAllBytes(resource.getFile().toPath()));
	}

	@Before
	public void setUp() throws MalformedURLException {
		URL wsdlUrl = new URL("http://localhost:" + port + "/services/TechnicalDesignResource?wsdl");
		Service s = Service.create(wsdlUrl, new QName("http://registry.soap.web.mcsr.maritime.frequentis.com/", "TechnicalDesignResourceImplService"));

		client = s.getPort(TechnicalDesignResource.class);
		SoapTestUtils.addHttpBasicSecurity(client);
	}


	private DesignDTO prepareValidDesignDTO() {
		DesignDTO designDTO = new DesignDTO();
		designDTO.name = "Some design";
		designDTO.designAsXml = new XmlDTO();
		designDTO.designAsXml.name = "Some design XML";
		designDTO.designAsXml.content = xml;
		designDTO.designAsXml.contentContentType = "application/xml";
		designDTO.status = "closed";
		designDTO.version = "1.2.3";
		designDTO.designId = Integer.toString(++usedDesignId);
		designDTO.comment = "dwaadwadawd";
		return designDTO;
	}

	@Test
	public void createSuccess() throws URISyntaxException, Exception {
		DesignDTO designDTO = prepareValidDesignDTO();

		DesignDescriptorDTO createdDesign = client.createDesign(designDTO, TOKEN);
		assertThat(createdDesign.name, is(designDTO.name));
	}

	@Test
	public void documentCounts() throws XmlValidateException, Exception {
		// Given
		long count = designResourceInternal.getAllDesigns(0).itemTotalCount;

		// When
		designResourceInternal.createDesign(prepareValidDesignDTO(), TOKEN);
		long cl = client.getAllDesigns(0).itemTotalCount;

		// Then
		assertEquals(count + 1, cl);
	}

	@Test
	public void getDesign() throws XmlValidateException, Exception {
		// Given
		DesignDescriptorDTO design = designResourceInternal.createDesign(prepareValidDesignDTO(), TOKEN);

		// When
		DesignDTO storedDesign = client.getDesign(design.designId, design.version);

		// Then
		assertEquals(design.id, storedDesign.id);
		assertEquals(design.version, storedDesign.version);
		assertEquals(design.comment, storedDesign.comment);
		assertEquals(design.name, storedDesign.name);
	}

	@Test
	public void searchById() throws XmlValidateException, Exception {
		// Given
		designResourceInternal.createDesign(prepareValidDesignDTO(), TOKEN);
		designResourceInternal.createDesign(prepareValidDesignDTO(), TOKEN);
		designResourceInternal.createDesign(prepareValidDesignDTO(), TOKEN);
		designResourceInternal.createDesign(prepareValidDesignDTO(), TOKEN);
		DesignDescriptorDTO newDesign = designResourceInternal.createDesign(prepareValidDesignDTO(), TOKEN);

		// When
		PageDTO<DesignDescriptorDTO> searchResult = client.getAllDesignsById(newDesign.designId, 0);

		// Then
		assertNotNull(searchResult.content);
		assertEquals("ID must be same", newDesign.id, searchResult.content.get(0).id);
		assertEquals("NAME must be same", newDesign.name, searchResult.content.get(0).name);

	}

	@Test
	public void documentRemove() throws XmlValidateException, Exception {
		// Given
		long countBefore = designResourceInternal.getAllDesigns(0).itemTotalCount;
		DesignDescriptorDTO newDto = designResourceInternal.createDesign(prepareValidDesignDTO(), TOKEN);

		// When
		client.deleteDesign(newDto.designId, newDto.version, TOKEN);

		// Then
		long countAfter = designResourceInternal.getAllDesigns(0).itemTotalCount;
		assertEquals(countBefore, countAfter);
	}

	@Test
	public void getAllDocumentVersion() throws XmlValidateException, Exception {
		// Given
		DesignDTO version1 = prepareValidDesignDTO();
		 designResourceInternal.createDesign(version1, TOKEN);
		DesignDTO version2 = prepareValidDesignDTO();
		version2.designId = version1.designId;
		version2.version = "1.6.7";
		designResourceInternal.createDesign(version2, TOKEN);
		DesignDTO version3 = prepareValidDesignDTO();
		version3.designId = version1.designId;
		version3.version = "1.7.4";
		designResourceInternal.createDesign(version3, TOKEN);

		// When
		PageDTO<DesignDescriptorDTO> designDescriptor = client.getAllDesignsById(version1.designId, 0);
		for(DesignDescriptorDTO dd : designDescriptor.content) {
			log.error("WHAT? Design {} in version {} and designId {}", dd.id, dd.version, dd.designId);
		}

		// Then
		assertEquals(3, designDescriptor.itemTotalCount);
		assertThat(designDescriptor.content, hasItem(hasVersion("1.6.7")));
		assertThat(designDescriptor.content, hasItem(hasVersion("1.7.4")));
		assertThat(designDescriptor.content, hasItem(hasVersion("1.2.3")));

	}

	@Test
	public void updateStatus() throws XmlValidateException, Exception {
		// Given
		DesignDescriptorDTO design = designResourceInternal.createDesign(prepareValidDesignDTO(), TOKEN);
		String newStatus = "newSamleStatus";

		// When
		client.updateDesignStatus(design.designId, design.version, newStatus, TOKEN);

		// Then
		DesignDTO designAfter = client.getDesign(design.designId, design.version);
		assertEquals(newStatus, designAfter.status);

	}

	@Test
	public void updateDocument() throws XmlValidateException, Exception {
		// Given
		String originalName = "originalName";
		String newName = "newName";
		DesignDTO design = prepareValidDesignDTO();
		design.name = originalName;
		DesignDescriptorDTO saved = designResourceInternal.createDesign(design, TOKEN);


		// When
		DesignDTO obtainedDesign = designResourceInternal.getDesign(saved.designId, saved.version);
		obtainedDesign.name = "newName";
		client.updateDesign(obtainedDesign, TOKEN);

		// Then
		DesignDTO currentDesign = designResourceInternal.getDesign(design.designId, design.version);
		assertEquals(newName, currentDesign.name);
	}


	//@Test
	public void getAllDesignBySpecificationId() {
	    // TODO There should be some test for getting all designs by specification id
	}

	@Test
	public void searchDocument() throws XmlValidateException, Exception {
		// Given
		DesignDTO d;
		for(int i = 0; i < 5; i++) {
			int rand =  (int) (Math.random() * 3000);
			d = prepareValidDesignDTO();
			d.name = "customSearchADwdaxawsadwwDAWdwsxcxadwakjhJHKHWDWA" + rand;
			d.status = "customSearchADwdaxawsadwwDAWdwsxcxadwakjhJHKHWDWA" + rand;
			designResourceInternal.createDesign(d, TOKEN);
		}

		// When
		PageDTO<DesignDescriptorDTO> pdd = client.searchDesigns("name:customSearch*", 0);

		// Then
		assertEquals(5, pdd.itemTotalCount);

	}

	private static Matcher<DesignDescriptorDTO> hasVersion(String version) {
		return new BaseMatcher<DesignDescriptorDTO>() {

			private String actVersion;

			@Override
			public boolean matches(Object item) {
				DesignDescriptorDTO descriptor = (DesignDescriptorDTO) item;
				actVersion = descriptor.version;
				if(descriptor.version == null) {
					return false;
				}
				return descriptor.version.equals(version);
			}

			@Override
			public void describeTo(Description description) {
				description.appendValue("version " + actVersion + " but version should be " + version);

			}
		};

	}

}
