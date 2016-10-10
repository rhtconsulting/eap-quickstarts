mutual-auth-rs-helloworld: REST Hello World Example using 2 way SSL mutual authentication 
===================
Author: Claude Adjai  
Level: Beginner  
Technologies: JAX-RS, JAAS login module  
Summary: The `mutual-auth-rs-helloworld` quickstart demonstrates the use of 2 way SL mutual authentication between client and server using a simple REST service.  
Target Product: JBoss EAP 7  
Source: <https://github.com/rhtconsulting/eap-quickstarts>  

What is it?
-----------

The `mutual-auth-rs-helloworld` quickstart demonstrates tthe use of 2 way SL mutual authentication between client and server using a simple REST service in Red Hat JBoss Enterprise Application Platform 7 or later using REST architecture.

The application is basically a simple HTML5 front-end using RESTful services on the backend.

 * HelloGuest.java - establishes the RESTful endpoints using JAX-RS 
 * HelloGuestService.java - returns a greeting string to the passed in name
 * Web.xml - maps RESTful endpoints to "/rest" and enforce encryption through the use of transport
 * guarantee user data constraint. It also uses an auth method of CLIENT-CERT
 * jboss-web.xml - declares the JAAS login module used to secure the application
 * index.html - is a jQuery augmented plain old HTML5 web page

The example can be deployed using Maven from the command line or from Eclipse using JBoss Tools.

System requirements
-------------------

The application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform 7 or later. 

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later. See [Configure Maven for JBoss EAP 7](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN_JBOSS_EAP7.md#configure-maven-to-build-and-deploy-the-quickstarts) to make sure you are configured correctly for testing the quickstarts.

An HTML5 compatible browser such as Chrome, Safari 5+, Firefox 5+, or IE 9+ is required.

With the prerequisites out of the way, you're ready to build and deploy.

Start with a Clean JBoss EAP Install
--------------------------------------

It is important to start with a clean version of JBoss EAP before testing this quickstart. Be sure to unzip or install a fresh JBoss EAP instance. 


Use of EAP7_HOME
---------------

In the following instructions, replace `EAP7_HOME` with the actual path to your JBoss EAP installation. The installation path is described in detail here: [Use of EAP7_HOME and JBOSS_HOME Variables](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/USE_OF_EAP7_HOME.md#use-of-eap_home-and-jboss_home-variables).

Generate the necessary keystores and truststores 
---------------
Run the provided create-certs-keystores.sh script to create the necessary keystores. By default it will create a root authority and two intermediate certificate authorities in a root authority keystore, a server keystore and truststore to be used to secure the Jboss EAP 7 instance, and a PKCS12 keystore for the webuser to be used to test the application from a browser. If more than one user is required update the script to add more web users.
Note that you will need to set your EAP7_HOME as an environment variable for the script and subsequent CLI scripts to run properly.

Import the webuser PKCS12 keystore into the browser 
---------------
Import the webuser keystore into the browser. If unclear on how to do this, lookup the right steps to imports certificates into the browser you are using. If you are using the default the keystore file is named `webuser.p12` and the password for both key and keystore is `password`

Start the JBoss EAP Server
-------------------------

1. Open a command line and navigate to the root of the JBoss EAP directory.
2. The following shows the command line to start the server with the default profile:

        For Linux:   EAP7_HOME/bin/standalone.sh
        For Windows: EAP7_HOME\bin\standalone.bat


Configure the JBoss EAP Server
---------------------------
You can configure the EAP 7 instance server by running JBoss CLI commands. For your convenience, this quickstart batches the commands into a `secure-jbosseap.cli` script provided in the root directory of this quickstart. 

1. Run the config-eap7.sh script to configure the customer JAAS SSL login module used in this quickstart. If you are running from windows then you can manually setup the module by creating the following directory ext-cert-login-module/main under the modules directory of your new EAP7 install. copy the module.xml and the extended-certificate-login-module.jar into that directory. 

2. Start with a fresh instance of the JBoss EAP as noted above under [Start with a Clean JBoss EAP Install](#start-with-a-clean-jboss-eap-install).

3. Run the provided cli `secure-jbosseap.cli` using the following command from the `EAP_HOME/bin` directory `./jboss-cli.sh -c --file=secure-jbosseap.cli`

4. Make sure you have created the necessary keystores as specified above under [Generate the necessary keystores and truststores](#generate-the-necessary-keystores-and-truststores). 

Build and Deploy the Quickstart
-------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../README.md#build-and-deploy-the-quickstarts) for complete instructions and additional options._

1. Make sure you have started the JBoss EAP server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy both the client and service applications:

        mvn clean package wildfly:deploy

4. This will deploy `target/mutualauth-rs-helloworld.war` to the running instance of the server.


Access the application 
---------------------

The application will be running at the following URL <https://localhost:8443/mutualauth-rs-helloworld/>.


* The *XML* content can be tested by sending an HTTP *POST* to the following URL: <https://localhost:8443/mutualauth-rs-helloworld/rest/xml/> 

Note that you will be prompted for a certificate. Select one of the certificates you imported into your browser above under [Import the webuser PKCS12 keystore into the browser] (#import-the-webuser-pkcs12-keystore-into-the-browser).


Undeploy the Archive
--------------------

1. Make sure you have started the JBoss EAP server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn jboss-as:undeploy


Run the Quickstart in Red Hat JBoss Developer Studio or Eclipse
-------------------------------------
You can also start the server and deploy the quickstarts or run the Arquillian tests from Eclipse using JBoss tools. For more information, see [Use JBoss Developer Studio or Eclipse to Run the Quickstarts](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/USE_JBDS.md#use-jboss-developer-studio-or-eclipse-to-run-the-quickstarts) 


Debug the Application
------------------------------------

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

        mvn dependency:sources
        mvn dependency:resolve -Dclassifier=javadoc


<!-- Build and Deploy the Quickstart to OpenShift - Coming soon! -->



