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

The `hornetq-core-bridge` quickstart demonstrates the use of a HornetQ Core Bridge with JBoss Enterprise Application Platform. In JMS messaging, a bridge is used to consume messages from a source queue, and reliably forward them to a target destination, usually on a different server. JBoss EAP supports two types of bridges:
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


Configure the JBoss EAP Servers using Domain mode
---------------------------

You configure the HornetQ Bridge and JMS queues by running JBoss CLI commands. For your convenience, this quickstart batches the commands into `configure-hornetq-bridge-domain.cli` script provided in the root directory of this quickstart. 

1. Before you begin, back up your server configuration file
    * If it is running, stop the JBoss EAP server.
    * Backup the file: `EAP_HOME/domain/configuration/domain.xml`
    * After you have completed testing this quickstart, you can replace this file to restore the server to its original configuration.
2. Start the JBoss EAP server in domain mode by typing the following: 

        For Linux:   EAP_HOME/bin/domain.sh
        For Windows: EAP_HOME\bin\domain.bat
3. Review the `configure-hornetq-bridge-domain.cli` file in the root of this quickstart directory. This script:
    * adds the `bridgeSourceQueue` queue and the HornetQ core bridge configuration to the `messaging` subsystem of the full profile
    * adds the `bridgeDestinationQueue` queue to the full-ha profile
    * creates the jms-producer server group and server using the full profile
    * creates the jms-consumer server group and server using the full-ha profile
    * starts both server groups

_NOTE_: The full-ha profile is used for convenience. This quickstart does not require any of the -ha subsystems.

4. Open a new command prompt, navigate to the root directory of this quickstart, and run the following command to stop any existing servers, replacing EAP_HOME with the path to your server:

        For Linux: EAP_HOME/bin/jboss-cli.sh --connect ":stop-servers" 
        For Windows: EAP_HOME\bin\jboss-cli.bat --connect ":stop-servers" 
   You should see the following result when you run the command:

        {
            "outcome" => "success",
            "result" => undefined,
            "server-groups" => undefined
        }

5. Run the following command to configure the server, replacing EAP_HOME with the path to your server:

        For Linux: EAP_HOME/bin/jboss-cli.sh --connect --file=configure-hornetq-bridge-domain.cli 
        For Windows: EAP_HOME\bin\jboss-cli.bat --connect --file=configure-hornetq-bridge-domain.cli 
   You should see the following result when you run the script:

        {
            "outcome" => "success",
            "result" => undefined,
            "server-groups" => undefined
        }


Build and Deploy the Quickstart web applications
-------------------------

To run the quickstart from the command line:

1. Make sure you have started the JBoss EAP server. See the instructions in the previous section.

2. Open a command prompt and navigate to the root of the hornetq-core-bridge quickstart directory:

        cd PATH_TO_QUICKSTARTS/hornetq-core-bridge

3. Type the following command to compile and deploy the quickstart web applications:

        mvn clean package jboss-as:deploy


Access the application 
---------------------

The application will be running at the following URL: <http://localhost:8080/jboss-helloworld-mdb-producer/> and will send some messages to the `bridgeSourceQueue` queue.

 
Investigate the Console Output
-------------------------

Check the server log file of the jms-consumer-1 server located at `EAP_HOME/domain/servers/jms-consumer-1/log/server.log`. You should see output similar to the following:

    13:55:20,202 INFO  [class org.jboss.as.quickstarts.mdb.HelloWorldQueueMDB] (Thread-0 (HornetQ-client-global-threads-2085562247)) Received Message from queue: This is message 1
    13:55:20,209 INFO  [class org.jboss.as.quickstarts.mdb.HelloWorldQueueMDB] (Thread-14 (HornetQ-client-global-threads-2085562247)) Received Message from queue: This is message 2
    13:55:20,221 INFO  [class org.jboss.as.quickstarts.mdb.HelloWorldQueueMDB] (Thread-0 (HornetQ-client-global-threads-2085562247)) Received Message from queue: This is message 3
    13:55:20,224 INFO  [class org.jboss.as.quickstarts.mdb.HelloWorldQueueMDB] (Thread-8 (HornetQ-client-global-threads-2085562247)) Received Message from queue: This is message 4
    13:55:20,234 INFO  [class org.jboss.as.quickstarts.mdb.HelloWorldQueueMDB] (Thread-0 (HornetQ-client-global-threads-2085562247)) Received Message from queue: This is message 5


Troubleshooting
---------------

If there are no log messages indicating JMS received on the consumer, check that the bridge has connected successfully. Check the logs of the jms-producer-1 server located at `EAP_HOME/domain/servers/jms-consumer-1/log/server.log`. You should see output similar to the following:

    13:54:55,762 INFO  [org.hornetq.core.server] (Thread-22 (HornetQ-server-HornetQServerImpl::serverUUID=21fb780c-1fda-11e8-8202-fd4a0dcd3ed6-1881433381)) HQ221027: Bridge BridgeImpl@1b947cac [name=example-bridge, queue=QueueImpl[name=jms.queue.bridgeSourceQueue, postOffice=PostOfficeImpl [server=HornetQServerImpl::serverUUID=21fb780c-1fda-11e8-8202-fd4a0dcd3ed6]]@155381ae targetConnector=ServerLocatorImpl (identity=Bridge example-bridge) [initialConnectors=[TransportConfiguration(name=netty_remote, factory=org-hornetq-core-remoting-impl-netty-NettyConnectorFactory) ?port=6445&host=localhost], discoveryGroupConfiguration=null]] is connected

If you do not see this log message indicating a successful connection, restart the jms-producer-1 server.


Remove the HornetQ Bridge Configuration
----------------------------

1. If it is running, stop the JBoss EAP server.
2. Replace the `EAP_HOME/domain/configuration/domain.xml` file with the back-up copy of the file.
