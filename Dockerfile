### 1. Step: Build OpenAPI Specification ###
FROM maven:3.6.3-jdk-11 AS openapi-builder

MAINTAINER Raphael Müßeler <raphael@muesseler.de>

# Set working directory
WORKDIR /usr/src/app/

# Copy source files
COPY backend/org.financer.server backend/org.financer.server
COPY backend/org.financer.shared backend/org.financer.shared
COPY backend/org.financer.util backend/org.financer.util
COPY backend/pom.xml backend/

# Generate OpenAPI Specification
RUN cd backend/ && mvn verify -DskipTests -P generate-openapi-specification -q

### 2. Step: Generate OpenAPI Code ###
FROM openapitools/openapi-generator-cli:latest AS openapi-codegen-builder

# Set working directory
WORKDIR /usr/src/app/

COPY --from=openapi-builder /usr/src/app/backend/org.financer.server/target/financer.openapi.json .

# Generate OpenAPI Code
RUN java -jar /opt/openapi-generator/modules/openapi-generator-cli/target/openapi-generator-cli.jar generate \
    -i financer.openapi.json \
    -g typescript-fetch \
    -o frontend/src/.openapi \
    --additional-properties="typescriptThreePlus=true,ngVersion=6.1.7,hateoas=true" \
    --skip-validate-spec

### 3. Step: Build Binaries ###
FROM node:14.5.0-alpine3.12 AS builder

# Create app directory
WORKDIR /usr/src/app

# Copy directory
COPY ./frontend/public frontend/public
COPY ./frontend/src frontend/src
COPY ./frontend/package.json frontend/
COPY ./frontend/tsconfig.json frontend/
COPY ./frontend/webpack.* frontend/
COPY ./frontend/.eslintrc.json frontend/
COPY ./frontend/tsconfig* frontend/

COPY --from=openapi-codegen-builder /usr/src/app/frontend/src/.openapi frontend/.src/openapi

# Install dependencies and build application
WORKDIR /usr/src/app/frontend
RUN yarn install
RUN yarn build

### 4. Step: Run Binaries ###
FROM nginx:alpine AS release

ARG REACT_APP_SERVER_URL="https://api.financer-project.org"
ENV REACT_APP_SERVER_URL=${REACT_APP_SERVER_URL}

# Copy binaries into working directory
COPY --from=builder /usr/src/app/frontend/build /usr/share/nginx/html

# Expose port
EXPOSE 80

# Run React application
CMD ["nginx", "-g", "daemon off;"]

