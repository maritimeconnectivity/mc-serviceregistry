package com.frequentis.maritime.mcsr.web.soap;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.service.DocService;
import com.frequentis.maritime.mcsr.web.soap.converters.doc.DocDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.doc.DocDescriptorDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDescriptorDTO;

@Component("docResourceSoap")
@Transactional
@WebService(serviceName="DocService", portName = "DocPort", endpointInterface = "com.frequentis.maritime.mcsr.web.soap.DocResource")
public class DocResourceImpl implements DocResource {
    Logger log = LoggerFactory.getLogger(DocResourceImpl.class);
    public static final int ITEMS_PER_PAGE = 50;

    @Autowired
    DocService docService;
    @Autowired
    DocDTOConverter docDTOConverter;
    @Autowired
    DocDescriptorDTOConverter docDescriptorDTOConverter;

    @Override
    public PageDTO<DocDescriptorDTO> getAllDocs(int page) {
        log.debug("SOAP request to get page {} of Docs", page);
        Page<Doc> pageResult = docService.findAll(PageRequest.of(page, ITEMS_PER_PAGE));

        // We shoud use DTO objects
        return PageResponse.buildFromPage(pageResult, docDescriptorDTOConverter);
    }

    @Override
    public DocDescriptorDTO createDoc(DocDTO doc) {
    	log.debug("SOAP request to create Doc : {}", doc);
        Doc newDoc = new Doc();
        newDoc.setFilecontent(doc.filecontent);
        newDoc.setComment(doc.comment);
        newDoc.setFilecontentContentType(doc.filecontentContentType);
        newDoc.setMimetype(doc.mimetype);
        newDoc.setName(doc.name);
        return docDescriptorDTOConverter.convert(docService.save(newDoc));
    }

    @Override
    public DocDescriptorDTO updateDoc(DocDTO doc) {
        log.debug("SOAP request to update Doc : {}", doc);
        Doc newDoc = new Doc();
        if (doc.id == null) {
            return createDoc(doc);
        }
        newDoc.setFilecontent(doc.filecontent);
        newDoc.setComment(doc.comment);
        newDoc.setFilecontentContentType(doc.filecontentContentType);
        newDoc.setMimetype(doc.mimetype);
        newDoc.setName(doc.name);
        newDoc.setId(doc.id);
        return docDescriptorDTOConverter.convert(docService.save(newDoc));
    }

    @Override
    public DocDTO getDoc(long id) {
        log.debug("SOAP request to get Doc with id {}", id);
        Doc doc = docService.findOne(id);
        return docDTOConverter.convert(doc);
    }

    @Override
    public void deleteDoc(long id) {
        log.debug("SOAP request to delete Doc : {}", id);
        docService.delete(id);
    }

    @Override
    public PageDTO<DocDescriptorDTO> searchDocs(String query, int page) {
        log.debug("REST request to search for a page of Docs for query {}", query);
        Page<Doc> pageResponse = docService.search(query, PageRequest.of(page, ITEMS_PER_PAGE));
        return PageResponse.buildFromPage(pageResponse, docDescriptorDTOConverter);
    }


}
