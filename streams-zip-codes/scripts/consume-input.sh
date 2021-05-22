#!/usr/bin/env bash
# Consume from the input Kafka topic. This is useful for sanity checking what the heck is going on.

set -eu

kafka-console-consumer --bootstrap-server localhost:9092 \
    --topic streams-zip-codes-zip-areas \
    --from-beginning
