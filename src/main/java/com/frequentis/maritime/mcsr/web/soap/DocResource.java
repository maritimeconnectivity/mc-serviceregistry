package com.frequentis.maritime.mcsr.web.soap;

import java.util.List;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.web.soap.dto.DocDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.SearchData;

@WebService(targetNamespace = "http://target.namespace/", name = "Doc")
public interface DocResource {
	@WebMethod
	@WebResult(name = "documents")
	public List<DocDTO> getAllDocs();
	
	@WebMethod
	@Oneway
	public void createDoc(@WebParam(name = "document") @XmlElement(required = true) DocDTO doc);
	
	@WebMethod
	@Oneway
	public void updateDoc(@WebParam(name = "document") @XmlElement(required = true) DocDTO doc);
	
	@WebMethod
	@WebResult(name = "document")
	public DocDTO getDoc(@WebParam(name = "documentId") long id);
	
	@WebMethod
	@Oneway
	public void deleteDoc(@WebParam(name = "documentId") long id);
	
	@WebMethod
	@WebResult(name = "documents")
	public List<Doc> searchDocs(@WebParam(name = "searchdata") @XmlElement(required = true) SearchData searchdata);
}
