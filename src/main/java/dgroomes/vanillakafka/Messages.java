package dgroomes.vanillakafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstraction over messages sourced from the Kafka topic "my-messages
 */
public class Messages {

    private static Logger log = LoggerFactory.getLogger(Messages.class);

    private BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

    private KafkaConsumer<Void, String> consumer;

    private Duration pollDuration = Duration.of(2, ChronoUnit.SECONDS);

    private AtomicBoolean active = new AtomicBoolean(false);

    public Messages() {
        Properties config = new Properties();
        config.put("group.id", "my-group");
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(config);
    }

    /**
     * Start consuming messages
     *
     * @throws InterruptedException if backing BlockingQueue is interrupted while waiting
     */
    public void start() throws InterruptedException {
        log.info("Starting...");
        active.getAndSet(true);
        consumer.subscribe(List.of("my-messages"));
        while (active.get()) {
            ConsumerRecords<Void, String> records = consumer.poll(pollDuration);
            for (ConsumerRecord<Void, String> record : records) {
                String message = record.value();
                log.debug("Putting message: {}", message);
                queue.put(message); // if this blocks for too long the consumer group will die right? Need a heart beat thread?
                consumer.commitSync();
            }
        }
    }

    /**
     * Take a message, blocking if none is available
     */
    public String take() throws InterruptedException {
        return queue.take();
    }
}
