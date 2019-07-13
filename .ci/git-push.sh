#!/bin/bash
set -e

if [ "$TRAVIS_BRANCH" != "master" ] || [ "$TRAVIS_PULL_REQUEST" = "true" ]; then
    echo "Skipping Git push, on branch $TRAVIS_BRANCH."
else
    echo "Pushing changes, on branch $TRAVIS_BRANCH."
    git checkout $TRAVIS_BRANCH
    git add .
    git status
    git commit -m "Auto-update sound bytes" -m "[skip ci]"
    git remote add origin-travis https://${GITHUB_TOKEN}@github.com/MrBean355/dota2-integration.git
    git push origin-travis
fi
