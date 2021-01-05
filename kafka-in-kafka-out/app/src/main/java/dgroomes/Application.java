package dgroomes;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Listen to incoming messages on a Kafka topic, quote the messages, and send the resulting text to another Kafka topic.
 */
public class Application {

    private static final String INPUT_TOPIC = "input-text";
    private static final Duration pollDuration = Duration.ofMillis(200);
    private static final String OUTPUT_TOPIC = "quoted-text";

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final Consumer<Void, String> consumer;
    private final Producer<Void, String> producer;
    private final AtomicBoolean active = new AtomicBoolean(false);
    private Thread consumerThread;
    private final boolean synchronous;

    public Application(Consumer<Void, String> consumer, Producer<Void, String> producer) {
        this.consumer = consumer;
        this.producer = producer;
        String synchronous = System.getenv("SYNCHRONOUS");
        if (synchronous == null) {
            this.synchronous = true;
        } else {
            this.synchronous = Boolean.parseBoolean(synchronous);
        }
        log.info("synchronous: {}", this.synchronous);
    }

    /**
     * (Non-blocking) Start the application
     */
    public void start() {
        active.getAndSet(true);
        consumer.subscribe(List.of(INPUT_TOPIC));
        consumerThread = new Thread(this::pollContinuously);
        consumerThread.start();
    }

    public void pollContinuously() {
        try {
            while (active.get()) {
                ConsumerRecords<Void, String> records = consumer.poll(pollDuration);
                for (ConsumerRecord<Void, String> record : records) {
                    var message = record.value();
                    log.debug("Got message: {}", message);
                    var quoted = quote(message);
                    log.debug("Quoted to: {}", quoted);
                    send(quoted);
                    if (synchronous) {
                        consumer.commitSync();
                    }
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
     * Quote a string and escape already existing quotes
     */
    private String quote(String text) {
        var quotesEscaped = text.replace("\"", "\\\"");
        return String.format("%s%s%s", '"', quotesEscaped, '"');
    }

    /**
     * Quote the input string and send the resulting quoted string to the Kafka topic "quoted-text"
     */
    private void send(String msg) {
        ProducerRecord<Void, String> record = new ProducerRecord<>(OUTPUT_TOPIC, null, msg);
        Future<RecordMetadata> future = producer.send(record);
        if (synchronous) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Something went wrong while waiting for the message to be completely sent to Kafka. Shutting down the app...", e);
                System.exit(1);
            }
        }
    }

    public void stop() throws InterruptedException {
        synchronized (active) {
            log.info("Stopping");
            if (active.get()) {
                active.getAndSet(false);
                consumer.wakeup();
                consumerThread.join();
                producer.close();
            } else {
                log.info("Already stopped");
            }
        }
    }
}
