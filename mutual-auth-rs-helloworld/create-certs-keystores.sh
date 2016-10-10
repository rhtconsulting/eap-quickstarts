#!/bin/sh

# Create a self signed key pair root CA .

function create_self_signing_cert_authority
{
ldname=$1
lksname=$2
lkalias=$3
lkeypass=$4
lkspass=$5

echo -e "Creating Root certificate ...$ldname  \n"

$keytool -genkeypair -v \
  -alias $lkalias \
  -dname $ldname \
  -keystore $lksname \
  -keypass $lkeypass \
  -storepass $lkspass \
  -keyalg RSA \
  -keysize 2048 \
  -sigalg SHA384withRSA \
  -ext KeyUsage:critiquickstartscal="keyCertSign" \
  -ext BasicConstraints:critical="ca:true,pathlen:10000" \
  -validity 9999
}

# create intermediate cert chain
function create_intermediate_signing_cert_authority
{
ldname=$1
lksname=$2
lkalias=$3
lkeypass=$4
lkspass=$5

echo -e "Creating Intermediate Root CA certificate... $ldname \n"

$keytool -genkeypair -v \
  -alias $lkalias \
  -dname $ldname \
  -keystore $lksname \
  -keypass $lkeypass \
  -storepass $lkspass \
  -keyalg RSA \
  -keysize 2048 \
  -sigalg SHA384withRSA \
  -ext KeyUsage:critical="keyCertSign" \
  -validity 9999
}


# Export the public certificate
function export_cert 
{
lksname=$1
lkalias=$2
lkeypass=$3
lkspass=$4
lcertname=$5

echo -e "Exporting certificate to file to be named $lcertname ... \n"

$keytool -exportcert -v \
  -alias $lkalias \
  -file $lcertname \
  -keypass $lkeypass \
  -storepass $lkspass \
  -keystore $lksname
}


# Create a server certificate
function create_serv_cert
{
ldname=$1
lksname=$2
lkalias=$3
lkeypass=$4
lkspass=$5

echo -e "Creating server certificate to be named $lksname ... \n"

$keytool -genkeypair -v \
  -alias $lkalias \
  -dname $ldname \
  -keystore $lksname \
  -keypass $lkeypass \
  -storepass $lkspass \
  -keyalg RSA \
  -sigalg SHA384withRSA \
  -keysize 2048 \
  -validity 385
}

# Create a certificate signing request
function create_cert_request
{ 
lksname=$1
lkalias=$2
lkeypass=$3
lkspass=$4
lcsrname=$5

echo -e "Creating cert request to be named $lcsrname ... \n"

$keytool -certreq \
  -alias $lkalias \
  -keypass $lkeypass \
  -storepass $lkspass \
  -keystore $lksname \
  -file $lcsrname
}


# Tell CA to sign the certificate. 
function sign_cert
{
lquickstartscaksname=$1
lquickstartscaalias=$2
lquickstartscakspass=$3
lcsrname=$4
lcertname=$5
lksalias=$6

echo -e "Signing certificate to be named $lcertname based on passed in CSR named $lcsrname ... \n"

$keytool -gencert -v \
  -alias $lquickstartscaalias \
  -keypass $lquickstartscakspass \
  -storepass $lquickstartscakspass \
  -keystore $lquickstartscaksname \
  -infile $lcsrname \
  -outfile $lcertname \
  -ext KeyUsage:critiquickstartscal="digitalSignature,keyEncipherment" \
  -ext EKU="serverAuth,clientAuth" \
  -ext SAN="DNS:$lksalias"
}

function sign_intermediate_cacert
{
lcaksname=$1
lcaalias=$2
lkeypass=$3
icalias=$4
lcsrname=$5
lcertname=$6

echo -e "Signing intermediate certificate to be named $icalias ... \n"

$keytool -gencert -v \
  -alias $lcaalias \
  -keypass $lkeypass \
  -storepass $lkeypass \
  -keystore $lcaksname \
  -infile $lcsrname \
  -outfile $lcertname \
  -ext KeyUsage:critical="keyCertSign" \
  -ext SubjectAlternativeName="dns:$icalias"
}


