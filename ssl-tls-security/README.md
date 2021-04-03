# ssl-tls-security

NOT YET IMPLEMENTED

An example Kafka project showcasing its support for SSL/TLS security.

## Notes

Dealing with certificates can be a bit of a mystery to most engineers. I'd like to de-mystify if with a practical example:
a Kafka producer and consumer using SSL!

## Instructions

* Install Kafka and `kafkacat`:
    * `brew install kafka`
    * `brew install kafkacat`
* Start Zookeeper and Kafka (TODO enable SSL):
    * `./scripts/start-kafka.sh`
* Start a Kafka consumer:
    * TODO
* Produce a message:
    * TODO
    * Verify that you see it in the consumer!
    * Success!
* Stop Zookeeper and Kafka:
    * `./scripts/stop-kafka.sh`

## Reference Materials

* [Wikipedia: *Public Key Infrastructure*](https://en.wikipedia.org/wiki/Public_key_infrastructure)
* [Wikipedia: *X.509*](https://en.wikipedia.org/wiki/X.509)
* [Wikipedia: *PKCS*](https://en.wikipedia.org/wiki/PKCS)
* [Wikipedia: *PKCS 8*](https://en.wikipedia.org/wiki/PKCS_8)
* [Wikipedia: *Privacy-Enhanced Mail*](https://en.wikipedia.org/wiki/Privacy-Enhanced_Mail)
* [Blog post on Kafka SSL](https://medium.com/analytics-vidhya/kafka-ssl-encryption-authentication-part-two-practical-example-for-implementing-ssl-in-kafka-d514f30fe782)