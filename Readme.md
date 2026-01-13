# MySQL Automation Using Ansible, Jenkins, and AWS

## Project Overview
This project automates the installation, uninstallation, and version checks of MySQL on multiple servers simultaneously using Ansible with dynamic inventory on AWS.  
The automation is triggered through Jenkins pipelines, and reusable logic is implemented using Jenkins Shared Libraries.

---

## Tools & Technologies Used
- Ansible – Configuration management and automation
- Jenkins – CI/CD pipeline orchestration
- Git – Version control
- AWS – Cloud infrastructure
- MySQL – Database service

---

## Features
- Dynamic inventory to fetch AWS EC2 instances automatically
- Install MySQL on multiple servers simultaneously
- Jenkins choice parameters for version selection
- Jenkins choice parameters for Install / Uninstall / version Check
- Reusable functions using Jenkins Shared Library
- Idempotent Ansible playbooks

---

## Project Structure
.
├── ansible  
│   ├── playbooks  
│   │   ├── install_mysql.yml  
│   │   ├── uninstall_mysql.yml  
│   │   └── mysql_check.yml  
│   ├── roles  
│   │   └── mysql  
│   └── inventory  
│       └── aws_ec2.yml  
│  
├── vars  
│   └── mysql_versions.yml  
│  
├── Jenkinsfile  
├── shared-library  
│   └── vars  
│       └── mysqlOperations.groovy  
│  
└── README.md  

---

## Dynamic Inventory (AWS)
The project uses Ansible AWS EC2 Dynamic Inventory to automatically discover target servers.

Requirements:
- AWS IAM role or access keys
- EC2 instances with proper tags (example: Role=mysql)

Example inventory configuration:
    plugin: aws_ec2
    regions:
      - us-east-1
    filters:
      tag:Role: mysql
    keyed_groups:
      - key: tags.Role

---

## Jenkins Pipeline
The Jenkins pipeline is parameterized to allow flexible execution.

### Jenkins Parameters
- ACTION: Install / Uninstall / Check
- MYSQL_VERSION: 5.7 / 8.0

---

## Jenkins Shared Library
Reusable logic is implemented using a Jenkins Shared Library to keep the pipeline clean.

Example shared library function:
    def mysqlAction(action, version) {
        sh """
        ansible-playbook ansible/playbooks/${action}_mysql.yml \
        -e mysql_version=${version}
        """
    }

This function is called from the Jenkinsfile based on user-selected parameters.

---

## How to Run
1. Clone the repository:
       git clone <repository-url>
       cd mysql-automation

2. Configure AWS credentials on Jenkins or the Ansible control node

3. Trigger the Jenkins pipeline:
   - Select ACTION
   - Select MYSQL_VERSION

4. Monitor execution logs in Jenkins

---

## Supported Actions
- Install – Installs selected MySQL version
- Uninstall – Removes MySQL from servers
- Check – Verifies MySQL service status and version

---

## Prerequisites
- Jenkins installed and configured
- Ansible installed on Jenkins agent
- AWS EC2 instances accessible via SSH
- IAM permissions for EC2 discovery

---

## Verification
After installation, the following checks are performed:
- MySQL service status
- Port 3306 availability
- MySQL version validation

---

## Conclusion
This project provides a scalable, reusable, and automated solution for managing MySQL installations across AWS infrastructure using industry-standard DevOps tools.

---

## Author
Bhawna Dangarh,
DevOps Engineer,
Ninja batch - 33.
