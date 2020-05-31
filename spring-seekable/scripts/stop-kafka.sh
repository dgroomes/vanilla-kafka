#!/usr/bin/env bash
# Stop Kafka and Zookeeper
#
# Annoyingly, the Kafka instance must be stopped while the Zookeeper instances is still up, or else it just fails to
# shutdown! So we must always shut down Kafka first and then Zookeeper. So, if you get into a bad state where Zookeeper
# goes down you might be in for a rude and confusing exercise when you try to shut down Kafka. See https://stackoverflow.com/a/48628948
#
# Assumes that Kafka and Zookeeper installed. (I installed Kafka with `brew install kafka`)

kafka-server-stop
zookeeper-server-stop
