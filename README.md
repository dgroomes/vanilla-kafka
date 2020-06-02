# kafka-playground

Learning and experimenting with Apache Kafka <https://kafka.apache.org/>.

---

The project illustrates different concepts, patterns and examples via sub-projects:

### `interactive/`

An interactive program to consume from Kafka using the _plain-ole'/regular/vanilla_ Java [KafkaConsumer](https://github.com/apache/kafka/tree/40b0033eedf823d3bd3c6781cfd871a949c5827e/clients/src/main/java/org/apache/kafka/clients/consumer).

See [interactive/README.md](interactive/README.md). 

### `streams/`

A basic [Kafka Streams](https://kafka.apache.org/documentation/streams/) application.

See [streams/README.md](streams/README.md).

### `spring-seekable/`

A basic [Spring Kafka](https://spring.io/projects/spring-kafka) application with a "seekable" Kafka listener.

See [spring-seekable/README.md](spring-seekable/README.md).

### `spring-headers/`

A basic [Spring Kafka](https://spring.io/projects/spring-kafka) application that showcases the Spring framework behavior
around Kafka message headers.

See [spring-headers/README.md](spring-headers/README.md).

### `spring-errors/`

A basic [Spring Kafka](https://spring.io/projects/spring-kafka) application that showcases the Spring framework features
and behavior around Kafka error handling.

See [spring-errors/README.md](spring-errors/README.md).
