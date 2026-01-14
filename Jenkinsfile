@Library('my-shared-library') _

pipeline {
    agent any
    
    environment {
        LC_ALL = 'en_US.UTF-8'
        LANG   = 'en_US.UTF-8'
        ANSIBLE_INVENTORY_ENABLED = 'ini,aws_ec2,yaml,script' // Ensure dynamic plugins are enabled
        ANSIBLE_HOST_KEY_CHECKING = 'False' // Prevents pipeline hang on new remote hosts
        
        // Define your dynamic inventory file path here
        INVENTORY_PATH = 'aws_ec2.yml' 
        // ID of the SSH credentials stored in Jenkins
        SSH_AUTH_ID    = 'my-server-ssh-key' 
    }

    parameters {
        choice(name: 'ACTION', choices: ['install', 'uninstall'], description: 'Select Action')
        choice(name: 'VERSION', choices: ['8.0', '8.4'], description: 'Select Version')
    }

    stages {
        stage('Initialize') {
            steps {
                script {
                    mysqlTask.checkout('main', 'https://github.com/Bhawna2611/Tool_MySql.git')
                }
            }
        }

        stage('Verify') {
            parallel {
                stage('Lint') {
                    steps { 
                        script { mysqlTask.runLint() } 
                    }
                }
                stage('Ping') {
                    steps { 
                        // Wrap in sshagent to allow remote connectivity
                        sshagent([env.SSH_AUTH_ID]) {
                            script { mysqlTask.checkPing(env.INVENTORY_PATH) } 
                        }
                    }
                }
            }
        }

        stage('Scan') {
            steps { 
                script { mysqlTask.sonarScan('MySQL_Project') } 
            }
        }

        stage('Dry Run') {
            steps {
                sshagent([env.SSH_AUTH_ID]) {
                    script {
                        mysqlTask.ansibleRun(
                            inventory: env.INVENTORY_PATH, 
                            action: params.ACTION, 
                            version: params.VERSION, 
                            isDryRun: true
                        )
                    }
                }
            }
        }

        stage('Deploy') {
            input { message "Proceed to Deploy to Ubuntu/RedHat servers?" }
            steps {
                sshagent([env.SSH_AUTH_ID]) {
                    script {
                        mysqlTask.ansibleRun(
                            inventory: env.INVENTORY_PATH, 
                            action: params.ACTION, 
                            version: params.VERSION, 
                            isDryRun: false
                        )
                    }
                }
            }
        }
    }
}
