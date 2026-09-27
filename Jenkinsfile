pipeline {
	agent any
	options {
		skipDefaultCheckout(true)
	}
	stages {
		stage('Checkout') {
			steps {
				checkout scm
			}
		}
		stage('Build') {
            steps {
                bat 'C:\\Tools\\Maven\\apache-maven-3.9.16\\bin\\mvn.cmd -B -DskipTests clean install'
                 }
                 }
		stage('Test') {
			steps {
				bat '"C:\\Tools\\Maven\\apache-maven-3.9.16\\bin\\mvn.cmd" -B test'
			}
		}
		stage('Code Coverage') {
			steps {
				bat '"C:\\Tools\\Maven\\apache-maven-3.9.16\\bin\\mvn.cmd" -B jacoco:report'
			}
		}
		stage('Publish Test Results') {
			steps {
				junit '**/target/surefire-reports/*.xml'
			}
		}
		stage('Publish Coverage Report') {
			steps {
				archiveArtifacts artifacts: 'target/site/jacoco/**', fingerprint: true
			}
		}
		
		stage('Docker Check') {
			steps {
        bat '"C:\\Users\\rajal\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" --version'
    }


		}
		stage('Docker Build') {
			steps {
				bat '"C:\\Users\\rajal\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe" build -t rthless/myapp:latest .'

			}
		}
	}
}