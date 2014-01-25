#!/bin/bash

certificate_file=$1
certificate_pass=$2

groovy -Dgroovy.grape.report.downloads=true -Djavax.net.ssl.trustStore=certificates/jssecacerts -Djavax.net.ssl.trustStorePassword=changeit -Djavax.net.ssl.keyStore=$certificate_file -Djavax.net.ssl.keyStorePassword=$certificate_pass -Djavax.net.ssl.keyStoreType=PKCS12 storylines.groovy
