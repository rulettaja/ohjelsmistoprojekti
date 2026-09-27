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
				bat 'mvn -B -DskipTests clean install'
			}
		}
		stage('Test') {
			steps {
				bat 'mvn -B test'
			}
		}
		stage('Code Coverage') {
			steps {
				bat 'mvn -B jacoco:report'
			}
		}
		stage('Publish Test Results') {
			steps {
				junit '**/target/surefire-reports/*.xml'
			}
		}
		stage('Publish Coverage Report') {
			steps {
				jacoco()
			}
		}
	}
}