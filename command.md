ansible-playbook -i aws_ec2.yml playbook.yml -e mysql_action=install -e mysql_version=8.4/8.0

 ansible-playbook -i aws_ec2.yml playbook.yml -e mysql_action=check

ansible-playbook -i aws_ec2.yml playbook.yml -e mysql_action=uninstall
