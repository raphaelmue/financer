#!/bin/bash

# Download SNAPSHOT version of jmod plugin
mkdir -p /root/.m2/repository/org/apache/maven/plugins/maven-jmod-plugin/3.0.0-alpha-2-SNAPSHOT/
cd /root/.m2/repository/org/apache/maven/plugins/maven-jmod-plugin/3.0.0-alpha-2-SNAPSHOT/
wget https://builds.apache.org/job/maven-box/job/maven-jmod-plugin/job/master/lastSuccessfulBuild/artifact/org/apache/maven/plugins/maven-jmod-plugin/3.0.0-alpha-2-SNAPSHOT/maven-jmod-plugin-3.0.0-alpha-2-SNAPSHOT.pom
wget https://repository.apache.org/content/repositories/snapshots/org/apache/maven/plugins/maven-jmod-plugin/3.0.0-alpha-2-SNAPSHOT/maven-jmod-plugin-3.0.0-alpha-2-20191003.201307-83.jar
mv maven-jmod-plugin-3.0.0-alpha-2-20191003.201307-83.jar maven-jmod-plugin-3.0.0-alpha-2-SNAPSHOT.jar

