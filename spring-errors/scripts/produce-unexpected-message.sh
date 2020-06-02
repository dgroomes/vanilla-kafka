#!/usr/bin/env bash
# Produce a test message to the `my-messages` Kafka topic that is missing a field in the JSON that the app is expecting

set -eu

echo "{\"message\": \"hello\"}" | kafkacat -P -b localhost:9092 -t my-messages
