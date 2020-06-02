#!/usr/bin/env bash

export SPRING_ERRORS_ROOT_DIR=$(pwd)

# Start Kafka and Zookeeper
startKafka() {
  "$SPRING_ERRORS_ROOT_DIR"/scripts/start-kafka.sh $@
}

# Stop Kafka and Zookeeper
stopKafka() {
  "$SPRING_ERRORS_ROOT_DIR"/scripts/stop-kafka.sh $@
}

# Build (without the tests)
build() {
  "$SPRING_ERRORS_ROOT_DIR"/scripts/build.sh
}

# Run the app
run() {
  "$SPRING_ERRORS_ROOT_DIR"/scripts/run.sh
}

# Produce a test message to the `my-messages` Kafka topic
produceValidMessage() {
  "$SPRING_ERRORS_ROOT_DIR"/scripts/produce-valid-message.sh
}

# Produce a test message to the `my-messages` Kafka topic that is missing a field in the JSON that the app is expecting
produceUnexpectedMessage() {
  "$SPRING_ERRORS_ROOT_DIR"/scripts/produce-unexpected-message.sh
}
