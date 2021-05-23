# spring-multi-broker

NOT YET IMPLEMENTED

A Spring Kafka application that connects to multiple Kafka brokers.

---

In moderately complex applications there might be a need to consume from multiple Kafka clusters. While Spring Boot offers
a low-code way to consume from a single Kafka broker it does not have an obvious way to consume from multiple Kafka brokers.
The goal of this project is to find and illustrate the idiomatic way to configure and code a Spring Boot app to connect
to multiple Kafka brokers. In other words:

> What is the "Spring way" to accommodate multi-cluster Kafka infrastructure?

### Instructions

* Use Java 11
* Install Kafka and `kafkacat`:
  * `brew install kafka`
  * `brew install kafkacat`
* Running the application and the test cases depend on a locally running Kafka instance. Use the `startKafka` and 
  `stopKafka` commands (see [`commands.sh`](#commandssh)) to run Kafka and Zookeeper.
* In a new terminal, build and run the program with `build && run`
* In a new terminal, produce some test data with `produceMessageA`. You should see the application react with new logs.

### `commands.sh`

Source the `commands.sh` file using `source commands.sh` which will load your shell with useful 
commands. Commands include:

  * `startKafka` start Kafka and Zookeeper
  * `stopKafka` stop Kafka and Zookeeper
  * `build` build (without tests)
  * `run` run the app
  * `produceMessageA` produce a test message to the `my-messages` Kafka topic for the "MessageA" type 
