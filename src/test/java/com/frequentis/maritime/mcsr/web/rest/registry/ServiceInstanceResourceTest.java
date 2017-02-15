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

package com.frequentis.maritime.mcsr.web.rest.registry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frequentis.maritime.mcsr.domain.Instance;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.web.rest.util.HeaderUtil;
import com.frequentis.maritime.mcsr.web.rest.util.InstanceUtil;
import com.frequentis.maritime.mcsr.web.rest.util.XmlUtil;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Test class for the ServiceInstanceResource REST controller.
 *
 * @see ServiceInstanceResource
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = McsrApp.class)
//@WebAppConfiguration
//@IntegrationTest
public class ServiceInstanceResourceTest {
/*
    @Inject
    private InstanceRepository instanceRepository;

    @Inject
    private InstanceService instanceService;

    @Inject
    private InstanceSearchRepository instanceSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restInstanceMockMvc;

    @Inject
    private ElasticsearchTemplate elasticsearchTemplate;

    private MockMvc restXmlMockMvc;

    @Inject
    private XmlRepository xmlRepository;

    @Inject
    private XmlService xmlService;

    @Inject
    private XmlSearchRepository xmlSearchRepository;

    private ServiceInstanceResource serviceInstanceResource;

    private Instance instance;
    */

    String wkt = "MULTIPOLYGON(((9.624023437500002 54.838663612975125,9.448242187500002 54.84498993218759,9.382324218750002 54.807017138462555,9.20654" +
        "2968750002 54.832336301970344,8.6572265625 54.90819859298938,8.536376953125 54.990221720048936,8.382568359375002 55.065786886591724," +
        "7.415771484375 55.19768334019969,5.778808593749998 55.528630522571916,5.44921875 55.24781504467555,5.185546875 55.24155203565252,4.7" +
        "57080078125 55.391592107033404,4.229736328125 55.76421316483771,3.3837890624999996 55.91227293006361,3.2739257812499996 56.090427143" +
        "99155,7.8662109375 57.48040333923342,8.887939453125 57.692405535264584,9.404296875 57.99063188288076,9.99755859375 58.26906557347328" +
        "4,10.535888671875 58.14751859907358,11.041259765625002 57.83305491291088,12.15087890625 56.5231395643722,12.10693359375 56.298253152" +
        "91387,12.384338378906248 56.20975914792473,12.634277343749996 56.058235955596075,12.664489746093746 56.015272531542365,12.6562499999" +
        "99998 55.91996893509676,12.711181640624998 55.82134464477079,12.892456054687496 55.64659898563684,12.878723144531248 55.607832700382" +
        "69,12.716674804687498 55.541064956111,12.7056884765625 55.48819145580225,12.617797851562498 55.41654360858007,12.6397705078125 55.28" +
        "5372382493534,12.7935791015625 55.15376626853558,13.062744140624998 55.06893234377864,13.1561279296875 55.01542594056298,12.93090820" +
        "3124998 54.82917227452137,12.7276611328125 54.76267040025496,12.453002929687498 54.680183097099984,12.117919921875 54.41573362292809" +
        ",11.942138671874996 54.36455818952146,11.678466796874998 54.35815677227373,11.321411132812498 54.56569261911193,11.118164062499996 5" +
        "4.62933821655574,10.925903320312498 54.63569730606386,10.739135742187498 54.54339315407256,10.623779296874998 54.54339315407256,10.3" +
        "60107421874998 54.62933821655574,10.184326171874998 54.77534585936445,10.057983398437496 54.77534585936445,9.876708984374998 54.8386" +
        "636129751,9.624023437500002 54.838663612975125)),((14.0020751953125 54.95869417101662,15.0457763671875 55.6930679264579,16.506958007" +
        "8125 55.363502833950776,14.633789062500002 54.53383250794428,14.414062499999998 54.65794628989232,14.3975830078125 54.81334841741929" +
        ",14.161376953124998 54.81334841741929,14.0020751953125 54.95869417101662)))";

