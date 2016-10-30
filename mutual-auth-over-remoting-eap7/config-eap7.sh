#!/bin/sh
EAP7_HOME=`echo $EAP7_HOME`
moduleDir="$EAP7_HOME/modules/custom-login-module/main"
echo -e "$EAP7_HOME \n"
echo -e "$moduleDir \n"

if [ -z "$EAP7_HOME" ]; then 
echo -e "EAP7_HOME needs to be set before running this script as it depends on an EAP 7 being installed. Please set it and re-run the script."
exit
fi

if [ ! -d "$moduleDir" ]; then
mkdir -p $moduleDir
fi

cp module.xml extended-certificate-login-module.jar wildfly-security-11.0.0.Alpha1-SNAPSHOT.jar wildfly-security-api-11.0.0.Alpha1-SNAPSHOT.jar picketbox-5.0.0.Beta1-SNAPSHOT.jar picketbox-infinispan-5.0.0.Beta1-SNAPSHOT.jar common-spi-5.0.0.Beta1-SNAPSHOT.jar $moduleDir