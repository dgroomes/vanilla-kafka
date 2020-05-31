# vanilla-kafka

Let's code to a vanilla KafkaConsumer (no frameworks) and learn something in the process!

### Instructions

* Use Java 11
* Install Kafka and `kafkacat`:
  * `brew install kafka`
  * `brew install kafkacat`
* Running the application and the test cases depend on a locally running Kafka instance. Use the `startKafka` and 
  `stopKafka` commands (see [`commands.sh`](#commandssh)) to run Kafka and Zookeeper.
* In a new terminal, build and run the program with `build && run`
* In a new terminal, produce some test data with `produce`. You should see the application react with new logs. Next,
  try `produce 10`.
* In the application terminal, start experimenting by typing in any of the commands: "start", "stop", "reset", "rewind",
  "current-offsets". Continue to experiment!

### `commands.sh`

Source the `commands.sh` file using `source commands.sh` which will load your shell with useful 
commands. Commands include:

  * `startKafka` start Kafka and Zookeeper
  * `stopKafka` stop Kafka and Zookeeper
  * `build` build (without running the tests)
  * `runTests` run the tests
  * `run` run the app
  * `consume` consume from the `my-messages` Kafka topic
  * `produce` produce a test message to the `my-messages` Kafka topic 
  * `currentOffsets` get current Kafka topic offsets for the `my-group` group 
  
### todo

  * Should prefer using interrupts to stop KafkaConsumer? Even though KafkaConsumer recommends
    using a flag? See <https://kafka.apache.org/0110/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html>
    
### Notes

A neat trick to check for processes that are using a port is to use the `lsof` command. For example, use

```echo "zookeeper port?" && lsof -i :2181; echo "kafka port?" && lsof -i :9092```

to check if Zookeeper and/or Kafka are running. 
