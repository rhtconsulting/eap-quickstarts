mutual-auth-remoting-ejb: Using 2 way SSL mutual authentication to control access to remoting connector
=====================================
Author: Jaikiran Pai, Mike Musgrove, Claude Adjai 
Level: Advance  
Technologies: EJB, JNDI  
Summary: The `mutualauth-remoting-ejb` quickstart uses *2Way SSL* ,*EJB* and *JNDI* to demonstrate how to access an EJB, deployed to JBoss EAP, from a remote Java client application through mutual PKI based authentication.  
Target Product: JBoss EAP 7   
Source: <https://github.com/rhtconsulting/eap-quickstarts>   

What is it?
-----------

The `mutualauth-remoting-ejb` demonstrates the use of PKI based mutual authentication on the remoting connector to control access to EJBs in JBoss EAP. The focus is on using 2 way SSL authentication between client and server in Red Hat JBoss Enterprise Application Platform 7 or later. 

There are two components to this example: 

1. A server side component:

    The server component is comprised of a stateful EJB and a stateless EJB. It provides both an EJB JAR that is deployed to the server and a JAR file containing the remote business interfaces required by the remote client application. Each of the EJB is secured using a PKI certificate based security domain with access rights granted to specific roles.
2. A remote client application that accesses the server component. 

    The remote client application depends on the remote business interfaces from the server component. This application looks up the stateless and stateful beans via JNDI and invokes a number of methods on them. Note that the lookup is done through the https-remoting protocol and therefore the client is required to provide a keystore as well as a truststore along with role derived from the client's DN.  

Each component is defined in its own standalone Maven module. The quickstart provides a top level Maven module to simplify the packaging of the artifacts.

This quickstart takes the following steps to implement EJB security:

1. Create keystore and truststore required for PKI based authentication.

2. Define a custom static module containing the custom JAAS login modules to be used to process PKI certificate authentication and authorization

3. Define a custom security domain

4. Define a security realm to be used to secure the https-listener for undertow 

5. Add the `@SecurityDomain("SSLRemotingSecurityDomain")` security annotation to the EJB declaration to tell the EJB container to apply authorization to this EJB.
6. Add the `@@DeclareRoles({ "client", "webuser", "ejbuser", "guest" })` annotation to the EJB declaration to authorize access only to users with one of the roles listed .
6. Add the `@RolesAllowed({ "client", "webuser", "ejbuser", "guest" })` annotation to the EJB declaration to authorize access only to users with one of the roles listed .
7. Update the various subsystems with EAP to use the security realm and security domain defined in steps 3 and 4 above .

Note that most of those steps are done through provided CLI scripts or already added via annotation to the code. All that you will need to do is if you update any part make sure you follow through and update the required affected sections appropriately.


System requirements
-------------------