# trust ca as a signer.
function  import_ca_cert
{
lksname=$1
lcaalias=$2
lkspass=$3
lcacertname=$4

echo -e "importing root CA certificate named $lcacertname into keystore named $lksname ... \n"

$keytool -importcert -v \
  -noprompt \
  -alias $lcaalias \
  -file $lcacertname \
  -trustcacerts \
  -keystore $lksname \
  -storetype JKS \
  -storepass $lkspass
}

# Import the signed certificate back into keystore 
function import_signed_cert
{
lksname=$1
lalias=$2
lkspass=$3
lcertname=$4

echo -e "importing signed certificate named $lcertname into keystore named $lksname ... \n"

$keytool -importcert -v \
  -alias $lalias \
  -file $lcertname \
  -keystore $lksname \
  -storetype JKS \
  -storepass $lkspass
}

# List out the contents
function list_ks_content
{
lksname=$1
lkspass=$2

echo -e "Listing content of keystore named $lksname with passed in pwd $lkspass ... \n"

$keytool -list -v \
  -keystore $lksname \
  -storepass $lkspass
}


# Create a PKCS#12 keystore containing the public and private keys.
function export_pkcs12_cert
{
lksname=$1
lkalias=$2
lkeypass=$3
lkspass=$4
lp12ksname=$5

echo -e "Exporting keystore named $lksname to PKCS12 keystore to be named $lp12ksname ... \n"

$keytool -importkeystore -v \
  -srcalias $lkalias \
  -srckeystore $lksname \
  -srcstoretype jks \
  -srckeypass $lkspass \
  -srcstorepass $lkspass \
  -destkeystore $lp12ksname \
  -destkeypass $lkeypass \
  -deststorepass $lkspass \
  -deststoretype PKCS12
}

# Export the private key using OpenSSL.
function export_pkcs12_cert_key
{
lkeypass=$1
lkeyfilename=$2
lp12ksname=$3

echo -e "Exporting private key to be named $lkeyfilename from PKCS12 keystore named $lp12ksname ... \n"

openssl pkcs12 \
  -nocerts \
  -nodes \
  -passout pass:$lkeypass \
  -passin pass:$lkeypass \
  -in $lp12ksname \
  -out $lkeyfilename
}

#derive intermediate key
function extract_intermediate_ca_key
{
lkeychainfile=$1
lintalias=$2
lintkeyfile=$3
searchStrg="friendlyName: $lintalias"
endSearch="-----END PRIVATE KEY-----"

echo -e "Extracting private key for intermediate ca: $lintalias to be named $lintkeyfile ... \n"

sed -n "/$searchStrg/,/$endSearch/p" $lkeychainfile | tail -n +4 > $lintkeyfile
}

function create_user_certificate
{
lcakeyname=$1
lcacertname=$2
usrname=$3
usrpasswd=$4
lcakspass=$5
lcntrcode=$6
lica2alias=$7
licaalias=$8
licaaliascert=$9
rcaalias=${10}
rcaaliascert=${11}
lcaksname=${12}
lica2aliascert=$lcacertname
lusrcertkey="${usrname}.key"
lusrcertcsr="${usrname}.csr"
lusrcertfile="${usrname}.crt"
lusrcertp12file="${usrname}.p12"
lusrcertksfile="${usrname}.jks"
llocal="L=Denver"
lou="OU=eapquickstarts,OU=consulting"
lst="Colorado"
lo="O=redhat"
ldname="CN=${usrname},${lou},${lo},${llocal},C=${lcntrcode}"

echo -e "Creating User Certificate $usrname.p12 for user named $usrname  ... \n"

import_ca_cert $lusrcertksfile $rcaalias $usrpasswd $rcaaliascert
import_ca_cert $lusrcertksfile $licaalias $usrpasswd $licaaliascert
import_ca_cert $lusrcertksfile $lica2alias $usrpasswd $lica2aliascert

create_serv_cert $ldname $lusrcertksfile $usrname $usrpasswd $usrpasswd
create_cert_request $lusrcertksfile $usrname $usrpasswd $usrpasswd $lusrcertcsr
sign_cert $lcaksname $lica2alias $lcakspass $lusrcertcsr $lusrcertfile $usrname
import_signed_cert $lusrcertksfile $usrname $usrpasswd $lusrcertfile
list_ks_content $lusrcertksfile $usrpasswd

export_cert $lusrcertksfile $usrname $usrpasswd $usrpasswd $cltkspubcert
export_pkcs12_cert $lusrcertksfile $usrname $usrpasswd $usrpasswd $lusrcertp12file
export_pkcs12_cert_key $usrpasswd $lusrcertkey $lusrcertp12file


 echo -e "\n*******************************************************\n"
 echo The certificate for your user to import into his/her browser is ${lusrcertp12file} in `pwd`.  The password to import the file into the browser is ${usrpasswd}.
 echo -e "\n*******************************************************\n"
}

