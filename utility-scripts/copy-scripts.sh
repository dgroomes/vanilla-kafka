#!/usr/bin/env bash
# Copy the utility scripts to the other sub-projects

set -eu

subProjects=(
'interactive'
'spring-errors'
'spring-headers'
'spring-seekable'
'streams'
)

for i in "${subProjects[@]}"; do
    cp start-kafka.sh "../$i/scripts"
    cp stop-kafka.sh "../$i/scripts"
done
