pipeline {
    agent any
    
    parameters {
        choice(name: 'ACTION', choices: ['install', 'uninstall', 'check'], description: 'Choose the operation')
        choice(name: 'VERSION', choices: ['8.0', '8.4'], description: 'Choose the MySQL version')
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
                // Checkout the 'local' branch from your repository
                git branch: 'local', url: 'https://github.com/Bhawna2611/Tool_MySql.git'
            }
        }

        stage('Ansible Validation') {
            parallel {
                stage('Syntax Check') {
                    steps {
                        // Validate Ansible playbook syntax
                        sh "ansible-playbook -i inventory.ini playbook.yml --syntax-check"
                    }
                }
                stage('Connectivity Ping') {
                    steps {
                        // Check connectivity to localhost defined in inventory.ini
                        sh "ansible all -i inventory.ini -m ping"
                    }
                }
            }
        }

        // --- INPUT APPROVAL STAGE ---
        stage('Manual Approval') {
            steps {
                script {
                    // This pauses the pipeline and waits for a user to click "Proceed"
                    input message: "Do you want to proceed with MySQL ${params.ACTION} (Version: ${params.VERSION}) on Localhost?", 
                          ok: "Yes, Deploy Now"
                }
            }
        }

        stage('Execute MySQL Role') {
            steps {
                echo "Executing MySQL Role..."
                // Running Ansible locally. Note: --private-key is removed for localhost setup.
                sh """
                    ansible-playbook -i inventory.ini playbook.yml \
                    -e "mysql_action=${params.ACTION}" \
                    -e "mysql_version=${params.VERSION}"
                """
            }
        }
    }

    post {
        success {
            mail to: 'bhavna123porwal@gmail.com',
                 subject: "SUCCESS: MySQL ${params.ACTION} Job #${env.BUILD_NUMBER}",
                 body: "The MySQL task was completed successfully. View details: ${env.BUILD_URL}"
        }
        failure {
            mail to: 'bhavna123porwal@gmail.com',
                 subject: "FAILED: MySQL ${params.ACTION} Job #${env.BUILD_NUMBER}",
                 body: "The MySQL task failed. Please check the logs: ${env.BUILD_URL}"
        }
    }
}
