package dgroomes;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * The application runner
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static final String KAFKA_BROKER_HOST = "localhost:9092";

    public static void main(String[] args) throws InterruptedException, IOException {
        log.info("Starting the app. This is a simple stateless transformation app: Kafka in, Kafka out.");
        var app = new Application(kafkaConsumer(), kafkaProducer(), isSync());
        app.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                log.info("Stopping the app");
                app.stop();
            } catch (InterruptedException e) {
                throw new RuntimeException("Shutdown thread was interrupted. Failed to stop the app.", e);
            }
        }));
    }

    /**
     * Should the Kafka consumption and producing be synchronous?
     *
     * @return true if synchronous; false if asynchronous
     */
    private static boolean isSync() {
        String syncStr = System.getenv("SYNCHRONOUS");
        boolean sync;
        if (syncStr == null) {
            // Default to 'synchronous' if the configuration was not specified explicitly.
            sync = true;
        } else {
            sync = Boolean.parseBoolean(syncStr);
        }
        log.info("Synchronous: {}", sync);
        return sync;
    }

    /**
     * Construct a KafkaConsumer
     */
    public static KafkaConsumer<Void,String> kafkaConsumer() {
        Properties config = new Properties();
        config.put("group.id", "my-group");
        config.put("bootstrap.servers", KAFKA_BROKER_HOST);
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return new KafkaConsumer<>(config);
    }

    /**
     * Construct a KafkaProducer
     */
    public static KafkaProducer<Void,String> kafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_BROKER_HOST);
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return new KafkaProducer<>(props);
    }
}
