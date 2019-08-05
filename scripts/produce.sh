#!/usr/bin/env bash
# Produce a test message to the Kafka topic

SECONDS=$(date +%s)
echo "hello $SECONDS" | kafkacat -b localhost:9092 -t my-messages