    String geoJson = "{\"type\":\"MultiPolygon\",\"coordinates\":[[[[9.624023437500002,54.838663612975125],[9.448242187500002,54.84498993218759],[9.382324218750" +
        "002,54.807017138462555],[9.206542968750002,54.832336301970344],[8.6572265625,54.90819859298938],[8.536376953125,54.990221720048936]," +
        "[8.382568359375002,55.065786886591724],[7.415771484375,55.19768334019969],[5.778808593749998,55.528630522571916],[5.44921875,55.2478" +
        "1504467555],[5.185546875,55.24155203565252],[4.757080078125,55.391592107033404],[4.229736328125,55.76421316483771],[3.38378906249999" +
        "96,55.91227293006361],[3.2739257812499996,56.09042714399155],[7.8662109375,57.48040333923342],[8.887939453125,57.692405535264584],[9" +
        ".404296875,57.99063188288076],[9.99755859375,58.269065573473284],[10.535888671875,58.14751859907358],[11.041259765625002,57.83305491" +
        "291088],[12.15087890625,56.5231395643722],[12.10693359375,56.29825315291387],[12.384338378906248,56.20975914792473],[12.634277343749" +
        "996,56.058235955596075],[12.664489746093746,56.015272531542365],[12.656249999999998,55.91996893509676],[12.711181640624998,55.821344" +
        "64477079],[12.892456054687496,55.64659898563684],[12.878723144531248,55.60783270038269],[12.716674804687498,55.541064956111],[12.705" +
        "6884765625,55.48819145580225],[12.617797851562498,55.41654360858007],[12.6397705078125,55.285372382493534],[12.7935791015625,55.1537" +
        "6626853558],[13.062744140624998,55.06893234377864],[13.1561279296875,55.01542594056298],[12.930908203124998,54.82917227452137],[12.7" +
        "276611328125,54.76267040025496],[12.453002929687498,54.680183097099984],[12.117919921875,54.41573362292809],[11.942138671874996,54.3" +
        "6455818952146],[11.678466796874998,54.35815677227373],[11.321411132812498,54.56569261911193],[11.118164062499996,54.62933821655574]," +
        "[10.925903320312498,54.63569730606386],[10.739135742187498,54.54339315407256],[10.623779296874998,54.54339315407256],[10.36010742187" +
        "4998,54.62933821655574],[10.184326171874998,54.77534585936445],[10.057983398437496,54.77534585936445],[9.876708984374998,54.83866361" +
        "29751],[9.624023437500002,54.838663612975125]]],[[[14.0020751953125,54.95869417101662],[15.0457763671875,55.6930679264579],[16.50695" +
        "80078125,55.363502833950776],[14.633789062500002,54.53383250794428],[14.414062499999998,54.65794628989232],[14.3975830078125,54.8133" +
        "4841741929],[14.161376953124998,54.81334841741929],[14.0020751953125,54.95869417101662]]]]}";

    String unLoCodeTestMapping = "[{\"Change\":\"\",\"Country\":\"AD\",\"Location\":\"ALV\",\"Name\":\"Andorra la Vella\",\"NameWoD" +
        "iacritics\":\"Andorra la Vella\",\"Subdivision\":\"\",\"Status\":\"AI\",\"Function\":\"--34-6-" +
        "-\",\"Date\":\"0601\",\"IATA\":\"\",\"Coordinates\":\"4230N 00131E\",\"Remarks\":\"\"},{\"Change\":" +
        "\"\",\"Country\":\"AD\",\"Location\":\"CAN\",\"Name\":\"Canillo\",\"NameWoDiacritics\":\"Canillo\"" +
        ",\"Subdivision\":\"\",\"Status\":\"RL\",\"Function\":\"--3-----\",\"Date\":\"0307\",\"IATA\":\"\",\"C" +
        "oordinates\":\"4234N 00135E\",\"Remarks\":\"\"}]\n";

    String instance_name = "DMA NW-NM T P Maritime Cloud REST Service";
    String instance_id = "urn:mrnx:mcl:service:instance:dma:nw-nm";
    String instance_version = "0.1";
    String instance_status = "provisional";
    String instance_description = "A DMA instance of the NW-NM REST Service.";
    String instance_keywords = "NW, NM, Navigational Warnings, Notices to Mariners, MSI, Maritime Cloud Service. REST. Danish Maritime Authority, DMA";
    String instance_url = "http://niord.e-navigation.net/rest";

