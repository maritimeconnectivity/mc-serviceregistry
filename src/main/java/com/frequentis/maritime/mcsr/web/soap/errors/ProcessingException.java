package com.frequentis.maritime.mcsr.web.soap.errors;

import java.io.Serializable;

import javax.xml.ws.WebFault;

@WebFault(name = "AccessDeniedException")
public class ProcessingException extends Exception implements Serializable {

	public ProcessingException(String message) {
		super(message);
	}
	
	public ProcessingException(String message, Throwable th) {
		super(message, th);
	}
	
    public java.lang.String getFaultInfo() {
        return this.getMessage();
    }

}
