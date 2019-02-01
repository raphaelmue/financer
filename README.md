# Financer

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

### 2.1 Installation

You can either download the executable JAR file, which is an Desktop application, based on the JavaFX frame work. Download the installer from [here](/).

Furthermore there is an test Android application, which you can test this [here](/). In the future there will be an iOS app.

### 2.1 Registration / Login

When opening either the Desktop or mobile application, it is possible to register yourself. Just enter your credentials and personal information and you will be automatically logged in. There is also an test user, that has test data, which helps to explore the application.  

After you are registered, you are able to log yourself in by entering your credentials. From here, you can use the entire application at its best. 

## 3 On Boarding

The On Boarding guide helps you as a developer to create your workspace and run the application locally, so that you can easily contribute to this project. 

### 3.1 Requirements

First of all, please make sure, you have the following IDE, tools and frameworks installed: 

1. IntelliJ IDEA Ultimate (latest version)
1. Android Studio for Android development (latest version)
1. JavaFX (OpenJFX) (latest version)
1. Git (latest version)
1. Maven (latest version)
1. MySQL database (only needed for offline development)
    1. For Windows you can install XAMPP, which provides you the possibility to have a MySQL database on your localhost.
    1. For a Linux distribution you just need to install the mysql-package (there are different tutorials online).

### 3.2 Configuring your IDE

1. Download JDK 11 (or JDK 11.0.1) and set it as default.
1. Import all Run Configurations (you find them in the directory: .idea/runConfigurations)
1. [Optional] You can your IntelliJ IDEA use the database tool, so that you do not have to access via [PHPMyAdmin](https://phpmyadmin.raphael-muesseler.de). 
    1. Open the Database panel (if this is not shown on the right toolbar, you can open it via: **View -> Tool Window -> Database**).
    2. Add a new Database (via the **+** Button) and select **Data Source -> MariaDB**.
    3. You can change the name of that connection. Host is "raphael-muesseler.de" and enter database credentials. 
    
### 3.3 Build

Before starting the application, you should first run the following maven goals:

```
mvn clean test install
```

It is also useful to build the whole project by either pressing **Ctrl + F9** or clicking **Build -> Build Project**. 

### 3.4 Execution

Please make sure, when you execute the Financer Server run configuration that the port 3500 is not blocked by your system firewall or your network firewall.  
    
## 4 Functionality

This chapter explains the whole functionality of this application. This helps you to understand, how the application works. 

### 4.1 Categories

tbd

### 4.2 Transactions

tbd

### 4.3 Fixed transactions

tbd

## 5 Development

### 5.1 Best practices

#### 5.1.1 Git

Each feature or bug has its own branch. It is useful to merge the master periodically into the branch. Only merge your branch into the master, when it is tested sufficiently, so that you can ensure, that no bug is on the master. 

Furthermore has each commit message the following structure: "<branch-name>: <commit-message".

#### 5.1.2 Formatter

Please use the IntelliJ IDEA Formatter on the code, that you changed, inserted or deleted. Please do not use the Formatter on the whole File, because you then have changes in your commit, which indicate, that you have changed these lines. For formatting file, please make an extra commit. 

#### 5.1.3 JavaDoc

Try to add JavaDocs to each method you create, if this is a rather complex method. Please stick to the JavaDoc conventions.

#### 5.1.4 Warnings

Try to keep the workspace warning-free. In some cases, there is no other way than to suppress the warning, but reduce this to a minimum. 

### 5.2 Module Structure

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

#### 5.2.1 Client

The client modules are organized in such a way that the main ```de.raphaelmuesseler.financer.client``` module contains all classes that each specific client application (such as android app or JavaFX application) can use, to connect to the server. 

Each submodule represents a specific client application, which contains only the client handling (like controllers and UI tests).

#### 5.2.2 Server

The module ```de.raphaelmuesseler.financer.server``` contains all the classes for the server. This is where all the backend logic and database handling takes place. 

#### 5.2.3 Shared

In the module ```de.raphaelmuesseler.financer.shared``` are all the classes that define the model of Financer and they are used by the client as well as by the server.

#### 5.2.4 Util

This module contains only utility classes that are used on client-side as well as on server-side. Examples for utility classes are collection or string utilities.

## 6 Authors

- Raphael Müßeler (Email: [raphael@muesseler.de](mailto:raphael@muesseler.de); GitHub: [raphaelmue](https://github.com/raphaelmue))
- Joshua Schulz (Email: [jschulz99@web.de](mailto:jschulz99@web.de); GitHub: [joshuaschu](https://github.com/joshuaschu)) 
- Robin Kuck (Email: [](); GitHub: [kucki99](https://github.com/Kucki99))

## 7 License

tbd