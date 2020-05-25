#!/usr/bin/env bash
# Take down the Docker containers using Docker Compose

docker-compose --project-directory "$KAFKA_STREAMS_ROOT_DIR" down