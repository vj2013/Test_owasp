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
                sh './gradlew clean build'
            }
        }

        stage('OWASP Dependency Check') {
            steps {
                dependencyCheckAnalyzer datadir: '',
                    includes: '**/*.jar',
                    isAutoupdateDisabled: false,
                    outdir: 'dependency-check-report',
                    scanpath: '.'
            }
            post {
                always {
                    dependencyCheckPublisher pattern: 'dependency-check-report/dependency-check-report.xml'
                }
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
            junit 'build/test-results/test/*.xml'
        }
    }
}
