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

# Build (without the tests)
build() {
  "$SEEKABLE_KAFKA_ROOT_DIR"/scripts/build.sh
}

# Run the app
run() {
  "$SEEKABLE_KAFKA_ROOT_DIR"/scripts/run.sh
}

# Execute the tests
# 'test' is a command already. Strange stuff happens if you try to declare a function called 'test'
# So let's call this 'runTests'.
runTests() {
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

# Produce test messages to the Kafka topic
produce() {
  "$SEEKABLE_KAFKA_ROOT_DIR"/scripts/produce.sh $@
}