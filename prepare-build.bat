@echo off

: Download SNAPSHOT version of jmod plugin
mkdir %UserProfile%\.m2\repository\org\apache\maven\plugins\maven-jmod-plugin\3.0.0-alpha-2-SNAPSHOT
cd %UserProfile%\.m2\repository\org\apache\maven\plugins\maven-jmod-plugin\3.0.0-alpha-2-SNAPSHOT
powershell -Command "Invoke-WebRequest https://builds.apache.org/job/maven-box/job/maven-jmod-plugin/job/master/lastSuccessfulBuild/artifact/org/apache/maven/plugins/maven-jmod-plugin/3.0.0-alpha-2-SNAPSHOT/maven-jmod-plugin-3.0.0-alpha-2-SNAPSHOT.pom -O maven-jmod-plugin-3.0.0-alpha-2-SNAPSHOT.pom"
powershell -Command "Invoke-WebRequest https://repository.apache.org/content/repositories/snapshots/org/apache/maven/plugins/maven-jmod-plugin/3.0.0-alpha-2-SNAPSHOT/maven-jmod-plugin-3.0.0-alpha-2-20191003.201307-83.jar -O maven-jmod-plugin-3.0.0-alpha-2-SNAPSHOT.jar"

: Migrate javax.persistence to JPMS
mkdir %UserProfile%\.m2\repository\javax\persistence\javax.persistence-api\2.2
cd %UserProfile%\.m2\repository\javax\persistence\javax.persistence-api\2.2
powershell -Command "Invoke-WebRequest https://search.maven.org/remotecontent?filepath=javax/persistence/javax.persistence-api/2.2/javax.persistence-api-2.2.jar -O javax.persistence-api-2.2.jar"
powershell -Command "Invoke-WebRequest https://search.maven.org/remotecontent?filepath=javax/persistence/javax.persistence-api/2.2/javax.persistence-api-2.2.pom -O javax.persistence-api-2.2.pom"
jdeps --generate-module-info info javax.persistence-api-2.2.jar
mkdir classes
cd classes
jar -xvf ../javax.persistence-api-2.2.jar
cd ..
javac -p javax.persistence -d classes/ info/java.persistence/module-info.java
jar uf javax.persistence-api-2.2.jar -C classes/ module-info.class