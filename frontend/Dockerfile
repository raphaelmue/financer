FROM node:latest

# Create app directory
WORKDIR /usr/src/app

# Copy directory
COPY ../ .

RUN yarn install
RUN yarn build:dev

EXPOSE 3000
CMD [ "yarn", "start:react" ]
