#!/bin/bash

# build maven

mvn clean install

# Define the file containing your docker command
file="build.sh"

# Extract the current version number
current_version=23

# Increment the version number by 1
new_version=$((current_version + 1))

# Replace the old version with the new version in the file
sed -i "" "s/current_version=$current_version/current_version=$new_version/g" "$file"

echo "Updated version to v$new_version in $file"

docker build --platform linux/amd64 --push -t kimsourtann/stock-management:v"$new_version" .

# Deploy to EC2
ssh -i "$HOME/Downloads/"stock-keypair.pem  ubuntu@ec2-3-17-133-210.us-east-2.compute.amazonaws.com "sed -i 's|image: kimsourtann/stock-management:v[0-9]*|image: kimsourtann/stock-management:v"$new_version"|' stockmanagement/docker-compose.yml && docker compose -f stockmanagement/docker-compose.yml up -d"