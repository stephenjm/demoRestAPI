package com.demo.rest.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement(name="Result")
@XmlType(name="success",propOrder = {
	    "policyID",
	    "statecode",
	    "county",
	    "construction",
	    "point_latitude",
	    "point_longitude"
	})
public class QueryResponse {
	private int policyID;
	private String statecode;
	private String county;
	private String construction;
	private Float pointLatitude;
	private Float pointLongitude;
	
	private static Logger logger = Logger.getLogger(QueryResponse.class);
	
	public QueryResponse(){}
	
	public QueryResponse(ResultSet queryResult){
		try{
			setPolicyID(queryResult.getInt("policyID"));
			setStatecode(queryResult.getString("statecode"));
			setCounty(queryResult.getString("county"));
			setConstruction(queryResult.getString("construction"));
			setPointLatitude(queryResult.getFloat("point_latitude"));
			setPointLongitude(queryResult.getFloat("point_longitude"));
		} catch(Exception e){
			logger.log(Level.ERROR,e);
		}
	}

	public int getPolicyID() {
		return policyID;
	}

	public void setPolicyID(int policyID) {
		this.policyID = policyID;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getConstruction() {
		return construction;
	}

	public void setConstruction(String construction) {
		this.construction = construction;
	}

	public Float getPointLatitude() {
		return pointLatitude;
	}

	public void setPointLatitude(Float point_latitude) {
		this.pointLatitude = point_latitude;
	}

	public String getStatecode() {
		return statecode;
	}

	public void setStatecode(String statecode) {
		this.statecode = statecode;
	}

	public Float getPointLongitude() {
		return pointLongitude;
	}

	public void setPointLongitude(Float point_longitude) {
		this.pointLongitude = point_longitude;
	}


    public static final Comparator<QueryResponse> sortByCounty = new Comparator<QueryResponse>() {
        public int compare(QueryResponse message1, QueryResponse message2) {
        	return message1.getCounty().compareTo(message2.getCounty());       	
        }
    };
}
