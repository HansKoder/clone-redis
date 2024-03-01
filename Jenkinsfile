pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh './gradlew clean build'
            }
        }
        stage('Test') {
            steps {
                // Run tests and generate JaCoCo coverage report
                sh './gradlew test testCodeCoverageReport'
            }
        }

        stage('Publish Coverage Report') {
            steps {
                // Publish JaCoCo coverage report to Jenkins
                // jacoco(execPattern: 'build/jacoco/test.exec')
                publishHTML(target: [reportDir: 'build/reports/jacoco/testCodeCoverageReport/html', reportFiles: 'index.html', reportName: 'JaCoCo Code Coverage Report'], keepAll: true, reportTitle: 'JaCoCo Code Coverage')
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
