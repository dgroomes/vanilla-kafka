#!/usr/bin/env bash

export VANILLA_KAFKA_ROOT_DIR=$(pwd)

# Bring up the Docker containers using Docker Compose
up() {
  "$VANILLA_KAFKA_ROOT_DIR"/scripts/up.sh
}

# Take down the Docker containers using Docker Compose
down() {
  "$VANILLA_KAFKA_ROOT_DIR"/scripts/down.sh
}

# Build (without the tests)
build() {
  "$VANILLA_KAFKA_ROOT_DIR"/scripts/build.sh
}

# Run the app
run() {
  "$VANILLA_KAFKA_ROOT_DIR"/scripts/run.sh
}

# Execute the tests
test() {
  "$VANILLA_KAFKA_ROOT_DIR"/scripts/test.sh
}

# Get the current offsets of the app's Kafka consumer group
currentOffsets() {
  "$VANILLA_KAFKA_ROOT_DIR"/scripts/current-offsets.sh
}

# Consume from the Kafka topic
consume() {
  "$VANILLA_KAFKA_ROOT_DIR"/scripts/consume.sh
}

# Produce a test message to the Kafka topic
produce() {
  "$VANILLA_KAFKA_ROOT_DIR"/scripts/produce.sh
}