### 1. Step: Build Binary ###
FROM maven:3.6.3-jdk-11 AS builder

MAINTAINER Raphael Müßeler <raphael@muesseler.de>

# Set working directory
WORKDIR /usr/src/app/

# Copy source files
COPY . .

# Build all dependencies for offline use and compile sources
RUN mvn dependency:go-offline clean package -B -DskipTests

### 2. Step: Run Binary ###
FROM adoptopenjdk/openjdk11:alpine-jre

ARG DB_DIALECT="org.hibernate.dialect.H2Dialect"
ARG DB_DRIVER="org.h2.Driver"
ARG DB_URL="h2:file:./.data/financer"
ARG DB_USER="root"
ARG DB_PASSWORD="password"

ENV DB_DIALECT=${DB_DIALECT}
ENV DB_DRIVER=${DB_DRIVER}
ENV DB_URL=${DB_URL}
ENV DB_USER=${DB_USER}
ENV DB_PASSWORD=${DB_PASSWORD}

# Set working directory
WORKDIR /usr/financer/

# Copy binaries into working directory
COPY --from=builder /usr/src/app/org.financer.server/target/financer-server.jar ./

# Expose port
EXPOSE 3000

# Run Financer server with environment variables
ENTRYPOINT ["sh", "-c", "java -Dfinancer.database.dialect=$DB_DIALECT -Dfinancer.database.driver=$DB_DRIVER -Dfinancer.database.url=$DB_URL -Dfinancer.database.user=$DB_USER -Dfinancer.database.password=$DB_PASSWORD -jar financer-server.jar"]