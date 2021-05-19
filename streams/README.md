# streams

A basic [Kafka Streams](https://kafka.apache.org/documentation/streams/) application.

## Description

This demo app illuminates the threading model of Kafka Streams by sleeping for each input message. For example, if ten messages are
input, and the Kafka Streams topology is bound by one thread, then it will take ten seconds to process the messages. By
contrast, if the input Kafka topic has five partitions and the Kafka Streams app is configured with five threads, then
it should take as little as two seconds to process the messages! Experiment with different configurations of the input
topic, Kafka Streams topology operations, and Kafka Streams configurations.   

## Instructions

1. Use Java 11
1. Start Kafka
   *  Read [`commands.sh`](#commandssh) and then execute the following command:
   * `startKafka`
1. Create the topics
   * Open a new terminal and create the input and output Kafka topics with the following command to start the Kafka broker:
   * `createTopics`
1. Build and run the program:
   * `build && run`
1. Produce and consume messages
   * In a new terminal, start a consumer process which will eventually receive messages on the output Kafka topic. Use
     the following command:
   * `consume`
   * In a new terminal, produce some test data with the following command:
   * `produce`
   * You should see some data in your consumer!
1. Produce even more messages:
   * `produce 10`
1. Continue to experiment!
1. Stop all components
   * When you are done, stop the Kafka consumer in the other terminal.
   * Stop the application in the other terminal.
   * Finally, stop the Kafka broker with the following command:
   * `stopKafka`

### `commands.sh`

Source the `commands.sh` file using `source commands.sh` which will load your shell with useful 
commands. Commands include:

  * `build` build (without running the tests)
  * `run` run the app
  * `consume` consume from the input Kafka topic
  * `produce` produce a test message to the input Kafka topic 
  * `currentOffsets` get current Kafka topic offsets for the `my-group` group 
  * `startKafka` start Kafka
  * `stopKafka` stop Kafka
  * `createTopics` create the input and output Kafka topics 
  * `cleanState` clean the Kafka Streams state directory (RocksDB data) for when things get messed up  
  
Dependencies required across all commands include:

  * `brew install kafka`
  * `brew install kafkacat`
  
### Wish List

Items I wish to implement for this project:

  * Should prefer using interrupts to stop KafkaConsumer? Even though KafkaConsumer recommends
    using a flag? See <https://kafka.apache.org/0110/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html>
  * implement some tests
  * Why, when starting the app, does it log a few hundred warning logs like this:
    ```
    00:23:45 [streams-wordcount-ec294eef-3f5a-401b-8b69-45084bc07506-StreamThread-10] WARN org.apache.kafka.clients.NetworkClient - [Consumer clientId=streams-wordcount-ec294eef-3f5a-401b-8b69-45084bc07506-StreamThread-10-consumer, groupId=streams-wordcount] Error while fetching metadata with correlation id 106 : {streams-wordcount-KSTREAM-AGGREGATE-STATE-STORE-0000000006-repartition=UNKNOWN_TOPIC_OR_PARTITION}
    ```
    Was it always like this? Is this normal? Is the out-of-the-box Kafka Streams operational experience always full of
    verbose warning logs? Is this a KRaft issue? 

## Notes

A neat trick to check for processes that are using a port is to use the `lsof` command. For example, use

```echo "kafka port?" && lsof -i :9092```

to check if Kafka is running. 

## Reference

* [Apache Kafka: Quick Start *Demo Application*](https://kafka.apache.org/25/documentation/streams/quickstart)
  * This project is adapted, in part, by the quick start
