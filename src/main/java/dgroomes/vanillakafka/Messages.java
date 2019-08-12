package dgroomes.vanillakafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
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
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(config);
    }

    /**
     * Start consuming messages
     */
    public void start() {
        log.info("Starting...");
        active.getAndSet(true);
        consumer.subscribe(List.of("my-messages"));
        var thread = new Thread(this::pollContinuously);
        thread.start();
    }

    private void pollContinuously() {
        try {
            while (active.get()) {
                ConsumerRecords<Void, String> records = consumer.poll(pollDuration);
                for (ConsumerRecord<Void, String> record : records) {
                    String message = record.value();
                    log.debug("Putting message: {}", message);
                    try {
                        queue.put(message); // if this blocks for too long the consumer group will die right? Need a heart beat thread?
                    } catch (InterruptedException e) {
                        log.info("Thread was interrupted. Stopping...");
                        stop();
                    }
                    consumer.commitSync();
                }
            }
        } catch (WakeupException e) {
            // Ignore exception if inactive because this is expected from the "stop" method
            if (active.get()) {
                log.info("rethrowing");
                throw e;
            }
        }
    }

    /**
     * Take a message, blocking if none is available
     */
    public String take() throws InterruptedException {
        return queue.take();
    }

    public void stop() {
        log.info("Stopping ...");
        active.getAndSet(false);
        consumer.wakeup();
    }
}
