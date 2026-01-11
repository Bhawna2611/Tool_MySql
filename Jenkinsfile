pipeline {
    agent any
    
    parameters {
        choice(name: 'ACTION', choices: ['install', 'uninstall', 'check'], description: 'Choose the operation')
        choice(name: 'VERSION', choices: ['8.0', '8.4'], description: 'Choose the MySQL version')
    }

    environment {
        SSH_KEY = credentials('my-aws-key')
    }

    stages {
        stage('Initialize & Clean') {
            steps {
                echo "Cleaning up old workspace..."
                cleanWs() 
            }
        }

        stage('Code Checkout') {
            steps {
                echo "Fetching code from GitHub..."
                // URL ADDED HERE
                git branch: 'local', 
                    url: ' https://github.com/Bhawna2611/Tool_MySql.git'
            }
        }

        stage('Ansible Validation') {
            parallel {
                stage('Syntax Check') {
                    steps {
                        sh "ansible-playbook -i inventory.ini playbook.yml --syntax-check"
                    }
                }
                stage('Connectivity Ping') {
                    steps {
                        sh "ansible all -i inventory.ini -m ping --private-key ${SSH_KEY}"
                    }
                }
            }
        }

        stage('Execute MySQL Role') {
            steps {
                sh """
                    ansible-playbook -i ansible/inventory.ini ansible/playbook.yml 
                    -e "mysql_action=${params.ACTION}" \
                    -e "mysql_version=${params.VERSION}" \
                    --private-key ${SSH_KEY}
                """
            }
        }
    }

    post {
        success {
            mail to: 'bhavna123porwal@gmail.com',
                 subject: "SUCCESS: MySQL ${params.ACTION} Job #${env.BUILD_NUMBER}",
                 body: "Build successful. Check: ${env.BUILD_URL}"
        }
        failure {
            mail to: 'bhavna123porwal@gmail.com',
                 subject: "FAILED: MySQL ${params.ACTION} Job #${env.BUILD_NUMBER}",
                 body: "Build failed. Check: ${env.BUILD_URL}"
        }
    }
}
