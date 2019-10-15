pipeline {
    agent any
    stages {
        stage('Build') {
            parallel {
                stage('Java') {
                    steps {
                        dir('java') {
                            sh 'cp /var/lib/jenkins/workspace/hibernate.cfg.xml ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler/financer/server/db/config/'
                            sh 'mvn clean install -DskipTests'
                        }
                    }
                }
                stage('Android') {
                    steps {
                        dir('android') {
                            // sh 'gradle build'
                        }
                    }
                }
                stage('NodeJS') {
                    steps {
                        dir('web') {
                            sh 'yarn install'
                        }
                    }
                }
            }
        }
        stage('JUnit Tests') {
            steps {
                dir('java') {
                    sh 'mvn test -P unitTests'
                }
            }
        }
        stage('JavaFX Tests') {
            steps {
                dir('java') {
                    sh 'mvn test -P integrationTests,headlessTesting'
                    sh 'rm ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler/financer/server/db/config/hibernate.cfg.xml'
                }
            }
        }
        stage('SonarQube Analysis') {
            environment {
                scannerHome = tool 'SonarQubeScanner'
            }
            steps {
                dir('java') {
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
            step([$class: 'JacocoPublisher'])
        }
    }
}