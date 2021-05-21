#!/usr/bin/env bash
# Create the Kafka topics.

# Create a Kafka topic.
#
# The topic name should be the first positional argument. The rest of the arguments will be applied as additional
# arguments to the "kafka-topics --create" command.
createTopic() {
  local name="$1"
  shift
  kafka-topics --create \
    --bootstrap-server localhost:9092 \
    --replication-factor 1 \
    --topic "$name" "${@}"
}

createTopic plaintext-input --partitions 1

createTopic streams-plaintext-input --partitions 10

createTopic streams-wordcount-KSTREAM-AGGREGATE-STATE-STORE-0000000006-repartition \
  --partitions 10 \
  --config cleanup.policy=delete \
  --config segment.bytes=52428800 \
  --config retention.ms=-1 \
  --config message.timestamp.type=CreateTime

createTopic streams-wordcount-output \
  --partitions 1 \
  --config cleanup.policy=compact

# Admire our work! Describe the Kafka topics (shows number of partitions and configurations)
kafka-topics --bootstrap-server localhost:9092 --describe
