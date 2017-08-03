package com.frequentis.maritime.mcsr.web.soap;



import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.service.DocService;
import com.frequentis.maritime.mcsr.web.soap.converters.Converter;
import com.frequentis.maritime.mcsr.web.soap.dto.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.SearchData;

@Component("docResourceSoap")
@Transactional
@WebService(serviceName="DocService", portName = "DocPort", endpointInterface = "com.frequentis.maritime.mcsr.web.soap.DocResource")
public class DocResourceImpl implements DocResource {
    Logger log = LoggerFactory.getLogger(DocResourceImpl.class);
    public static final int ITEMS_PER_PAGE = 50;

    @Autowired
    DocService docService;
    @Autowired
    Converter<Doc, DocDTO> docDTOConverter;

    @Override
    public List<DocDTO> getAllDocs() {
        log.debug("SOAP request to get a page of Docs");
        Pageable pageable = new PageRequest(0, ITEMS_PER_PAGE);
        Page<Doc> page = docService.findAll(pageable);

        // We shoud use DTO objects
        return new ArrayList<DocDTO>(docDTOConverter.convert(page.getContent()));
    }

    @Override
    public void createDoc(DocDTO doc) {
        Doc newDoc = new Doc();
        newDoc.setFilecontent(doc.filecontent);
        newDoc.setComment(doc.comment);
        newDoc.setFilecontentContentType(doc.filecontentContentType);
        newDoc.setMimetype(doc.mimetype);
        log.debug("SOAP request to create Doc : {}", doc);
        docService.save(newDoc);
    }

    @Override
    public void updateDoc(DocDTO doc) {
        log.debug("SOAP request to update Doc : {}", doc);
        Doc newDoc = new Doc();
        if (doc.id == null) {
            createDoc(doc);
        }
        docService.save(newDoc);
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
    public List<Doc> searchDocs(SearchData searchdata) {
        log.debug("REST request to search for a page of Docs for query {}", searchdata.query);
        Pageable pageable = new PageRequest(searchdata.page, ITEMS_PER_PAGE);
        Page<Doc> page = docService.search(searchdata.query, pageable);
        return page.getContent();
    }


}
