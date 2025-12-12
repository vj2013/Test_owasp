pipeline {
    agent any

    tools {
        jdk 'jdk17'
        gradle 'gradle-8'
    }

    environment {
        DC_HOME = '/var/jenkins_home/tools/dependency-check/DC/dependency-check-12.1.0-release'
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
//                 /* script{
//                     def dcHome = tool 'DC'
//                     sh "chmod +x ${dcHome}/dependency-check"
                    dependencyCheck additionalArguments: '''
                        -o "."
                        -s "."
                        -f "ALL"
                        --prettyPrint
                        --failOnCVSS 7.0
                    ''',
                    odcInstallation: 'DC'

//                 } */
                sh """
                    dependency-check.sh --project 'SpringBootApp' \
                                        --scan . \
                                        --format XML \
                                        --out build/reports
                    """
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
