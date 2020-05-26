# kafka-streams-playground

NOT YET IMPLEMENTED Learning and exploring Kafka Streams <https://kafka.apache.org/documentation/streams/>.

---

This project is adapted from <https://github.com/dgroomes/vanilla-kafka> and <https://kafka.apache.org/25/documentation/streams/quickstart>.

### Development

Running the application and the test cases depend on a locally running Kafka instance. Use the `startKafka` and 
`stopKafka` commands to run Kafka and Zookeeper. See [`commands.sh`](#commandssh) 

### `commands.sh`

Source the `commands.sh` file using `source commands.sh` which will load your shell with useful 
commands. Commands include:

  * `build` build (without running the tests)
  * `run` run the app
  * `consume` consume from the `my-messages` Kafka topic
  * `produce` produce a test message to the `my-messages` Kafka topic 
  * `currentOffsets` get current Kafka topic offsets for the `my-group` group 
  * `startKafka` start Zookeeper and Kafka
  * `stopKafka` start Kafka and Zookeeper
  
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
    
### Notes

A neat trick to check for processes that are using a port is to use the `lsof` command. For example, use
`echo "zookeeper port?" && lsof -i :2181; echo "kafka port?" && lsof -i :9092` to check if Zookeeper and/or Kafka are running. 