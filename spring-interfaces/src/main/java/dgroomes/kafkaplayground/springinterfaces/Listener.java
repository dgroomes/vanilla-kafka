package dgroomes.kafkaplayground.springinterfaces;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.MessageListener;

/**
 * Process messages from the "my-messages" Kafka topic.
 */
public class Listener implements MessageListener<Void, String> {

    private static final Logger log = LoggerFactory.getLogger(Listener.class);

    @Override
    public void onMessage(ConsumerRecord<Void, String> record) {
        log.info("Got message: {}", record.value());
    }
}
