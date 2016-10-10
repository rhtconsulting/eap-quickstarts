#!/bin/sh
EAP7_HOME=`echo $EAP7_HOME`
moduleDir=$EAP7_HOME/modules/ext-cert-login-module/main
workingDir=`pwd`

if [ -z "$EAP7_HOME" ]; then 
echo -e "$EAP7_HOME needs to be set before running this script as it depends on an EAP 7 being installed. Please set it and re-run the script."
exit
fi

mkdir -p $moduleDir

cp module.xml extended-certificate-login-module.jar $moduleDir