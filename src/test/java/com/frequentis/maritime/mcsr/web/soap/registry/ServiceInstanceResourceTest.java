package com.frequentis.maritime.mcsr.web.soap.registry;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.domain.SpecificationTemplate;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.domain.enumeration.SpecificationTemplateType;
import com.frequentis.maritime.mcsr.service.DocService;
import com.frequentis.maritime.mcsr.service.SpecificationTemplateService;
import com.frequentis.maritime.mcsr.service.XmlService;
import com.frequentis.maritime.mcsr.web.soap.SoapTestUtils;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.design.DesignDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.design.DesignReference;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocReference;
import com.frequentis.maritime.mcsr.web.soap.dto.instance.InstanceDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.instance.InstanceParameterDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationTemplateReference;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.xml.XmlReference;
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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
public class ServiceInstanceResourceTest {
	Logger log = LoggerFactory.getLogger(ServiceInstanceResourceTest.class);
	private static final String TOKEN = "dwad45a4w5dw5a.urn:mrn:stm:org:carment.dw5a4d5wad5aw5d";
	private static int instanceId = 0;

	@Autowired
	@Qualifier("technicalInstanceResource")
	private Endpoint instanceResource;

	@Autowired
	private ServiceInstanceResource instanceInternal;

	@Autowired
	DocService docService;

	@Autowired
	XmlService xmlService;

	@Autowired
	SpecificationTemplateService specTemplateService;

	@LocalServerPort
	private int port;

	private ServiceInstanceResource client;
	private static String xml;

	private static final String[] KEYWORDS = { "new", "neutral", "classical", "elemental", "specialized", "broken",
	        "fixed", "apropo", "manual", "France", "individual", "steam", "stream", "soft", "hard", "critical" };

	private static final String[] STATUSES = { "provisional", "released", "deprecated", "deleted" };

	@BeforeClass
	public static void loadResources() throws IOException {
		DefaultResourceLoader rl = new DefaultResourceLoader();
		Resource resource = rl.getResource("classpath:dataload/xml/AddressForPersonLookupServiceInstance.xml");
		xml = new String(Files.readAllBytes(resource.getFile().toPath()));
	}

	@Before
	public void setUp() throws MalformedURLException {
		URL wsdlUrl = new URL("http://localhost:" + port + "/services/ServiceInstanceResource?wsdl");
		Service s = Service.create(wsdlUrl, new QName("http://registry.soap.web.mcsr.maritime.frequentis.com/",
		        "ServiceInstanceResourceImplService"));

		client = s.getPort(ServiceInstanceResource.class);
		SoapTestUtils.addHttpBasicSecurity(client);
	}

	private InstanceParameterDTO prepareValidWithXML() throws Exception {
		return prepareValidWithXML(new InstanceValues());
	}

	private class InstanceValues {
		public String name;
		public String instanceId;
		public String version;
		public String status;
		public String keywords;
		public String wktGeometry;
		public String unloCode;
		public SpecificationTemplateReference implementedSpecificationVersion;
		public List<DocReference> docs;
		public DocReference instanceAsDoc;
		public List<DesignReference> designs;

		public InstanceValues() {
			// Name
			name = randomStringGenerator(25) + "_" + (++ServiceInstanceResourceTest.this.instanceId);
			// Version
			version = String.valueOf((int) (Math.random() * 10)) + ".";
			version += String.valueOf((int) (Math.random() * 10)) + ".";
			version += String.valueOf((int) (Math.random() * 10));
			// InstanceId
			instanceId = randomStringGenerator(12) + "_" + String.valueOf(ServiceInstanceResourceTest.this.instanceId);
			// Status
			status = STATUSES[(int) Math.random() * STATUSES.length];
			// Keywords
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 4; i++) {
				int index = (int) (Math.random() * KEYWORDS.length);
				sb.append(KEYWORDS[index]);
				if (i != 3) {
					sb.append(", ");
				}
			}
			keywords = sb.toString();
			// UnloCode
			unloCode = "CZ";

			instanceAsDoc = createRandomDocReference(name);
			docs = new ArrayList<DocReference>();
			docs.add(createRandomDocReference(name + RandomStringUtils.randomAlphanumeric(10)));
			docs.add(createRandomDocReference(name + RandomStringUtils.randomAlphanumeric(10)));

