#!/usr/bin/env bash
# Start Kafka and Zookeeper
#
# Assumes that Kafka and Zookeeper are installed. I installed Kafka with `brew install kafka`. PRO TIP: change the log
# levels by editing `/usr/local/etc/kafka/log4j.properties` to reduce noise as needed.
#
# WARNING: this is a pretty naive way to start Zookeeper and Kafka because it deletes the data directories before
# starting. I've found that if I don't delete the data directories first, then I often get a "node already exists"
# startup error in Kafka (because of stale connections that were peristed in Zookeeper's log files???). Ideally, I
# would know how to operation Zookeeper and Kafka correctly, but the "delete everything every time" approach works.

ATTEMPTS=3
KAFKA_BROKER=localhost:9092

startKafkaFresh() {
  rm /usr/local/var/lib/zookeeper/version-2/*
  rm -rf /usr/local/var/lib/kafka-logs/*

  echo "Starting Kafka and Zookeeper..."

  zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties &
  kafka-server-start /usr/local/etc/kafka/server.properties &
}

# Use kafkacat to check if Kafka is up and running. There is a timeout built in to the metadata query ('-L' command)
# of 5 seconds https://github.com/edenhill/kafkacat/issues/144
checkKafka() {
  kafkacat -L -b $KAFKA_BROKER
}

waitForExit() {
  echo "Waiting until Kafka and Zookeeper exit. Shut them down with 'stopKafka'"
  wait < <(jobs -p)
  exit 0
}

waitForUp() {
  for i in $(seq 1 $ATTEMPTS) ; do
      if ((i > 1)); then
        echo "Checking if Kafka is up and running..."
      fi
      checkKafka &> /dev/null && echo "Kafka is up and running!" && waitForExit
  done

  # Change text output color to red https://stackoverflow.com/a/20983251/1333713
  tput bold
  tput setaf 1
  echo >&2 "Gave up waiting for Zookeeper to be up and running!"
  tput sgr0
}

startKafkaFresh
waitForUp
waitForExit