    String instanceXmlEmptyCoverage = "" +
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<ServiceInstanceSchema:serviceInstance xmlns:ServiceInstanceSchema=\"http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd\" xmlns:ServiceSpecificationSchema=\"http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceSpecificationSchema.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd ServiceInstanceSchema.xsd \">\n" +
        " <name>" + instance_name + "</name>\n" +
        " <id>" + instance_id + "</id>\n" +
        " <version>" + instance_version + "</version>\n" +
        " <status>"+ instance_status +"</status>\n" +
        " <description>" + instance_description + "</description>\n" +
        " <keywords>" + instance_keywords + "</keywords>\n" +
        " <URL>" + instance_url + "</URL>\n" +
        " <coversAreas>\n" +
        "  <coversArea>\n" +
        "   <name>Danish Maritime Waters</name>\n" +
        "   <description> Loosely defined region in the western part of the North Atlantic Ocean. </description>\n" +
        "   <geometryAsWKT></geometryAsWKT>\n" +
        "  </coversArea>\n" +
        " </coversAreas>\n" +
        "</ServiceInstanceSchema:serviceInstance>\n";

    String instanceXmlWithCoverage = "" +
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<ServiceInstanceSchema:serviceInstance xmlns:ServiceInstanceSchema=\"http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd\" xmlns:ServiceSpecificationSchema=\"http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceSpecificationSchema.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd ServiceInstanceSchema.xsd \">\n" +
        " <name>" + instance_name + "</name>\n" +
        " <id>" + instance_id + "</id>\n" +
        " <version>" + instance_version + "</version>\n" +
        " <status>"+ instance_status +"</status>\n" +
        " <description>" + instance_description + "</description>\n" +
        " <keywords>" + instance_keywords + "</keywords>\n" +
        " <URL>" + instance_url + "</URL>\n" +
        " <coversAreas>\n" +
        "  <coversArea>\n" +
        "   <name>Danish Maritime Waters</name>\n" +
        "   <description> Loosely defined region in the western part of the North Atlantic Ocean. </description>\n" +
        "   <geometryAsWKT>" + wkt + "</geometryAsWKT>\n" +
        "  </coversArea>\n" +
        " </coversAreas>\n" +
        "</ServiceInstanceSchema:serviceInstance>\n";

    String simpleTestXML = "" +
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<test>\n" +
        " <one>1</one>\n" +
        " <two>2</two>\n" +
        "</test>\n";

    @PostConstruct
    public void setup() throws Exception {

/*
        serviceInstanceResource = new ServiceInstanceResource();
        serviceInstanceResource.loadUnLoCodeMapping(new ByteArrayInputStream(unLoCodeTestMapping.getBytes(StandardCharsets.UTF_8)));
        MockitoAnnotations.initMocks(this);
        instanceSearchRepository.deleteAll();
        XmlResource xmlResource = new XmlResource();
        ReflectionTestUtils.setField(xmlResource, "xmlService", xmlService);
        this.restXmlMockMvc = MockMvcBuilders.standaloneSetup(xmlResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();

        InstanceResource instanceResource = new InstanceResource();
        ReflectionTestUtils.setField(instanceResource, "instanceService", instanceService);
        this.restInstanceMockMvc = MockMvcBuilders.standaloneSetup(instanceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
*/
    }

/*    @Before
    public void initTest() {
        xmlSearchRepository.deleteAll();
        instanceSearchRepository.deleteAll();
    }
*/
    @Test
    public void updateXmlNode() throws Exception {
        String newValue = "Bananas";
        String resultXml = XmlUtil.updateXmlNode(newValue, simpleTestXML, "/test/two");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(resultXml.getBytes(StandardCharsets.UTF_8)));
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();
        XPathExpression xPathExpression = xPath.compile("/test/two");
        String updatedValue = xPathExpression.evaluate(doc);

