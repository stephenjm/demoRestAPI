package com.demo.rest.dbinterface;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.demo.rest.dbconfiguration.ConnectionHelper;
import com.demo.rest.model.QueryResponse;
import com.demo.rest.util.AuditLog;

/**
* <p>Title: DatabaseAccessor </p>
* <p>Description: Class to access database data </p>
*
* @author Stephen Mudehwe
* 
*/
public class DatabaseAccessor {


	private String dbConnectionString;
	private String dbUsername;
	private String dbPassword;
	private int dbPoolSize;
    private int dbMaxActiveConnections;
    private int dbConnectionTimeout;
	private int dbQueryTimeout;

	private String configurationConnectionString;
	private String configurationUsername;
	private String configurationPassword;
	
	private static DatabaseAccessor self = null;
	
	private static Logger logger = Logger.getLogger(DatabaseAccessor.class);	
	private static Logger auditLogger = Logger.getLogger(AuditLog.class);
	
	private DbPool dbPool = null;
	
	private DatabaseAccessor(){
		loadConfiguration();     
	}
	
	public synchronized static DatabaseAccessor instanceOf(){
		if(self==null){
			self = new DatabaseAccessor();
		}
        return self;    
	}
	
	private boolean loadConfiguration(){
		try {    		
			HashMap<String, Object> credentials = ConnectionHelper.getProperties();  

			configurationConnectionString = (String) credentials.get("configurationConnectionString");
			configurationUsername = (String) credentials.get("configurationUsername");
			configurationPassword = (String) credentials.get("configurationPassword");

			dbConnectionString = (String) credentials.get("dbConnectionString");
			dbUsername = (String) credentials.get("dbUsername");
			dbPassword = (String) credentials.get("dbPassword");
			
			dbPoolSize = Integer.parseInt((String) credentials.get("dbPoolSize"));
			dbMaxActiveConnections = Integer.parseInt((String) credentials.get("dbMaxActiveConnections"));
			dbConnectionTimeout = Integer.parseInt((String) credentials.get("dbConnectionTimeout"));
			dbQueryTimeout = Integer.parseInt((String) credentials.get("dbQueryTimeout"));
		    		
			initialiseDbPool();
			
			loadSourceData((String) credentials.get("sourceData"));
		} catch (Exception e) {
			logger.log(Level.ERROR,"Error loading properties.",e);
			return false;
		}
		return true;
	}

	private void initialiseDbPool(){			
		try {
			dbPool = new DbPool("DbPool");
			
			// Try to set up the connection pool
			dbPool.setupDriver(dbConnectionString,dbUsername, dbPassword, 
					dbMaxActiveConnections, dbConnectionTimeout);

		} catch (Exception e) {
			logger.log(Level.ERROR,e);
		}	
	}

	private Connection getDbConnection() throws Exception {
		return dbPool.getConnectionToDatabase();
	}
	
	private void closeDbConnection(Connection connection) throws Exception {
		dbPool.closeConnection(connection);
	}
	
