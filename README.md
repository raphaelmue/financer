# Financer

[![Build Status](https://jenkins.raphael-muesseler.de/job/financer/job/master/badge/icon)](https://jenkins.raphael-muesseler.de/job/financer/job/master/)
[![Quality Gate Status](https://sonarqube.raphael-muesseler.de/api/project_badges/measure?project=financer&metric=alert_status)](https://sonarqube.raphael-muesseler.de/dashboard?id=financer)
[![Coverage](https://sonarqube.raphael-muesseler.de/api/project_badges/measure?project=financer&metric=coverage)](https://sonarqube.raphael-muesseler.de/dashboard?id=financer)

## Content

1. [Overview](#1-overview)
2. [Get Started](#2-get-started)
3. [On Boarding](#3-on-boarding)
4. [Functionality](#4-functionality)
5. [Development](#5-development)
6. [Authors](#6-authors)
7. [License](#7-license)

## 1 Overview

Financer is an application that helps to manage your personal expenses and revenues. It helps you to analyze how much you have spend on living, eating etc. Besides you can take a look at the temporal progression of your expenses as well as on your revenues. 

## 2 Get started 

### 2.1 Client Installation

You can download the latest release for the desktop application [here](https://github.com/raphaelmue/financer/releases/latest). Furthermore will soon be an Android application released, which can be downloaded as test version. 

### 2.2 Server Installation

As Financer is also available as on premise version, you can easily execute the `docker-compose.yml` file in order to run the server on your own machine. If you can want to provide your own database, you can change this by using the Financer image available on [DockerHub](https://hub.docker.com/repository/docker/raphaelmue/financer). 

## 3 On Boarding

The On Boarding guide helps you as a developer to create your workspace and run the application locally, so that you can easily contribute to this project. 

### 3.1 Requirements

First of all, please make sure, you have the following IDE, tools and frameworks installed: 

1. IntelliJ IDEA Ultimate (latest version)
1. Android Studio for Android development (latest version)
1. Java 11
1. Maven (latest version)
1. Git (latest version)
1. MySQL database (only needed for offline development) e.g. as provided in `docker-compose`

### 3.2 Configuring your IDE

1. Download JDK 11 (or JDK 11.0.1) and set it as default.
1. Import all Run Configurations (you find them in the directory: .idea/runConfigurations)
    
### 3.3 Build

Before starting the application, you should first run the following maven goals:

```
bash prepare-build.sh
mvn clean install -DskipTests
```

### 3.4 Execution

Please make sure, when you execute the Financer Server run configuration that the port 3500 is not blocked by your system firewall or your network firewall.  
    

## 4 Development

### 4.1 Best practices

#### 4.1.1 Git

Each feature or bug has its own branch. It is useful to merge the master periodically into the branch. Only merge your branch into the master, when it is tested sufficiently, so that you can ensure, that no bug is on the master. 

Furthermore has each commit message the following structure: "<branch-name>: <commit-message".

#### 4.1.2 Formatter

Please use the IntelliJ IDEA Formatter on the code, that you changed, inserted or deleted. Please do not use the Formatter on the whole File, because you then have changes in your commit, which indicate, that you have changed these lines. For formatting file, please make an extra commit. 

#### 4.1.3 JavaDoc

Try to add JavaDocs to each method you create, if this is a rather complex method. Please stick to the JavaDoc conventions.

#### 4.1.4 Warnings

Try to keep the workspace warning-free. In some cases, there is no other way than to suppress the warning, but reduce this to a minimum. 

### 4.2 Module Structure

The maven module structure is defined as follows:

```
|-- financer
|   |-- client
|   |   |-- javafx
|   |   |-- app
|   |-- server
|   |-- shared
|   |-- util
```

#### 4.2.1 Client

The client modules are organized in such a way that the main ```de.raphaelmuesseler.financer.client``` module contains all classes that each specific client application (such as android app or JavaFX application) can use, to connect to the server. 

Each submodule represents a specific client application, which contains only the client handling (like controllers and UI tests).

#### 4.2.2 Server

The module ```de.raphaelmuesseler.financer.server``` contains all the classes for the server. This is where all the backend logic and database handling takes place. 

#### 4.2.3 Shared

In the module ```de.raphaelmuesseler.financer.shared``` are all the classes that define the model of Financer and they are used by the client as well as by the server.

#### 4.2.4 Util

This module contains only utility classes that are used on client-side as well as on server-side. Examples for utility classes are collection or string utilities.

## 5 Authors

- Raphael Müßeler (Email: [raphael@muesseler.de](mailto:raphael@muesseler.de); GitHub: [raphaelmue](https://github.com/raphaelmue))
- Joshua Schulz (Email: [jschulz99@web.de](mailto:jschulz99@web.de); GitHub: [joshuaschu](https://github.com/joshuaschu)) 
- Robin Kuck (Email: [](); GitHub: [kucki99](https://github.com/Kucki99))

## 6 License

This project is using an BSD-3-Clause, which you can see [here](LICENSE)