// The root URL for the RESTful services
var rootURL = "service";
var demoData = null;
var demoLive = {};
var mymap = null;

control();


$('#formAreaClose').click( function() { 
	$('#formArea').hide();
});
function refreshView(){
	$('#render').empty();
	$('#buttonArea').empty();
	$('#formArea').hide();
	$('#demoForm').hide();
	return false;
}

function control() {
	refreshView();
	$('#render').append($('<div class="yui-dt table" id="demo_table"></div><div id ="paginator"></div>'));

	$('#buttonArea').append($('<button id="newdemoData" class="demo-button" style="width:16%">Add New</button>'));
	$('#buttonArea').append($('<button class="demo-button" id="demoSearch" style="width:20%">Search</button>'));
	$('#buttonArea').append($('<button class="demo-button" id="demoUpdate" style="width:16%">Edit</button>'));			
	$('#buttonArea').append($('<button class="demo-button" id="demoDelete" style="width:16%">Delete</button>'));		
	$('#buttonArea').append($('<button class="demo-button" id="demoGeoLocation" style="width:16%">GeoLocate</button>'));

	readAlldemoData();	
	return false;
}

//CRUD demo Number Data
function refreshdemoListView(){
	refreshView();
	$('#render').append($('<div class="yui-dt table" id="demo_table"></div><div id ="paginator"></div>'));

	$('#buttonArea').append($('<button id="newdemoData" class="demo-button" style="width:16%">Add New</button>'));
	$('#buttonArea').append($('<button class="demo-button" id="demoSearch" style="width:20%">Search</button>'));
	$('#buttonArea').append($('<button class="demo-button" id="demoUpdate" style="width:16%">Edit</button>'));			
	$('#buttonArea').append($('<button class="demo-button" id="demoDelete" style="width:16%">Delete</button>'));
	$('#buttonArea').append($('<button class="demo-button" id="demoGeoLocation" style="width:16%">GeoLocate</button>'));

	readAlldemoData();	
	return false;
}

