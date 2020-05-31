package dgroomes.kafkaplayground.springseekable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Abstraction over messages incoming from the Kafka topic "my-messages"
 */
@Component
public class Messages extends SeekableKafkaListener {

    private static Logger log = LoggerFactory.getLogger(Messages.class);

    private BlockingQueue<String> queue;

    public Messages() {
        this.queue = new ArrayBlockingQueue<>(10);
    }

    @KafkaListener(topics = "my-messages")
    public void listen(String message, Acknowledgment acknowledgment) {
        log.info("Got Kafka message: {}", message);
        try {
            queue.put(message);
            acknowledgment.acknowledge();
        } catch (InterruptedException e) {
            log.error("Failed to capture message: {} {}", message, e);
        }
    }

    /**
     * Take the latest message. Blocks until a message becomes available.
     */
    public String take() throws InterruptedException {
        return queue.take();
    }
}
