#!/bin/bash

# Define the SSH key and user@host
KEY="~/.ssh/swpp-key.pem"
USER_HOST="ec2-user@ec2-13-125-91-166.ap-northeast-2.compute.amazonaws.com"

# Get the current branch name on the local machine
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)

# Use SSH to run commands on the remote server
ssh -o "IdentitiesOnly yes" -i "$KEY" "$USER_HOST" <<ENDSSH
cd swpp-2023-project-team-15/backend/
git pull
git checkout $CURRENT_BRANCH
poetry install
sudo systemctl restart gunicorn
ENDSSH
