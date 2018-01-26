migrate-existing-eapapp-to-ocp: REST Hello World Example using 2 way SSL mutual authentication being used to show how to shift and lift to OCP
===================
Author: Claude Adjai  
Level: Beginner  
Technologies: JAX-RS, JAAS login module, OCP  
Summary: The `migrate-existing-eapapp-to-ocp` quickstart demonstrates how to shift and lift an existing rest application that uses 2 way SSL mutual authentication between client and server, (originally deployed to Jboss EAP 6) to Openshift Container Platform (OCP).  
Target Product: JBoss EAP 7  
Source: <https://github.com/rhtconsulting/eap-quickstarts>  

What is it?
-----------

The `migrate-existing-eapapp-to-ocp` quickstart demonstrates how to perform a forklift (shift and lift) migration of an existing REST service originally deployed to EAP 6 to OCP 3.7 or later. The key here is that no change is being made to the existing application or how it is built except to use a an Openshift build config to build it but even that is not required but for simplicity we are going to use an Openshift provided template to build and deploy this. Note that the application can be deployed to EAP 6 or EAP 7. The only change that will be required is using EAP 7.1+ would be to update the configuration accordingly given the change in securuty subsystem.

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

It is assumed that you have access to an Openshift Container platform cluster version 3.7 or later where you can run the commands provided later to test this migration. 
It is also assumed that you have an OCP client readily available to be used to perform the steps required to deploy this application to OCP. 
It is not the goal of this to setup or help you configure an OCP cluster. It is assumed that one is available and can be used. 
It is also assumed that you have access to standalone JBoss EAP 6 and/or 7 so that you can deployed the application produced into it even though that is not necessary. 

The application this project produces is designed to be run on Red Hat JBoss Enterprise Application Platform 6 or later. 

