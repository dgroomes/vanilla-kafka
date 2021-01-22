# spring-interfaces

NOT YET IMPLEMENTED

A basic [Spring Kafka](https://spring.io/projects/spring-kafka) application that demonstrates how to use Spring Kafka
by implementing Spring Kafka interfaces instead of using annotations like `KafkaListener`.

---

### Instructions

* Use Java 11
* Install Kafka and `kafkacat`:
  * `brew install kafka`
  * `brew install kafkacat`
* Running the application and the test cases depend on a locally running Kafka instance. Start kafka with:
  * `./scripts/start-kafka.sh`
* In a new terminal, build and run the program with:
  * `./gradlew run`
* In a new terminal, produce some test Kafka messages with:
  * `./scripts/produce.sh`
* Look at the app logs! The app will be processing the messages.
* Stop Kafka with:
  * `./scripts/stop-kafka.sh`
