FROM maven:3.6.2-jdk-11

MAINTAINER Raphael Müßeler <raphael@muesseler.de>

# Define environemt variables
ENV PORT 3500
ENV DEPLOYMENT_ENVIRONEMT prod

# Set working directory
WORKDIR /usr/src/app/

# Copy source files
COPY de.raphaelmuesseler.financer.client ./de.raphaelmuesseler.financer.client
COPY de.raphaelmuesseler.financer.client.javafx ./de.raphaelmuesseler.financer.client.javafx
COPY de.raphaelmuesseler.financer.server ./de.raphaelmuesseler.financer.server
COPY de.raphaelmuesseler.financer.shared ./de.raphaelmuesseler.financer.shared
COPY de.raphaelmuesseler.financer.util ./de.raphaelmuesseler.financer.util
COPY pom.xml .

# Compile source files
RUN mvn clean install -DskipTests -pl de.raphaelmuesseler.financer.server,de.raphaelmuesseler.financer.shared,de.raphaelmuesseler.financer.util

EXPOSE $PORT

# Run Financer server
CMD ["java", "-jar", "financer-server.jar", "--database=$DEPLOYMENT_ENVIRONMENT", "--port=$PORT"]