All you need to build this project is Java 8.0 (Java SDK 1.8) or later and Maven 3.1.1 or later. See [Configure Maven for JBoss EAP 6 or 7](https://github.com/jboss-developer/jboss-developer-shared-resources/blob/master/guides/CONFIGURE_MAVEN_JBOSS_EAP7.md#configure-maven-to-build-and-deploy-the-quickstarts) to make sure you are configured correctly for testing the quickstarts.

An HTML5 compatible browser such as Chrome, Safari 5+, Firefox 5+, or IE 9+ is required.

With the prerequisites out of the way, you're ready to build and deploy.


Use of OCP_CLUSTER_URL , GUEST_EAP_HOME and WORKING_DIR
---------------

In the following instructions, replace `OCP_CLUSTER_FQDN` with the actual path to your your OCP cluster master or load balancer.
Replace `GUEST_EAP_HOME` with the EAP HOME path with the container, for  the eap70-https-s2i image it is /opt/eap 
Also replace `WORKING_DIR` with the path to where you have checkout this project or at least where you ar storing your working artifacts

Generate the necessary keystores and truststores 
---------------
Run the provided create-certs-keystores.sh script to create the necessary keystores. By default it will create a root authority and two intermediate certificate authorities in a root authority keystore, a server keystore and truststore to be used to secure the Jboss EAP 7 instance, and a PKCS12 keystore for the webuser to be used to test the application from a browser. If more than one user is required update the script to add more web users.
Note that you will need to set your EAP7_HOME as an environment variable for the script and subsequent CLI scripts to run properly.
Note that default keystore and truststore have been provided in case you don't want to go through the trouble of generating your own. 

Import the webuser PKCS12 keystore into the browser 
---------------
Import the webuser keystore into the browser. If unclear on how to do this, lookup the right steps to imports certificates into the browser you are using. If you are using the default the keystore file is named `webuser.p12` and the password for both key and keystore is `password`

Connect to your OCP cluster
-------------------------

1. Open a command line and navigate to the root of the JBoss EAP directory.
2. The following shows the command line to login to the cluster :

        oc login -u <your-username> http://OCP_CLUSTER_FQDN:8443 

Configure the namespace for the deployment
---------------------------
Run the following set of commands to configure the namespace along with the various permissions required. For more information consult https://access.redhat.com/documentation/en/red-hat-xpaas/0/openshift-primer/openshift-primer#install_the_openshift_enterprise_package and https://access.redhat.com/documentation/en-us/red_hat_jboss_middleware_for_openshift/3/html-single/red_hat_jboss_enterprise_application_platform_for_openshift/index#how_does_jboss_eap_work_on_openshift for mode details on JBoss on OCP. 

1. Create the project/namespace running the following command.
 	`oc new-project eap7-ocp-deployment`

2. Create a service account if required by the template to be used to deploy app the application using the followin command.
	`oc create serviceaccount eap7-service-account -n $(oc project -q)`

3. Grant Kubernetes REST API view access to the project created above to the service account using the following command .
	`oc adm policy add-role-to-user view system:serviceaccount:$(oc project -q):eap7-service-account -n $(oc project -q)`

4. Create secrets for necessary security artifacts e.g. keystore, truststore using the following commands. 
	`oc secrets new eap-ks ${WORKING_DIR}/server.jks`

	`oc secrets new eap-ts ${WORKING_DIR}/truststore.jks`
5. Create needed optional secrets if template being used requires them e.g. jgroup jceks keystore and others
	`oc secrets new jg-ks ${WORKING_DIR}/jgroups.jceks`

6. Use one of the existing templates (eap71-https-s2i, eap71-basic-s2i, eap70-https-s2i, eap64-basic-s2i) as a starting point to create your own
	`oc get templates -n openshift | grep eap`

7. Describe the existing template you picked above (using eap70-https-s2i as starting point or base here)
	`oc describe template eap70-https-s2i -n openshift`

8. Create a pamaters file to be used to process the template. A sample file is provided here (named eap70-https-s2i.params) for the eap70-https-s2i

9. Process the template picked using the parameter file created above using the following command
	`oc process eap70-https-s2i -n openshift --param-file=${WORKING_DIR}/eap70-https-s2i.params -o json > ${WORKING_DIR}/mutual-auth-rs-helloworld.json`
_Note: The eap70-https-s2i by default points to the jboss-eap70-openshift:1.6 image but you will need to downgrade it the 1.3 tag because the other are missing lot of libraries. For example some modules folders are empty when they were supposed to contain libraries and module.xml config. 

Build and deploy the Quickstart to the OCP cluster
-------------------------


1. Deploy the processed template using one of the following two commands.
	`oc create -f ${WORKING_DIR}/mutual-auth-rs-helloworld.json`
	`oc apply -f ${WORKING_DIR}/mutual-auth-rs-helloworld.json`
2. create and mount a volume for the keystore.
	`oc set volume dc/mutual-auth-rs-helloworld --add --name=keystore -m ${GUEST_EAP_HOME}/standalone/server.jks -t secret --secret-name=eap-ks --sub-path=server.jks --default-mode=0664`
3. create and mount a volume for the truststore
	`oc set volume dc/mutual-auth-rs-helloworld --add --name=truststore -m ${GUEST_EAP_HOME}/standalone/truststore.jks -t secret --secret-name=eap-ts --sub-path=truststore.jks --default-mode=0664`
4. Create a configmap for using the existing configuration.
_Note: The configuration should be massaged to be ocp compatible (e.g. logging subsystem adjustment, socket binding, infinispan, jgroup, web subsystem might all need to be adjusted. Use the standalone-openshift.xml deployed with the original deployment of the template app as a model).
A default config has been provided named standalone-openshift.xml
	`oc create configmap eap-config --from-file=standalone-openshift.xml=${WORKING_DIR}/standalone-openshift.xml`
5. Create and mount a volume using the configmap created above
	`oc set volume dc/mutual-auth-rs-helloworld --add --name=standalone-config --configmap-name=eap-config -m ${GUEST_EAP_HOME}/standalone/configuration/standalone-openshift.xml -t configmap --sub-path=standalone-openshift.xml --default-mode=0664`
6. Create a config map for the custom login module 
	`oc create configmap eap-custom-module --from-file=module.xml=${WORKING_DIR}/ext-cert-login-module/main/module.xml --from-file=extended-certificate-login-module.jar=${WORKING_DIR}/ext-cert-login-module/main/extended-certificate-login-module.jar`
7. Create and mount a volume for using the configmap created above
	`oc set volume dc/mutual-auth-rs-helloworld --add --name=custom-login-module --configmap-name=eap-custom-module -m ${GUEST_EAP_HOME}/modules/ext-cert-login-module/main -t configmap --default-mode=0777`
_Note: !!! Step 6 and 7 from above are not required because a modules directory was added to the source repo and was built and deployed into the image. You will only need to do this if for whatever reason you don't want to build the modules into the image or if the module config changes between environments. However, there are still few things to work out when using the configmap approach. EAP in the image is not able to process the libraries deployed via configmap (I am suspecting that it is because of the way the items are mounted into the directory i.e. as symlink from ../data/...). 
8. Update the route created to make sure it matches the route config e.g. TLS passthrough if required...
	`oc edit route/secure-mutual-auth-rs-helloworld`
9. Optionally if you want you can scale your deployment as follows
	`oc scale dc mutual-auth-rs-helloworld --replicas=3`

10. Optionally if you want to cluster the eap instances in the scaled up deployment from above make to set the following environment variables
	`oc set env dc/mutual-auth-rs-helloworld --env=OPENSHIFT_KUBE_PING_NAMESPACE=$(oc project -q)`
	`oc set env dc/mutual-auth-rs-helloworld --env=OPENSHIFT_KUBE_PING_LABELS=app=mutual-auth-rs-helloworld`
_Note: KUBE_PING is the default JGroup protocol being used in the standalone-openshift.xml provided, which you can change to use another one like DNS_PING, in which case you will have to change the two environment variables above to match the JGroup protocol (e.g. for DNS_PING you will be setting OPENSHIFT_DNS_PING_NAMESPACE and OPENSHIFT_DNS_PING_LABELS)


Access the application 
---------------------

The application will be running at the following URL <https://ServiceName-Namespace.AppDomainInCluster/app-context>.
Where ServiceName is the name of the service the route was created for above (e.g. secure-mutual-auth-rs-helloworld, mutual-auth-rs-test)
Namespace is the project name in the OCP cluster (e.g. eap7-ocp-deployment)
AppDomainInCluster is the wildcard app domain name in the OCP cluster (e.g. apps.1aec.example.opentlc.com)
app-context is the context root of the application (e.g. mutualauth-rs-helloworld)

example URL would look like https://secure-mutual-auth-rs-helloworld-eap7-ocp-deployment.apps.1aec.example.opentlc.com/mutualauth-rs-helloworld

_Note: You can change the URL by deploying your application to the root context and in that case you will need to make the appropriate adjustments to the standalone configuration used to deploy the aplication.
If after deploying the app and exposing the route you get a 403 error code try a different browser. I saw this with the EAP7 version of the deployment on FF but it works fine in chrome.
