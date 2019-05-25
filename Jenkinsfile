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
                sh 'cp /var/lib/jenkins/workspace/hibernate.cfg.xml ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler/financer/server/db/config/'
                sh 'mvn clean install -DskipTests'
            }
        }
        stage('JUnit Tests') {
            steps {
                sh 'mvn test -P unitTests'
            }
        }
        stage('JavaFX Tests') {
           steps {
                sh 'mvn test -P integrationTests,headlessTesting'
                sh 'rm ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler/financer/server/db/config/hibernate.cfg.xml'
            }
        }
        stage('Publish test results') {
            when {
                branch 'master'
            }
            steps {
                sh 'bash service/publish-test-report.sh'
            }
        }
        stage('Deploy') {
            when {
                branch 'deployment'
            }
            steps {
                sh 'JENKINS_NODE_COOKIE=dontKillMe nohup bash ./service/start-financer-server.sh'
            }
        }
    }
    post {
        always {
            junit '**/target/surefire-reports/TEST-*.xml'
            step( [ $class: 'JacocoPublisher' ] )
            //publishCoverage adapters: [jacocoAdapter('**/target/sites/jacoco/jacoco.xml')], sourceFileResolver: sourceFiles('STORE_ALL_BUILD')
        }
    }
}