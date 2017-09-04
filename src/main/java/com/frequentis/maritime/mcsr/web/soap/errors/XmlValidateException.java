package com.frequentis.maritime.mcsr.web.soap.errors;

import java.io.Serializable;

import javax.xml.ws.WebFault;

@WebFault(name = "XmlParsingException")
public class XmlValidateException extends Exception implements Serializable {


	public XmlValidateException(String message) {
		super(message);
	}

	public XmlValidateException(String message, Throwable th) {
		super(message, th);
	}

    public java.lang.String getFaultInfo() {
        return this.getMessage();
    }

}
