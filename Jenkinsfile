pipeline {
    agent any
    environment {
        IMAGE_NAME = 'rthless/temperature-converter'
    }
    stages {
        stage('Build and Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn -B clean verify'
                    } else {
                        bat '"C:\\Tools\\Maven\\apache-maven-3.9.16\\bin\\mvn.cmd" -B clean verify'
                    }
                }
            }
        }
        stage('Docker Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'docker build -t "$IMAGE_NAME:latest" .'
                    } else {
                        bat '"C:\\Users\\rajal\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" build -t %IMAGE_NAME%:latest .'
                    }
                }
            }
        }
        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_TOKEN')]) {
                    script {
                        try {
                            if (isUnix()) {
                                sh 'printf "%s" "$DOCKERHUB_TOKEN" | docker login --username "$DOCKERHUB_USERNAME" --password-stdin'
                                sh 'docker push "$IMAGE_NAME:latest"'
                            } else {
                                powershell '$env:DOCKERHUB_TOKEN | & "C:\Users\rajal\AppData\Local\Programs\DockerDesktop\resources\bin\docker.exe" login --username $env:DOCKERHUB_USERNAME --password-stdin'
                                bat '"C:\\Users\\rajal\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" push %IMAGE_NAME%:latest'
                            }
                        } finally {
                            if (isUnix()) {
                                sh 'docker logout'
                            } else {
                                bat '"C:\\Users\\rajal\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" logout'
                            }
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            archiveArtifacts allowEmptyArchive: true, artifacts: 'target/site/jacoco/**', fingerprint: true
        }
    }
}
