#!/bin/bash

# build maven

mvn clean install

# Define the file containing your docker command
file="build.sh"

# Extract the current version number
current_version=5

# Increment the version number by 1
new_version=$((current_version + 1))

# Replace the old version with the new version in the file
sed -i "" "s/current_version=$current_version/current_version=$new_version/g" "$file"

echo "Updated version to v$new_version in $file"

docker build --platform linux/amd64 --push -t kimsourtann/stock-management:v"$new_version" .