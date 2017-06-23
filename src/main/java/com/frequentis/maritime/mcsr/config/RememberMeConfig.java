package com.frequentis.maritime.mcsr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

@Configuration
public class RememberMeConfig {
	
//  @Bean
//  public RememberMeServices rememberMeService(
//  		JHipsterProperties jHipsterProperties, 
//  		org.springframework.security.core.userdetails.UserDetailsService userDetailsService,
//  		PersistentTokenRepository persistentTokenRepository,
//  		UserRepository userRepository) {
//  	log.info("Creating rememberMeService");
//  	CustomPersistentRememberMeServices rms = new CustomPersistentRememberMeServices(jHipsterProperties, 
//  			userDetailsService, 
//  			persistentTokenRepository,
//  			userRepository);
//  	return rms;
//  }
	
	@Bean
	public RememberMeServices rememberMeServices(
			JHipsterProperties jHipsterProperties, 
  		org.springframework.security.core.userdetails.UserDetailsService userDetailsService) {
		
		TokenBasedRememberMeServices tb = new TokenBasedRememberMeServices(jHipsterProperties.getSecurity().getRememberMe().getKey(), userDetailsService);
		return tb;
	}
}