EAP7_HOME=`echo $EAP7_HOME`

certsdir="$EAP7_HOME/certs"

javaHome=`echo $JAVA_HOME`

if [ -z "$javaHome" ]; then 
echo -e "JAVA_HOME needs to be set before running this script as it depends on java provided $$keytool. Please set it and re-run the script."
exit
fi

if [ -z "$EAP7_HOME" ]; then 
echo -e "EAP7_HOME needs to be set before running this script. Please set it and re-run the script."
exit
fi

keytool=$javaHome/bin/keytool

mkdir $certsdir
if [ $? -ne 0 ]
then
echo -e "Delete ./$certsdir or rename it before running this script."
exit
fi

cd $certsdir

dname="CN=quickstartsCA,OU=eapquickstarts,OU=consulting,O=redhat,L=Denver,ST=Colorado,C=US"
intca1dname="CN=quickstartsIntCA1,OU=eapquickstarts,OU=consulting,O=redhat,L=Denver,ST=Colorado,C=US"
intca2dname="CN=quickstartsIntCA2,OU=eapquickstarts,OU=consulting,O=redhat,L=Denver,ST=Colorado,C=US"
caksname="ca.jks"
servcertdname="CN=server,OU=eapquickstarts,OU=consulting,O=redhat,L=Denver,ST=Colorado,C=US"
capwd="rootcapwd"
caalias="quickstartsca"
intca1alias="quickstartsca1"
intca2alias="quickstartsca2"
ca1cert="ca1.crt"
ca2cert="ca2.crt"
ca1pubcert="ca1.pub"
ca2pubcert="ca2.pub"
cacert="ca.crt"
capubcert="ca.pub"
cap12ks="ca.p12"
ca1p12ks="ca1.p12"
ca2p12ks="ca2.p12"
cakey="ca.key"
ca1key="ca1.key"
ca2key="ca2.key"
ca1csr="ca1.csr"
ca2csr="ca2.csr"
ksname="server.jks"
keyalias="server"
keypass="password"
kspass="password"
kscsr="server.csr"
kscert="server.crt"
kspubcert="server.pub"
p12ks="server.p12"
kskey="server.key"
tsname="truststore.jks"
tskeypass="changeit"
tspass="changeit"
tspubcert="truststore.pub"
p12ts="truststore.p12"
tskey="truststore.key"
countrycode="US"
usrpasswd="password"
usrname1="webuser"
usrname2="ejbuser"
usrname3="superuser"
cltcertdname="CN=client,OU=eapquickstarts,OU=consulting,O=redhat,L=Denver,ST=Colorado,C=US"
cltksname="client.jks"
cltkeyalias="client"
cltkeypass="password"
cltkspass="password"
cltkscsr="client.csr"
cltkscert="client.crt"
cltkspubcert="client.pub"
cltp12ks="client.p12"
cltkskey="client.key"

