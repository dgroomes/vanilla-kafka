#!/usr/bin/env bash
# Create the Kafka topics.

# Create a Kafka topic.
#
# The topic name should be the first positional argument. The rest of the arguments will be applied as additional
# arguments to the "kafka-topics --create" command.
createTopic() {
  local FIFTY_MIB=52428800 # 50MiB in bytes
  local name="$1"
  shift
  kafka-topics --create \
    --partitions 1 \
    --bootstrap-server localhost:9092 \
    --replication-factor 1 \
    --config segment.bytes="$FIFTY_MIB" \
    --if-not-exists \
    --topic "$name" "${@}"
}

createTopic streams-zip-codes-zip-areas

# Explicitly create the internal topic that the Kafka Streams application uses. I prefer to not use auto topic creation.
createTopic streams-zip-codes-keyed-by-city-state-repartition \
  --config retention.ms=-1 \
  --config message.timestamp.type=CreateTime

createTopic streams-zip-codes-avg-pop-by-city \
  --config cleanup.policy=compact

# Admire our work! Describe the Kafka topics (shows number of partitions and configurations)
kafka-topics --bootstrap-server localhost:9092 --describe
