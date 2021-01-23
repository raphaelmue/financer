# Financer

| Component | Code Quality | Tests | Statistics |
| --------- | ------------ | ----- | ---------- |
| All       | [![Build Status](https://jenkins.raphael-muesseler.de/job/financer/job/master/badge/icon)](https://jenkins.raphael-muesseler.de/job/financer/job/master/) | | | |
| Backend   | [![Quality Gate Status](https://sonarqube.raphael-muesseler.de/api/project_badges/measure?project=financer&metric=alert_status)](https://sonarqube.raphael-muesseler.de/dashboard?id=financer) <br /> [![Code Smells](https://sonarqube.raphael-muesseler.de/api/project_badges/measure?project=financer&metric=code_smells)](https://sonarqube.raphael-muesseler.de/dashboard?id=financer) <br /> [![Duplicated Lines (%)](https://sonarqube.raphael-muesseler.de/api/project_badges/measure?project=financer&metric=duplicated_lines_density)](https://sonarqube.raphael-muesseler.de/dashboard?id=financer) | [![Coverage](https://sonarqube.raphael-muesseler.de/api/project_badges/measure?project=financer&metric=coverage)](https://sonarqube.raphael-muesseler.de/dashboard?id=financer) | [![Lines of Code](https://sonarqube.raphael-muesseler.de/api/project_badges/measure?project=financer&metric=ncloc)](https://sonarqube.raphael-muesseler.de/dashboard?id=financer) |
| Frontend  | [![Quality Gate Status](https://sonarqube.raphael-muesseler.de/api/project_badges/measure?project=financer-frontend&metric=alert_status)](https://sonarqube.raphael-muesseler.de/dashboard?id=financer-frontend) <br /> [![Code Smells](https://sonarqube.raphael-muesseler.de/api/project_badges/measure?project=financer-frontend&metric=code_smells)](https://sonarqube.raphael-muesseler.de/dashboard?id=financer-frontend) <br /> [![Duplicated Lines (%)](https://sonarqube.raphael-muesseler.de/api/project_badges/measure?project=financer-frontend&metric=duplicated_lines_density)](https://sonarqube.raphael-muesseler.de/dashboard?id=financer-frontend) | [![Coverage](https://sonarqube.raphael-muesseler.de/api/project_badges/measure?project=financer-frontend&metric=coverage)](https://sonarqube.raphael-muesseler.de/dashboard?id=financer-frontend) <br /> [![Financer](https://img.shields.io/endpoint?url=https://dashboard.cypress.io/badge/detailed/ridaia/master&style=flat&logo=cypress)](https://dashboard.cypress.io/projects/ridaia/runs) | [![Lines of Code](https://sonarqube.raphael-muesseler.de/api/project_badges/measure?project=financer-frontend&metric=ncloc)](https://sonarqube.raphael-muesseler.de/dashboard?id=financer-frontend) | 


## What is Financer? 😍

Financer is a open source application that is completely free to use. It can be deployed as a on-premise application on your server, or you can use it in the cloud.

With Financer helps you to ...
- 💰 manage your expenses and revenues
- 📈 analyze your financials
- ⚙ customize your App
- 🔒 secure your financials so that only you can access them

If you are interested into financer, have a look at our [website](https://financer-project.org/).

## Getting Started 💨 

Financer is separated into a Spring backend application and a React frontend application. Both are part of this repository and can be independently deployed as a docker container.

The following code snippet is an example of a deployment that deploys frontend as well as backend in one single docker stack.

```yaml
version: "3"

services:
    server:
        image: raphaelmue/financer-server:latest
        container_name: financer-server
        restart: always
        ports:
            - 3000:3000

    client:
        image: raphaelmue/financer-client
        container_name: financer-client
        restart: always
        ports:
            - 8080:80
        links:
            - server
        volumes:
            - ./config.js:/usr/share/nginx/html/config.js
```

For more information, please have a look at our wiki page [Getting Started](https://github.com/raphaelmue/financer/wiki/Getting-Started).

## Contributors 👨‍🔧

- Raphael Müßeler (Webiste: [raphael-muesseler.de](https://raphael-muesseler.de), Email: [raphael@muesseler.de](mailto:raphael@muesseler.de); GitHub: [raphaelmue](https://github.com/raphaelmue))

## License 📜

This project is using an BSD-3-Clause, which you can see [here](LICENSE)
