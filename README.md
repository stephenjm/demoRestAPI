# demoRestAPI
 
This sample REST service does the following:
1. Loads data from a CSV file into a MySQL database
2. Exposes an API to Create, Update, Read and Delete data from the tables created in step 1.
3. Provides a client-side interface that presents the data in a YUI table as well as a Geo-location map.

Requirements:
1. MySQL
2. Apache Tomcat - tested with 8.5
3. ANT builder to create jar and war file

Before running ANT to execute the build.xml targets, do the following:
1. Run the create.sql script to add the two tables to the schema.
2. Update the rest-api.properties file with the database username and password to match the credentials you've configured in your MySQL instance.
3. Update the rest-api.properties file with the full path to the csv file.

To Do:
1. You'll notice the user authentication and authorization is incomplete - so, that's something you'll have to do.
2. The logging will also need to be modified in order to allow for the log level to be used to adjust what's written.

Apart from that, feel free to play around with this. Modify as you see fit and have fun.
If you have any pointers/comments then feel free to let me know.

Next project will use springboot.
