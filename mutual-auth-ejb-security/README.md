mutual-auth-ejb-security: Using Java EE Declarative Security to Control Access using 2 way SSL mutual authentication 
===================
Author: Sherif F. Makary, Claude Adjai  
Level: Intermediate  
Technologies: EJB, Security  
Summary: The `mutual-auth-ejb-security` quickstart demonstrates the use of Java EE declarative security to control access to Servlets and EJBs in JBoss EAP through 2 way SSL mutual authentication between client and server.  
Target Product: JBoss EAP 7  
Source: <https://github.com/rhtconsulting/eap-quickstarts>  

What is it?
-----------

The `mutual-auth-ejb-security` quickstart demonstrates the use of Java EE declarative security to control access to Servlets and EJBs in JBoss EAP through 2 way SSL mutual authentication between client and server in Red Hat JBoss Enterprise Application Platform 7 or later. The focus is on using 2 way SSL authentication.

The application is basically a simple HTML5 front-end using secured servlet and EJB on the backend.

 * SecuredEJBServlet.java - entrypoint to the web application 
 * SecuredEJB.java - provide logged in principal information through the security context  
 * Web.xml - provides declarative security through security constraint tags for the servlet
 *         - enforces encryption through the use of transport guarantee user data constraint.
 *         - uses an auth method of CLIENT-CERT
 * jboss-web.xml - declares the JAAS login module used to secure the application
 * index.html - is a jQuery augmented plain old HTML5 web page

The example can be deployed using Maven from the command line or from Eclipse using JBoss Tools.

This quickstart takes the following steps to implement EJB security:

1. Create keystore and truststore required for PKI based authentication.

2. Define a custom static module containing the custom JAAS login module to be used to process PKI certificate authentication and authorization

3. Define a custom security domain

4. Add the `@SecurityDomain("ejbuser")` security annotation to the EJB declaration to tell the EJB container to apply authorization to this EJB.
5. Add the `@RolesAllowed({ "ejbuser" })` annotation to the EJB declaration to authorize access only to users with `guest` role access rights.
6. Add the `@RolesAllowed({ "ejbuser" })` annotation to the Servlet declaration to authorize access only to users with `ejbuser` role access rights.
7. Add a `<security-constraint>` security constraint to the `WEB-INF/web.xml` file to force the login prompt.

8. Add a `<login-config>` security constraint to the `WEB-INF/web.xml` file to force the login prompt.

Note that most of those steps are done through provided CLI scripts or already added via annotation to the code. All that you will need to do is if you update any part make sure you follow through and update the required affected sections appropriately.


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
Run the provided create-certs-keystores.sh script to create the necessary keystores. By default it will create a root authority and two intermediate certificate authorities in a root authority keystore, a server keystore and truststore to be used to secure the Jboss EAP 7 instance, and a PKCS12 keystore for the ejbuser to be used to test the application from a browser. If more than one user is required update the script to add more web users.
Note that you will need to set your EAP7_HOME as an environment variable for the script and subsequent CLI scripts to run properly.

Import the ejbuser PKCS12 keystore into the browser 
---------------
Import the ejbuser keystore into the browser. If unclear on how to do this, lookup the right steps to imports certificates into the browser you are using. If you are using the default the keystore file is named `ejbuser.p12` and the password for both key and keystore is `password`

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

4. This will deploy `target/mutualauth-ejb-security.war` to the running instance of the server.


Access the application 
---------------------

The application will be running at the following URL <https://localhost:8443/mutualauth-ejb-security/>.


When you access the application, you are presented with a browser prompt for the PKI certificate to use. 

1. If you pick a certificate other than the one setup for this, the login challenge will be redisplayed.
2. When you login successfully using the PKI certificate for `ejbuser` or the user whose certificate you imported above under [Import the ejbuser PKCS12 keystore into the browser] (#import-the-ejbuser-pkcs12-keystore-into-the-browser), the browser displays the following security info:

        Successfully called Secured EJB

        Principal : CN=ejbuser, OU=eapquickstarts, OU=consulting, O=redhat, L=Denver, C=US
        Remote User : CN=ejbuser, OU=eapquickstarts, OU=consulting, O=redhat, L=Denver, C=US
        Authentication Type : CLIENT_CERT
        
3. Now close and reopen the brower session and access the application using a different PKI certificate like `webuser` credentials. In this case, the Servlet, which only allows the `ejbuser` role, restricts the access and you get a security exception similar to the following: 

        HTTP Status 403 - Access to the requested resource has been denied

        type Status report
        message Access to the requested resource has been denied
        description Access to the specified resource (Access to the requested resource has been denied) has been forbidden.

Note that if you use a different certificate you will need to update the you will be prompted for a certificate. Select one of the certificates you imported into your browser above under [Import the ejbuser PKCS12 keystore into the browser] (#import-the-ejbuser-pkcs12-keystore-into-the-browser).

4. Next, change the EJB (SecuredEJB.java) to a different role, for example, `@RolesAllowed({ "other-role" })`. Do not modify the `ejbuser` role in the Servlet (SecuredEJBServlet.java). Build and redeploy the quickstart, then close and reopen the browser and login using the `ejbuser` PKI certificate. This time the Servlet will allow the `ejbuser` access, but the EJB, which only allows the role `other-role`, will throw an EJBAccessException:

        HTTP Status 500

        message
        description  The server encountered an internal error () that prevented it from fulfilling this request.
        exception
        javax.ejb.EJBAccessException: WFLYEJB0364: Invocation on method: public java.lang.String org.jboss.as.quickstarts.ejb_security.SecuredEJB.getSecurityInfo() of bean: SecuredEJB is not allowed


Undeploy the Archive
--------------------

1. Make sure you have started the JBoss EAP server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn jboss-as:undeploy


Run the Quickstart in Red Hat JBoss Developer Studio or Eclipse
-------------------------------------
You can also start the server and deploy the quickstarts or run the Arquillian tests from Eclipse using JBoss tools. For more information, see [Use JBoss Developer Studio or Eclipse to Run the Quickstarts](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/USE_JBDS.md#use-jboss-developer-studio-or-eclipse-to-run-the-quickstarts) 

You can also start the server and deploy the quickstarts or run the Arquillian tests from Eclipse using JBoss tools. For general information about how to import a quickstart, add a JBoss EAP server, and build and deploy a quickstart, see [Use JBoss Developer Studio or Eclipse to Run the Quickstarts](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/USE_JBDS.md#use-jboss-developer-studio-or-eclipse-to-run-the-quickstarts) 

* Be sure to [Generate the necessary keystores and truststores](#generate-the-necessary-keystores-and-truststores) as well as [Import the ejbuser PKCS12 keystore into the browser] (#import-the-ejbuser-PKCS12-keystore-into-the-browser) as described above.
* To deploy the server project, right-click on the `mutualauth-ejb-security` project and choose `Run As` --> `Run on Server`.
* You are presented with a browser login challenge. Select the appropriate PKI certificate as described above to access and test the running application.


Debug the Application
------------------------------------

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

        mvn dependency:sources
        mvn dependency:resolve -Dclassifier=javadoc


<!-- Build and Deploy the Quickstart to OpenShift - Coming soon! -->



