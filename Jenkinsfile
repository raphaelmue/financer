pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'cp /var/lib/jenkins/workspace/hibernate.cfg.xml ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler/financer/server/db/config/'
                sh 'mvn clean'
                sh 'mkdir -p de.raphaelmuesseler.financer.client.javafx/target/staging/'
                sh 'mvn install -DskipTests'
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
        stage('SonarQube Analysis') {
            environment {
                scannerHome = tool 'SonarQubeScanner'
            }
            steps {
                sh 'cp target/jacoco.exec de.raphaelmuesseler.financer.client/target/'
                sh 'cp target/jacoco.exec de.raphaelmuesseler.financer.client.javafx/target/'
                sh 'cp target/jacoco.exec de.raphaelmuesseler.financer.server/target/'
                sh 'cp target/jacoco.exec de.raphaelmuesseler.financer.shared/target/'
                sh 'cp target/jacoco.exec de.raphaelmuesseler.financer.util/target/'
                sh 'mvn dependency:copy-dependencies'
                withSonarQubeEnv('SonarQubeServer') {
                    script {
                        if (env.CHANGE_ID) {
                            sh "${scannerHome}/bin/sonar-scanner " +
                                    "-Dsonar.pullrequest.base=master " +
                                    "-Dsonar.pullrequest.key=${env.CHANGE_ID} " +
                                    "-Dsonar.pullrequest.branch=${env.BRANCH_NAME} " +
                                    "-Dsonar.pullrequest.provider=github " +
                                    "-Dsonar.pullrequest.github.repository=raphaelmue/financer"
                        } else {
                            if (env.BRANCH_NAME != 'master') {
                                sh "${scannerHome}/bin/sonar-scanner " +
                                        "-Dsonar.branch.name=${env.BRANCH_NAME} " +
                                        "-Dsonar.branch.target=master"
                            } else {
                                sh "${scannerHome}/bin/sonar-scanner"
                            }
                        }
                    }
                }
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
            archiveArtifacts artifacts: '**/financer-installer.jar, **/financer.jar, **/financer-portable.exe, **/financer-server.jar', fingerprint: true
            step([$class: 'JacocoPublisher'])
        }
    }
}