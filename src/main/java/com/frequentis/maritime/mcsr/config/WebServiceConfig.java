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
import com.frequentis.maritime.mcsr.web.soap.registry.ServiceSpecificationResource;

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
        log.debug("Test register");
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