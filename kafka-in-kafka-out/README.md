# kafka-in-kafka-out

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
  * `scripts/stop-kafka.sh`

### Wish List

Items I wish to implement for this project:

* The test is flaky. The first time it runs, it fails (at least in my own test runs) but subsequent runs it succeeds. I
  want to dive deeper into what the consumer is doing. When is it ready? 
