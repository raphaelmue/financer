FROM maven:3.6.2-jdk-11

MAINTAINER Raphael Müßeler <raphael@muesseler.de>

# Set working directory
WORKDIR /usr/src/app/

# Copy source files
COPY org.financer.client ./org.financer.client
COPY org.financer.client.javafx ./org.financer.client.javafx
COPY org.financer.server ./org.financer.server
COPY org.financer.shared ./org.financer.shared
COPY org.financer.util ./org.financer.util
COPY pom.xml .

# Compile source files
RUN mvn clean install -DskipTests -pl org.financer.server,org.financer.shared,org.financer.util

EXPOSE 3500

# Run Financer server
CMD ["java", "-jar", "org.financer.server/target/financer-server.jar", "--database=prod"]
