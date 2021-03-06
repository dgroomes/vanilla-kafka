# utility-scripts

Utility Bash scripts for starting and stopping Kafka.

---

Starting a Kafka cluster locally for development purposes takes a few steps:

* (Optionally) Delete any pre-existing data files. I.e. "start fresh"
* Start Zookeeper (but NOT if using Kafka in 'KRaft mode' which this script does)
* Start Kafka  
* Wait for Kafka to be up

For the sake of a "fast, pleasant, and please-uplift-my-spirits" local development workflow it is worth automating
these steps into one step. The resulting script (`start-kafka.sh`) is ergonomic, adds some useful logging, and is
adorned with comments for the curious.

There is also a `stop-kafka.sh` script which does nothing more than execute a single command. This is not very useful,
but at least it is symmetrical to the `start-kafka.sh` script.

These scripts should be copied and pasted somewhere on your `PATH` or straight into a project where you need to run a
Kafka cluster locally (and tweak the scripts to your needs!).

### Copy To Other Projects

Copy the scripts to the other sub-projects with `./copy-scripts.sh`.

### Referenced Materials

* [Apache Blog: *What’s New in Apache Kafka 2.8.0*](https://blogs.apache.org/kafka/entry/what-s-new-in-apache5)
  * This release includes KIP-500 which brings experimental support for running Kafka without Zookeeper. I want to use
    this for local development so I can drop the Zookeeper configuration and the related scripting to handle starting and
    stopping it. This mode is called "KRaft mode". Read more about it in the [KRaft README](https://github.com/apache/kafka/blob/2.8/config/kraft/README.md).

### Wish list

General clean ups, TODOs and things I wish to implement for this project:

* DONE Add a wait loop in the stop script. I'm pretty sure Kafka takes a while to stop sometimes and if you kick off the stop
  command without waiting for Kafka to come to a complete stop, then it's undefined what happens next if you execute other
  commands in the mean time.
