# SemanticAlignmentTool
This is the current version of the Semantic Alignment Tool of Cloud4All.

The Semantic Alignment Tool is a portlet using the [Liferay Platform] (http://www.liferay.com/). In order to build and run the portlet use the following instructions.

1)	Download and install [Eclipse] (https://eclipse.org/).

2)	Download and extract the [Liferay Portal 6.1.1] (http://sourceforge.net/projects/lportal/files/Liferay%20Portal/6.1.1%20GA2/liferay-portal-tomcat-6.1.1-ce-ga2-20120731132656558.zip/download) and [Liferay Plugins SDK] (http://sourceforge.net/projects/lportal/files/Liferay%20Portal/6.1.1%20GA2/liferay-plugins-sdk-6.1.1-ce-ga2-20121004092655026.zip/download). Use the suggested versions to avoid incompatibility problems. 

3)	Install the Liferay Portal and Liferay Plugins SDK in Eclipse using the following [instructions] (http://www.liferay.com/community/wiki/-/wiki/Main/Liferay+IDE+Getting+Started+Tutorial). By the end of this step you should have created a new Liferay portal (Tomcat) server in Eclipse.

4)	Download the Semantic Alignment Tool and import it in Eclipse. 

5)	Add the portlet your Liferay portal (Tomcat Server) by right clicking on the server and selecting “Add and Remove” option. Start the server and the portlet will be deployed in your http://localhost:8080. 

6)	Visit http://localhost:8080 and access Liferay portal. This page will show basic configuration page and needs to fill information like Portal Name, Default Language, Admin First Name, Admin Last Name, Admin Email (default is test@liferay.com). Use the default option for the database and click on Finish Configuration in order to store your changes. Once you’ve configured it successfully, click Go to My Portal. Accept the terms by clicking on I Agree. Enter your password that will be used for logging into your Portal later on and click on save.

7)	By this step you should have installed Liferay Portal, run and access it through http://localhost:8080 entering your mail and password to log into your Dashboard. Once you log into your Dashboard, click on Add button and a new panel will be viewed and set of tabs are shown. Select More and expand the Sample node. Drag Semantic Alignment Tool into your dashboard and refresh the page. By the end of this step you should be able to see and test Semantic Alignment Tool.

## Funding Acknowledgement

The research leading to these results has received funding from the European
Union's Seventh Framework Programme (FP7/2007-2013) under grant agreement No.289016
([Cloud4all](http://www.cloud4all.info/)).
