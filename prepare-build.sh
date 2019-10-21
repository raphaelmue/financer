#!/bin/bash

# Download SNAPSHOT version of jmod plugin
mkdir -p ~/.m2/repository/org/apache/maven/plugins/maven-jmod-plugin/3.0.0-alpha-2-SNAPSHOT/
cd ~/.m2/repository/org/apache/maven/plugins/maven-jmod-plugin/3.0.0-alpha-2-SNAPSHOT/ || exit
wget https://builds.apache.org/job/maven-box/job/maven-jmod-plugin/job/master/lastSuccessfulBuild/artifact/org/apache/maven/plugins/maven-jmod-plugin/3.0.0-alpha-2-SNAPSHOT/maven-jmod-plugin-3.0.0-alpha-2-SNAPSHOT.pom
wget -O maven-jmod-plugin-3.0.0-alpha-2-SNAPSHOT.jar https://repository.apache.org/content/repositories/snapshots/org/apache/maven/plugins/maven-jmod-plugin/3.0.0-alpha-2-SNAPSHOT/maven-jmod-plugin-3.0.0-alpha-2-20191003.201307-83.jar

# Migrate javax.persistence to JPMS
mkdir -p ~/.m2/repository/javax/persistence/javax.persistence-api/2.2/
cd ~/.m2/repository/javax/persistence/javax.persistence-api/2.2/ || exit
wget -O javax.persistence-api-2.2.jar https://search.maven.org/remotecontent?filepath=javax/persistence/javax.persistence-api/2.2/javax.persistence-api-2.2.jar
wget -O javax.persistence-api-2.2.pom https://search.maven.org/remotecontent?filepath=javax/persistence/javax.persistence-api/2.2/javax.persistence-api-2.2.pom
jdeps --generate-module-info info javax.persistence-api-2.2.jar
mkdir classes
cd classes || exit
jar -xvf ../javax.persistence-api-2.2.jar
cd ..
javac -p javax.persistence -d classes/ info/java.persistence/module-info.java
jar uf javax.persistence-api-2.2.jar -C classes/ module-info.class