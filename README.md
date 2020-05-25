# kafka-streams-playground

NOT YET IMPLEMENTED Learning and exploring Kafka Streams <https://kafka.apache.org/documentation/streams/>.

---

This project is adapted from <https://github.com/dgroomes/vanilla-kafka>.

### Development

Running the application and the test cases depend on a locally running Kafka instance. Use Docker 
Compose (see the `docker-compose.yml` file in the root) to run Kafka and Zookeeper. See [`commands.sh`](#commandssh) 

### `commands.sh`

Source the `commands.sh` file using `source commands.sh` which will load your shell with useful 
commands. Commands include:

  * `up` bring up the Docker containers using Docker Compose
  * `down` take down the Docker containers using Docker Compose
  * `build` build (without running the tests)
  * `run` run the app
  * `consume` consume from the `my-messages` Kafka topic
  * `produce` produce a test message to the `my-messages` Kafka topic 
  * `currentOffsets` get current Kafka topic offsets for the `my-group` group 
  
Dependencies required across all commands include:

  * `brew install kafka`
  * `brew install kafkacat`
  
### Wish List

Items I wish to implement for this project:

  * Should prefer using interrupts to stop KafkaConsumer? Even though KafkaConsumer recommends
    using a flag? See <https://kafka.apache.org/0110/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html>
  * implement some tests
  * Make the Gradle files more idiomatic. There is so much to learn about Gradle and the Gradle Kotlin DSL! Without 
    knowing it, I can't make the Gradle files idiomatic. Fortunately, there is a lot of knowledge and advice in the 
    official docs <https://docs.gradle.org/current/userguide/kotlin_dsl.html> 