# utility-scripts

Utility Bash scripts for starting and stopping Kafka and Zookeeper.

---

Starting a Kafka cluster locally for development purposes takes a few steps:

* (Optionally) Delete any pre-existing data files. I.e. "start fresh"
* Start Zookeeper
* Start Kafka  
* Wait for Kafka to be up

For the sake of a "fast, pleasant, and please-uplift-my-spirits" local development workflow it is worth automating
these steps into one step. The resulting script (`start-kafka.sh`) is ergonomic, adds some useful logging, and is
adorned with comments for the curious.

There is also a `stop-kafka.sh` script which is only two commands. Its value is not to save keystrokes but rather its
explanation of *why* these two commands work and an important "gotcha".

These scripts should be copied and pasted somewhere on your `PATH` or straight into a project where you need to run a
Kafka cluster locally (and tweak the scripts to your needs!).

### Copy to other projects

Copy the scripts to the other sub-projects with `./copy-scripts.sh`.
