JBoss EAP Quickstart: HornetQ Core Bridge Example
=================================================
Author: Serge Pagop, Andy Taylor, Jeff Mesnil, Bryan Parry  
Level: Advanced  
Technologies: JMS  
Summary: The `hornetq-core-bridge` quickstart demonstrates the use of a HornetQ Core Bridge with JBoss EAP.  
Target Product: JBoss EAP  
Source: <https://github.com/rhtconsulting/eap-quickstarts/>  

What is it?
-----------

The `hornetq-core-bridge` quickstart demonstrates the use of a HornetQ Core Bridge with JBoss Enterprise Application Platform. In JMS messaging, a bridge is used to consumer messages from a source queue, and reliably forward them to a target destination, usually on a different server. JBoss EAP supports two types of bridges:
* the [HornetQ Core Bridge](https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/6.4/html/administration_and_configuration_guide/sect-configuration1#Configuring_HornetQ_Core_Bridge) which can be used to bridge JBoss EAP with another server running HornetQ
* the [JMS Bridge](https://access.redhat.com/documentation/en-us/red_hat_jboss_enterprise_application_platform/6.4/html/administration_and_configuration_guide/sect-configuration1#Configuring_HornetQ_JMS_Bridge) which can be used to bridge JBoss EAP with any other server that supports the JMS specification

This quickstart contains the following:

1. A message producer servlet that sends messages to a local JMS destination deployed to a JBoss EAP server (jms-producer-1).

2. A message consumer MDB that receives messages from a local JMS destination deployed to a JBoss EAP server (jms-consumer-1).

3. Configuration for JBoss EAP to create a HornetQ core bridge that forwards JMS messages from the source destination to the target destination.

This quickstart requires JBoss EAP to be run in domain mode to create and manage multiple servers.

System requirements
-------------------

The application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform 6.4.x. 

All you need to build this project is Java 6.0 (Java SDK 1.6) or later, Maven 3.0 or later.


Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN.md#configure-maven-to-build-and-deploy-the-quickstarts) before testing the quickstarts.


Use of EAP_HOME
---------------

In the following instructions, replace `EAP_HOME` with the actual path to your JBoss EAP 6 installation. The installation path is described in detail here: [Use of EAP_HOME and JBOSS_HOME Variables](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/USE_OF_EAP_HOME.md#use-of-eap_home-and-jboss_home-variables).


Add an Application User
----------------

This quickstart uses secured management interfaces and requires that you create the following application user to access the running application. 

| **UserName** | **Realm** | **Password** | **Roles** |
|:-----------|:-----------|:-----------|:-----------|
| quickstartUser| ApplicationRealm | quickstartPwd1!| guest |

To add the application user, open a command prompt and type the following command:

        For Linux:   EAP_HOME/bin/add-user.sh -a -u 'quickstartUser' -p 'quickstartPwd1!' -g 'guest'
        For Windows: EAP_HOME\bin\add-user.bat  -a -u 'quickstartUser' -p 'quickstartPwd1!' -g 'guest'

If you prefer, you can use the add-user utility interactively. 
For an example of how to use the add-user utility, see the instructions located here: [Add an Application User](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CREATE_USERS.md#add-an-application-user).


Start the JBoss EAP Server in Domain mode
---------------

1. Open a command prompt and navigate to the root of the JBoss EAP directory.
2. The following shows the command line to start JBoss EAP in domain mode:

        For Linux:   EAP_HOME/bin/domain.sh
        For Windows: EAP_HOME\bin\domain.bat


Configure the JBoss EAP Servers
---------------------------


_NOTE_: The full-ha profile is used for convenience. This quickstart does not require any of the -ha subsystems.


Build and Deploy the Quickstart web applications
-------------------------

To run the quickstart from the command line:

1. Make sure you have started the JBoss EAP server. See the instructions in the previous section.

2. Open a command prompt and navigate to the root of the hornetq-core-bridge quickstart directory:

        cd PATH_TO_QUICKSTARTS/hornetq-core-bridge

3. Type the following command to compile and deploy the quickstart web applications:

        mvn clean package jboss-as:deploy


Execute the Quickstart
----------------------

 
Investigate the Console Output
-------------------------




Remove the JMS Configuration
----------------------------

