pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'cp /var/lib/jenkins/workspace/hibernate.cfg.xml ./de.raphaelmuesseler.financer.server/src/main/resources/de/raphaelmuesseler/financer/server/db/config/'
        sh 'mvn clean install -DskipTests'
        sh 'chmod 775 android/de.raphaelmuesseler.financer.client.app/gradlew'
        sh 'android/de.raphaelmuesseler.financer.client.app/gradlew clean assemble -p android/de.raphaelmuesseler.financer.client.app/'
        sh 'mv android/de.raphaelmuesseler.financer.client.app/app/build/outputs/apk/release/app-release-unsigned.apk android/de.raphaelmuesseler.financer.client.app/app/build/outputs/apk/release/financer-app.apk'
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
      archiveArtifacts(artifacts: '**/financer-installer.jar, **/financer-portable.jar, **/financer-portable.exe, **/financer-app.apk', fingerprint: true)
      step([$class: 'JacocoPublisher'])

    }

  }
}