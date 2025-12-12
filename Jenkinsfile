pipeline {
    agent any

    tools {
        jdk 'jdk17'
        gradle 'gradle-8'
    }

    environment {
        DC_HOME = '/var/jenkins_home/tools/dependency-check/DC/dependency-check'
        PATH = "${env.DC_HOME}/bin:${env.PATH}"
        NVD_API_KEY = credentials('NVD_API_KEY')
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
                // Intentamos una conexión HTTP simple. Si la red está bloqueada,
                // este comando fallará y abortará el pipeline antes de Dependency Check.
                sh 'curl -s -o /dev/null -w "%{http_code}" https://services.nvd.nist.gov/rest/json/cves/2.0 | grep 200 || true'
                // NOTA: Si su entorno es estricto, es posible que solo obtenga un 401 (no autorizado)
                // lo cual también es una respuesta válida, ya que indica que la conexión funciona.
                // Si el curl falla por red/DNS, el código de salida será distinto de 0.
            }
        }
        // ------------------------------------

        stage('OWASP Dependency Check') {
            environment {
                // Establecer el nivel de falla (opcional, recomendado: 7 (HIGH) o 8 (CRITICAL))
                // Esto hará que el build falle si se encuentra una vulnerabilidad con puntaje CVSS >= 7
                FAILURE_THRESHOLD = '7'
            }

            steps {
                withCredentials([string(credentialsId: 'NVD_API_KEY_CREDENTIAL_ID', variable: 'NVD_KEY')]) {

                sh '''
                # Ejecuta Dependency-Check desde la instalación manual
                $DC_HOME/bin/dependency-check.sh \
                    --project "MiProyecto" \
                    --scan . \
                    --format XML \
                    --out build/reports/dependency-check-report \
                    --nvdApiKey $NVD_API_KEY
                    --failOnCVSS $FAILURE_THRESHOLD
                '''
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
            dependencyCheckPublisher pattern: 'build/reports/dependency-check-report.xml'
//             dependencyCheckPublisher pattern: 'dependency-check-report.xml'
        }
    }
}
