package com.demo.rest.dbinterface;

import java.sql.DriverManager;
import java.sql.Connection;
import java.text.MessageFormat;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class DbPool extends Thread {
	private static Logger logger = Logger.getLogger(DbPool.class);
	
	protected String identifier = "db";

	private int maxConnections;
	
	public DbPool(String name) {
		super(name);
	}

	public void setupDriver(String uri, String username, String password, int maxConnections, int connectionTimeout) throws Exception {		
		this.maxConnections = maxConnections;
		
		//
		// First, we'll need a ObjectPool that serves as the
		// actual pool of connections.
		//
		// We'll use a GenericObjectPool instance, although
		// any ObjectPool implementation will suffice.
		//
		ObjectPool connectionPool = 
		new GenericObjectPool(null, maxConnections, GenericObjectPool.WHEN_EXHAUSTED_BLOCK, connectionTimeout, 1, true, true);
		
		//
		// Next, we'll create a ConnectionFactory that the
		// pool will use to create Connections.
		// We'll use the DriverManagerConnectionFactory,
		// using the connect string passed in the command line
		// arguments.
		//
		ConnectionFactory connectionFactory = 
		new DriverManagerConnectionFactory(uri, username, password);
		
		//
		// Now we'll create the PoolableConnectionFactory, which wraps
		// the "real" Connections created by the ConnectionFactory with
		// the classes that implement the pooling functionality.
		//
		PoolableConnectionFactory poolableConnectionFactory = 
		new PoolableConnectionFactory(connectionFactory,connectionPool,null,null,false,true);
		
		//
		// Finally, we create the PoolingDriver itself...
		//
		Class.forName("org.apache.commons.dbcp.PoolingDriver");
		PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
		
		//
		// ...and register our pool with it.
		//
		driver.registerPool(identifier,connectionPool);
	}
	
	public void destroy() {
		try{
			shutdownDriver();
		
		} catch(Exception ex){
			logger.log(Level.ERROR,ex);
		}		
	}
	
	public void shutdownDriver() throws Exception {
		Class.forName("org.apache.commons.dbcp.PoolingDriver");
		PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
		
		driver.closePool(identifier);
	}
	
	public Connection getConnectionToDatabase() throws Exception {
		Connection connection = null;
		boolean success = true;
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:apache:commons:dbcp:"+ identifier);
			
		} catch(Exception ex){		
			logger.log(Level.ERROR,ex);
			logger.error(MessageFormat.format("failedToConnect", identifier));
		
			success = false;
		}
	
		if(success){
			if(logger.isEnabledFor(Level.TRACE)){
				logger.trace("successfullConnect");
			}
			return connection; 
		}
		
		throw new Exception("exitingString");	
	}

	public void closeConnection(Connection connection) throws Exception {
		try {
			connection.close();
		} catch(Exception ex){
				logger.error(ex.toString());
		}
	}
}