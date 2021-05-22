# streams-zip-codes

NOT YET IMPLEMENTED

An intermediate Kafka Streams project that aggregates ZIP code data.

## Description

This is a Kafka Streams re-implementation of the MongoDB example project [*Aggregation with the Zip Code Data Set*](https://docs.mongodb.com/manual/tutorial/aggregation-zip-code-data-set/)
on the MongoDB website. Also, this project is designed after another project of my own in the GitHub repo [dgroomes/mongodb-playground](https://github.com/dgroomes/mongodb-playground/tree/main/incremental)
and specifically the `incremental/` sub-project there.

## Why?

I want to compare and contrast technologies like Kafka Streams with MongoDB when it comes to domain problems that could be
solved by either technology. ZIP code population data is an excellent example domain. It is a familiar concept, the data
is simple and the data is easily available.

## Instructions

1. Use Java 16
1. Install Kafka and `kafkacat`:
    * `brew install kafka`
    * `brew install kafkacat` 
1. Start Kafka
    *  Read [`commands.sh`](#commandssh) and then execute the following command to start the Kafka broker:
    * `startKafka`
1. Create the topics
    * Open a new terminal and create the input, intermediate, and output Kafka topics with the following command:
    * `createTopics`
1. Build and run the program:
    * `build && run`
1. Stop all components
    * When you are done, stop the Kafka consumer in the other terminal.
    * Stop the application in the other terminal.
    * Finally, stop the Kafka broker with the following command:
    * `stopKafka`

## `commands.sh`

Source the `commands.sh` file using `source commands.sh` which will load your shell with useful
commands. Commands include: `build`, `startKafka` `run`, `consume` etc. See the contents of the file for more.

## TODO

* DONE Scaffold a compilable and runnable project
* DONE Get rid of the repartitioning. I only want a baseline benchmark of a single partition. Keep things simple.
* Reset the state store using in Java using the Kafka Streams client. This is more convenient than the Bash script
* Get the Zip code data in
* Replace the copy/pasted word count stuff with zip code stuff