        assertThat(updatedValue.compareTo(newValue) == 0).isTrue();
    }

    @Test
    public void convertWKTtoGeoJson() throws Exception {
        ServiceInstanceResource serviceInstanceResource = new ServiceInstanceResource();
        InstanceUtil.loadUnLoCodeMapping(new ByteArrayInputStream(unLoCodeTestMapping.getBytes(StandardCharsets.UTF_8)));
        JsonNode jsonNode = InstanceUtil.convertWKTtoGeoJson(wkt);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode comparisonGeoJson = mapper.readTree(geoJson);

        assertThat(jsonNode.toString().compareTo(comparisonGeoJson.toString()) == 0).isTrue();
    }

    @Test
    public void applyUnLoCodeMapping() throws Exception {
        ServiceInstanceResource serviceInstanceResource = new ServiceInstanceResource();
        InstanceUtil.loadUnLoCodeMapping(new ByteArrayInputStream(unLoCodeTestMapping.getBytes(StandardCharsets.UTF_8)));
        Instance i = new Instance();
        Xml xml = new Xml();
        xml.setContent(instanceXmlEmptyCoverage);
        i.setInstanceAsXml(xml);
        InstanceUtil.applyUnLoCodeMapping(i, "AD CAN");
        assertThat(i.getGeometry().toString().length() > 0).isTrue();
        assertEquals("{\"type\":\"Point\",\"coordinates\":[1.35,42.34]}", i.getGeometry().toString());
    }

    @Test
    public void parseInstanceAttributesFromXML() throws Exception {
        ServiceInstanceResource serviceInstanceResource = new ServiceInstanceResource();
        InstanceUtil.loadUnLoCodeMapping(new ByteArrayInputStream(unLoCodeTestMapping.getBytes(StandardCharsets.UTF_8)));
        Instance i = new Instance();
        Xml xml = new Xml();
        xml.setContent(instanceXmlWithCoverage);
        i.setInstanceAsXml(xml);
        InstanceUtil.parseInstanceAttributesFromXML(i);

        assertThat(instance_name.equalsIgnoreCase(i.getName())).isTrue();
        assertThat(instance_version.equalsIgnoreCase(i.getVersion())).isTrue();
        assertThat(instance_id.equalsIgnoreCase(i.getInstanceId())).isTrue();
        assertThat(instance_description.equalsIgnoreCase(i.getComment())).isTrue();
        assertThat(instance_keywords.equalsIgnoreCase(i.getKeywords())).isTrue();

        InstanceUtil.parseInstanceGeometryFromXML(i);

        JsonNode jsonNode = InstanceUtil.convertWKTtoGeoJson(wkt);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode comparisonGeoJson = mapper.readTree(geoJson);

        assertThat(i.getGeometry().toString().length() > 0).isTrue();
        assertEquals(comparisonGeoJson.toString(),i.getGeometry().toString());
    }

    @Test
    public void parseOrganization() throws Exception {
        String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImp0aSI6IjE4OTFlZDVmLWNmZTktNGNkYy04ZjhiLWVhYWNkYzUwNWNiYSIsImlhdCI6MTQ4MTcwNjIyNywiZXhwIjoxNDgxNzExNzY2LCJvcmciOiJ0ZXN0In0.pqhTKJlDPmVAI-GjXUKdVY1fxDXaJ3UkOv3I7PCATBA";
        String organization = HeaderUtil.extractOrganizationIdFromToken(jwtToken);
        String expectedResult = "test";
        assertEquals(organization, expectedResult);
    }


/*    @Test
    public void searchGeometry() throws Exception{
        int databaseSizeBeforeUpdate = instanceRepository.findAll().size();
        Instance updatedInstance = new Instance();
        Xml xml = new Xml();
        xml.setName("xmlname");
        xml.setComment("xmlcomment");
        xml.setContent(instanceXmlWithCoverage);
        xml.setContentContentType("application/xml");

        int xmlDatabaseSizeBeforeCreate = xmlRepository.findAll().size();

        // Create the Xml
        restXmlMockMvc.perform(post("/api/xmls")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(xml)))
            .andExpect(status().isCreated());

        // Validate the Xml in the database
        List<Xml> xmls = xmlRepository.findAll();
        assertThat(xmls).hasSize(xmlDatabaseSizeBeforeCreate + 1);
        Xml testXml = xmls.get(xmls.size() - 1);

        updatedInstance.setInstanceAsXml(testXml);
        serviceInstanceResource.parseInstanceAttributesFromXML(updatedInstance);

        restInstanceMockMvc.perform(put("/api/instances")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedInstance)))
            .andExpect(status().isOk());

        List<Instance> instances = instanceRepository.findAll();
        assertThat(instances).hasSize(databaseSizeBeforeUpdate);
        Instance testInstance = instances.get(instances.size() - 1);

        Page<Instance> resultInstances = instanceService.findByLocation(14.0020751953125, 54.95869417101662, new PageRequest(0,1));
        System.out.println(resultInstances.getTotalElements());

    }
*/

}
