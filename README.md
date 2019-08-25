# demoRestAPI
 
This sample REST service does the following:
1. Loads data from a CSV file into a MySQL database
2. Exposes an API to Create, Update, Read and Delete data from the tables created in step 1.
3. Provides a client-side interface that presents the data in a YUI table as well as a Geo-location map.

Requirements:
1. MySQL
2. Apache Tomcat - tested with 8.5
3. ANT builder to create jar and war file

Update the rest-api.properties file with the full path to the csv file before build war file.
