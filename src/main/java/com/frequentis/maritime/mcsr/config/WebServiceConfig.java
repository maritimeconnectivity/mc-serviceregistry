package com.frequentis.maritime.mcsr.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import com.frequentis.maritime.mcsr.repository.PersistentTokenRepository;
import com.frequentis.maritime.mcsr.repository.UserRepository;
import com.frequentis.maritime.mcsr.security.CustomPersistentRememberMeServices;
 
 
@Configuration
public class WebServiceConfig {
	Logger log = LoggerFactory.getLogger(WebServiceConfig.class);
    //@Autowired
    //private Bus bus;
 
    //@Bean
    //public Endpoint docResourceEndpoint(DocResource docResource) {
    //    EndpointImpl endpoint = new EndpointImpl(bus, docResource);
    //    endpoint.publish("/DocService");
    //    return endpoint;
    //}
    
    //@Bean
    //public Endpoint serviceSpecificationResourceEndpoint(ServiceSpecificationResource resource) {
    //    EndpointImpl endpoint = new EndpointImpl(bus, resource);
    //    endpoint.publish("/ServiceSpecification");
	//    return endpoint;
    //}
	
//    @Bean
//    public RememberMeServices rememberMeService(
//    		JHipsterProperties jHipsterProperties, 
//    		org.springframework.security.core.userdetails.UserDetailsService userDetailsService,
//    		PersistentTokenRepository persistentTokenRepository,
//    		UserRepository userRepository) {
//    	log.info("Creating rememberMeService");
//    	CustomPersistentRememberMeServices rms = new CustomPersistentRememberMeServices(jHipsterProperties, 
//    			userDetailsService, 
//    			persistentTokenRepository,
//    			userRepository);
//    	return rms;
//    }
	
	@Bean
	public RememberMeServices rememberMeServices(
			JHipsterProperties jHipsterProperties, 
    		org.springframework.security.core.userdetails.UserDetailsService userDetailsService) {
		
		TokenBasedRememberMeServices tb = new TokenBasedRememberMeServices(jHipsterProperties.getSecurity().getRememberMe().getKey(), userDetailsService);
		return tb;
	}
}