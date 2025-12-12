pipeline {
    agent any

    tools {
        jdk 'jdk17'
        gradle 'Gradle8.14.3
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'gradlew.bat clean build'
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
                bat 'gradlew.bat test'
            }
        }

    }

    post {
        always {
            junit 'build/test-results/test/*.xml'
        }
    }
}