			SpecificationTemplate st = new SpecificationTemplate();
			String rsn = RandomStringUtils.randomAlphanumeric(20);
			st.setName(rsn);
			st.setComment(rsn);
			st.setType(SpecificationTemplateType.INSTANCE);
			st.setVersion(rsn);
			specTemplateService.save(st);
			implementedSpecificationVersion = new SpecificationTemplateReference();
			implementedSpecificationVersion.id = st.getId();
		}

		@Transactional
		private DesignReference createRandomDesign(String name2) {
			Doc doc = docService.save(createRandomDoc(name2 + "dad"));

			Xml xml = new Xml();
			xml.setName(name2);
			xml.setComment("dwdw");
			xml.setContent("daddddwa");
			xml.setContentContentType("text/plain");
			xmlService.save(xml);

			Design designDTO = new Design();
			designDTO.setName("DesignFor" + name2);
			designDTO.setComment("caw");
			designDTO.setDesignAsDoc(doc);
			designDTO.setDesignAsXml(xml);
			designDTO.setStatus(STATUSES[0]);
			designDTO.setVersion("1.0." + (int) (Math.random() * 9));
			designDTO.setDesignId("dsg:" + randomStringGenerator(30));

			DesignReference dr = new DesignReference();
			dr.id = designDTO.getId();
			return dr;
		}

		public Doc createRandomDoc(String fn) {
			Doc nd = new Doc();
			nd.setName("GuidlineDoc fro " + fn);
			nd.setComment(randomStringGenerator(5));
			nd.setFilecontent("plain text".getBytes());
			nd.setFilecontentContentType("text/plain");
			nd.setMimetype("text/plain");

			return nd;
		}

		public DocReference createRandomDocReference(String fn) {
			Doc d = createRandomDoc(fn);
			docService.save(d);
			DocReference dr = new DocReference();
			dr.id = d.getId();
			return dr;
		}

		public DocReference createGuidlineDoc(String fn) {
			return createRandomDocReference(fn);
		}

