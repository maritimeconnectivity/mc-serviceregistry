package com.frequentis.maritime.mcsr.config;

import java.util.List;

import javax.servlet.Servlet;
import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.MessageObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.frequentis.maritime.mcsr.web.soap.DocResource;
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
        return publishEndpoint(docResource, "/DocResource");
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
    	org.apache.cxf.endpoint.Endpoint endp = ep.getServer().getEndpoint();
    	List<Interceptor<? extends Message>> outInterceptors = endp.getOutInterceptors();
    	for(Interceptor<? extends Message> i : outInterceptors) {
    		log.error("INT {}", i.getClass());
    	}
    	MessageObserver outFaultObserver = endp.getOutFaultObserver();
    	log.error("Observer {}", outFaultObserver);

    	return ep;
    }

}