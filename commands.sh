#!/usr/bin/env bash

export VANILLA_KAFKA_ROOT_DIR=$(pwd)

# Start Kafka and Zookeeper
startKafka() {
  "$VANILLA_KAFKA_ROOT_DIR"/scripts/start-kafka.sh $@
}

# Stop Kafka and Zookeeper
stopKafka() {
  "$VANILLA_KAFKA_ROOT_DIR"/scripts/stop-kafka.sh $@
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
# 'test' is a command already. Strange stuff happens if you try to declare a function called 'test'
# So let's call this 'runTests'.
runTests() {
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
  "$VANILLA_KAFKA_ROOT_DIR"/scripts/produce.sh $@
}