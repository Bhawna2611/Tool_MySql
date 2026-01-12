@Library('my-shared-library') _

pipeline {
    agent any
    
    environment {
        LC_ALL = 'en_US.UTF-8'
        LANG   = 'en_US.UTF-8'
        ANSIBLE_INVENTORY_ENABLED = 'ini'
    }

    parameters {
        choice(name: 'ACTION', choices: ['install', 'uninstall'], description: 'Select Action')
        choice(name: 'VERSION', choices: ['8.0', '8.4'], description: 'Select Version')
    }

    stages {
        stage('Initialize') {
            steps {
                script {
                    // File name is mysqlTask.groovy, so we use mysqlTask here
                    mysqlTask.checkout('local', 'https://github.com/Bhawna2611/Tool_MySql.git')
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
                        script { mysqlTask.checkPing('inventory.ini') } 
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
                script {
                    mysqlTask.ansibleRun(
                        inventory: 'inventory.ini', 
                        action: params.ACTION, 
                        version: params.VERSION, 
                        isDryRun: true
                    )
                }
            }
        }

        stage('Deploy') {
            input { message "Proceed to Deploy?" }
            steps {
                script {
                    mysqlTask.ansibleRun(
                        inventory: 'inventory.ini', 
                        action: params.ACTION, 
                        version: params.VERSION, 
                        isDryRun: false
                    )
                }
            }
        }
    }
}
