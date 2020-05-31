#!/usr/bin/env bash

# Consume from the Kafka topic
kafkacat -b localhost:9092 -t my-messages