create_self_signing_cert_authority $dname $caksname $caalias $capwd $capwd
create_intermediate_signing_cert_authority $intca1dname $caksname $intca1alias $capwd $capwd
create_cert_request $caksname $intca1alias $capwd $capwd $ca1csr
sign_intermediate_cacert $caksname $caalias $capwd $intca1alias $ca1csr $ca1cert
import_signed_cert $caksname $intca1alias $capwd $ca1cert
create_intermediate_signing_cert_authority $intca2dname $caksname $intca2alias $capwd $capwd
create_cert_request $caksname $intca2alias $capwd $capwd $ca2csr
sign_intermediate_cacert $caksname $intca1alias $capwd $intca2alias $ca2csr $ca2cert
import_signed_cert $caksname $intca2alias $capwd $ca2cert

export_cert $caksname $caalias $capwd $capwd $cacert
export_cert $caksname $caalias $capwd $capwd $capubcert
export_cert $caksname $intca1alias $capwd $capwd $ca1pubcert
export_cert $caksname $intca2alias $capwd $capwd $ca2pubcert
export_pkcs12_cert $caksname $caalias $capwd $capwd $cap12ks
export_pkcs12_cert $caksname $intca1alias $capwd $capwd $cap12ks
export_pkcs12_cert $caksname $intca2alias $capwd $capwd $cap12ks
export_pkcs12_cert_key $capwd $cakey $cap12ks
extract_intermediate_ca_key $cakey $intca1alias $ca1key
extract_intermediate_ca_key $cakey $intca2alias $ca2key

# create server keystores
import_ca_cert $ksname $caalias $kspass $cacert
import_ca_cert $ksname $intca1alias $kspass $ca1cert
import_ca_cert $ksname $intca2alias $kspass $ca2cert
create_serv_cert $servcertdname $ksname $keyalias $keypass $kspass
create_cert_request $ksname $keyalias $keypass $kspass $kscsr
sign_cert $caksname $intca2alias $capwd $kscsr $kscert $keyalias
import_signed_cert $ksname $keyalias $kspass $kscert
list_ks_content $ksname $kspass

export_cert $ksname $keyalias $keypass $kspass $kspubcert
export_pkcs12_cert $ksname $keyalias $keypass $kspass $p12ks
export_pkcs12_cert_key $keypass $kskey $p12ks

# create client keystores
import_ca_cert $cltksname $caalias $cltkspass $cacert
import_ca_cert $cltksname $intca1alias $cltkspass $ca1cert
import_ca_cert $cltksname $intca2alias $cltkspass $ca2cert
create_serv_cert $cltcertdname $cltksname $cltkeyalias $cltkeypass $cltkspass
create_cert_request $cltksname $cltkeyalias $cltkeypass $cltkspass $cltkscsr
sign_cert $caksname $intca2alias $capwd $cltkscsr $cltkscert $cltkeyalias
import_signed_cert $cltksname $cltkeyalias $cltkspass $cltkscert
list_ks_content $cltksname $cltkspass

export_cert $cltksname $cltkeyalias $cltkeypass $cltkspass $cltkspubcert
export_pkcs12_cert $cltksname $cltkeyalias $cltkeypass $cltkspass $cltp12ks
export_pkcs12_cert_key $cltkeypass $cltkskey $cltp12ks

# create truststore
import_ca_cert $tsname $caalias $tskeypass $cacert
import_ca_cert $tsname $intca1alias $tskeypass $ca1cert
import_ca_cert $tsname $intca2alias $tskeypass $ca2cert

list_ks_content $tsname $tspass

# create user certs to be imported into browser
create_user_certificate $ca2key $ca2cert $usrname1 $usrpasswd $capwd $countrycode $intca2alias $intca1alias $ca1cert $caalias $cacert $caksname
create_user_certificate $ca2key $ca2cert $usrname2 $usrpasswd $capwd $countrycode $intca2alias $intca1alias $ca1cert $caalias $cacert $caksname
create_user_certificate $ca2key $ca2cert $usrname3 $usrpasswd $capwd $countrycode $intca2alias $intca1alias $ca1cert $caalias $cacert $caksname

chmod 755 *

