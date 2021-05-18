#!/usr/bin/env bash
# Create the Kafka topics.

kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 1 \
  --topic plaintext-input

kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 10 \
  --topic streams-plaintext-input

kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 1 \
  --topic streams-wordcount-output \
  --config cleanup.policy=compact

# Admire our work! Describe the Kafka topics (shows number of partitions etc.)
kafka-topics --bootstrap-server localhost:9092 --describe