function builddemoList(data){
	$('#formArea').hide();
	$('#demoForm').hide();
	demoData = data;
	
	YUI().use("gallery-datatable-selection","datatable-paginator","datasource-local","datasource-get","datasource-jsonschema","gallery-datatable-celleditor-popup","datatable-datasource","datatable-sort","datatable-scroll", function (Y) {
        tempSource = new Y.DataSource.Local({source: data});
        tempSource .plug(Y.Plugin.DataSourceJSONSchema, {
            schema: {
                resultFields: ["policyID","statecode","county","construction","pointLatitude","pointLongitude","notes"]
            }
        });

		Y.Paginator = Y.Base.create('paginator',Y.DataTable.Paginator.View, [],{
		_modelChange : function (e) {
            var changed = e.changed,
                page = (changed && changed.page),
                itemsPerPage = (changed && changed.itemsPerPage),
                totalItems = (changed && changed.totalItems);

            if (totalItems) {
                this._updateControlsUI(e.target.get('page'));
            }
            if (page) {
                this._updateControlsUI(page.newVal);
            }
            if (itemsPerPage) {
                this._updateItemsPerPageUI(itemsPerPage.newVal);
                if (!page) {
                    this._updateControlsUI(e.target.get('page'));
                }
            }

        }});
		
        var cols = [
            
            {key:"policyID",sortable:true, label:"PolicyID",
            	title: 'This is an internal code. This field is mandatory',
                allowHTML: true, 
                width:"10%"              
            },
            {key:"statecode", label:"State Code", sortable:true, allowHTML: true, title: 'This is the state code', name:"State code", width:"10%"},
            {key:"county", label:"County", sortable:true, allowHTML: true, title: 'This is the county', name:"County", width:"25%"},
            {key:"construction", label:"Construction", sortable:true, allowHTML: true, title: 'This is the construction', name:"construction", width:"10%"},
            {key:"pointLatitude", label:"Latitude", sortable:true, allowHTML: true, title: 'This is the latitude', name:"pointLatitude"},
            {key:"pointLongitude", label:"Longitude", sortable:true, allowHTML: true, title: 'This is the longitude', name:"pointLongitude"},
            {key:"notes", label:"Notes", allowHTML: true, title: 'This is the notes', name:"notes"}
        ];
        
        table = new Y.DataTable({
			paginatorView: "Paginator",
            columnset: cols,
            scrollable: "y",
            editable: false,
            defaultEditor: 'textarea',
            editOpenType: 'dblclick',
            summary: "Demo Data",
			highlightMode: 'cell',
			selectionMode: 'row',
			selectionMulti: true,
            sortable: ['policyID','statecode','county','construction','pointLatitude','pointLongitude'],
			rowsPerPage: 15,
			pageSizes: [15, 30, 45, 60, 'Show All']
        }); 
        
		table.after('sort', function(e){
			try{
				table.set('selectedRows',[0,1]);
			} catch(e){}
		});
		
		Y.one("#buttonArea #demoGeoLocation").on("click",function(){
			var array = this.get('selectedRows');
						
			displayOnMap(array[0].tr._node.cells[4].innerText, array[0].tr._node.cells[5].innerText);
			
		},table);
	
		Y.one("#buttonArea #demoUpdate").on("click",function(){
			var array = this.get('selectedRows');
			
			demoLive.policyID = array[0].tr._node.cells[0].innerText;
			demoLive.statecode = array[0].tr._node.cells[1].innerText;
			demoLive.county = array[0].tr._node.cells[2].innerText;
			demoLive.construction = array[0].tr._node.cells[3].innerText;
			demoLive.pointLatitude = array[0].tr._node.cells[4].innerText;
			demoLive.pointLongitude = array[0].tr._node.cells[5].innerText;
			demoLive.notes = array[0].tr._node.cells[6].innerText;
			
        	$('#insert').hide();
        	$('#edit').show();
        	$('#search').hide();
			document.getElementById("demoForm").reset();
			$('#demoForm #demoUndoSearch').hide();
        	$('#demoForm #demoUpdate').show();					
        	$('#demoForm #demoDelete').hide();
        	$('#demoForm #demoSearch').hide();	
        	$('#demoForm #demoCreate').hide();				
        	$('#demoForm #demoClear').hide();
        	
			builddemoForm(demoLive);
		
		},table);
		
		Y.one("#buttonArea #demoDelete").on("click",function(){
			var array = this.get('selectedRows');
			for(var i = 0; i < array.length; i++){
				var updatedURL = rootURL + '/data/'+array[i].tr._node.cells[0].innerText;

				if(confirm('Deleting Policy Code: ' + array[i].tr._node.cells[0].innerText)){			
					$.ajax({
						type: 'DELETE',
						url: updatedURL,
						error: function(jqXHR, textStatus, errorThrown){
							if(!(textStatus==("parsererror"))){
								alert('delete demoData: ' + textStatus);
							}
						}
					});
				}
			}
			refreshdemoListView();				
		},table);
	  
			
        table.plug(Y.Plugin.DataTableDataSource, { datasource: tempSource  });
        table.render("#demo_table");
        table.datasource.load();
        
     	    
		$('#newdemoData').click(function(){
			$('#insert').show();	
			$('#edit').hide();
			$('#search').hide();
			$('#demoForm #demoUpdate').hide();					
			$('#demoForm #demoDelete').hide();
			$('#demoForm #demoSearch').hide();
			$('#demoForm #demoUndoSearch').hide();	
			$('#demoForm #demoCreate').show();				
			$('#demoForm #demoClear').show();
			$('#formArea').show();
			$('#demoForm').show();
			$('#demoForm').trigger("reset");
			view = 1;
		});
        
        
        $('#buttonArea #demoSearch').click(function(){
        	$('#insert').hide();
        	$('#edit').hide();
        	$('#search').show();
			document.getElementById("demoForm").reset();
			$('#demoForm #demoUndoSearch').show();
        	$('#demoForm #demoUpdate').hide();					
        	$('#demoForm #demoDelete').hide();
        	$('#demoForm #demoSearch').show();	
        	$('#demoForm #demoCreate').hide();				
        	$('#demoForm #demoClear').show();
        	$('#formArea').show();
        	$('#demoForm').show();
			view = 3;
        });
    });	 
	
}

