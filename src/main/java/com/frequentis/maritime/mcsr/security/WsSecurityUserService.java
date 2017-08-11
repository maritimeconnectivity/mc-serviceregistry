package com.frequentis.maritime.mcsr.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class WsSecurityUserService implements CallbackHandler {
	Logger log = LoggerFactory.getLogger(WsSecurityUserService.class);

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		log.error("Something for hadnle {}", callbacks);
		
	}





}
