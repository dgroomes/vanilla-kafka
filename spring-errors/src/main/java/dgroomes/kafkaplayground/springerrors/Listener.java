package dgroomes.kafkaplayground.springerrors;

import dgroomes.kafkaplayground.springerrors.model.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.stereotype.Component;

@Component
public class Listener implements ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(Listener.class);

    /**
     * Listen for messages from the "my-messages" Kafka topic.
     */
    @KafkaListener(topics = "my-messages")
    public void listen(Message message) {
        log.info("Got Kafka message: {}", message);
    }

    @Override
    public void handle(Exception thrownException, ConsumerRecord<?, ?> data) {
//        Object val = data.value(); why is the value null? Shouldn't it be bytes?
        log.error("Error when processing message for partition {} offset {}", data.partition(), data.offset());
    }
}
