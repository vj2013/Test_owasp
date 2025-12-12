pipeline {
    agent any

    tools {
        jdk 'jdk17'
        gradle 'gradle-8'
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
                dependencyCheck odcInstallation: 'DC',
                                    scanPath: '.',
                                    additionalArguments: '',
                                     stopBuild: false
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
//             dependencyCheckPublisher pattern: 'build/reports/dependency-check-report.xml'
            dependencyCheckPublisher pattern: 'dependency-check-report.xml'
        }
    }
}
