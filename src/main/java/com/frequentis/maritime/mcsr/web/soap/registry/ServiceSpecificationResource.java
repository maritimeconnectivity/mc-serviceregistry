package com.frequentis.maritime.mcsr.web.soap.registry;

import java.util.List;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.SearchData;
import com.frequentis.maritime.mcsr.web.soap.dto.SpecificationDTO;

@WebService(targetNamespace = "com.frequentis.maritime.mcsr.web.soap.registry.ServiceInstanceResource", name = "ServiceSpecification")
public interface ServiceSpecificationResource {

    @WebMethod
    public void createSpecification(
            @WebParam(name = "specification") @XmlElement(required = true) Specification specification,
            @WebParam(name = "bearerToken") @XmlElement(required = true) String bearerToken) throws Exception;

    @WebMethod
    public void updateSpecification(
            @WebParam(name = "specification") @XmlElement(required = true) Specification specification,
            @WebParam(name = "bearerToken") @XmlElement(required = true) String bearerToken) throws IllegalAccessException, Exception;

    @WebMethod
    @WebResult(name = "specifications")
    public PageDTO<SpecificationDTO> getAllSpecifications(
            @WebParam(name = "page") int page);

    @WebMethod
    @WebResult(name = "specification")
    public SpecificationDTO getSpecification(
            @WebParam(name = "specificationId") @XmlElement(required = true) String id,
            @WebParam(name = "version") @XmlElement(required = true) String version);

    @WebMethod
    @WebResult(name = "specifications")
    public PageDTO<SpecificationDTO> getAllSpecificationsById(
            @WebParam(name = "specificationId") @XmlElement(required = true) String id,
            @WebParam(name = "page") @XmlElement(required = true) int page);

    @WebMethod
    public void deleteSpecification(
            @WebParam(name = "specificationId") @XmlElement(required = true) String id,
            @WebParam(name = "version") @XmlElement(required = true) String version,
            @WebParam(name = "bearerToken") @XmlElement(required = true) String bearerToken) throws IllegalAccessException;

    @WebMethod
    @WebResult(name = "specifications")
    public PageDTO<SpecificationDTO> searchSpecifications(
            @WebParam(name = "searchData") @XmlElement(required = true) SearchData searchData);

    @WebMethod
    public void updateSpecificationStatus(
            @WebParam(name = "specificationId") @XmlElement(required = true) String id,
            @WebParam(name = "version") @XmlElement(required = true) String version,
            @WebParam(name = "status") @XmlElement(required = true) String status,
            @WebParam(name = "bearerToken") @XmlElement(required = true) String bearerToken) throws IllegalAccessException, Exception;

}
