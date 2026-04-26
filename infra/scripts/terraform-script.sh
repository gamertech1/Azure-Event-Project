#!/bin/bash

set -e
ACTION=$1
ENV=$2
COMPONENT=$3
if [ -z "$ACTION" ] || [ -z "$ENV" ]; then
  echo "Usage: $0 <plan|apply|destroy> <environment>"
  exit 1
fi
WORKDIR="infra/environments/$ENV/$COMPONENT"
echo "👉 Running Terraform $ACTION for $ENV"

cd $WORKDIR

terraform init \
  -backend-config="key=$ENV/terraform.tfstate"

if [ "$ACTION" == "plan" ]; then
  terraform plan -out=tfplan
elif [ "$ACTION" == "apply" ]; then
  terraform apply tfplan -auto-approve
elif [ "$ACTION" == "destroy" ]; then
  terraform destroy -auto-approve
fi