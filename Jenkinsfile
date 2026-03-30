pipeline {
    agent any

    tools {
        maven 'Maven 3'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh './mvnw clean package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('Unit Tests') {
            steps {
                sh './mvnw test jacoco:report'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Code Quality') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh './mvnw sonar:sonar'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                waitForQualityGate abortPipeline: true
            }
        }

        stage('Integration & E2E Tests') {
            when {
                branch 'main'
            }
            steps {
                sh './mvnw failsafe:integration-test failsafe:verify'
            }
            post {
                always {
                    junit 'target/failsafe-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            when {
                branch 'main'
            }
            steps {
                script {
                    def shortCommit = env.GIT_COMMIT[0..6]
                    sh "docker build -t car-rental-api:${shortCommit} -t car-rental-api:latest ."
                }
            }
        }

        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                script {
                    def shortCommit = env.GIT_COMMIT[0..6]
                    sh "chmod +x deploy.sh && ./deploy.sh ${shortCommit}"
                }
            }
        }

        stage('Smoke Test') {
            when {
                branch 'main'
            }
            steps {
                script {
                    def healthy = sh(
                        script: 'curl --retry 10 --retry-delay 5 --retry-connrefused -f http://car-rental-app:8080/actuator/health',
                        returnStatus: true
                    ) == 0

                    if (!healthy) {
                        def previousTag = sh(script: 'cat /tmp/car-rental-previous-tag', returnStdout: true).trim()
                        if (previousTag != 'none' && previousTag != '') {
                            echo "Smoke test failed — rolling back to ${previousTag}"
                            sh "chmod +x deploy.sh && ./deploy.sh ${previousTag}"
                        } else {
                            echo "Smoke test failed — no previous image to roll back to"
                        }
                        error('Smoke test failed: application did not become healthy')
                    }
                }
            }
        }

    }
}
