pipeline {
    agent any

    tools {
        jdk 'jdk17'
        gradle 'gradle-8'
    }

    environment {
        DC_HOME = '/var/jenkins_home/tools/dependency-check/DC/dependency-check'
        PATH = "${env.DC_HOME}/bin:${env.PATH}"
//         NVD_API_KEY = credentials('NVD_API_KEY')
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

        // 🎯 NUEVA ETAPA DE VERIFICACIÓN
        stage('Verificar Conectividad NVD') {
            steps {
                echo 'Verificando la conectividad saliente a la API de NVD...'
                sh '''
                CODE=$(curl -s -o /dev/null -w "%{http_code}" https://services.nvd.nist.gov/rest/json/cves/2.0)
                echo "HTTP CODE: $CODE"
                '''
            }
        }
        // ------------------------------------

        stage('OWASP Dependency Check') {
            environment {
                // Establecer el nivel de falla (opcional, recomendado: 7 (HIGH) o 8 (CRITICAL))
                // Esto hará que el build falle si se encuentra una vulnerabilidad con puntaje CVSS >= 7
                FAILURE_THRESHOLD = '11'
            }

            steps {
                withCredentials([string(credentialsId: 'NVD_API_KEY', variable: 'NVD_KEY')]) {

                catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                    sh '''
                    # Ejecuta Dependency-Check desde la instalación manual
                    ${DC_HOME}/bin/dependency-check.sh \
                        --project "MiProyecto" \
                        --scan . \
                        --format XML \
                        --out build/reports/dependency-check-report \
                        --nvdApiKey ${NVD_KEY} \
                        --failOnCVSS ${FAILURE_THRESHOLD} \
                        --disableOssIndex \
                        --suppression dependency-check-suppressions.xml
                    '''
                    }
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

            // Publica el reporte de OWASP Dependency-Check
//             dependencyCheckPublisher pattern: 'build/reports/dependency-check/dependency-check-report.xml'
            dependencyCheckPublisher pattern: 'build/reports/dependency-check-report/dependency-check-report.xml'
//             dependencyCheckPublisher pattern: 'build/reports/dependency-check-report.xml'
//             dependencyCheckPublisher pattern: 'dependency-check-report.xml'
        }
    }
}
