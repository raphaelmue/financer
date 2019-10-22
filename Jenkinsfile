pipeline {
    environment {
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
                stage('Default') {
                    steps {
                        sh 'bash prepare-build.sh'
                        sh 'mvn clean install -DskipTests -Djpackager.path="$JENKINS_HOME/jdk-14/bin/jpackage"'
                    }
                }
                stage('Build Docker Image') {
                    steps {
                        script {
                            docker.build registry + ":$BUILD_NUMBER"
                        }
                    }
                }
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
            archiveArtifacts(artifacts: '*.msi, *.rpm, *.dmg, *.apk', fingerprint: true)
            step([$class: 'JacocoPublisher'])
        }
    }
}