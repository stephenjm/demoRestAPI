package com.demo.rest.resource;

import java.util.Enumeration;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.demo.rest.dbinterface.DatabaseAccessor;
import com.demo.rest.model.ErrorResponse;
import com.demo.rest.model.QueryResponse;
import com.demo.rest.util.AuditLog;


/**
 * <p>Title: REST-API</p>
 * <p>Description: This an interface to the  configuration</p>
 *
 * @author Stephen Mudehwe
 * @version 1.0 
 */


@Path("")
public class Resource {
	
	private DatabaseAccessor DAO = DatabaseAccessor.instanceOf();
	private static Logger logger = Logger.getLogger(Resource.class);
	private static Logger auditLogger = Logger.getLogger(AuditLog.class);
	
	
	@GET
	@Path("/data")
	@Produces ({MediaType.APPLICATION_JSON,MediaType.TEXT_XML})
	public Response findAllData(@Context HttpServletRequest req, @HeaderParam("Accept") String acceptHeader) {
		
		Response rs = null;		
			
		try {
			List<QueryResponse> response = DAO.getAllData();
			
			if(response.isEmpty()) {
				rs = processingFailureResponse("No Data Found");
			} else {
				rs = getResponseWithMediaType(acceptHeader, response);
			}
			
		} catch (Exception e) {
			return internalServerError(e);
		}
					
		return rs;
	}
	
	@GET
	@Path("/data/{policyID}")
	@Produces ({MediaType.APPLICATION_JSON,MediaType.TEXT_XML})
	public Response findDataByPolicyID(@Context HttpServletRequest req, @HeaderParam("Accept") String acceptHeader,
			@PathParam("policyID") int policyID) {
		
		Response rs = null;
		if(policyID > 0){
			try {
				List<QueryResponse> response = DAO.getDataByPolicyId(policyID);
				
				if(response != null) {
					rs = getResponseWithMediaType(acceptHeader, response);
				} else {
					rs = processingFailureResponse("No data found with policyID: "+policyID);
				}
			} catch (Exception e) {
				return internalServerError(e);
			}
		
		} else {
			rs = Response
	                .status(Response.Status.BAD_REQUEST)
	                .build();
		}
		return rs;
	}
	
