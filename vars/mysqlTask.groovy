// Step 1: Checkout Function
def checkout(String branch, String url) {
    cleanWs()
    git branch: branch, url: url
}

// Step 2: Linting Function
def runLint() {
    echo "Running Ansible Lint..."
    sh "unset ANSIBLE_CONFIG && ansible-lint playbook.yml || true"
}

// Step 3: Connectivity Function
def checkPing(String invFile) {
    echo "Checking Server Connectivity..."
    sh "unset ANSIBLE_CONFIG && ansible all -i ${invFile} -m ping"
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

// Step 5: Ansible Execution Function (Dry-run and Deploy)
def ansibleRun(Map args) {
    def checkFlag = args.isDryRun ? "--check" : ""
    sh "unset ANSIBLE_CONFIG && ansible-playbook -i ${args.inventory} playbook.yml -e 'mysql_action=${args.action}' -e 'mysql_version=${args.version}' ${checkFlag}"
}
