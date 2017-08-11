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

import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.web.soap.dto.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.DesignDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.SpecificationDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.XmlDTO;
import com.frequentis.maritime.mcsr.web.soap.errors.XmlValidateException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@WithMockUser("test-user")
public class ServiceSpecificationResourceTest {
	Logger log = LoggerFactory.getLogger(ServiceSpecificationResourceTest.class);
	private static final String TOKEN = "";
	private static int usedDesignId = 0;

	@Autowired
	@Qualifier("")
	private Endpoint designResource;
	
	@Autowired
	private ServiceSpecificationResource serviceSpecificationResourceInternal;
	
	@LocalServerPort
	private int port;
	
	private ServiceSpecificationResource client;
	private static String xml;
	
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
		return null;
	}
	
	@Test
	public void createSuccess() throws URISyntaxException, Exception {
		SpecificationDTO designDTO = prepareValidSpecificationDTO();
		
	}
	


}
