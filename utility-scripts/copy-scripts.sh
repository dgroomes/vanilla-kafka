#!/usr/bin/env bash
# Copy the utility scripts to the other sub-projects

set -eu

subProjects=(
'interactive'
'kafka-in-kafka-out'
'spring-barebones'
'spring-errors'
'spring-headers'
'spring-seekable'
'streams'
'streams-zip-codes'
'ssl-tls-security'
)

for i in "${subProjects[@]}"; do
    DIR="../$i/scripts"
    cp log4j.properties "$DIR"
    cp server.properties "$DIR"
    cp start-kafka.sh "$DIR"
    cp stop-kafka.sh "$DIR"
done
