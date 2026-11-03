pipeline {
    agent any

    triggers {
        // Check GitHub for new commits every 2 minutes
        pollSCM('H/2 * * * *')
    }

    environment {
        PATH = "C:\\tools\\apache-maven-3.9.16\\bin;C:\\Users\\pranav\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin;${env.PATH}"
        IMAGE = "asia-south1-docker.pkg.dev/cia2-513606/library-manager/library-manager:${BUILD_NUMBER}"
        GCP_KEY = credentials('gcp-key')
    }

    stages {
        stage('Build and Test') {
            steps {
                bat 'mvn -B -ntp clean package'
            }
        }

        stage('Docker Build and Push') {
            steps {
                bat 'docker build -t %IMAGE% .'
                bat 'type "%GCP_KEY%" | docker login -u _json_key --password-stdin https://asia-south1-docker.pkg.dev'
                bat 'docker push %IMAGE%'
            }
        }

        stage('Deploy with Ansible') {
            steps {
                bat 'docker run --rm -v "%WORKSPACE%:/work" -v "%GCP_KEY%:/key.json" -w /work google/cloud-sdk:slim bash ansible/deploy.sh %IMAGE%'
            }
        }
    }
}
