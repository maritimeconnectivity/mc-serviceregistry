package com.frequentis.maritime.mcsr.web.soap.registry;

import javax.inject.Inject;
import javax.jws.WebService;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frequentis.maritime.mcsr.domain.Design;
import com.frequentis.maritime.mcsr.domain.Doc;
import com.frequentis.maritime.mcsr.domain.Instance;
import com.frequentis.maritime.mcsr.domain.Specification;
import com.frequentis.maritime.mcsr.service.DesignService;
import com.frequentis.maritime.mcsr.service.DocService;
import com.frequentis.maritime.mcsr.service.InstanceService;
import com.frequentis.maritime.mcsr.service.XmlService;
import com.frequentis.maritime.mcsr.web.rest.util.InstanceUtil;
import com.frequentis.maritime.mcsr.web.rest.util.XmlUtil;
import com.frequentis.maritime.mcsr.web.soap.PageResponse;
import com.frequentis.maritime.mcsr.web.soap.converters.Converter;
import com.frequentis.maritime.mcsr.web.soap.converters.instance.InstanceDTOConverter;
import com.frequentis.maritime.mcsr.web.soap.converters.instance.InstanceParameterDTOToInstanceConverter;
import com.frequentis.maritime.mcsr.web.soap.dto.PageDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.instance.InstanceDTO;
import com.frequentis.maritime.mcsr.web.soap.dto.instance.InstanceParameterDTO;
import com.frequentis.maritime.mcsr.web.soap.errors.AccessDeniedException;
import com.frequentis.maritime.mcsr.web.soap.errors.InstanceAlreadyExistException;
import com.frequentis.maritime.mcsr.web.soap.errors.ProcessingException;
import com.frequentis.maritime.mcsr.web.soap.errors.XmlValidateException;
import com.frequentis.maritime.mcsr.web.util.WebUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

@Component("serviceInstanceResourceSoap")
@Transactional
@WebService(endpointInterface = "com.frequentis.maritime.mcsr.web.soap.registry.ServiceInstanceResource")
@Secured("ROLE_USER")
public class ServiceInstanceResourceImpl implements ServiceInstanceResource {
	private static final String SCHEMA_SERVICE_INSTANCE = "ServiceInstanceSchema.xsd";
	private static final int ITEMS_PER_PAGE = 10;
	Logger log = LoggerFactory.getLogger(ServiceInstanceResourceImpl.class);
	
	@Inject
	InstanceService instanceService;
	
	@Inject
	DesignService designService;
	
	@Inject
	InstanceDTOConverter instanceDtoConverter;
	
	@Inject
	InstanceParameterDTOToInstanceConverter instanceParameterConverter;
	

	@Override
	public InstanceDTO createInstance(InstanceParameterDTO instanceDto, String bearerToken) 
			throws AccessDeniedException, InstanceAlreadyExistException, XmlValidateException, ProcessingException {
		log.debug("SOAP request to create instance");
		String organizationId = WebUtils.extractOrganizationIdFromToken(bearerToken, log);
		if(instanceDto.id != null) {
			throw new InstanceAlreadyExistException("A new instance cannot already have an ID");
		}
		Instance instance = instanceParameterConverter.convert(instanceDto);
		
		if(instance.getInstanceAsXml() == null || instance.getInstanceAsXml().getContent() == null) {
			throw new XmlValidateException("Instance must be created as XML (instanceAsXml must not be null)");
		}

		if(instance.getInstanceAsXml() != null && instance.getInstanceAsXml().getContent() != null) {
			String xml = instance.getInstanceAsXml().getContent().toString();
			log.info("XML: " + xml);
			try {
				XmlUtil.validateXml(xml, SCHEMA_SERVICE_INSTANCE);
				instance = InstanceUtil.parseInstanceAttributesFromXML(instance);
			} catch (Exception e) {
				throw new XmlValidateException(e.getMessage(), e);
			}
		}
		instance.setOrganizationId(organizationId);
		
		// Why? It's so ugly (based on REST service implementation)
        if (instance.getDesigns() != null && instance.getDesigns().size() > 0) {
            Design design = instance.getDesigns().iterator().next();
            log.error("Design {}", design);
            if (design != null) {
            	// We need reference ID
            	designService.save(design);
                instance.setDesignId(design.getDesignId());
                if (design.getSpecifications() != null && design.getSpecifications().size()> 0) {
                    Specification specification = design.getSpecifications().iterator().next();
                    if (specification != null) {
                        instance.setSpecificationId(specification.getSpecificationId());
                        
                    }
                }
            }
        }
      
        Instance result = instanceService.save(instance);
        if(result.getInstanceAsXml() != null && result.getInstanceAsXml().getContent() != null) {
	        try {
	        	result = InstanceUtil.parseInstanceGeometryFromXML(result);
	        } catch (Exception e) {
	        	throw new XmlValidateException(e.getMessage(), e);
	        }
        }
        
        // saveGeometry must be call even thought geometry is null (design decision?)
        try {
			instanceService.saveGeometry(result);
		} catch (Exception e) {
			throw new ProcessingException(e.getMessage());
		}
        
        return instanceDtoConverter.convert(result);
	}

