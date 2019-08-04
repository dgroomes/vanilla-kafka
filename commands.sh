#!/usr/bin/env bash

export SEEKABLE_KAFKA_ROOT_DIR=$(pwd)

# Bring up the Docker containers using Docker Compose
up() {
  "$SEEKABLE_KAFKA_ROOT_DIR"/scripts/up.sh
}

# Take down the Docker containers using Docker Compose
down() {
  "$SEEKABLE_KAFKA_ROOT_DIR"/scripts/down.sh
}

# Execute the tests
test() {
  "$SEEKABLE_KAFKA_ROOT_DIR"/scripts/test.sh
}

# Get the current offsets of the app's Kafka consumer group
currentOffsets() {
  "$SEEKABLE_KAFKA_ROOT_DIR"/scripts/current-offsets.sh
}

# Consume from the Kafka topic
consume() {
  "$SEEKABLE_KAFKA_ROOT_DIR"/scripts/consume.sh
}