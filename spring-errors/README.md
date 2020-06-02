# spring-errors

NOT YET FULLY IMPLEMENTED A basic Spring Kafka application that showcases the Spring framework features and behavior 
around Kafka error handling.

---

Spring Kafka (<https://spring.io/projects/spring-kafka>) has a significant amount of software machinery around error 
handling ([relevant section in docs](https://docs.spring.io/spring-kafka/reference/html/#annotation-error-handling)).
This project aims to de-mystify and illuminate it. Let's learn something!    

### Instructions

* Use Java 11
* Install Kafka and `kafkacat`:
  * `brew install kafka`
  * `brew install kafkacat`
* Running the application and the test cases depend on a locally running Kafka instance. Use the `startKafka` and 
  `stopKafka` commands (see [`commands.sh`](#commandssh)) to run Kafka and Zookeeper.
* In a new terminal, build and run the program with `build && run`
* In a new terminal, produce some test data with `produceValidMessage`. You should see the application react with new 
  logs.
* In a new terminal, produce some test data with `produceUnexpectedMessage`. You should see the application react with
  new logs.

### `commands.sh`

Source the `commands.sh` file using `source commands.sh` which will load your shell with useful 
commands. Commands include:

  * `startKafka` start Kafka and Zookeeper
  * `stopKafka` stop Kafka and Zookeeper
  * `build` build (without tests)
  * `run` run the app
  * `produceValidMessage` produce a test JSON message to the `my-messages` Kafka topic 
  * `produceInvalidMessage` produce a test JSON message to the `my-messages` Kafka topic that has a field of the wrong type 
    JSON that the app is expecting 
