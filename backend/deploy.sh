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

# Compare .env files between local and remote
LOCAL_ENV_COUNT=$(grep -c -E '^[^# ]+' .env)  # Count lines that aren't comments or blank. These should represent keys.

# Execute command remotely to count keys in the remote .env file. This command counts only the lines that are not comments or blank.
REMOTE_ENV_COUNT=$(ssh -o "IdentitiesOnly yes" -i "$KEY" "$USER_HOST" "grep -c -E '^[^# ]+' swpp-2023-project-team-15/backend/.env")

# Check if local .env has more keys than remote .env
if [ "$LOCAL_ENV_COUNT" -gt "$REMOTE_ENV_COUNT" ]; then
    echo "Error: Local .env file has more keys ($LOCAL_ENV_COUNT) than remote .env file ($REMOTE_ENV_COUNT)."
    # You can choose to abort deployment or continue with warnings
    exit 1
fi

DEPLOYED_HASH=$(git rev-parse --short HEAD)

# Use SSH to run commands on the remote server
ssh -o "IdentitiesOnly yes" -i "$KEY" "$USER_HOST" <<ENDSSH
set -e # stops on the first error
set -x # print commands for debugging

cd swpp-2023-project-team-15/backend/
git fetch origin

# Check if the branch exists in remote's context, and create a local branch that tracks the remote branch if necessary
git checkout $CURRENT_BRANCH 2>/dev/null || git checkout --track origin/$CURRENT_BRANCH

# Pull the latest changes from the remote branch
git pull

poetry install
sudo systemctl restart gunicorn

# Log the branch, hash, and timestamp of the deployment
echo "Deployed branch $CURRENT_BRANCH at revision $DEPLOYED_HASH on $(date)" >> deploy.log
ENDSSH