	@Override
	public InstanceDTO updateInstance(InstanceParameterDTO instanceDto, String bearerToken)
			throws AccessDeniedException, XmlValidateException, InstanceAlreadyExistException, ProcessingException {
		log.debug("SOAP request to update instance");
		if (instanceDto.id == null) {
			return createInstance(instanceDto, bearerToken);
		}
		Instance instance = instanceParameterConverter.convert(instanceDto);
		
		if (instance.getInstanceAsXml() == null || instance.getInstanceAsXml().getContent() == null) {
			throw new XmlValidateException("Instance must be created as XML (instanceAsXml must not be null)");
		}
		String xml = instance.getInstanceAsXml().getContent().toString();
		log.info("XML: " + xml);
		try {
			XmlUtil.validateXml(xml, SCHEMA_SERVICE_INSTANCE);
			instance = InstanceUtil.parseInstanceAttributesFromXML(instance);
		} catch (Exception e) {
			throw new XmlValidateException(e.getMessage(), e);
		}
		
		String organizationId = WebUtils.extractOrganizationIdFromToken(bearerToken, log);
		if (!InstanceUtil.checkOrganizationId(instance, organizationId)) {
			String msg = "Cannot update entity, organization ID "+organizationId+" does not match that of entity: "+instance.getOrganizationId();
            log.warn(msg);
            throw new AccessDeniedException(msg);
		}
		
		// Why? It's so ugly (based on REST service implementation)
        if (instance.getDesigns() != null && instance.getDesigns().size() > 0) {
            Design design = instance.getDesigns().iterator().next();
            if (design != null) {
                instance.setDesignId(design.getDesignId());
                if (design.getSpecifications() != null && design.getSpecifications().size()> 0) {
                    Specification specification = design.getSpecifications().iterator().next();
                    if (specification != null) {
                        instance.setSpecificationId(specification.getSpecificationId());
                    }
                }
            }
        }
        
        Instance result = instanceService.save(instance);
        try {
	        result = InstanceUtil.parseInstanceGeometryFromXML(result);
	        instanceService.saveGeometry(result);        
        } catch (Exception e) {
        	throw new XmlValidateException(e.getMessage(), e);
        }
		
        return instanceDtoConverter.convert(result);
	}

	@Override
	public PageDTO<InstanceDTO> getAllInstances(boolean includeDoc, int page) {
		log.debug("SOAP request to get {} page of instances based on includeDoc {}", page, includeDoc);
		Page<Instance> pageOfInstances = instanceService.findAll(PageRequest.of(page, ITEMS_PER_PAGE));
		if(pageOfInstances != null && pageOfInstances.getContent() != null && !includeDoc) {
			for (Instance instance : pageOfInstances.getContent()) {
				instance.setDocs(null);
				instance.setInstanceAsDoc(null);
			}
		}
		return PageResponse.buildFromPage(pageOfInstances, instanceDtoConverter);
	}

	@Override
	public InstanceDTO getInstance(String id, String version, boolean includeDoc) {
		log.debug("SOAP request to get Instance via domain id {} and version {}", id, version);
		Instance instance = null;
		if(version.equalsIgnoreCase("latest")) {
			instance = instanceService.findLatestVersionByDomainId(id);
		} else {
			instance = instanceService.findByDomainId(id, version);
		}
		if (instance != null && !includeDoc) {
			instance.setDocs(null);
			instance.setInstanceAsDoc(null);
		}
		
		return instanceDtoConverter.convert(instance);
	}

	@Override
	public PageDTO<InstanceDTO> getAllInstancesById(String id, boolean includeDoc, int page) {
		log.debug("SOAP request to get a page of Instances by id {}", id);
		Page<Instance> pageOfInstances = instanceService.findAllByDomainId(id, PageRequest.of(page, ITEMS_PER_PAGE));
		if(pageOfInstances != null && pageOfInstances.getContent() != null && !includeDoc) {
			for(Instance ins : pageOfInstances.getContent()) {
				ins.setDocs(null);
				ins.setInstanceAsDoc(null);
			}
		}
		
		return PageResponse.buildFromPage(pageOfInstances, instanceDtoConverter);
	}

	@Override
	public void deleteInstance(String id, String version, String bearerToken) throws AccessDeniedException {
		log.debug("SOAP request to delete Instance id {} version {}", id, version);
		Instance instance = instanceService.findByDomainId(id, version);
		
		String organizationId = WebUtils.extractOrganizationIdFromToken(bearerToken, log);
		if(!InstanceUtil.checkOrganizationId(instance, organizationId)) {
			String msg = "Cannot delete entity, organization ID "+organizationId+" does not match that of entity: "+instance.getOrganizationId();
			log.warn(msg);
            throw new AccessDeniedException(msg);
		}
		
		instanceService.delete(instance.getId());
	}

