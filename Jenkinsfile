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
                // Calling from same file
                mysqlUtils.checkout('local', 'https://github.com/Bhawna2611/Tool_MySql.git')
            }
        }
        stage('Verify') {
            parallel {
                stage('Lint') {
                    steps { mysqlUtils.runLint() }
                }
                stage('Ping') {
                    steps { mysqlUtils.checkPing('inventory.ini') }
                }
            }
        }
        stage('Scan') {
            steps { mysqlUtils.sonarScan('MySQL_Project') }
        }
        stage('Dry Run') {
            steps {
                mysqlUtils.ansibleRun(inventory: 'inventory.ini', action: params.ACTION, version: params.VERSION, isDryRun: true)
            }
        }
        stage('Deploy') {
            input { message "Proceed to Deploy?" }
            steps {
                mysqlUtils.ansibleRun(inventory: 'inventory.ini', action: params.ACTION, version: params.VERSION, isDryRun: false)
            }
        }
    }
}
