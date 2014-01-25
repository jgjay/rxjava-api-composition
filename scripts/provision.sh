#!/bin/bash

sudo yum install unzip java-1.7.0-openjdk -y

sudo sh -c 'echo "export JAVA_HOME=/usr/lib/jvm/jre-1.7.0-openjdk.x86_64" > /etc/profile.d/java.sh'
source /etc/profile.d/java.sh

curl -s get.gvmtool.net | bash
source ~/.gvm/bin/gvm-init.sh
mkdir -p ~/.gvm/etc
echo "gvm_auto_answer=true\ngvm_auto_selfupdate=false" > ~/.gvm/etc/config
gvm install groovy
