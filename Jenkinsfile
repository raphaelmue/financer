pipeline {
    environment {
        registry = 'raphaelmue/financer'
        registryCredentials = 'dockerhub'
    }

    agent any

    tools {
        maven 'Maven 3.6.2'
        jdk 'JDK 11.0.8'
        nodejs 'NodeJS 13.6.0'
    }

    stages {
        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'mvn clean install -DskipTests -P generate-openapi-specification'
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: '**/financer-server.jar', fingerprint: true
                }
            }
        }
        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh 'npm install -g yarn'
                    sh 'yarn install --ignore-engines'
                    sh 'yarn run generate:api'
                    sh 'yarn build'
                }
            }
        }

        stage('Unit Tests') {
            parallel {
                stage('Backend') {
                    steps {
                        dir('backend') {
                            sh 'mvn test -P unit-tests'
                        }
                    }
                    post {
                        always {
                            junit '**/target/surefire-reports/TEST-*.xml'
                            step([$class: 'JacocoPublisher'])
                        }
                    }
                }
                stage('Frontend') {
                    environment {
                        CYPRESS_RECORD_KEY = credentials('cypress-token')
                    }
                    steps {
                        dir('frontend') {
                            sh 'docker run -i --rm \
                                --volumes-from jenkins \
                                --workdir ${PWD}/frontend \
                                --name financer-integration-tests \
                                --entrypoint yarn \
                                --env CYPRESS_RECORD_KEY \
                                cypress/included:6.1.0 \
                                test'
                        }
                    }
                    post {
                        always {
                            junit '**/.test/.report/cypress-report.xml'
                            step([$class: 'CoberturaPublisher',
                                coberturaReportFile: 'frontend/.test/.coverage/cobertura-coverage.xml',
                                autoUpdateHealth: false,
                                autoUpdateStability: false,
                                failUnhealthy: false,
                                failUnstable: false,
                                maxNumberOfBuilds: 0,
                                onlyStable: false,
                                sourceEncoding: 'ASCII',
                                zoomCoverageChart: false
                            ])
                        }
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            parallel {
                stage('Backend') {
                    environment {
                        scannerHome = '$JENKINS_HOME/SonarQubeScanner'
                    }
                    steps {
                        dir('backend') {
                            sh 'cp target/jacoco.exec org.financer.server/target/'
                            sh 'cp target/jacoco.exec org.financer.shared/target/'
                            sh 'cp target/jacoco.exec org.financer.util/target/'
                            sh 'mvn dependency:copy-dependencies'
                            withSonarQubeEnv('SonarQubeServer') {
                                script {
                                    if (env.CHANGE_ID) {
                                        sh "${scannerHome}/bin/sonar-scanner " +
                                                "-Dsonar.pullrequest.key=${env.CHANGE_ID} " +
                                                "-Dsonar.pullrequest.branch=${env.BRANCH_NAME} "
                                    } else {
                                        if (env.BRANCH_NAME != 'master') {
                                            sh "${scannerHome}/bin/sonar-scanner " +
                                                    "-Dsonar.branch.name=${env.BRANCH_NAME}"
                                        } else {
                                            sh "${scannerHome}/bin/sonar-scanner"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                stage('Frontend') {
                    environment {
                        scannerHome = '$JENKINS_HOME/SonarQubeScanner'
                    }
                    steps {
                        dir('frontend') {
                            withSonarQubeEnv('SonarQubeServer') {
                                script {
                                    if (env.CHANGE_ID) {
                                        sh "${scannerHome}/bin/sonar-scanner " +
                                                "-Dsonar.pullrequest.key=${env.CHANGE_ID} " +
                                                "-Dsonar.pullrequest.branch=${env.BRANCH_NAME} "
                                    } else {
                                        if (env.BRANCH_NAME != 'master') {
                                            sh "${scannerHome}/bin/sonar-scanner " +
                                                    "-Dsonar.branch.name=${env.BRANCH_NAME} "
                                        } else {
                                            sh "${scannerHome}/bin/sonar-scanner"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    post {
        always {
            dir('frontend') {
                sh 'yarn run clean:modules'
            }
        }
    }
}
