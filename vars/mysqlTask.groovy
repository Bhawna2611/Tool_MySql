// Step 1: Source Code Management
def checkout(String branch, String url) {
    // Cleans the current workspace to avoid conflicts from previous builds
    cleanWs()
    // Clones the specified branch from the provided GitHub repository URL
    git branch: branch, url: url
}

// Step 2: Static Code Analysis for Ansible

def runLint() {
    echo "Running Ansible Lint..."
   
    sh "ansible-lint playbook.yml || true"
}

// Step 3: Server Reachability Test
def checkPing(String invSource) {
    echo "Checking Server Connectivity via: ${invSource}"
    // Enables the AWS EC2 plugin and pings all servers discovered in the inventory
    sh """
        export ANSIBLE_INVENTORY_ENABLED=aws_ec2,ini,yaml
        ansible all -i aws_ec2.yml -m ping || true
    """
}

// Step 4: Security and Quality Scanning
def sonarScan(String projectKey) {
    try {
        // Locates the SonarScanner tool configured in Jenkins Global Tool Configuration
        def scannerHome = tool 'SonarScanner'
        // Wraps execution with SonarQube server environment variables
        withSonarQubeEnv('SonarQubeServer') {
            sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=${projectKey} -Dsonar.sources=."
        }
    } catch (Exception e) {
        // Logs a warning but allows the pipeline to proceed if SonarQube is unreachable
        echo "SonarQube skipped or failed: ${e.message}"
    }
}

// Step 5: Ansible Playbook Execution
def ansibleRun(Map args) {
    // Determines if the run should be a simulation (--check) or a real deployment
    def checkFlag = args.isDryRun ? "--check" : ""
    echo "Executing Ansible on Inventory: ${args.inventory}"
    
    sh """
        # Enable dynamic inventory plugins
        export ANSIBLE_INVENTORY_ENABLED=aws_ec2,ini,yaml
        # Disable host key checking to prevent the pipeline from waiting for manual SSH approval
        export ANSIBLE_HOST_KEY_CHECKING=False
        
        ansible-playbook -i ${args.inventory} playbook.yml \
        -e 'mysql_action=${args.action}' \
        -e 'mysql_version=${args.version}' \
        --become \
        ${checkFlag}
    """
}