function displayOnMap(latitude, longitude){
	console.log(latitude, longitude);
	if (mymap !== undefined && mymap !== null) {
	  mymap.remove(); // should remove the map from UI and clean the inner children of DOM element
	}
	mymap = L.map('mapid').setView([latitude, longitude], 13);
				L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw', {
				maxZoom: 18,
				attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, ' +
					'<a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
					'Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
				id: 'mapbox.streets'
			}).addTo(mymap);
}

function builddemoForm(data){
	$('#formArea').show();
	$('#demoForm').show();	
	
	$('#demoForm').find('input#policyID').val(data.policyID);	
	$('#demoForm').find('input#statecode').val(data.statecode);	
	$('#demoForm').find('input#county').val(data.county);	
	$('#demoForm').find('input#construction').val(data.construction);	
	$('#demoForm').find('input#latitude').val(data.pointLatitude);	
	$('#demoForm').find('input#longitude').val(data.pointLongitude);
	$('#demoForm').find('input#notes').val(data.notes);	
	return false;
}

function readAlldemoData(){
	console.log('readAlldemoData');
	$.ajax({
		type: 'GET',
		headers: { 
			'Accept' : 'application/json'
		},
		url: rootURL + '/data',
		accepts: "application/json",
		success:  builddemoList,
		error: function(jqXHR, textStatus, errorThrown){
			alert('Find all demoData: '  + errorThrown +"\n"+jqXHR.responseText);
		}
	});			
	return false;
}

$('#demoForm #demoSearch').click(function(){
	readdemoDataByID($('input#policyID').val());		
	return false;
});
$('#demoForm #demoUndoSearch').click(function(){
	document.getElementById("demoForm").reset();
	readdemoDataByID($('input#policyID').val());		
	return false;
});
function readdemoDataByID(policyID){
	console.log('readdemoDataByID');
	refreshView();
	$('#render').append($('<div class="yui-dt table" id="demo_table"></div><div id ="paginator"></div>'));

	$('#buttonArea').append($('<button id="newdemoData" class="demo-button" style="width:16%">Add New</button>'));
	$('#buttonArea').append($('<button class="demo-button" id="demoSearch" style="width:20%">Search</button>'));
	$('#buttonArea').append($('<button class="demo-button" id="demoUpdate" style="width:16%">Edit</button>'));			
	$('#buttonArea').append($('<button class="demo-button" id="demoDelete" style="width:16%">Delete</button>'));
	$('#buttonArea').append($('<button class="demo-button" id="demoGeoLocation" style="width:16%">GeoLocate</button>'));

	if(!validatePolicyID(policyID)){
		console.log('invalid input'+policyID);
		refreshdemoListView();
		return false;
	} 
	$.ajax({
		type: 'GET',
		headers: { 
			'Accept' : 'application/json'
		},
		url: rootURL + '/data/'+policyID,
		success:  builddemoList,
		error: function(jqXHR, textStatus, errorThrown){
			alert('Find all demoDataBydemo: ' + errorThrown +"\n"+jqXHR.responseText);
		}
	});	
	
	return false;
}
$('#demoCreate').click(function() {
	if(entryAlreadyExists($('input#construction').val(),$('input#latitude').val(),$('input#statecode').val(),$('input#county').val())){
		alert('Combination of: {'+'construction-'+$('input#construction').val()+',latitude-'+$('input#latitude').val()+',statecode-'+$('input#statecode').val()+',county-'+$('input#county').val()+'} already exists');
		return false;
	} else{
		createNewdemoData($('input#policyID').val(),$('#demoForm').serialize());		
		return false;	
	}
});	
function createNewdemoData(policyID,formData){
	console.log('createNewdemoData');
	if(!validatePolicyID(policyID)){
		console.log('invalid input'+policyID);
		refreshdemoListView();
		return false;
	} 
	if(confirm('Creating new entry with Policy Code: ' + $('#demoForm').find('input#policyID').val())){
		$.ajax({
			type: 'POST',
			headers: { 
				'Accept' : 'text/plain',
				'Content-type' : 'application/x-www-form-urlencoded'
			},
			url: rootURL + '/data/'+policyID,
			data: formData,
			error: function(jqXHR, textStatus, errorThrown){
				if(!(textStatus==("parsererror"))){
					alert('Server Error: ' + errorThrown +"\n"+jqXHR.responseText+"\nData not added to database");
				}
			}
		});	
	}
	refreshdemoListView();
	return false;
}