The application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform 7 or later. 

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later. See [Configure Maven for JBoss EAP 7](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN_JBOSS_EAP7.md#configure-maven-to-build-and-deploy-the-quickstarts) to make sure you are configured correctly for testing the quickstarts.

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


Start the JBoss EAP Server
-------------------------

1. Open a command prompt and navigate to the root of the JBoss EAP directory.
2. The following shows the command line to start the server:

        For Linux:   EAP7_HOME/bin/standalone.sh
        For Windows: EAP7_HOME\bin\standalone.bat

Configure the JBoss EAP Server
---------------------------
You can configure the EAP 7 instance server by running JBoss CLI commands. For your convenience, this quickstart batches the commands into a `secure-jbosseap.cli` script provided in the root directory of this quickstart. 

1. Run the config-eap7.sh script to configure the customer JAAS SSL login modules used in this quickstart. If you are running from windows then you can manually setup the module by creating the following directory custom-login-module/main under the modules directory of your new EAP7 install. copy the module.xml and the extended-certificate-login-module.jar extended-remoting-login-module.jar into that directory. 

2. Start with a fresh instance of the JBoss EAP as noted above under [Start with a Clean JBoss EAP Install](#start-with-a-clean-jboss-eap-install).

3. Run the provided cli `secure-jbosseap.cli` using the following command from the `EAP_HOME/bin` directory `./jboss-cli.sh -c --file=secure-jbosseap.cli`

4. Make sure you have created the necessary keystores as specified above under [Generate the necessary keystores and truststores](#generate-the-necessary-keystores-and-truststores). 


Build and Deploy the Quickstart
-------------------------

Since this quickstart builds two separate components, you can not use the standard *Build and Deploy* commands used by most of the other quickstarts. You must follow these steps to build, deploy, and run this quickstart.

1. Make sure you have started the JBoss EAP server. See the instructions in the previous section.
2. Open a command prompt and navigate to the ejb-remote quickstart directory
3. Build and install the server side component:
    * Navigate to the server-side subdirectory:

            cd server-side
    * Build the EJB and client interfaces JARs and install them in your local Maven repository.

            mvn clean install        
    * Deploy the EJB JAR to your server. 
    Use the CLI or the web console to deploy the built artifact `server-side/target/mutualauth-	remoting-ejb-server-side.jar`. 
    You can also use the following command to perform the deployment to EAP

            mvn wildfly:deploy
            (or mvn clean package wildfly:deploy)
            
4. Build and run the client application as an executable jar
    * Navigate to the client subdirectory:

            cd ../client
    * Compile the client code
		
          mvn clean compile
          (or mvn clean install)
          
    Note that this compiles a client jar with all dependencies and expects all JNDI properties values to be set programmatically based on input from the user.
    
    
    * Execute the client application using the following command
		
		java -jar target/mutualauth-remoting-ejb-client.jar 
		
		You will be prompted for the values for the JNDI parameters required to run the client.
	

Investigate the Console Output
-------------------------

When the client application runs, it performs the following steps:

1. Obtains a stateless session bean instance.
2. Sends method invocations to the stateless bean to add two numbers, and then displays the result.
3. Sends a second invocation to the stateless bean subtract two numbers, and then displays the result.
4. Obtains a stateful session bean instance.
5. Sends several method invocations to the stateful bean to increment a field in the bean, displaying the result each time.
6. Sends several method invocations to the stateful bean to decrement a field in the bean, displaying the result each time.

The output in the terminal window  will look like the following:

      Obtained a remote stateless calculator for invocation
      Adding 204 and 340 via the remote stateless calculator deployed on the server
      Remote calculator returned sum = 544
      Subtracting 2332 from 3434 via the remote stateless calculator deployed on the server
      Remote calculator returned difference = 1102
      Obtained a remote stateful counter for invocation
      Counter will now be incremented 5 times
      Incrementing counter
      Count after increment is 1
      Incrementing counter
      Count after increment is 2
      Incrementing counter
      Count after increment is 3
      Incrementing counter
      Count after increment is 4
      Incrementing counter
      Count after increment is 5
      Counter will now be decremented 5 times
      Decrementing counter
      Count after decrement is 4
      Decrementing counter
      Count after decrement is 3
      Decrementing counter
      Count after decrement is 2
      Decrementing counter
      Count after decrement is 1
      Decrementing counter
      Count after decrement is 0

Logging statements have been removed from this output here to make it clearer.


Undeploy the Archive
--------------------

To undeploy the server side component from the JBoss EAP server:

1. Navigate to the server-side subdirectory:

        cd ../server-side
2. Type the following command:

        mvn wildfly:undeploy



Run the Quickstart in Red Hat JBoss Developer Studio or Eclipse
-------------------------------------
You can also start the server and deploy the quickstarts or run the Arquillian tests from Eclipse using JBoss tools. For general information about how to import a quickstart, add a JBoss EAP server, and build and deploy a quickstart, see [Use JBoss Developer Studio or Eclipse to Run the Quickstarts](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/USE_JBDS.md#use-jboss-developer-studio-or-eclipse-to-run-the-quickstarts) 


This quickstart consists of multiple projects, so it deploys and runs differently in JBoss Developer Studio than the other quickstarts.

1. Install the required Maven artifacts and deploy the server side of the quickstart project.
   * Right-click on the `mutualauth-remoting-ejb-server-side` project and choose `Run As` --> `Maven Install`.
   * Right-click on the `mutualauth-remoting-ejb-server-side` project and choose `Run As` --> `Run on Server`.

2. Build and run the client side of the quickstart project.
   * Right-click on the `mutualauth-remoting-ejb-client` project and choose `Run As` --> `Java Application`. 
   * In the `Select Java Application` window, choose `RemoteEJBClient - com.redhat.consulting.eapquickstarts.mutualauth.remoting.ejb.client` and click `OK`.
   
   You will need to provide values for each of the arguments using Arguments Tab on the the  Run Configurations Wizard before running the client.
   
   * The client output displays in the `Console` window.
   

Debug the Application
------------------------------------

If you want to debug the source code of any library in the project, run the following command to pull the source into your local repository. The IDE should then detect it.

        mvn dependency:sources

