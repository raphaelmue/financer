#!/bin/bash

# Download SNAPSHOT version of jmod plugin
mkdir -p ~/.m2/repository/org/apache/maven/plugins/maven-jmod-plugin/3.0.0-alpha-2-SNAPSHOT/
cp .build/3.0.0-alpha-2-SNAPSHOT.zip ~/.m2/repository/org/apache/maven/plugins/maven-jmod-plugin/
cd ~/.m2/repository/org/apache/maven/plugins/maven-jmod-plugin/ || exit
unzip -o 3.0.0-alpha-2-SNAPSHOT.zip

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