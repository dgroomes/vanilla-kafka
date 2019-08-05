# seekable-kafka

A basic Spring Kafka application with a "seekable" Kafka listener.

In essence, the Kafka listener base class has operations to seek to the beginning or end of the 
Kafka topic. This is a useful feature when executing tests or when needing to replay all Kafka 
messages from the beginning of a Kafka topic.

### Development

Running the application and the test cases depend on a locally running Kafka instance. Use Docker 
Compose (see the `docker-compose.yml` file in the root) to run Kafka and Zookeeper. See [`commands.sh`](#commandssh) 

### `commands.sh`

Source the `commands.sh` file using `source commands.sh` which will load your shell with useful 
commands. Commands include:

  * `up` bring up the Docker containers using Docker Compose
  * `down` take down the Docker containers using Docker Compose
  * `build` build (without tests)
  * `run` run the app
  * `test` execute the tests
  * `consume` consume from the `my-messages` Kafka topic
  * `produce` produce a test message to the `my-messages` Kafka topic 
  * `current-offsets` get current Kafka topic offsets for the `my-group` group 
  
Dependencies required across all commands include:

  * `brew install kafka`
  * `brew install kafkacat`
  
### todo

  * Implement the actual "seek to beginning/end" operations (with tests)
  * Why does seeking to the beginning stop the Kafka consumer? When it seeks to beginning, it reads
    up to the latest offset but then will stop polling and not consume new messages...