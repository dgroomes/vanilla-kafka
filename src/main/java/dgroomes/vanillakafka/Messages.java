package dgroomes.vanillakafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
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
import java.util.stream.Collectors;

/**
 * Abstraction over messages sourced from the Kafka topic "my-messages
 */
public class Messages {

    public static final String MY_MESSAGES_TOPIC = "my-messages";
    private static Logger log = LoggerFactory.getLogger(Messages.class);

    private BlockingQueue<String> queue = new ArrayBlockingQueue<>(10);

    private KafkaConsumer<Void, String> consumer;

    private Duration pollDuration = Duration.of(2, ChronoUnit.SECONDS);

    private AtomicBoolean active = new AtomicBoolean(false);

    private Thread consumerThread;

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
        log.info("Starting");
        active.getAndSet(true);
        consumer.subscribe(List.of(MY_MESSAGES_TOPIC));
        consumerThread = new Thread(this::pollContinuously);
        consumerThread.start();
    }

    private void pollContinuously() {
        try {
            pollLoop:
            while (active.get()) {
                ConsumerRecords<Void, String> records = consumer.poll(pollDuration);
                for (ConsumerRecord<Void, String> record : records) {
                    String message = record.value();
                    log.debug("Putting message: {}", message);
                    try {
                        queue.put(message); // if this blocks for too long the consumer group will die right? Need a heart beat thread?
                    } catch (InterruptedException e) {
                        log.info("Thread was interrupted. Stopping");
                        break pollLoop;
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

    public void stop() throws InterruptedException {
        log.info("Stopping");
        active.getAndSet(false);
        consumer.wakeup();
        consumerThread.join();
    }

    /**
     * (Synchronous) Reset the Kafka offsets to the beginning
     */
    public void reset() throws InterruptedException {
        stop();
        log.info("Resetting offsets");
        var partitionInfos = consumer.partitionsFor(MY_MESSAGES_TOPIC);
        var topicPartitions = partitionInfos.stream()
                .map(it -> new TopicPartition(it.topic(), it.partition()))
                .collect(Collectors.toList());
        consumer.seekToBeginning(topicPartitions);
        start();
    }

    /**
     * (Synchronous) Rewind the Kafka offsets of each TopicPartition by N spots
     */
    public void rewind(int n) throws InterruptedException {
        stop();
        log.info("Rewinding offsets by {} spots for each topic partition", n);
        var partitionInfos = consumer.partitionsFor(MY_MESSAGES_TOPIC);
        for (PartitionInfo partitionInfo : partitionInfos) {
            var topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
            var nextPosition = consumer.position(topicPartition);
            var currentPosition = nextPosition - 1; // KafkaConsumer#position returns the next position, so we need to subtract 1 to get the current position
            consumer.seek(topicPartition, currentPosition - n);
        }
        start();
    }
}
