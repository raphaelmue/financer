@Library('github.com/spotify/jenkins-coverage-poster@1.0') _


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
        stage('Code Coverage') {
            postJacocoCoverage(threshold: 75)
        }
        stage('Deploy') {
            when {
                branch 'deployment'
            }
            steps {
                sh ' bash ./service/start-financer-server.sh'
            }
        }
    }
    post {
        always {
            junit '**/target/surefire-reports/TEST-*.xml'
        }
    }
}