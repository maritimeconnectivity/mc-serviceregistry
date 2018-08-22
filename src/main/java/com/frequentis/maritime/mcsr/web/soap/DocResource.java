package com.frequentis.maritime.mcsr.web.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import org.apache.cxf.annotations.WSDLDocumentation;

import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.doc.DocDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.errors.ProcessingException;

@WebService(targetNamespace = "http://target.namespace/", name = "Doc")
public interface DocResource {
    @WebMethod
    @WebResult(name = "documents")
    @WSDLDocumentation("This method list all documents.")
    public PageDTO<DocDescriptorDTO> getAllDocs(@WebParam(name = "page") @XmlElement(required = true, defaultValue = "0") int page);

    @WebMethod
    @WSDLDocumentation("This method crate new document.")
    public DocDescriptorDTO createDoc(@WebParam(name = "document") @XmlElement(required = true) DocDTO doc) throws ProcessingException;

    @WebMethod
    @WSDLDocumentation("This method update the document.")
    public DocDescriptorDTO updateDoc(@WebParam(name = "document") @XmlElement(required = true) DocDTO doc) throws ProcessingException;

    @WebMethod
    @WebResult(name = "document")
    @WSDLDocumentation("Return concrete document by given id")
    public DocDTO getDoc(@WebParam(name = "documentId") long id);

    @WebMethod
    @WSDLDocumentation("Remove document by given id")
    public void deleteDoc(@WebParam(name = "documentId") long id);

    @WebMethod
    @WebResult(name = "documents")
    @WSDLDocumentation("Find documents")
    public PageDTO<DocDescriptorDTO> searchDocs(@WebParam(name = "query") @XmlElement(required = true) String query, @WebParam(name = "page") @XmlElement(required = false, defaultValue = "0") int page);
}
