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
    --config message.timestamp.type=CreateTime \
    --if-not-exists \
    --topic "$name" "${@}"
}

# Create the input and output topics
createTopic streams-zip-codes-zip-areas

# Explicitly create the internal topic that the Kafka Streams application uses. I prefer to not use auto topic creation.
createTopic streams-zip-codes-zip-areas-to-tabler-repartition

createTopic streams-zip-codes-zip-areas-changelog \
  --config cleanup.policy=compact

createTopic streams-zip-codes-by-city-changelog \
  --config cleanup.policy=compact

createTopic streams-zip-codes-by-city-repartition

createTopic streams-zip-codes-city-stats-changelog \
  --config cleanup.policy=compact

createTopic streams-zip-codes-by-state-repartition

createTopic streams-zip-codes-state-stats-changelog \
  --config cleanup.policy=compact

# Admire our work! Describe the Kafka topics (shows number of partitions and configurations)
kafka-topics --bootstrap-server localhost:9092 --describe
