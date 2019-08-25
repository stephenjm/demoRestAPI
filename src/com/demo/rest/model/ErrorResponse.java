package com.demo.rest.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement(name="Result")
@XmlType(name="error",propOrder = {
	    "errorCode",
	    "errorMessage"
	})
public class ErrorResponse {
	private int errorCode;
	private String errorMessage;

	public ErrorResponse(){}
	
	/**
	 * @return the errorCode
	 */
	@XmlElement(name = "errorCode", required = false, nillable = false)
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
