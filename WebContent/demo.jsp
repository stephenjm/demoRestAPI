<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
		<meta http-equiv="Pragma" content="no-cache"/>
		<meta http-equiv="Cache-Control" content="no-cache"/>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<script type="text/javascript" src="js/yui-min.js"></script>
		<link rel="stylesheet" type="text/css" href="./css/demo.css" />	
		<link rel="stylesheet" href="https://unpkg.com/leaflet@1.5.1/dist/leaflet.css"
		   integrity="sha512-xwE/Az9zrjBIphAcBb3F6JVqxf46+CDLwfLMHloNu6KEQCAWi6HcDUbeOfBIptF7tcCzusKFjFw2yuvEpDL9wQ=="
		   crossorigin=""/>	
		    <!-- Make sure you put this AFTER Leaflet's CSS -->
		<script src="https://unpkg.com/leaflet@1.5.1/dist/leaflet.js"
		   integrity="sha512-GffPMF3RvMeYyc1LWMHtK8EbPv0iNZ8/oTtHPx9/cc2ILxQ+u905qIwdpULaqDkyBKgOaB57QTMg7ztg8Jm2Og=="
		   crossorigin=""></script>	
		<title>Demo Table</title>
	</head>
	<body class="yui-skin-sam demo-skin">
		<div id="top1">
		  	<h2>Demo Random Data</h2>
		</div>
		<div id="mapid"></div>
		<table width="100%" height="100%" cellspacing="2" cellpading="5" style="border:0">
			<tr width="100%">
	        	<td>
					<div id="ccom" width="inherit">
						<table width="100%" height="100%" cellspacing="5" cellpading="5" style="border:1px dotted #dedede">
							<tr width="100%">
								<td valign="top" halign="left" width="100%">
									<table width="100%" height="100%" ><tr><td  overflow="auto">
										<div id="render" class="example yui3-skin-sam" style="height:530px; width:100%; overflow:auto"></div>
										</td></tr>
										<tr><td width="100%"  height="10%">
											<div id="buttonArea"></div>
										</td>
										</tr>
									</table>
								</td>
								<td valign="middle" halign="left">
									<div id="formArea">
										<form id="demoForm" title="Demo Form">
											<a id="formAreaClose"><img style="float:right" src="css/cc_close.gif" alt="CLOSE"/></a>
											<table width="100%" height="100%">
												<tr><td align="middle" colspan="2">
													<label id="edit" style="width: 100%; text-align:right">Edit a single database entry</label>
													<label id="insert">Insert new database entry</label>
													<label id="search">Search for single or multiple data.<br/><i>(PolicyID only for now)</i></label>
													</td></tr>
												<tr><td><label id="policyID">Policy ID</label>
													<td><input title="This is an internal code that may be used to identify the row" type="text"  id="policyID" name="policyID" style="width: 100%; text-align:left" maxlength="10"/>
												<tr><td><label>State Code</label>
													<td><input type="text" title="This is the statecode"  id="statecode" name="statecode" style="width: 100%; text-align:left" maxlength="3"/>
												<tr><td><label>County</label>
													<td><input type="text" title="This is the county" id="county" name="county" style="width: 100%; text-align:left" maxlength="20"/>
												<tr><td><label>Construction</label>
													<td><input type="text" title="This is the construction" id="construction" name="construction" style="width: 100%; text-align:left" maxlength="20"/>
												<tr><td><label>Latitude</label>
													<td><input type="text" title="This is the latitude" id="latitude" name="latitude" style="width: 100%; text-align:left" maxlength="7"/>
												<tr><td><label>Longitude</label>
													<td><input type="text"  title="This is the longitude" id="longitude" name="longitude" style="width: 100%; text-align:left" maxlength="7"/>
													</td></tr>
											</table>					
											<div id="formButtonArea"  width="90%" align="center">
												<button class="demo-button" id="demoCreate" style="width:23%">Create</button>
												<button class="demo-button" id="demoUpdate" style="width:23%">Update</button>				
												<button class="demo-button" id="demoDelete" style="width:23%">Delete</button>
												<button class="demo-button" id="demoClear" style="width:23%">Clear</button>						
												<button class="demo-button" id="demoSearch" style="width:23%">Search</button>					
												<button class="demo-button" id="demoUndoSearch" style="width:23%">Undo Search</button>						
												<button class="demo-button" id="demoCancel" style="width:23%">Cancel</button>
											</div>
										</form>
									</div>
								</td>
							</tr>
						</table>
					</div>
	        	</td>
	        </tr>       
	    </table>	
		<script type="text/javascript" src="js/jquery-1.7.1.min.js"></script>
		<script type="text/javascript" src="js/demo.js"></script>	               
	</body>
</html>