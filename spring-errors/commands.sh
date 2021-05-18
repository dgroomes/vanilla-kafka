#!/usr/bin/env bash

export SPRING_ERRORS_ROOT_DIR=$(pwd)

# Start Kafka
startKafka() {
  "$SPRING_ERRORS_ROOT_DIR"/scripts/start-kafka.sh $@
}

# Stop Kafka
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

# Produce a test JSON message to the `my-messages` Kafka topic
produceValidMessage() {
  "$SPRING_ERRORS_ROOT_DIR"/scripts/produce-valid-message.sh
}

# Produce a test JSON message to the `my-messages` Kafka topic that has a field of the wrong type
produceInvalidMessage() {
  "$SPRING_ERRORS_ROOT_DIR"/scripts/produce-invalid-message.sh
}

# Consume from the Kafka topic
consumeDeadLetterTopic() {
  "$SPRING_ERRORS_ROOT_DIR"/scripts/consume-dead-letter-topic.sh
}
