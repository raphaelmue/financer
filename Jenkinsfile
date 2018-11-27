pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }
        stage('Test') {
            parallel {
                stage('JUnit Tests') {
                    steps {
                        sh 'mvn test -pl de.raphaelmuesseler.financer.util,de.raphaelmuesseler.financer.shared,de.raphaelmuesseler.financer.server,de.raphaelmuesseler.financer.client'
                    }
                }
                stage('JavaFX Tests') {
                    steps {
                        sh 'mvn test -pl de.raphaelmuesseler.financer.client.javafx'
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                sh 'pkill -f "java -jar"'
                sh 'cd /home/raphael/.m2/repository/de/raphaelmuesseler/financer/de.raphaelmuesseler.financer.server/1.0-SNAPSHOT'
                sh 'java -jar de.raphaelmuesseler.financer.server-1.0-SNAPSHOT-jar-with-dependencies.jar &'
            }
        }
    }
}