# vanilla-kafka

Experimenting with the vanilla, plain-old, Java Kafka consumer.

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

  * Should prefer using interrupts to stop KafkaConsumer? Even though KafkaConsumer recommends
    using a flag? See <https://kafka.apache.org/0110/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html>
  * How do I set log levels? (I'm not in Spring Boot anymore I can' use `application.yml`...)
  * Can we pause and resume the KafkaConsumer?
  * Can we seek the KafkaConsumer?