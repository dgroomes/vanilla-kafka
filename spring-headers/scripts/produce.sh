#!/usr/bin/env bash
# Produce a test message to the Kafka topic with a "type" header
# Example to produce ten messages: ./produce.sh

if [[ -z "$1" ]]; then
  echo >&2 "missing argument. Must provide a 'type' for the header. For example: $0 dgroomes.kafkaplayground.springheaders.model.A"
  exit 1
fi

TYPE=$1

set -eu

echo "hello($SECONDS) type=$TYPE" | kafkacat -P -b localhost:9092 -t my-messages -H "__Type__=$TYPE"
