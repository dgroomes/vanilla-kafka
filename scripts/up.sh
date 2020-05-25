#!/usr/bin/env bash
# Bring up the Docker containers using Docker Compose

# Use `--renew-anon-volumes` option to really start fresh instead of reusing any volumes that may
# have been created earlier. This is especially important for Kafka which seems to get into a bad
# state if reusing old volumes. I don't know why.
docker-compose --project-directory "$KAFKA_STREAMS_ROOT_DIR" up --detach --renew-anon-volumes