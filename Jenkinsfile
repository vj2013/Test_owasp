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
                sh './gradlew dependencyCheckAnalyze'
                dependencyCheck additionalArguments: '',
                                scanBuildArtifacts: true,
                                suppressionFile: '',
                                outdir: 'dependency-check-report'
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
            junit allowEmptyResults: true, testResults: 'build/test-results/test/*.xml'
        }
    }
}
