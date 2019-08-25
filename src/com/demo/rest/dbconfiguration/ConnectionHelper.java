package com.demo.rest.dbconfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * <p>Title: ConnectionHelper</p>
 * <p>Description: This a connection tool to be used to create, manage and terminate database connections</p>
 *
 * @author Stephen Mudehwe
 * @version 1.0
 */

public class ConnectionHelper{
	
	private String adminUsername;
	private String adminPassword;
	private String dbConnectionString;
	private String dbUsername;
	private String dbPassword;
	private String restApiSecurityEnabled;
	private String dbPoolSize;
    private String dbMaxActiveConnections;
    private String dbConnectionTimeout;
	private String dbQueryTimeout;
	private String sourceData;

	private static ConnectionHelper instance;
	private static Logger logger = Logger.getLogger(ConnectionHelper.class);
		
	/**Method to extract data from rest-api.properties file at deployment.
	 * No defaults are provided so, if this process fails a connection to the database is never established and as such,
	 * none of the methods will work.
	 */
	private ConnectionHelper(){
        
        String propertiesFileFullPath = null;
	    String propertiesFilename = null;
	    String propertiesFileDir  = null;
		try {
			propertiesFileFullPath = this.getClass().getClassLoader().getResource("rest-api.properties").toURI().getPath();
		} catch (Exception e) {
			propertiesFileDir = System.getProperty("catalina.base")+"/webapps/demoRestAPI/WEB-INF/classes/";
	        propertiesFilename = "rest-api.properties";
		}
        
		Properties plainTextProperties = readPropertiesFromConfigurationFile(propertiesFileFullPath);
		try {
			if(propertiesFileFullPath==null){
				throw new Exception("Failed to load properties from file.");
			}

			restApiSecurityEnabled=plainTextProperties.getProperty("rest.api.security.enabled");

			dbConnectionString=plainTextProperties.getProperty("rest.api.db.connection.string");
			dbUsername=plainTextProperties.getProperty("rest.api.db.username");
			dbPassword=plainTextProperties.getProperty("rest.api.db.plaintext.password");
			
			dbPoolSize=plainTextProperties.getProperty("rest.api.db.connection.pool.size");
		    
		    dbMaxActiveConnections = plainTextProperties.getProperty("rest.api.db.max.active.connections");
			dbConnectionTimeout = plainTextProperties.getProperty("rest.api.db.connection.timeout.secs");
			dbQueryTimeout = plainTextProperties.getProperty("rest.api.db.query.timeout.secs");
			
			adminUsername=plainTextProperties.getProperty("rest.api.admin.username");
			adminPassword=plainTextProperties.getProperty("rest.api.admin.plaintext.password");
			
			sourceData=plainTextProperties.getProperty("rest.api.source.data");
			
		} catch (Exception e) {
			logger.error("Failed to load properties from file.",e);
		}       
	}

	public static HashMap<String,Object> getProperties() {		
		if(instance == null)
			instance = new ConnectionHelper();
		
		HashMap<String,Object> result = new HashMap<String,Object>();

		result.put("restApiSecurityEnabled", instance.restApiSecurityEnabled);
		
		result.put("sourceData",instance.sourceData);
		
		result.put("dbConnectionString",instance.dbConnectionString);
		result.put("dbUsername",instance.dbUsername);
		result.put("dbPassword",instance.dbPassword);
		
		result.put("dbPoolSize",instance.dbPoolSize);   
		result.put("dbMaxActiveConnections",instance.dbMaxActiveConnections);
		result.put("dbConnectionTimeout",instance.dbConnectionTimeout);
		result.put("dbQueryTimeout",instance.dbQueryTimeout);
		result.put("adminUsername",instance.adminUsername);
		result.put("adminPassword",instance.adminPassword);

		
		return result;
	}
	
	public void reset(){
		instance = null;
	}
	
	private Properties readPropertiesFromConfigurationFile(String propertiesFile){        
		Properties extractedProperties = new Properties();
		
		try(FileInputStream in = new FileInputStream(new File(propertiesFile))){

			extractedProperties.load(in);
		} catch(Exception e){
			Logger.getLogger(ConnectionHelper.class).log(Level.ERROR, "Failed to read from properties file.",e);
		}
				
		return extractedProperties;
	}
}