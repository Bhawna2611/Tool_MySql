// Step 1: Checkout Function
def checkout(String branch, String url) {
    cleanWs()
    git branch: branch, url: url
}

// Step 2: Linting Function
def runLint() {
    echo "Running Ansible Lint..."
    // Standard linting on the playbook
    sh "unset ANSIBLE_CONFIG && ansible-lint playbook.yml || true"
}

// Step 3: Connectivity Function (Updated for Dynamic Inventory)
def checkPing(String invSource) {
    echo "Checking Server Connectivity via: ${invSource}"
    // 'all' targets all hosts returned by your dynamic inventory
    sh "ansible all -i ${invSource} -m ping"
}

// Step 4: Sonar Scanner Function
def sonarScan(String projectKey) {
    def scannerHome = tool 'SonarScanner'
    try {
        withSonarQubeEnv('SonarQubeServer') {
            sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=${projectKey} -Dsonar.sources=."
        }
    } catch (Exception e) {
        echo "SonarQube skipped: ${e.message}"
    }
}

// Step 5: Ansible Execution (Updated with --become for remote sudo)
def ansibleRun(Map args) {
    def checkFlag = args.isDryRun ? "--check" : ""
    echo "Executing Ansible on Dynamic Inventory..."
    
    sh """
        unset ANSIBLE_CONFIG && \
        ansible-playbook -i ${args.inventory} playbook.yml \
        -e 'mysql_action=${args.action}' \
        -e 'mysql_version=${args.version}' \
        --become \
        ${checkFlag}
    """
}
