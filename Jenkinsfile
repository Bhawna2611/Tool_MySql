pipeline {
    agent any
    
    parameters {
        // Option to choose between install, uninstall, or just check
        choice(name: 'ACTION', choices: ['install', 'uninstall', 'check'], description: 'Choose MySQL Operation')
        choice(name: 'VERSION', choices: ['8.0', '8.4'], description: 'Choose MySQL Version')
    }

    environment {
        SSH_KEY = credentials('my-aws-key') 
    }

    stages {
        stage('Initialize & Clean Old Workspace') {
            steps {
                echo "Deleting old workspace files for a fresh build..."
                cleanWs() 
            }
        }

        stage('Code Checkout') {
            steps {
                echo "Downloading code from Repository..."
                checkout scm
            }
        }

        stage('Parallel Pre-Validation') {
            parallel {
                stage('Ansible Syntax Check') {
                    steps {
                        sh "ansible-playbook -i inventory.ini playbook.yml --syntax-check"
                    }
                }
                stage('Server Ping Test') {
                    steps {
                        sh "ansible all -i inventory.ini -m ping --private-key ${SSH_KEY}"
                    }
                }
            }
        }

        stage('Execute MySQL Operation') {
            steps {
                echo "Running Ansible with Action: ${params.ACTION} and Version: ${params.VERSION}"
                sh """
                    ansible-playbook -i inventory.ini playbook.yml \
                    -e "mysql_action=${params.ACTION}" \
                    -e "mysql_version=${params.VERSION}" \
                    --private-key ${SSH_KEY}
                """
            }
        }
    }

    post {
        success {
            echo "Deployment Successful!"
            mail to: 'bhavna123porwal@gmail.com',
                 subject: "SUCCESS: MySQL ${params.ACTION}",
                 body: "Build #${env.BUILD_NUMBER} finished successfully. Check: ${env.BUILD_URL}"
        }
        failure {
            echo "Deployment Failed!"
            mail to: 'bhavna123porwal@gmail.com',
                 subject: "FAILED: MySQL ${params.ACTION}",
                 body: "Build #${env.BUILD_NUMBER} failed. Review logs: ${env.BUILD_URL}"
        }
        // Always section has been removed as per your request
    }
}
