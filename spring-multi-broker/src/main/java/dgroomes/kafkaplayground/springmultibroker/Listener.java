package dgroomes.kafkaplayground.springmultibroker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Listener {

    private static final Logger log = LoggerFactory.getLogger(Listener.class);

    @KafkaListener(topics = "my-messages")
    public void listen(String message) {
        log.info("Got Kafka message on the 'A' broker: {}", message);
    }
}
