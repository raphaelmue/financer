pipeline {
    environment {
        JPACKAGE = '/var/jenkins_home/jdk-14/bin/jpackage'
        registry = 'raphaelmue/financer'
        registryCredentials = 'dockerhub'
    }

    agent any

    tools {
        maven 'Maven 3.6.2'
        jdk 'JDK 11.0.1'
        nodejs 'NodeJS 13.6.0'
    }

    stages {
        stage('Build') {
            parallel {
                stage('Java') {
                    steps {
                        dir('java') {
                            sh 'bash prepare-build.sh'
                            sh 'mvn clean install -DskipTests -P deploy'
                        }
                    }
                    post {
                        always {
                            archiveArtifacts artifacts: '**/financer-server.jar, **/*.msi, **/*.deb, **/*.dmg, **/*.apk', fingerprint: true
                        }
                    }
                }
                stage('Android') {
                    steps {
                        dir('java') {
                            sh 'mvn clean install -DskipTests -P android-dependency -pl ' +
                                    '!org.financer.client.javafx,' +
                                    '!org.financer.server,'
                        }
                        dir('android') {
                            sh 'chmod +x gradlew'
                            sh 'echo "sdk.dir=$JENKINS_HOME/android-sdk" >> local.properties'
                            sh './gradlew clean assembleDebug'
                            sh 'mv app/build/outputs/apk/debug/app-debug.apk app/build/outputs/apk/debug/financer-debug.apk'
                        }
                    }
                    post {
                        always {
                            archiveArtifacts artifacts: '**/*.apk', fingerprint: true
                        }
                    }
                }
                stage('NodeJS') {
                    steps {
                        dir('web') {
                            sh 'npm install -g yarn'
                            sh 'yarn install'
                        }
                    }
                }
            }
        }

        stage('Unit Tests') {
            parallel {
                stage('Java') {
                    steps {
                        dir('java') {
                            sh 'mvn test -P unit-tests'
                        }
                    }
                }
                stage('Android') {
                    steps {
                        dir('android') {
                            sh 'chmod +x gradlew'
                            sh './gradlew test'
                        }
                    }
                }
            }
        }
        stage('Integration Tests') {
            steps {
                dir('java') {
                    sh 'mvn test -P integration-tests'
                }
            }
        }

        stage('SonarQube Analysis') {
            environment {
                scannerHome = '$JENKINS_HOME/SonarQubeScanner'
            }
            steps {
                dir('java') {
                    sh 'cp target/jacoco.exec org.financer.client/target/'
                    sh 'cp target/jacoco.exec org.financer.client.javafx/target/'
                    sh 'cp target/jacoco.exec org.financer.server/target/'
                    sh 'cp target/jacoco.exec org.financer.shared/target/'
                    sh 'cp target/jacoco.exec org.financer.util/target/'
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
