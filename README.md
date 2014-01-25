# API Composition with RxJava

A demonstration of simple API composition with RxJava. The API composes upon the BBC's Linked Data Platform APIs to return information about the 5 most used tag concepts for the current day, along with a summary of the latest 5 creative works for each.

# Prerequisites

To run natively:

* [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
* [Groovy](http://groovy.codehaus.org/)

To run in a vagrant virtual machine:

* [VirtualBox](https://www.virtualbox.org/)
* [Vagrant](http://www.vagrantup.com/)

# Getting Started

To access the Linked Data Platform APIs you'll need to have configured your certificate and the BBC truststore in JAVA\_OPTS:

```
-Djavax.net.ssl.trustStore=certificates/jssecacerts
-Djavax.net.ssl.keyStore=/path/to/cert.p12
-Djavax.net.ssl.keyStorePassword=yourpassword
-Djavax.net.ssl.keyStoreType=PKCS12
```

If you're running on the Reith network from within the BBC you'll additionally need to have configured the proxy details:

```
-Dhttp.proxyHost=www-cache.reith.bbc.co.uk
-Dhttp.proxyPort=80
```

Assuming the above steps have been done, you can run the application. The first time you do this groovy will download the necessary dependencies (so be patient, it may take a little while).

```
$ groovy -Dgroovy.grape.report.downloads=true storylines.groovy
```
