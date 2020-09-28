### 1. Step: Build Binary ###
FROM maven:3.6.3-jdk-11 AS builder

MAINTAINER Raphael Müßeler <raphael@muesseler.de>

# Set working directory
WORKDIR /usr/src/app/

# Copy source files
COPY org.financer.server ./org.financer.server
COPY org.financer.shared ./org.financer.shared
COPY org.financer.util ./org.financer.util
COPY pom.xml .

# Build all dependencies for offline use and compile sources
RUN mvn dependency:go-offline package -B -DskipTests

### 2. Step: Run Binary ###
FROM openjdk:11-jre-slim

ARG DB_DIALECT="org.hibernate.dialect.HSQLDialect"
ARG DB_DRIVER="org.hsqldb.jdbcDriver"
ARG DB_URL="hsqldb:file:.data/financer"
ARG DB_USER="root"
ARG DB_PASSWORD="password"
ARG ENABLE_SMTP="false"
ARG SMTP_PORT="465"
ARG SMTP_HOST=""
ARG SMTP_EMAIL=""
ARG SMTP_PASSWORD=""

ENV DB_DIALECT=${DB_DIALECT}
ENV DB_DRIVER=${DB_DRIVER}
ENV DB_URL=${DB_URL}
ENV DB_USER=${DB_USER}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV ENABLE_SMTP=${ENABLE_SMTP}
ENV SMTP_PORT=${SMTP_PORT}
ENV SMTP_HOST=${SMTP_HOST}
ENV SMTP_EMAIL=${SMTP_EMAIL}
ENV SMTP_PASSWORD=${SMTP_PASSWORD}

# Set working directory
WORKDIR /usr/financer/

# Copy binaries into working directory
COPY --from=builder /usr/src/app/org.financer.server/target/financer-server.jar ./

# Expose port
EXPOSE 3000

# Run Financer server with environment variables
ENTRYPOINT ["sh", "-c", "java -Dfinancer.database.dialect=$DB_DIALECT -Dfinancer.database.driver=$DB_DRIVER -Dfinancer.database.url=$DB_URL -Dfinancer.database.user=$DB_USER -Dfinancer.database.password=$DB_PASSWORD -Dfinancer.server.smtp=$ENABLE_SMTP -Dfinancer.server.smtp.port=$SMTP_PORT -Dfinancer.server.smtp.host=$SMTP_HOST -Dfinancer.server.smtp.email=$SMTP_EMAIL -Dfinancer.server.smtp.password=$SMTP_PASSWORD -jar financer-server.jar"]