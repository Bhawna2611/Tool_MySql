@Library('my-shared-library') _

pipeline {
    // Defines the execution node; 'built-in' refers to the Jenkins controller node
    agent { label 'built-in' }
    
    environment {
        // Setting system locale to UTF-8 to prevent encoding issues during Ansible execution
        LC_ALL = 'en_US.UTF-8'
        LANG   = 'en_US.UTF-8'
        
        // Enabling necessary Ansible plugins for INI and AWS EC2 dynamic inventory
        ANSIBLE_INVENTORY_ENABLED = 'ini,aws_ec2,yaml,script'
        
        // Skipping SSH host key verification to prevent the pipeline from hanging on new servers
        ANSIBLE_HOST_KEY_CHECKING = 'False'
        
        // Path to the AWS dynamic inventory configuration file
        INVENTORY_PATH = 'aws_ec2.yml' 
        
        // Credential ID for the SSH private key stored in Jenkins
        SSH_AUTH_ID    = 'my-server-ssh-key' 
        
        // Credential ID for AWS Access Key and Secret Key stored in Jenkins
        AWS_CREDS_ID   = 'aws-keys' 
    }

    // User-defined parameters to select MySQL version and the action to perform
    parameters {
        choice(name: 'ACTION', choices: ['install', 'uninstall', 'check',], description: 'Select Action to perform on MySQL')
        choice(name: 'VERSION', choices: ['8.0', '8.4'], description: 'Select MySQL Version')
    }

    stages {
        // Stage 1: Fetching the source code from the Git repository
        stage('Initialize') {
            steps {
                script {
                    // Uses shared library to clone the repository
                    mysqlTask.checkout('local', 'https://github.com/Bhawna2611/Tool_MySql.git')
                }
            }
        }

        // Stage 2: Quality checks and connectivity testing
        stage('Verify') {
            parallel {
                // Task: Checking the Ansible code for syntax and best practice violations
                stage('Lint') {
                    steps { 
                        script { mysqlTask.runLint() } 
                    }
                }
                // Task: Testing connectivity to the AWS EC2 instances
                stage('Ping') {
                    steps { 
                        // Injecting AWS credentials for the dynamic inventory plugin to fetch EC2 instances
                        withCredentials([usernamePassword(credentialsId: env.AWS_CREDS_ID, 
                                         passwordVariable: 'AWS_SECRET_ACCESS_KEY', 
                                         usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
                            // Using SSH agent to provide the private key for server login
                            sshagent([env.SSH_AUTH_ID]) {
                                script { mysqlTask.checkPing(env.INVENTORY_PATH) } 
                            }
                        }
                    }
                }
            }
        }

        // Stage 3: Code analysis using SonarQube
        stage('Scan') {
            steps { 
                script { mysqlTask.sonarScan('MySQL_Project') } 
            }
        }

        // Stage 4: Running Ansible in check-mode (simulating changes without applying them)
        stage('Dry Run') {
            steps {
                withCredentials([usernamePassword(credentialsId: env.AWS_CREDS_ID, 
                                 passwordVariable: 'AWS_SECRET_ACCESS_KEY', 
                                 usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
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
        }

        // Stage 5: Final deployment after manual approval
        stage('Deploy') {
            // Pauses the pipeline and waits for a user to click "Proceed"
            input { message "Proceed to Deploy to Ubuntu/RedHat servers?" }
            steps {
                withCredentials([usernamePassword(credentialsId: env.AWS_CREDS_ID, 
                                 passwordVariable: 'AWS_SECRET_ACCESS_KEY', 
                                 usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
                    sshagent([env.SSH_AUTH_ID]) {
                        script {
                            // Actual execution of the Ansible playbook to install/uninstall MySQL
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
}

