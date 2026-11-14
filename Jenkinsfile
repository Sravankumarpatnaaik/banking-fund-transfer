pipeline {
    agent any

    parameters {
        string(name: 'TOMCAT_URL', defaultValue: 'http://127.0.0.1:8080', description: 'Tomcat base URL, including the manager application')
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B test'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn -B -DskipTests package'
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'tomcat-manager', usernameVariable: 'TOMCAT_USER', passwordVariable: 'TOMCAT_PASS')]) {
                    sh '''
                        curl --fail --show-error --user "$TOMCAT_USER:$TOMCAT_PASS" \
                          --upload-file target/meridian-bank.war \
                          "${TOMCAT_URL}/manager/text/deploy?path=/&update=true"
                    '''
                }
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'target/meridian-bank.war', fingerprint: true
        }
    }
}
