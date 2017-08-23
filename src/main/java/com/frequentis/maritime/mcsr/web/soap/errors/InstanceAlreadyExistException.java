package com.frequentis.maritime.mcsr.web.soap.errors;

import java.io.Serializable;

import javax.xml.ws.WebFault;

@WebFault(name = "InstanceAlreadyExistException")
public class InstanceAlreadyExistException extends Exception implements Serializable {

	public InstanceAlreadyExistException(String message) {
		super(message);
	}

	public InstanceAlreadyExistException(String message, Throwable th) {
		super(message, th);
	}

    public java.lang.String getFaultInfo() {
        return this.getMessage();
    }

}
