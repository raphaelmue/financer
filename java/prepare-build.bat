@echo off
setLocal

REM Download SNAPSHOT version of jmod plugin
mkdir %UserProfile%\.m2\repository\org\apache\maven\plugins\maven-jmod-plugin
powershell Expand-Archive .build\3.0.0-alpha-2-SNAPSHOT.zip -DestinationPath %UserProfile%\.m2\repository\org\apache\maven\plugins\maven-jmod-plugin

REM Migrate javax.persistence to JPMS
mkdir %UserProfile%\.m2\repository\javax\persistence\javax.persistence-api\2.2
cd %UserProfile%\.m2\repository\javax\persistence\javax.persistence-api\2.2
powershell -Command "Invoke-WebRequest https://search.maven.org/remotecontent?filepath=javax/persistence/javax.persistence-api/2.2/javax.persistence-api-2.2.jar -O javax.persistence-api-2.$powershell -Command "Invoke-WebRequest https://search.maven.org/remotecontent?filepath=javax/persistence/javax.persistence-api/2.2/javax.persistence-api-2.2.pom -O javax.persistence-api-2.$jdeps --generate-module-info info javax.persistence-api-2.2.jar
mkdir classes
cd classes
jar -xvf ../javax.persistence-api-2.2.jar
cd ..
javac -p javax.persistence -d classes/ info/java.persistence/module-info.java
jar uf javax.persistence-api-2.2.jar -C classes/ module-info.class