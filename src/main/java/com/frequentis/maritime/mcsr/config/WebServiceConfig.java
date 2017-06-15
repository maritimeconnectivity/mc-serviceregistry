package com.frequentis.maritime.mcsr.config;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.frequentis.maritime.mcsr.web.soap.registry.ServiceSpecificationResource;
import com.frequentis.maritime.mcsr.web.soap.DocResource;
 
 
@Configuration
public class WebServiceConfig {
    @Autowired
    private Bus bus;
 
    @Bean
    public Endpoint docResourceEndpoint(DocResource docResource) {
        EndpointImpl endpoint = new EndpointImpl(bus, docResource);
        endpoint.publish("/DocService");
        return endpoint;
    }
    
    @Bean
    public Endpoint serviceSpecificationResourceEndpoint(ServiceSpecificationResource resource) {
        EndpointImpl endpoint = new EndpointImpl(bus, resource);
        endpoint.publish("/ServiceSpecification");
        return endpoint;
    }
}