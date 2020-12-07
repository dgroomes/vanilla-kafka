# kafka-in-kafka-out

NOT YET FULLY IMPLEMENTED

A simple *Kafka in, Kafka out* Java program accompanied by an out-of-process test harness.

Let's make a simple program that reads data from a Kafka topic and outputs data to another Kafka topic in a way that models
a so-called [*pure function*](https://en.wikipedia.org/wiki/Pure_function). A pure function takes data in and puts data
out. This style of program is a perfect match for Kafka. 


## Architecture

This is a multi-module Gradle project with the following sub-projects:

* `app/`
  * This is the *Kafka in, Kafka out* Java program
  * See [app/README.md](app/README.md).
* `test-harness/`
  * This is a [test harness](https://en.wikipedia.org/wiki/Test_harness) for running and executing automated tests against `app`.
  * See [test-harness/README.md](test-harness/README.md).

### Instructions

* Use Java 11
* Install Kafka and `kafkacat`:
  * `brew install kafka`
  * `brew install kafkacat`
* Running the application and the `test-harness` depend on a locally running Kafka instance. Start Zookeeper and Kafka
  with:
  * `scripts/start-kafka.sh`
* In a new terminal, build and run the `app` program with:
  * `./gradlew app:run`
* In a new terminal, build and run the tests with:
  * `./gradlew test-harness:test`
* Stop Kafka with:
  * `scripts/stop-kaka.sh`

### todo

  * Should prefer using interrupts to stop KafkaConsumer? Even though KafkaConsumer recommends
    using a flag? See <https://kafka.apache.org/0110/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html>
    
### Notes

A neat trick to check for processes that are using a port is to use the `lsof` command. For example, use

```echo "zookeeper port?" && lsof -i :2181; echo "kafka port?" && lsof -i :9092```

to check if Zookeeper and/or Kafka are running. 
