package com.frequentis.maritime.mcsr.web.soap.registry;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.specification.SpecificationDescriptorDTO;
import com.frequentis.maritime.mcsr.web.soap.errors.AccessDeniedException;

@WebService(targetNamespace = "com.frequentis.maritime.mcsr.web.soap.registry.ServiceInstanceResource", name = "ServiceSpecification")
public interface ServiceSpecificationResource {

    @WebMethod
    public SpecificationDescriptorDTO createSpecification(
            @WebParam(name = "specification") @XmlElement(required = true) SpecificationDTO specification) throws Exception;

    @WebMethod
    public SpecificationDescriptorDTO updateSpecification(
            @WebParam(name = "specification") @XmlElement(required = true) SpecificationDTO specification
            ) throws IllegalAccessException, Exception;

    @WebMethod
    @WebResult(name = "specifications")
    public PageDTO<SpecificationDescriptorDTO> getAllSpecifications(
            @WebParam(name = "page") int page);

    @WebMethod
    @WebResult(name = "specification")
    public SpecificationDTO getSpecification(
            @WebParam(name = "specificationId") @XmlElement(required = true) String id,
            @WebParam(name = "version") @XmlElement(required = true) String version);

    @WebMethod
    @WebResult(name = "specifications")
    public PageDTO<SpecificationDescriptorDTO> getAllSpecificationsById(
            @WebParam(name = "specificationId") @XmlElement(required = true) String id,
            @WebParam(name = "page") @XmlElement(required = true) int page);

    @WebMethod
    public void deleteSpecification(
            @WebParam(name = "specificationId") @XmlElement(required = true) String id,
            @WebParam(name = "version") @XmlElement(required = true) String version) throws IllegalAccessException, AccessDeniedException;

    @WebMethod
    @WebResult(name = "specifications")
    public PageDTO<SpecificationDescriptorDTO> searchSpecifications(
            @WebParam(name = "searchData") @XmlElement(required = true) String query,
            @WebParam(name = "page") @XmlElement(required = false, defaultValue = "0") int page);

    @WebMethod
    public void updateSpecificationStatus(
            @WebParam(name = "specificationId") @XmlElement(required = true) String id,
            @WebParam(name = "version") @XmlElement(required = true) String version,
            @WebParam(name = "status") @XmlElement(required = true) String status) throws IllegalAccessException, AccessDeniedException, Exception;

}
