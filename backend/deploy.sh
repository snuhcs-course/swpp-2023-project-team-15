#!/bin/bash

# Define the SSH key and user@host
KEY="~/.ssh/swpp-key.pem"
USER_HOST="ec2-user@ec2-13-125-91-166.ap-northeast-2.compute.amazonaws.com"

# Get the current branch name on the local machine
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)

# Check if local branch is in sync with remote origin
LOCAL_COMMIT=$(git rev-parse HEAD)
REMOTE_COMMIT=$(git ls-remote origin -h refs/heads/$CURRENT_BRANCH | cut -f1)

if [ "$LOCAL_COMMIT" != "$REMOTE_COMMIT" ]; then
    echo "Error: Local branch $CURRENT_BRANCH is not in sync with origin. Aborting deployment."
    exit 1
fi

# Use SSH to run commands on the remote server
ssh -o "IdentitiesOnly yes" -i "$KEY" "$USER_HOST" <<ENDSSH
cd swpp-2023-project-team-15/backend/
git pull
git checkout $CURRENT_BRANCH
poetry install
sudo systemctl restart gunicorn
ENDSSH
