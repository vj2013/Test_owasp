pipeline {
    agent any

    tools {
        jdk 'jdk17'
        gradle 'gradle-8'
    }

    environment {
        DC_HOME = '/var/jenkins_home/tools/dependency-check/DC/dependency-check'
        PATH = "${env.DC_HOME}/bin:${env.PATH}"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build'
            }
        }

        stage('OWASP Dependency Check') {
            steps {
                sh '''
                # Ejecuta Dependency-Check desde la instalación manual
                $DC_HOME/bin/dependency-check.sh \
                    --project "MiProyecto" \
                    --scan . \
                    --format XML \
                    --out build/reports/dependency-check-report
                '''
            }
        }

        stage('Tests') {
            steps {
                sh './gradlew test'
            }
        }

    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'build/test-results/test/*.xml'

            // Publica el reporte de OWASP Dependency-Check
            dependencyCheckPublisher pattern: 'build/reports/dependency-check-report.xml'
//             dependencyCheckPublisher pattern: 'dependency-check-report.xml'
        }
    }
}
