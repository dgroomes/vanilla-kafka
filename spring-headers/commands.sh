#!/usr/bin/env bash

export SPRING_HEADERS_ROOT_DIR=$(pwd)

# Start Kafka and Zookeeper
startKafka() {
  "$SPRING_HEADERS_ROOT_DIR"/scripts/start-kafka.sh $@
}

# Stop Kafka and Zookeeper
stopKafka() {
  "$SPRING_HEADERS_ROOT_DIR"/scripts/stop-kafka.sh $@
}

# Build (without the tests)
build() {
  "$SPRING_HEADERS_ROOT_DIR"/scripts/build.sh
}

# Run the app
run() {
  "$SPRING_HEADERS_ROOT_DIR"/scripts/run.sh
}

# Produce a test message to the Kafka topic with a "type" header equal to "dgroomes.kafkaplayground.springheaders.model.A"
produceA() {
  "$SPRING_HEADERS_ROOT_DIR"/scripts/produce.sh dgroomes.kafkaplayground.springheaders.model.A
}
