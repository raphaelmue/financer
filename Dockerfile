FROM maven:3.6.2-jdk-11

MAINTAINER Raphael Müßeler <raphael@muesseler.de>

# Set working directory
WORKDIR /usr/src/app/

# Copy source files
COPY service/prepare-build.sh .
COPY de.raphaelmuesseler.financer.client ./de.raphaelmuesseler.financer.client
COPY de.raphaelmuesseler.financer.client.javafx ./de.raphaelmuesseler.financer.client.javafx
COPY de.raphaelmuesseler.financer.server ./de.raphaelmuesseler.financer.server
COPY de.raphaelmuesseler.financer.shared ./de.raphaelmuesseler.financer.shared
COPY de.raphaelmuesseler.financer.util ./de.raphaelmuesseler.financer.util
COPY pom.xml .

# Compile source files
RUN bash prepare-build.sh
RUN mvn clean install -DskipTests -pl de.raphaelmuesseler.financer.server,de.raphaelmuesseler.financer.shared,de.raphaelmuesseler.financer.util

EXPOSE 3500

# Run Financer server
CMD ["java", "-jar", "de.raphaelmuesseler.financer.server/target/financer-server.jar", "--database=prod"]
