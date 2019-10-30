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
    }

    stages {
        stage('Build') {
            parallel {
                stage('Java') {
                    steps {
                        sh 'bash prepare-build.sh'
                        sh 'mvn clean install -DskipTests -P deploy'
                    }
                    post {
                        always {
                            archiveArtifacts artifacts: '**/*.msi, **/*.deb, **/*.dmg, **/*.apk', fingerprint: true
                        }
                    }
                }
                stage('Android') {
                    steps {
                        dir('android/de.raphaelmuesseler.financer.client.app') {
                            // sh 'gradlew assembleDebug'
                        }
                    }
                    // post {
                    //    always {
                    //        archiveArtifacts artifacts: '**/*.apk', fingerprint: true
                    //    }
                    //}
                }
            }
        }

        stage('Test') {
            parallel {
                stage('Java Unit Tests') {
                    steps {
                        sh 'mvn test -P unit-tests'
                    }
                }
                stage('Java Integration Tests') {
                    steps {
                        sh 'mvn test -P integration-tests,headless-testing'
                    }
                }
            }
        }
        stage('SonarQube Analysis') {
            environment {
                scannerHome = 'SonarQubeScanner'
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
            step([$class: 'JacocoPublisher'])
        }
    }
}