		public InstanceValues copy() {
			InstanceValues iv = new InstanceValues();
			Field[] fields = this.getClass().getDeclaredFields();
			for (Field f : fields) {
				try {
					f.set(iv, f.get(this));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// nothing
				}
			}
			return iv;
		}
	}

	/**
	 *
	 * @param name
	 *            override service name from XML
	 * @return
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws XPathExpressionException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	private InstanceParameterDTO prepareValidWithXML(InstanceValues values) throws Exception {
		InstanceParameterDTO inst = new InstanceParameterDTO();
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(xml)));
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate("/*[local-name()='serviceInstance']/*[local-name()='name']", document,
		        XPathConstants.NODE);
		node.setTextContent(values.name);
		node = (Node) xpath.evaluate("/*[local-name()='serviceInstance']/*[local-name()='version']", document,
		        XPathConstants.NODE);
		node.setTextContent(values.version);
		if (values.wktGeometry != null) {
			node = (Node) xpath.evaluate(
			        "/*[local-name()='serviceInstance']/*[local-name()='coversAreas']/*[local-name()='coversArea']/*[local-name()='geometryAsWKT']",
			        document, XPathConstants.NODE);
			node.setTextContent(values.wktGeometry);
		}
		node = (Node) xpath.evaluate("/*[local-name()='serviceInstance']/*[local-name()='id']", document,
		        XPathConstants.NODE);
		node.setTextContent(values.instanceId);

		node = (Node) xpath.evaluate("/*[local-name()='serviceInstance']/*[local-name()='status']", document,
		        XPathConstants.NODE);
		node.setTextContent(values.status);

		node = (Node) xpath.evaluate("/*[local-name()='serviceInstance']/*[local-name()='keywords']", document,
		        XPathConstants.NODE);
		node.setTextContent(values.keywords);

		node = (Node) xpath.evaluate("/*[local-name()='serviceInstance']/*[local-name()='unLoCode']", document,
		        XPathConstants.NODE);
		if (node != null && values.unloCode != null) {
			node.setTextContent(values.unloCode);
		}

		DOMSource ds = new DOMSource(document);
		StringWriter sw = new StringWriter();
		StreamResult sr = new StreamResult(sw);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(ds, sr);

		inst.unlocode = values.unloCode;
		// Instance as XML
		Xml instanceAsXml = new Xml();
		instanceAsXml.setName("Xml for instance " + values.name);
		instanceAsXml.setComment("Some comment " + (int) Math.random() * 3000);
		instanceAsXml.setContentContentType("application/xml");
		instanceAsXml.setContent(sw.toString());
		xmlService.save(instanceAsXml);
		XmlReference xmlRef = new XmlReference();
		xmlRef.id = instanceAsXml.getId();
		inst.instanceAsXml = xmlRef;

		inst.instanceAsDoc = values.instanceAsDoc;
		return inst;
	}

	@Test
	public void createInstance() throws Exception {
		// Given
		InstanceValues iv = new InstanceValues();
		InstanceParameterDTO instance = prepareValidWithXML(iv);
		long itemCountBefore = instanceInternal.getAllInstances(false, 0).itemTotalCount;

		// When
		InstanceDTO saved = client.createInstance(instance);

		// Then
		assertEquals(iv.name, saved.name);
		assertEquals(iv.version, saved.version);
		long itemCountAfter = instanceInternal.getAllInstances(false, 0).itemTotalCount;
		assertEquals(itemCountBefore + 1, itemCountAfter);

	}

	@Test
	public void getInstance() throws ProcessingException, Exception {
		// Given
		InstanceValues iv = new InstanceValues();
		InstanceDTO instance = instanceInternal.createInstance(prepareValidWithXML(iv));
		for (int i = 0; i < 5; i++) {
			instanceInternal.createInstance(prepareValidWithXML());
		}

		// When
		InstanceDTO o = instanceInternal.getInstance(instance.instanceId, instance.version, true, true);
		InstanceDTO o2 = instanceInternal.getInstance(instance.instanceId, instance.version, false, true);

		// Then
		assertEquals(instance.name, o.name);
		assertEquals(instance.comment, o.comment);
		assertEquals(instance.keywords, o.keywords);

	}

	@Test
	public void getAllInstances() throws ProcessingException, Exception {
		// Given
		int instanceCount = 3;
		long startCount = instanceInternal.getAllInstances(false, 0).itemTotalCount;
		InstanceDTO[] instances = new InstanceDTO[instanceCount];
		for (int i = 0; i < instanceCount; i++) {
			instances[i] = instanceInternal.createInstance(prepareValidWithXML());
		}

		// When
		List<InstanceDTO> resultList = new ArrayList<>();
		PageDTO<InstanceDTO> pageOfInstances;
		int page = 0;
		do {
			pageOfInstances = client.getAllInstances(false, page);
			log.error("Page {} from {}", pageOfInstances.page, pageOfInstances.pageCount);
			resultList.addAll(pageOfInstances.content);
		} while (pageOfInstances != null && pageOfInstances.page < pageOfInstances.pageCount && page++ < 30);

		// Then
		long endCount = instanceInternal.getAllInstances(false, 0).itemTotalCount;
		assertEquals(startCount + instanceCount, endCount);
		for (int i = 0; i < instanceCount; i++) {
			assertThat(resultList, hasItem(hasInstance(instances[i])));
		}

	}

	@Test
	public void getAllInstancesById() throws Exception {
		// Given
		int itemCount = 3;
		String versionPrefix = "1.0.";
		InstanceParameterDTO template;
		InstanceValues iv = new InstanceValues();
		InstanceDTO[] instances = new InstanceDTO[itemCount];
		for (int i = 0; i < itemCount; i++) {
			iv.version = versionPrefix + i;
			template = prepareValidWithXML(iv);
			instances[i] = instanceInternal.createInstance(template);
			iv = new InstanceValues();
			iv.instanceId = instances[i].instanceId;
		}

		// When
		PageDTO<InstanceDTO> resultPage = client.getAllInstancesById(instances[0].instanceId, false, true, 0);

		// Then
		assertEquals(itemCount, resultPage.itemTotalCount);
		for (int i = 0; i < itemCount; i++) {
			assertThat(resultPage.content, hasItem(hasInstance(instances[i])));
		}
	}

	@Test
	public void deleteInstance() throws ProcessingException, Exception {
		// Given
		InstanceDTO inst = instanceInternal.createInstance(prepareValidWithXML());
		boolean exist = instanceInternal.getInstance(inst.instanceId, inst.version, false, true) != null;

		// When
		client.deleteInstance(inst.instanceId, inst.version);

		// Then
		boolean existAfter = instanceInternal.getInstance(inst.instanceId, inst.version, false, true) != null;
		assertEquals(true, exist);
		assertEquals(false, existAfter);

	}

	@Test
	public void searchInstance() throws Exception {
		// Given
		String prefix = "akd08u9i0_ed4e4d43e43_" + ((int) (Math.random() * 100000)) + "_kbkiww09x_";
		int instanceCount = 3;
		InstanceDTO[] instances = new InstanceDTO[instanceCount];
		for (int i = 0; i < instanceCount; i++) {
			InstanceValues iv = new InstanceValues();
			iv.name = prefix + i;
			instances[i] = instanceInternal.createInstance(prepareValidWithXML(iv));
			instanceInternal.createInstance(prepareValidWithXML());
		}

		// When
		long count = client.searchInstances("name:" + prefix + "*", false, true, 0).itemTotalCount;
		// Then
		assertEquals(instanceCount, count);
	}

	@Test
	public void searchInstancesByKeywords() throws Exception {
		// Given
		String customKeyword = randomStringGenerator(12);

		int instanceCount = 3;
		InstanceDTO[] instances = new InstanceDTO[instanceCount];
		for (int i = 0; i < instanceCount; i++) {
			InstanceValues iv = new InstanceValues();
			iv.keywords = "created adam " + customKeyword;
			InstanceParameterDTO instPar = prepareValidWithXML(iv);
			instances[i] = instanceInternal.createInstance(instPar);
			instanceInternal.createInstance(prepareValidWithXML());
		}
		// When
		PageDTO<InstanceDTO> result = client.searchInstancesByKeywords(customKeyword, false, true, 0);
		// Then
		assertEquals(instanceCount, result.itemTotalCount);
	}

	@Test
	public void searchInstancesByUnlocode() throws Exception {
		// Given
		String randomPrefix = randomStringGenerator(5);
		InstanceValues in = new InstanceValues();
		in.unloCode = randomPrefix + "FR";
		in.name += " FR";
		InstanceParameterDTO instance = prepareValidWithXML(in);
		InstanceDTO franceInstance = instanceInternal.createInstance(instance);
		InstanceValues oi = new InstanceValues();
		oi.unloCode = randomPrefix + "DE";
		oi.name += " DE";
		instanceInternal.createInstance(prepareValidWithXML(oi));
		oi = new InstanceValues();
		oi.unloCode = randomPrefix + "PE";
		oi.name += " PE";
		instanceInternal.createInstance(prepareValidWithXML(oi));

		// When
		PageDTO<InstanceDTO> result = client.searchInstancesByUnlocode(randomPrefix + "FR", false, true, 0);

		// Then
		assertEquals(1, result.itemTotalCount);
		assertEquals(in.name, result.content.get(0).name);
		assertEquals(in.unloCode, result.content.get(0).unlocode);

	}

	@Test
	public void searchInstancesByLocation() throws Exception {
		// Given
		InstanceValues iv = new InstanceValues();
		InstanceParameterDTO instance = prepareValidWithXML(iv);
		instanceInternal.createInstance(instance);
		for (int i = 0; i < 3; i++) {
			instanceInternal.createInstance(prepareValidWithXML());
		}

		// When
		PageDTO<InstanceDTO> result = client.searchInstancesByLocation("26.31311263768267", "-70.048828125",
		        "name:" + iv.name, false, true, 0);

		// Then
		assertEquals(1, result.itemTotalCount);
	}

	@Test
	public void searchInstancesByGeometryGeojson() throws ProcessingException, Exception {
		// Given
		InstanceValues iv = new InstanceValues();
		iv.wktGeometry = "POINT(14.42024230957031 50.08666612902112)";
		instanceInternal.createInstance(prepareValidWithXML(iv));
		for (int i = 0; i < 3; i++) {
			instanceInternal.createInstance(prepareValidWithXML());
		}
		String polygon = "{ \"type\": \"Polygon\", \"coordinates\": "
		        + "[ [ [ 14.379215240478514, 50.07912075533354 ], " + "[ 14.467706680297852, 50.0696460498075 ], "
		        + "[ 14.474401473999022, 50.111441846083586 ], " + "[ 14.397068023681642, 50.11089141378986 ], "
		        + "[ 14.379215240478514, 50.07912075533354 ] ] ] }";

		String badPolygon = "{ \"type\": \"Polygon\", \"coordinates\": "
		        + "[ [ [ 14.372949600219725, 50.11711093052596 ], " + "[ 14.354925155639648, 50.07526499644457 ], "
		        + "[ 14.411916732788084, 50.11573500084129 ], " + "[ 14.372949600219725, 50.11711093052596 ] ] ] }";

		// When
		PageDTO<InstanceDTO> resultPage = client.searchInstancesByGeometryGeojson(polygon, "name:" + iv.name, false, true, 0);
		PageDTO<InstanceDTO> badResultPage = client.searchInstancesByGeometryGeojson(badPolygon, "name:" + iv.name,
		        false, true, 0);

		// Then
		assertEquals(1, resultPage.itemTotalCount);
		assertEquals(0, badResultPage.itemTotalCount);
		assertEquals(iv.name, resultPage.content.get(0).name);

	}

	@Test
	public void searchInstancesByGeometryWKT() throws ProcessingException, Exception {
		// Given
		InstanceValues iv = new InstanceValues();
		iv.wktGeometry = "POINT(14.42024230957031 50.08666612902112)";
		instanceInternal.createInstance(prepareValidWithXML(iv));
		for (int i = 0; i < 3; i++) {
			instanceInternal.createInstance(prepareValidWithXML());
		}
		String polygon = "POLYGON((" + "14.415714740753174 50.08683134289367," + "14.42206621170044 50.08995653136852,"
		        + "14.425349235534668 50.0859088914852," + "14.416379928588867 50.08242544472934,"
		        + "14.415714740753174 50.08683134289367))";

		String badPolygon = "POLYGON((" + "14.421186447143555 50.096330182758116,"
		        + "14.403848648071289 50.086776271666096," + "14.403204917907715 50.09652289335359,"
		        + "14.421186447143555 50.096330182758116))";

		// When
		PageDTO<InstanceDTO> resultPage = client.searchInstancesByGeometryWKT(polygon, "name:" + iv.name, false, true, 0);
		PageDTO<InstanceDTO> badResultPage = client.searchInstancesByGeometryWKT(badPolygon, "name:" + iv.name, false, true,
		        0);

		// Then
		assertEquals(1, resultPage.itemTotalCount);
		assertEquals(0, badResultPage.itemTotalCount);
		assertEquals(iv.name, resultPage.content.get(0).name);

	}

	private static String randomStringGenerator(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int type = (int) (Math.random() * 3);
			switch (type) {
			case 0:
				sb.append((char) (65 + Math.random() * 25));
				break;
			case 1:
				sb.append((char) (97 + Math.random() * 25));
				break;
			default:
				sb.append((int) (Math.random() * 9));
				break;
			}
		}
		return sb.toString();
	}

	private static Matcher<InstanceDTO> hasInstance(InstanceDTO instance) {
		return new BaseMatcher<InstanceDTO>() {

			private InstanceDTO actInstance;

			@Override
			public boolean matches(Object item) {
				InstanceDTO in = (InstanceDTO) item;
				actInstance = in;
				if (!in.name.equals(instance.name)) {
					return false;
				}
				if (!in.comment.equals(instance.comment)) {
					return false;
				}
				if (!in.keywords.equals(instance.keywords)) {
					return false;
				}
				if (!in.version.equals(instance.version)) {
					return false;
				}

				return true;
			}

			@Override
			public void describeTo(Description description) {
				if (actInstance == null) {
					description.appendText("Instance should not be null");
				}
				description.appendValue(
				        "Instance " + prepareString(actInstance) + " should be " + prepareString(instance));

			}

			private String prepareString(InstanceDTO inst) {
				return String.format("InstanceDTO {name = %s, version = %s, comment = %s, keyword = %s}", inst.name,
				        inst.version, inst.comment, inst.keywords);
			}
		};

	}

}
