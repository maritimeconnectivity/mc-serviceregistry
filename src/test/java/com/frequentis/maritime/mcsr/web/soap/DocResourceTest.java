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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "integration")
@WithMockUser("test-user")
public class DocResourceTest {
	Logger log = LoggerFactory.getLogger(DocResourceTest.class);
	private static final int RANDOM_NAME_LENGTH = 12;
	private static int instanceId = 0;

	@Autowired
	@Qualifier("technicalInstanceResource")
	private Endpoint instanceResource;
	
	@Autowired
	private DocResource internal;
	
	@LocalServerPort
	private int port;
	
	private DocResource client;
	
	
	@Before
	public void setUp() throws MalformedURLException {
		URL wsdlUrl = new URL("http://localhost:" + port + "/services/DocResource?wsdl");
		Service s = Service.create(wsdlUrl, new QName("http://soap.web.mcsr.maritime.frequentis.com/", "DocService"));
		
		client = s.getPort(DocResource.class);
	}
	
	private DocDTO createDocument() {
		DocDTO doc = new DocDTO();
		doc.name = RandomStringUtils.randomAlphanumeric(15);
		doc.comment = RandomStringUtils.randomAlphanumeric(10);
		doc.filecontent = RandomStringUtils.randomAlphanumeric(1024).getBytes();
		doc.filecontentContentType = "text/plain";
		doc.mimetype = "text/plain";
		
		return doc;
	}

	@Test
	public void create() {
		// Given
		DocDTO newDoc = createDocument();
		
		// When
		DocDescriptorDTO savedDocument = client.createDoc(newDoc);
		
		// Then
		assertNotNull(savedDocument);
		assertEquals(newDoc.name, savedDocument.name);
		assertEquals(newDoc.comment, savedDocument.comment);
		assertEquals(newDoc.mimetype, savedDocument.mimetype);
		
	}

	@Test
	public void getDocument() {
		// Given
		DocDTO newDoc = createDocument();
		DocDescriptorDTO doc = internal.createDoc(newDoc);
		for(int i = 0; i < 3; i++) {
			internal.createDoc(createDocument());
		}
		
		// When
		DocDTO storedDocument = client.getDoc(doc.id);
		
		// Then
		assertEquals(newDoc.name, storedDocument.name);
		assertEquals(newDoc.comment, storedDocument.comment);
		assertEquals(new String(newDoc.filecontent), new String(storedDocument.filecontent));
		assertEquals(newDoc.mimetype, storedDocument.mimetype);
		assertEquals(newDoc.filecontentContentType, storedDocument.filecontentContentType);
	}
	
	@Test
	public void getAllDocuments() {
		// Given
		int instanceCount = 4;
		DocDescriptorDTO [] docs = new DocDescriptorDTO[instanceCount];
		for(int i = 0; i < instanceCount; i++) {
			docs[i] = internal.createDoc(createDocument());
		}
		
		// When
		int page = 0;
		List<DocDescriptorDTO> storedDocs = new ArrayList<DocDescriptorDTO>();
		PageDTO<DocDescriptorDTO> resultPage = null;
		do {
			resultPage = client.getAllDocs(page++);
			if(resultPage.content != null) {
				storedDocs.addAll(resultPage.content);
			}
		} while (resultPage != null && resultPage.page < resultPage.pageCount);
		
		// Then
		for(int i = 0; i < instanceCount; i++) {
			assertThat(storedDocs, hasItem(hasDoc(docs[i])));
		}
	}
	
	@Test
	public void rmeoveDocument() {
		// Given
		long docCountBefore = internal.getAllDocs(0).itemTotalCount;
		DocDescriptorDTO doc = internal.createDoc(createDocument());
		
		// When
		client.deleteDoc(doc.id);
		
		// Then
		long docCountAfter = internal.getAllDocs(0).itemTotalCount;
		DocDTO docNull = internal.getDoc(doc.id);
		assertEquals(docCountBefore, docCountAfter);
		assertNull(docNull);
	}
	
	@Test
	public void searchDocument() {
		// Given
		DocDTO template = createDocument();
		String randomPrefix = RandomStringUtils.randomAlphanumeric(RANDOM_NAME_LENGTH);
		template.name = randomPrefix + "_" + RandomStringUtils.randomAlphanumeric(RANDOM_NAME_LENGTH);
		DocDescriptorDTO rightDoc = internal.createDoc(template);
		
		template.name = randomPrefix + "_" + RandomStringUtils.randomAlphanumeric(RANDOM_NAME_LENGTH);
		DocDescriptorDTO rightDoc2 = internal.createDoc(template);
		// some other docs
		for(int i = 0; i < 5; i++) {
			internal.createDoc(createDocument());
		}
		
		// When
		PageDTO<DocDescriptorDTO> resultPage = client.searchDocs("name:" + randomPrefix + "*", 0);
		
		// Then
		assertNotNull(resultPage);
		assertEquals(2, resultPage.itemTotalCount);
		assertThat(resultPage.content, hasItem(hasDoc(rightDoc)));
		
	}
	
	@Test
	public void updateDocument() {
		// Given
		DocDTO doc = createDocument();
		DocDescriptorDTO docBefore = internal.createDoc(doc);
		
		// When
		doc.id = docBefore.id;
		doc.name = RandomStringUtils.randomAlphabetic(RANDOM_NAME_LENGTH);
		client.updateDoc(doc);
		
		// Then
		DocDTO docAfter = internal.getDoc(docBefore.id);
		assertEquals(doc.name, docAfter.name);
	}
	
	private static Matcher<DocDescriptorDTO> hasDoc(DocDescriptorDTO doc) {
		return new BaseMatcher<DocDescriptorDTO>() {

			private DocDescriptorDTO actDoc;
			
			@Override
			public boolean matches(Object item) {
				DocDescriptorDTO d = (DocDescriptorDTO) item;
				actDoc = d;
				if (!d.name.equals(doc.name)) {
					return false;
				}
				if (!d.comment.equals(doc.comment)) {
					return false;
				}
				if (!d.mimetype.equals(doc.mimetype)) {
					return false;
				}
				return true;
			}

			@Override
			public void describeTo(Description description) {
				description.appendValue("Document " + actDoc.name + " should be " + doc.name);
				
			}
		};

	}

}
