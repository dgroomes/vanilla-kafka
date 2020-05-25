#!/usr/bin/env bash

export KAFKA_STREAMS_ROOT_DIR=$(pwd)

# Bring up the Docker containers using Docker Compose
up() {
  "$KAFKA_STREAMS_ROOT_DIR"/scripts/up.sh
}

# Take down the Docker containers using Docker Compose
down() {
  "$KAFKA_STREAMS_ROOT_DIR"/scripts/down.sh
}

# Build (without the tests)
build() {
  "$KAFKA_STREAMS_ROOT_DIR"/scripts/build.sh
}

# Run the app
run() {
  "$KAFKA_STREAMS_ROOT_DIR"/scripts/run.sh
}

# Get the current offsets of the app's Kafka consumer group
currentOffsets() {
  "$KAFKA_STREAMS_ROOT_DIR"/scripts/current-offsets.sh
}

# Consume from the Kafka topic
consume() {
  "$KAFKA_STREAMS_ROOT_DIR"/scripts/consume.sh
}

# Produce a test message to the Kafka topic
produce() {
  "$KAFKA_STREAMS_ROOT_DIR"/scripts/produce.sh $@
}