	@Override
	public PageDTO<InstanceDTO> searchInstances(String query, boolean includeDoc, int page) {
		log.debug("SOAP request to search for a page of Instances for query {}", query);
        Page<Instance> pageOfInstances = instanceService.search(query, PageRequest.of(page, ITEMS_PER_PAGE));
        if (pageOfInstances != null && pageOfInstances.getContent() != null && !includeDoc) {
            for(Instance instance : pageOfInstances.getContent()) {
                instance.setDocs(null);
                instance.setInstanceAsDoc(null);
            }
        }
        return PageResponse.buildFromPage(pageOfInstances, instanceDtoConverter);
	}

	@Override
	public PageDTO<InstanceDTO> searchInstancesByKeywords(String query, boolean includeDoc, int page) {
        log.debug("SOAP request to search for a page of Instances for keywords {}", query);
        Page<Instance> pageOfInstances = instanceService.searchKeywords(query, PageRequest.of(page, ITEMS_PER_PAGE));
        if (pageOfInstances != null && pageOfInstances.getContent() != null && !includeDoc) {
            for(Instance instance:pageOfInstances.getContent()) {
                instance.setDocs(null);
                instance.setInstanceAsDoc(null);
            }
        }

        return PageResponse.buildFromPage(pageOfInstances, instanceDtoConverter);
	}

	@Override
	public PageDTO<InstanceDTO> searchInstancesByUnlocode(String query, boolean includeDoc, int page) {
        log.debug("SOAP request to search for a page of Instances for unlocode {}", query);
        Page<Instance> pageOfInstances = instanceService.searchUnlocode(query, PageRequest.of(page, ITEMS_PER_PAGE));
        if (pageOfInstances != null && pageOfInstances.getContent() != null && !includeDoc) {
            for(Instance instance:pageOfInstances.getContent()) {
                instance.setDocs(null);
                instance.setInstanceAsDoc(null);
            }
        }
        
        return PageResponse.buildFromPage(pageOfInstances, instanceDtoConverter);
	}

	@Override
	public PageDTO<InstanceDTO> searchInstancesByLocation(String latitude, String longitude, String query, boolean includeDoc, int page) throws ProcessingException {
		Page<Instance> pageOfInstances;
        log.debug("SOAP request to get Instance by lat {} long {}", latitude, longitude);
        try {
	        pageOfInstances = instanceService.findByLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), query, PageRequest.of(page, ITEMS_PER_PAGE));
	        if (pageOfInstances != null && pageOfInstances.getContent() != null && !includeDoc) {
	            for(Instance instance:pageOfInstances.getContent()) {
	                instance.setDocs(null);
	                instance.setInstanceAsDoc(null);
	            }
	        }
        } catch (Exception e) {
        	throw new ProcessingException(e.getMessage(), e);
        }
        
        return PageResponse.buildFromPage(pageOfInstances, instanceDtoConverter);
	}

	@Override
	public PageDTO<InstanceDTO> searchInstancesByGeometryGeojson(String geometry, String query, boolean includeDoc, int page) throws Exception {
        log.debug("SOAP request to get Instance by geojson ", geometry);
        Page<Instance> pageOfInstances = instanceService.findByGeoshape(geometry, query, PageRequest.of(page, ITEMS_PER_PAGE));
        removeIncludedDoc(pageOfInstances, includeDoc);

        return PageResponse.buildFromPage(pageOfInstances, instanceDtoConverter);
	}

	@Override
	public PageDTO<InstanceDTO> searchInstancesByGeometryWKT(String geometry, String query, boolean includeDoc, int page) throws ProcessingException {
        log.debug("SOAP request to get Instance by wkt ", geometry);
        String geoJson = null;
        Page<Instance> pageOfInstances;
        try {
	        geoJson = InstanceUtil.convertWKTtoGeoJson(geometry).toString();
	        log.debug("Converted Geojson: " + geoJson);
	        pageOfInstances = instanceService.findByGeoshape(geoJson, query, PageRequest.of(page, ITEMS_PER_PAGE));
        } catch (Exception e) {
        	throw new ProcessingException(e.getMessage(), e);
        }
        if (pageOfInstances != null && pageOfInstances.getContent() != null && !includeDoc) {
            for(Instance instance : pageOfInstances.getContent()) {
                instance.setDocs(null);
                instance.setInstanceAsDoc(null);
            }
        }
        
		return PageResponse.buildFromPage(pageOfInstances, instanceDtoConverter);
	}

	@Override
	public void updateInstanceStatus(String id, String version, String status, String bearerToken) throws AccessDeniedException {
        log.debug("SOAP request to update status of Instance {} version {}", id, version);
        Instance instance = instanceService.findByDomainId(id, version);

        String organizationId = WebUtils.extractOrganizationIdFromToken(bearerToken, log);
        if (!InstanceUtil.checkOrganizationId(instance, organizationId)) {
            String msg = "Cannot update entity, organization ID "+organizationId+" does not match that of entity: "+instance.getOrganizationId();
            throw new AccessDeniedException(msg);
        }
	}
	
	private void removeIncludedDoc(Page<Instance> instPage, boolean includeDoc) {
		if(instPage != null && instPage.getContent() != null && !includeDoc) {
	          for(Instance instance : instPage.getContent()) {
	                instance.setDocs(null);
	                instance.setInstanceAsDoc(null);
	            }
		}
	}

}