	@POST
	@Path("/data/{policyID}")
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED})
	@Produces ({MediaType.TEXT_PLAIN})
	public Response addNewEntry(@Context HttpServletRequest req, @HeaderParam("Accept") String acceptHeader,
			@PathParam("policyID") int policyID,
			@FormParam("statecode")String statecode,
			@FormParam("county")String county,
			@FormParam("construction")String construction,
			@FormParam("latitude")String latitude,
			@FormParam("longitude")String longitude,
			@FormParam("notes")String notes) {
		
		Response rs = null;
		if(policyID > 0){
			try {
				logger.debug(statecode+county+construction+notes);
				boolean result1 = DAO.insertDataToDemoTableByID(policyID,statecode,county,construction,notes); 
				boolean result2 = DAO.insertDataToGeoTableByID(policyID,Float.valueOf(latitude),Float.valueOf(longitude));
				rs = getTextResponse("/data added successfully");
				
			} catch (Exception e) {
				return internalServerError(e);
			}
		
		} else {
			rs = Response
	                .status(Response.Status.BAD_REQUEST)
	                .build();
		}
		return rs;
	}
	
	@PUT
	@Path("/data/{policyID}")
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED})
	@Produces ({MediaType.TEXT_PLAIN})
	public Response updateData(@Context HttpServletRequest req, @HeaderParam("Accept") String acceptHeader,
			@PathParam("policyID") int policyID,
			@FormParam("statecode")String statecode,
			@FormParam("county")String county,
			@FormParam("construction")String construction,
			@FormParam("latitude")String latitude,
			@FormParam("longitude")String longitude,
			@FormParam("notes")String notes) {
		
		Response rs = null;
		if(policyID > 0){
			try {
				logger.info(statecode+county+construction);
				int result = DAO.updateDemoTableByID(policyID,statecode,county,construction,notes) +
						DAO.updateGeoTableByID(policyID,Float.valueOf(latitude),Float.valueOf(longitude));
				
				if(result > 0) {
					rs = getTextResponse("Successfully updated data for policyID: "+policyID);
				} else {
					rs = getTextResponse("Failed to update data for policyID: "+policyID);
				}
			} catch (Exception e) {
				return internalServerError(e);
			}
		
		} else {
			rs = Response
	                .status(Response.Status.BAD_REQUEST)
	                .build();
		}
		return rs;
	}
	
	@DELETE
	@Path("/data/{policyID}")
	@Produces ({MediaType.TEXT_PLAIN})
	public Response deleteDataByPolicyID(@Context HttpServletRequest req,
			@PathParam("policyID") int policyID) {
		
		Response rs = null;
		if(policyID > 0){
			try {
				if(DAO.deleteByPolicyID(policyID)) {
					rs = getTextResponse("Successfully deleted policyID: "+policyID);
				} else {
					rs = getTextResponse("Failed to delete policyID: "+policyID);
				}
			} catch (Exception e) {
				return internalServerError(e);
			}
		
		} else {
			rs = Response
	                .status(Response.Status.BAD_REQUEST)
	                .build();
		}
		return rs;
	}
	
	private Response getTextResponse(String result) {
		return Response
                .status(Response.Status.OK)
                .entity(result)
                .type( MediaType.TEXT_PLAIN)
                .build();
	}
	
	private Response getResponseWithMediaType(String acceptHeader, Object obj) {
		Response rs = null;
		if(acceptHeader.equalsIgnoreCase(MediaType.APPLICATION_JSON)){
			rs = Response
	                .status(Response.Status.OK)
	                .entity(obj)
	                .type( MediaType.APPLICATION_JSON)
	                .build();
		} else if(acceptHeader.equalsIgnoreCase(MediaType.TEXT_XML)){
			rs = Response
		            .status(Response.Status.OK)
		            .entity(obj)
		            .type( MediaType.TEXT_XML)
		            .build();
		} else {
			rs = Response
	                .status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
	                .build();
		}
		
		return rs;
	}
    
    private ErrorResponse getFailureResponse(int code, String message){
    	ErrorResponse response = new ErrorResponse();
    	
    	response.setErrorCode(code);
    	response.setErrorMessage(message);
    	
    	return response;
    }
    
	private boolean isAdminUserAuthxd(String username, String password, HttpServletRequest request){
		boolean result = false;

        if(StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)){

            if(auditLogger.isInfoEnabled()){
            	auditLogger.log(Level.INFO, 
    				MessageFormat.format("Query by Admin: {0} from i.p. address: {1} at {2}",  
    						username, request.getRemoteAddr(), Calendar.getInstance().getTime()) ); 	 
    		}
            
            result = true;            
        }		
		return result;
	}
	
	private Response processingFailureResponse(String msg) {
		return  Response
                .status(Response.Status.BAD_REQUEST)
                .entity(getFailureResponse(400,msg))
                .type( MediaType.APPLICATION_JSON)
                .build();
	}
	
	private Response internalServerError(Exception e) {
		logger.error("Service encountered error whilst processing request",e);
		
		return Response
				.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Service encountered error whilst processing request")
                .type( MediaType.TEXT_PLAIN)
				.build();
	}
	
	@GET
	@Path("admin/refresh")
	@Produces({MediaType.TEXT_PLAIN})
	public Response refresh(@Context HttpServletRequest req,
			@QueryParam("username") String username, 
			@QueryParam("password") String password ){
		
		String result = "Refresh Failed";
		
		if(logger.isDebugEnabled()){
			logger.log(Level.DEBUG,"Time: " + Calendar.getInstance().getTime() + " Entered method refresh");
		}
		
		return  Response
	            .status(Response.Status.OK)
	            .entity(result)
	            .type( MediaType.TEXT_PLAIN)
	            .build();
	}		
	
	@GET
	@Path("admin/changeLogLevel")
	@Produces({MediaType.TEXT_PLAIN})
	public Response changeLogLevel(@Context HttpServletRequest req,
			@QueryParam("level") String level,
			@QueryParam("username") String username, 
			@QueryParam("password") String password ){
		
		String result = "";
		if(isAdminUserAuthxd(username,password, req) && setLogLevel(level)) {
			result = "Log level changed to: "+level;
		} else {
			result = "Failed to change logging level to: "+level;
		}
		
		
		if(logger.isDebugEnabled()){
			logger.log(Level.DEBUG,"Time: " + Calendar.getInstance().getTime() + " Entered method changeLogLevel");
		}
		
		return  Response
	            .status(Response.Status.OK)
	            .entity(result)
	            .type( MediaType.TEXT_PLAIN)
	            .build();	
	}
	
	private boolean setLogLevel(String level){
		
        Level selectedLevel = Level.ALL;
        boolean ok = true;

        if (level == null || level.length() < 0) {
            ok = false;
        } else if (level.equalsIgnoreCase(Level.ALL.toString())) {
            selectedLevel = Level.ALL;
        } else if (level.equalsIgnoreCase(Level.DEBUG.toString())) {
            selectedLevel = Level.DEBUG;
        } else if (level.equalsIgnoreCase(Level.ERROR.toString())) {
            selectedLevel = Level.ERROR;
        } else if (level.equalsIgnoreCase(Level.FATAL.toString())) {
            selectedLevel = Level.FATAL;
        } else if (level.equalsIgnoreCase(Level.INFO.toString())) {
            selectedLevel = Level.INFO;
        } else if (level.equalsIgnoreCase(Level.OFF.toString())) {
            selectedLevel = Level.OFF;
        } else if (level.equalsIgnoreCase(Level.TRACE.toString())) {
            selectedLevel = Level.TRACE;
        } else if (level.equalsIgnoreCase(Level.WARN.toString())) {
            selectedLevel = Level.WARN;
        } else {
            ok = false;
        }
        
        if (ok) {
            
            Enumeration<?> loggers = org.apache.log4j.LogManager.getCurrentLoggers();
            while (loggers.hasMoreElements()) {
                org.apache.log4j.Logger currentLogger = (Logger) loggers.nextElement();
                
                String loggerName = currentLogger.getName();
                if (loggerName.contains("com.anam")) {
                		currentLogger.setLevel(selectedLevel);               	
                }
            }
            if (logger.isEnabledFor(Level.INFO)) {
                logger.info("Logging level changed to "+selectedLevel);
            }
        } else {
        	if (logger.isEnabledFor(Level.INFO)) {
                logger.info("Unable to change logging level to "+level);
            }
        }
        return ok;
	}
}
