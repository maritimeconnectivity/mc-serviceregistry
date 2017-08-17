package com.frequentis.maritime.mcsr.config;

import javax.servlet.Servlet;
import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.frequentis.maritime.mcsr.web.soap.DocResource;
import com.frequentis.maritime.mcsr.web.soap.ElasticsearchIndexResource;
import com.frequentis.maritime.mcsr.web.soap.SpecificationTemplateResource;
import com.frequentis.maritime.mcsr.web.soap.SpecificationTemplateSetResource;
import com.frequentis.maritime.mcsr.web.soap.XmlResource;
import com.frequentis.maritime.mcsr.web.soap.XsdResource;
import com.frequentis.maritime.mcsr.web.soap.registry.ServiceInstanceResource;
import com.frequentis.maritime.mcsr.web.soap.registry.ServiceSpecificationResource;
import com.frequentis.maritime.mcsr.web.soap.registry.TechnicalDesignResource;

/**
 * SOAP services configuration.
 *
 *
 * @author Vorisek Lukas <lukas.vorisek@neoware.cz>
 *
 */
@Configuration
public class WebServiceConfig {
    Logger log = LoggerFactory.getLogger(WebServiceConfig.class);
    @Autowired
    private Bus bus;

    @Bean
    public Bus cxf() {
        return new SpringBus();
    }

    @Bean
    public ServletRegistrationBean<Servlet> registerApacheCxfServlet() {
        Servlet sl = new org.apache.cxf.transport.servlet.CXFServlet();
        ServletRegistrationBean<Servlet> srb = new ServletRegistrationBean<>();
        srb.addUrlMappings("/services/*");
        srb.setName("apache-cxf-ws");
        srb.setServlet(sl);
        srb.setLoadOnStartup(1);
        return srb;
    }

    @Bean
    public Endpoint docResourceEndpoint(DocResource docResource) {
        return publishEndpoint(docResource, "/DocResource");
    }
    
    @Bean
    public Endpoint elasticsearchIndexResourceEndpoint(ElasticsearchIndexResource resource) {
    	return publishEndpoint(resource, "/ElasticsearchIndexResource");
    }
    
    @Bean
    public Endpoint specificationTemplateSetResourceEndpoint(SpecificationTemplateSetResource resource) {
    	return publishEndpoint(resource, "/SpecificationTemplateSetResource");
    }
    
    @Bean
    public Endpoint xmlResourceEndpoint(XmlResource xmlResource) {
    	return publishEndpoint(xmlResource, "/XmlResource");
    }
    
    @Bean
    public Endpoint specificationTemplateResourceEndpoint(SpecificationTemplateResource resource) {
    	return publishEndpoint(resource, "/SpecificationTemplateResource");
    }
    
    @Bean
    public Endpoint xsdResourceEndpoint(XsdResource xsdResource) {
    	return publishEndpoint(xsdResource, "/XsdResource");
    }

    @Bean
    public Endpoint serviceSpecificationResourceEndpoint(ServiceSpecificationResource resource) {
        return publishEndpoint(resource, "/ServiceSpecification");
    }
    
    @Bean
    public Endpoint technicalDesignResource(TechnicalDesignResource resource) {
    	return publishEndpoint(resource, "/TechnicalDesignResource");
    }
    
    @Bean
    public Endpoint technicalInstanceResource(ServiceInstanceResource resource) {
    	return publishEndpoint(resource, "/ServiceInstanceResource");
    }

    private Endpoint publishEndpoint(Object resource, String url) {
    	EndpointImpl ep = new EndpointImpl(bus, resource);
    	ep.publish(url);

    	return ep;
    }

}