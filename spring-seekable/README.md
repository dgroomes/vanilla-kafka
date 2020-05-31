# spring-seekable

WARNING: DOES NOT ACTUALLY SEEK CORRECTLY!

A basic Spring Kafka application with a "seekable" Kafka listener.

---

In essence, the Kafka listener base class has operations to seek to the beginning or end of the 
Kafka topic. This is a useful feature when executing tests or when needing to replay all Kafka 
messages from the beginning of a Kafka topic.

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
* In the application terminal, press "enter" to seek the consumer to the beginning of the topic. It should replay all 
  the messages on the topic. WARNING: Unfortunately there is a bug. If you press enter a second time, then it won't 
  replay the messages. Why?

### `commands.sh`

Source the `commands.sh` file using `source commands.sh` which will load your shell with useful 
commands. Commands include:

  * `startKafka` start Kafka and Zookeeper
  * `stopKafka` stop Kafka and Zookeeper
  * `build` build (without tests)
  * `run` run the app
  * `runTests` execute the tests
  * `consume` consume from the `my-messages` Kafka topic
  * `produce` produce a test message to the `my-messages` Kafka topic 
  * `current-offsets` get current Kafka topic offsets for the `my-group` group 
  
### TODO

  * Implement the actual "seek to beginning/end" operations (with tests)
  * Why does seeking to the beginning stop the Kafka consumer? When it seeks to beginning, it reads
    up to the latest offset but then will stop polling and not consume new messages...
