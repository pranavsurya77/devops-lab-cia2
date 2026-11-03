#!/bin/bash
# Runs inside the google/cloud-sdk container: connect to GKE, then deploy with Ansible
set -e

# Work on a copy, since the mounted Jenkins workspace can't be edited from the container
cp -r /work /tmp/app && cd /tmp/app

apt-get update -qq && apt-get install -y -qq ansible-core kubectl google-cloud-cli-gke-gcloud-auth-plugin > /dev/null

gcloud auth activate-service-account --key-file=/key.json
gcloud container clusters get-credentials cia2-gke --region asia-south1 --project cia2-513606

ansible-playbook -i ansible/inventory.ini ansible/deploy.yml -e image="$1"
