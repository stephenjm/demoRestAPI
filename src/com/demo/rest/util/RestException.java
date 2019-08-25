package com.demo.rest.util;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
* <p>Title: RestException </p>
* <p>Description: Class to create a custom exception </p>
*
* @author Stephen Mudehwe
* 
*/
@Provider
public class RestException extends WebApplicationException {
    public RestException(String message) {
        super(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}