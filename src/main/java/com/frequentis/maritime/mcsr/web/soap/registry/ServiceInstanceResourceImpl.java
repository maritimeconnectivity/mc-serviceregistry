package com.frequentis.maritime.mcsr.web.soap.registry;

import javax.inject.Inject;
import javax.jws.WebService;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.frequentis.maritime.mcsr.domain.Instance;
import com.frequentis.maritime.mcsr.domain.Xml;
import com.frequentis.maritime.mcsr.service.DesignService;
import com.frequentis.maritime.mcsr.service.InstanceService;
import com.frequentis.maritime.mcsr.service.XmlService;
import com.frequentis.maritime.mcsr.web.exceptions.GeometryParseException;
import com.frequentis.maritime.mcsr.web.exceptions.XMLValidationException;
import com.frequentis.maritime.mcsr.web.rest.util.InstanceUtil;
import com.frequentis.maritime.mcsr.web.rest.util.XmlUtil;
import com.frequentis.maritime.mcsr.web.soap.PageResponse;
import com.frequentis.maritime.mcsr.web.soap.SoapHTTPUtil;
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
import com.frequentis.maritime.mcsr.domain.util.EntityUtils;

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
	XmlService xmlService;

	@Inject
	InstanceDTOConverter instanceDtoConverter;

	@Inject
	InstanceParameterDTOToInstanceConverter instanceParameterConverter;


	@Override
	public InstanceDTO createInstance(InstanceParameterDTO instanceDto)
			throws AccessDeniedException, InstanceAlreadyExistException, XmlValidateException, ProcessingException {
		log.debug("SOAP request to create instance");

		return saveInstance(instanceDto, true);
	}

	private InstanceDTO saveInstance(InstanceParameterDTO instanceDto, boolean isNew) throws XmlValidateException, InstanceAlreadyExistException, AccessDeniedException {
	        String bearerToken = SoapHTTPUtil.currentBearerToken();
	        String organizationId = WebUtils.extractOrganizationIdFromToken(bearerToken, log);
	        if(instanceDto.id != null) {
	            throw new InstanceAlreadyExistException("A new instance cannot already have an ID");
	        }
	        Instance instance = instanceParameterConverter.convert(instanceDto);

	        if(instance.getInstanceAsXml() == null || instance.getInstanceAsXml().getContent() == null) {
	            throw new XmlValidateException("Instance must be created as XML (instanceAsXml must not be null)");
	        }

    		if (!InstanceUtil.checkRolePermissions(instance.getOrganizationId(), bearerToken)) {
		    String msg = "User does not have permission to create instances for organization: "+instance.getOrganizationId();
		    log.warn(msg);
        	    throw new AccessDeniedException(msg);
    		}

		instance.setOrganizationId(organizationId);

		if (isNew) {
		    instance.setPublishedAt(EntityUtils.getCurrentUTCTimeISO8601());
		    instance.setLastUpdatedAt(instance.getPublishedAt());
		} else {
    		    instance.setLastUpdatedAt(EntityUtils.getCurrentUTCTimeISO8601());
    		    if (instance.getPublishedAt() == null) {
        		instance.setPublishedAt(instance.getLastUpdatedAt());
    		    }
		}

	        try {
	            InstanceUtil.prepareInstanceForSave(instance, designService);
	            JsonNode geometry = instance.getGeometry();
	            instanceService.save(instance);
	            instance.setGeometry(geometry);
	            instanceService.saveGeometry(instance);


	        } catch (XMLValidationException e) {
	            throw new XmlValidateException(e.getMessage(), e);
	        } catch (GeometryParseException e) {
	            instanceService.save(instance);
	            throw new XmlValidateException(e.getMessage(), e);
	        } catch (Exception e) {
	            throw new XmlValidateException(e.getMessage(), e);
	        }

	        return instanceDtoConverter.convert(instance);
	}

	@Override
	public InstanceDTO updateInstance(InstanceParameterDTO instanceDto)
			throws AccessDeniedException, XmlValidateException, InstanceAlreadyExistException, ProcessingException {
		log.debug("SOAP request to update instance");
		if (instanceDto.id == null) {
			return createInstance(instanceDto);
		}

	        Instance instance = instanceParameterConverter.convert(instanceDto);
		String bearerToken = SoapHTTPUtil.currentBearerToken();
    		if (!InstanceUtil.checkRolePermissions(instance.getOrganizationId(), bearerToken)) {
		    String msg = "Cannot update entity, organization ID does not match that of entity: "+instance.getOrganizationId();
		    log.warn(msg);
        	    throw new AccessDeniedException(msg);
    		}

		return saveInstance(instanceDto, false);
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
	public InstanceDTO getInstance(String id, String version, boolean includeDoc, boolean includeNonCompliant) {
		log.debug("SOAP request to get Instance via domain id {} and version {}", id, version);
		Instance instance = null;
		if(version.equalsIgnoreCase("latest")) {
			instance = instanceService.findLatestVersionByDomainId(id, includeNonCompliant);
		} else {
			instance = instanceService.findAllByDomainId(id, version);
		}
		if (instance != null && !includeDoc) {
			instance.setDocs(null);
			instance.setInstanceAsDoc(null);
		}

		return instanceDtoConverter.convert(instance);
	}

	@Override
	public PageDTO<InstanceDTO> getAllInstancesById(String id, boolean includeDoc, boolean includenonCompliant, int page) {
		log.debug("SOAP request to get a page of Instances by id {}", id);
		Page<Instance> pageOfInstances = instanceService.findAllByDomainId(id, includenonCompliant, PageRequest.of(page, ITEMS_PER_PAGE));
		if(pageOfInstances != null && pageOfInstances.getContent() != null && !includeDoc) {
			for(Instance ins : pageOfInstances.getContent()) {
				ins.setDocs(null);
				ins.setInstanceAsDoc(null);
			}
		}

		return PageResponse.buildFromPage(pageOfInstances, instanceDtoConverter);
	}

	@Override
	public void deleteInstance(String id, String version) throws AccessDeniedException {
		log.debug("SOAP request to delete Instance id {} version {}", id, version);
		Instance instance = instanceService.findAllByDomainId(id, version);
		String bearerToken = SoapHTTPUtil.currentBearerToken();
	        if (!InstanceUtil.checkRolePermissions(instance.getOrganizationId(), bearerToken)) {
		    String msg = "Cannot delete entity, organization ID does not match that of entity: "+instance.getOrganizationId();
		    log.warn(msg);
	            throw new AccessDeniedException(msg);
	        }
		instanceService.delete(instance.getId());
	}

	@Override
	public PageDTO<InstanceDTO> searchInstances(String query, boolean includeDoc, boolean includeNonCompliant, int page) {
		log.debug("SOAP request to search for a page of Instances for query {}", query);
        Page<Instance> pageOfInstances = instanceService.search(query, includeNonCompliant, PageRequest.of(page, ITEMS_PER_PAGE));
        if (pageOfInstances != null && pageOfInstances.getContent() != null && !includeDoc) {
            for(Instance instance : pageOfInstances.getContent()) {
                instance.setDocs(null);
                instance.setInstanceAsDoc(null);
            }
        }
        return PageResponse.buildFromPage(pageOfInstances, instanceDtoConverter);
	}

	@Override
	public PageDTO<InstanceDTO> searchInstancesByKeywords(String query, boolean includeDoc, boolean includeNonCompliant, int page) {
        log.debug("SOAP request to search for a page of Instances for keywords {}", query);
        Page<Instance> pageOfInstances = instanceService.searchKeywords(query, includeNonCompliant, PageRequest.of(page, ITEMS_PER_PAGE));
        if (pageOfInstances != null && pageOfInstances.getContent() != null && !includeDoc) {
            for(Instance instance:pageOfInstances.getContent()) {
                instance.setDocs(null);
                instance.setInstanceAsDoc(null);
            }
        }

        return PageResponse.buildFromPage(pageOfInstances, instanceDtoConverter);
	}

	@Override
	public PageDTO<InstanceDTO> searchInstancesByUnlocode(String query, boolean includeDoc, boolean includeNonCompliant, int page) {
        log.debug("SOAP request to search for a page of Instances for unlocode {}", query);
        Page<Instance> pageOfInstances = instanceService.searchUnlocode(query, includeNonCompliant, PageRequest.of(page, ITEMS_PER_PAGE));
        if (pageOfInstances != null && pageOfInstances.getContent() != null && !includeDoc) {
            for(Instance instance:pageOfInstances.getContent()) {
                instance.setDocs(null);
                instance.setInstanceAsDoc(null);
            }
        }

        return PageResponse.buildFromPage(pageOfInstances, instanceDtoConverter);
	}

	@Override
	public PageDTO<InstanceDTO> searchInstancesByLocation(String latitude, String longitude, String query, boolean includeDoc, boolean includeNonCompliant, int page) throws ProcessingException {
		Page<Instance> pageOfInstances;
        log.debug("SOAP request to get Instance by lat {} long {}", latitude, longitude);
        try {
	        pageOfInstances = instanceService.findByLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), query, includeNonCompliant, PageRequest.of(page, ITEMS_PER_PAGE));
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
	public PageDTO<InstanceDTO> searchInstancesByGeometryGeojson(String geometry, String query, boolean includeDoc, boolean includeNonCompliant, int page) throws Exception {
        log.debug("SOAP request to get Instance by geojson ", geometry);
        Page<Instance> pageOfInstances = instanceService.findByGeoshape(geometry, query, includeNonCompliant, PageRequest.of(page, ITEMS_PER_PAGE));
        removeIncludedDoc(pageOfInstances, includeDoc);

        return PageResponse.buildFromPage(pageOfInstances, instanceDtoConverter);
	}

	@Override
	public PageDTO<InstanceDTO> searchInstancesByGeometryWKT(String geometry, String query, boolean includeDoc, boolean includeNonCompliant, int page) throws ProcessingException {
        log.debug("SOAP request to get Instance by wkt ", geometry);
        String geoJson = null;
        Page<Instance> pageOfInstances;
        try {
	        geoJson = InstanceUtil.convertWKTtoGeoJson(geometry).toString();
	        log.debug("Converted Geojson: " + geoJson);
	        pageOfInstances = instanceService.findByGeoshape(geoJson, query, includeNonCompliant, PageRequest.of(page, ITEMS_PER_PAGE));
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
	public void updateInstanceStatus(String id, String version, String status) throws AccessDeniedException {
        log.debug("SOAP request to update status of Instance {} version {}", id, version);
        Instance instance = instanceService.findAllByDomainId(id, version);

	String bearerToken = SoapHTTPUtil.currentBearerToken();
        if (!InstanceUtil.checkRolePermissions(instance.getOrganizationId(), bearerToken)) {
	    String msg = "Cannot delete entity, organization ID does not match that of entity: "+instance.getOrganizationId();
	    log.warn(msg);
            throw new AccessDeniedException(msg);
        }

        try {
            instanceService.updateStatus(instance.getId(), status);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
