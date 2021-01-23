package dgroomes.kafkaplayground.springinterfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;

public class Listener {

    private static final Logger log = LoggerFactory.getLogger(Listener.class);

    /**
     * Listen for messages from the "my-messages" Kafka topic.
     */
    @KafkaListener(topics = "my-messages")
    public void listen(String message, ConsumerRecordMetadata metaData) {
        log.info("Got Kafka message (metadata|message): {}|{}", metaData, message);
    }
}
