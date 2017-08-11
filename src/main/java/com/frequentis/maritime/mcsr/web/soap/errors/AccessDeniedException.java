package com.frequentis.maritime.mcsr.web.soap.errors;

import java.io.Serializable;

import javax.xml.ws.WebFault;

@WebFault(name = "AccessDeniedException")
public class AccessDeniedException extends Exception implements Serializable {

	public AccessDeniedException(String message) {
		super(message);
	}
	
	public AccessDeniedException(String message, Throwable th) {
		super(message, th);
	}
	
    public java.lang.String getFaultInfo() {
        return this.getMessage();
    }

}