$('#demoForm #demoUpdate').click(function() {
	if(!($('input#construction').val()+$('input#latitude').val()+$('input#statecode').val()+$('input#county').val()+$('input#longitude').val() == demoLive.construction+demoLive.latitude+demoLive.statecode+demoLive.county+demoLive.longitude) 
			&& entryAlreadyExists($('input#construction').val(),$('input#latitude').val(),$('input#statecode').val(),$('input#county').val(),$('input#longitude').val())){
		alert('Combination already exists');
		return false;
	} else{
		updatedemoData(
				demoLive.policyID,
				$('#demoForm').serialize());		
		return false;
	}
});	
function updatedemoData(policyID,formData){
	console.log('updatedemoData');
	var updatedURL = rootURL + '/data/'+policyID;
	if(!validatePolicyID(policyID)){
		console.log('invalid input'+policyID);
		refreshdemoListView();
		return false;
	} 
			
	if(confirm('Updating Policy Code: ' + policyID)){	
			$.ajax({
			type: 'PUT',
			headers: { 
				'Accept' : 'text/plain',
				'Content-type' : 'application/x-www-form-urlencoded'
			},
			url: updatedURL,
			dataType: "application/x-www-form-urlencoded",
			data: formData,
			error: function(jqXHR, textStatus, errorThrown){
				if(!(textStatus==("parsererror"))){
					alert('Server Error: ' + errorThrown +"\n"+jqXHR.responseText+"\nData not added to database");
				}
			}
		});	
	}
	refreshdemoListView();
	return false;
}

$('#demoForm #demoDelete').click(function() {
	deletedemoData($('input#policyID').val());		
	return false;
});	
function deletedemoData(policyID){
	console.log('deletedemoData');
	var updatedURL = rootURL + '/data/'+policyID;
	if(!validatePolicyID(policyID)){
		console.log('invalid input'+policyID);
		refreshdemoListView();
		return false;
	} 
	
	if(confirm('Deleting Policy Code: ' + policyID)){	
		$.ajax({
			type: 'DELETE',
			url: updatedURL,
			error: function(jqXHR, textStatus, errorThrown){
				if(!(textStatus==("parsererror"))){
					alert('delete demoData: ' + errorThrown +"\n"+jqXHR.responseText);
				}
			}
		});	
	}
	refreshdemoListView();	
	return false;
}

$('#demoForm #demoClear').click(function(){
	document.getElementById("demoForm").reset();		
	return false;
});
$('#demoForm #demoCancel').click(function(){
	$('#formArea').hide();
	document.getElementById("demoForm").reset();		
return false;
});
    
$('#demoForm input').keypress(function (e) {
	if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
		if(view == 1){
			$('#demoForm #demoCreate').click();
		}else if(view == 2) {
			$('#demoForm #demoUpdate').click();
		} else if(view == 3){
			$('#demoForm #demoSearch').click();
		} else { view = 0;}
		return false;
	} else {
		return true;
	}
});

function entryAlreadyExists(construction,latitude,statecode,county){
	for(var x = 0; x < demoData.length; x++){
		if(demoData[x].statecode==statecode &&
			demoData[x].county==county &&
			demoData[x].construction==construction &&
			demoData[x].pointLatitude==latitude &&
			demoData[x].pointLongitude==longitude){
			return true;
		}
	}
	return false;
}

function validatePolicyID(inputtxt){
	var numbers = /^[0-9]+$/;
	if(inputtxt.match(numbers)){
		return true;
	} else {
		alert('Invalid input - enter digits');
		return false;
	}
} 