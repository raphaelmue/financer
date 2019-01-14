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
                        sh 'pkill -f "java -jar"'
                        sh 'mkdir ./de.raphaelmuesseler.financer.server/src/main/resources'
                        sh 'mkdir ./de.raphaelmuesseler.financer.server/src/main/resources/de'
                        sh 'mkdir ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler'
                        sh 'mkdir ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler/financer'
                        sh 'mkdir ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler/financer/server'
                        sh 'mkdir ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler/financer/server/db'
                        sh 'mkdir ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler/financer/server/db/config'
                        sh 'cp /var/lib/jenkins/workspace/database.conf ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler/financer/server/db/config/'
                        sh 'mvn clean install -DskipTests'
                        sh 'mvn test -pl de.raphaelmuesseler.financer.client.javafx -Dtestfx.robot=glass -Dglass.platform=Monocle -Dmonocle.platform=Headless'
                        sh 'rm ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler/financer/server/db/config/database.conf'
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                sh 'cd /home/raphael/.m2/repository/de/raphaelmuesseler/financer/de.raphaelmuesseler.financer.server/1.0-SNAPSHOT'
                sh 'java -jar de.raphaelmuesseler.financer.server-1.0-SNAPSHOT-jar-with-dependencies.jar --database=prod &'
            }
        }
    }
}