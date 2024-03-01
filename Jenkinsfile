pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                // Clean and build the project using Gradle
                sh './gradlew clean build'
            }
        }

        stage('Test') {
            steps {
                // Run tests and generate JaCoCo coverage report
                sh './gradlew test jacocoTestReport'
            }
        }

        stage('Publish Coverage Report') {
            steps {
                // Publish JaCoCo coverage report to Jenkins
                jacoco(execPattern: 'build/jacoco/test.exec')
                publishHTML (target : [allowMissing: false,
                 alwaysLinkToLastBuild: true,
                 keepAll: true,
                 reportDir: 'build/reports/jacoco/test',
                 reportFiles: 'index.html',
                 reportName: 'example',
                 reportTitles: 'example'])
            }
        }

        stage('SonarQube Analysis') {
                    steps {
                        // Run SonarQube analysis
                        script {
                            def scannerHome = tool 'SonarQube Scanner'
                            withSonarQubeEnv('SonarQube') {
                                sh "${scannerHome}/bin/sonar-scanner"
                            }
                        }
                    }
                }
    }

    post {
        always {
            // Archive artifacts for later reference
            archiveArtifacts artifacts: 'build/libs/*.jar'
        }

        success {
            // Send notification if the build is successful
            echo 'Build successful!'
        }

        failure {
            // Send notification if the build fails
            echo 'Build failed!'
        }
    }
}
