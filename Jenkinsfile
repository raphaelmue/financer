pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }
        stage('Preparing tests') {
            steps {
                sh 'mkdir ./de.raphaelmuesseler.financer.server/src/main/resources'
                sh 'mkdir ./de.raphaelmuesseler.financer.server/src/main/resources/de'
                sh 'mkdir ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler'
                sh 'mkdir ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler/financer'
                sh 'mkdir ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler/financer/server'
                sh 'mkdir ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler/financer/server/db'
                sh 'mkdir ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler/financer/server/db/config'
                sh 'cp /var/lib/jenkins/workspace/database.conf ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler/financer/server/db/config/'
                sh 'mvn clean install -DskipTests'
            }
        }
        stage('JUnit Tests') {
            steps {
                sh 'mvn test -pl de.raphaelmuesseler.financer.util,de.raphaelmuesseler.financer.shared,de.raphaelmuesseler.financer.server,de.raphaelmuesseler.financer.client'
            }
        }
        stage('JavaFX Tests') {
            steps {
                sh 'mvn test -pl de.raphaelmuesseler.financer.client.javafx -Dtestfx.robot=glass -Dglass.platform=Monocle -Dmonocle.platform=Headless'
                sh 'rm ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler/financer/server/db/config/database.conf'
            }
        }
        /*stage('Deploy') {
            steps {
                sh 'systemctl restart financer-server.service'
            }
        }*/
    }
    post {
        always {
            junit 'build/reports/**/*.xml'
        }
    }
}