	private static void closeConfigurationDbConnection(Connection connection){
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			logger.log(Level.ERROR,e);
		}
	}
	
	private void reset(){
		try {
			dbPool.shutdownDriver();
		} catch (Exception e) {
			logger.log(Level.ERROR,e);
		}
		
		loadConfiguration();
	}

	//CREATE
	private void loadSourceData(String fileName) throws IOException {
		//BufferedReader bis = new BufferedReader(new FileReader(fileName));
		List<String> table1Data = new ArrayList<String>(4);
		List<List<String>> t1Data = new ArrayList<List<String>>();
		
		List<String> table2Data = new ArrayList<String>(3);
		List<List<String>> t2Data = new ArrayList<List<String>>();
		
		StringBuffer table1Query = new StringBuffer(Queries.insertDemoTableData);
		StringBuffer table2Query = new StringBuffer(Queries.insertGeoTableData);
		Files.lines(new File(fileName).toPath()).skip(1).forEach(line -> {
			String[] temp = line.split(",");
			table1Query.append("(")
			.append(temp[0]).append(",")
			.append("'").append(temp[1]).append("',")
			.append("'").append(temp[2]).append("',")
			.append("'").append(temp[16]).append("',")
			.append("''),");
			
			table2Query.append("(")
			.append(temp[0]).append(",")
			.append("").append(temp[13]).append(",")
			.append("").append(temp[14]).append("),");
		});
		
		try {
			insertDataToTable(table1Query.toString());
			insertDataToTable(table2Query.toString());
		} catch (Exception e) {
			logger.error("Failed to load default data",e);
		}
		
	}
	
	//CREATE
	private boolean insertDataToTable(String dbString) throws Exception {
		boolean result = false;
		Class.forName("com.mysql.jdbc.Driver");
		try(Connection c = getDbConnection()) {
			String dbData = dbString.substring(0,dbString.lastIndexOf(",")).concat(";");
			
			result = c.prepareStatement(dbData).execute();
			//closeDbConnection(c);

		} catch (SQLException e) {
			logger.log(Level.ERROR,e);
		} catch (Exception e) {
			logger.log(Level.ERROR,e);
		}
		return result;
	}

	public boolean insertDataToDemoTableByID(int policyID, String statecode, String county, String construction, String notes) throws ClassNotFoundException {

		boolean result = false;
		Class.forName("com.mysql.jdbc.Driver");
		try(
				Connection c = getDbConnection();
				PreparedStatement pstmt = c.prepareStatement(Queries.insertDemoTableData.concat("(?,?,?,?,?)"))) {
			
			pstmt.setInt(1, policyID);
			pstmt.setString(2, statecode);
			pstmt.setString(3, county);
			pstmt.setString(4, construction);
			pstmt.setString(5, notes);
			
			logger.info("notes: "+notes);
			logger.info(pstmt.toString());
			result = pstmt.execute();

		} catch (SQLException e) {
			logger.log(Level.ERROR,e);
		} catch (Exception e) {
			logger.log(Level.ERROR,e);
		}
		return result;
	}
	
	public boolean insertDataToGeoTableByID(int policyID, float latitude, float longitude) throws ClassNotFoundException {

		boolean result = false;
		Class.forName("com.mysql.jdbc.Driver");
		try(
				Connection c = getDbConnection();
				PreparedStatement pstmt = c.prepareStatement(Queries.insertGeoTableData.concat("(?,?,?)"))) {
			
			pstmt.setInt(1, policyID);
			pstmt.setFloat(2, latitude);
			pstmt.setFloat(3, longitude);
			
			result = pstmt.execute();

		} catch (SQLException e) {
			logger.log(Level.ERROR,e);
		} catch (Exception e) {
			logger.log(Level.ERROR,e);
		}
		return result;
	}
	
	//READ
	public List<QueryResponse> getAllData() throws Exception{
		List<QueryResponse> data = new ArrayList<QueryResponse>();

		Class.forName("com.mysql.jdbc.Driver");
		try(
				Connection c = getDbConnection();
				ResultSet rs = c.prepareStatement(Queries.querySelectAll).executeQuery()){
			
			
			while(rs.next()) {
				data.add(new QueryResponse(rs));
			}
			
		}
		return data;
	}
	
	//READ
	public List<QueryResponse> getDataByPolicyId(int policyID) throws SQLException, Exception {
		List<QueryResponse> result = new ArrayList<QueryResponse>();
		
		Class.forName("com.mysql.jdbc.Driver");
		try(
				Connection c = getDbConnection();
				PreparedStatement ps= c.prepareStatement(Queries.querySelectEntry)){

			try {
				ps.setInt(1, policyID);
				
				ResultSet rs  = ps.executeQuery();
				
				if(rs.next())
					result.add(new QueryResponse(rs));
				
				rs.close();
			} catch (SQLException e) {
				logger.log(Level.ERROR,e);
			}
						
		}
		return result;
		
	}
	
	//UPDATE
	public int updateDemoTableByID(int policyID, String statecode,
			String county, String construction, String notes) throws SQLException, Exception {
		
		int result = -1;
		
		Class.forName("com.mysql.jdbc.Driver");
		try(
				Connection c = getDbConnection();
				PreparedStatement ps= c.prepareStatement(Queries.queryUpdateSingleDemoEntry)){

			try {
				ps.setString(1, statecode);
				ps.setString(2, county);
				ps.setString(3, construction);
				ps.setString(4, notes);
				ps.setInt(5, policyID);
				
				result  = ps.executeUpdate();
								
			} catch (SQLException e) {
				logger.log(Level.ERROR,e);
			}
						
		}
		return result;
	}
	
	//UPDATE
	public int updateGeoTableByID(int policyID, float latitude, float longitude) throws SQLException, Exception {
		int result = -1;
		Class.forName("com.mysql.jdbc.Driver");
		try(
				Connection c = getDbConnection();
				PreparedStatement ps= c.prepareStatement(Queries.queryUpdateSingleGeoEntry)){

			try {
				ps.setFloat(1, latitude);
				ps.setFloat(2, longitude);
				ps.setInt(3, policyID);
				
				result  = ps.executeUpdate();
								
			} catch (SQLException e) {
				logger.log(Level.ERROR,e);
			}
						
		}
		return result;
	}
	
	//DELETE
	public boolean deleteByPolicyID(int policyID) throws SQLException, Exception {
		return deleteDemoDataByPolicyID(policyID) == deleteGeoDataByPolicyID(policyID) ? true : false;
	}
	
	private boolean deleteGeoDataByPolicyID(int policyID) throws SQLException, Exception {
		return deleteEntry(Queries.queryDeleteSingleGeoEntry,policyID);
	}
	
	private boolean deleteDemoDataByPolicyID(int policyID) throws SQLException, Exception {
		return deleteEntry(Queries.queryDeleteSingleDemoEntry,policyID);
	}
	
	private boolean deleteEntry(String deleteQuery, int policyID) throws SQLException, Exception {
		boolean result = false;

		Class.forName("com.mysql.jdbc.Driver");
		try(
				Connection c = getDbConnection();
				PreparedStatement ps= c.prepareStatement(deleteQuery)){

			try {
				ps.setInt(1, policyID);
				
				result  = ps.execute();		
				
			} catch (SQLException e) {
				logger.log(Level.ERROR,e);
			}
						
		}
		return result;
	}
}
