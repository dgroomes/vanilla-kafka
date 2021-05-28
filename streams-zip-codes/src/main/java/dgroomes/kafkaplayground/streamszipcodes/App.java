package dgroomes.kafkaplayground.streamszipcodes;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Properties;

/**
 * A Kafka Streams app
 */
public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    private final KafkaStreams kafkaStreams;

    public App() {
        final Properties props = config();
        Topology topology = new TopologyBuilder().build();
        log.info("Created a Kafka Streams topology:\n\n{}", topology.describe());

        kafkaStreams = new KafkaStreams(topology, props);
        kafkaStreams.cleanUp();
    }

    public static Properties config() {
        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-zip-codes");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 10);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return props;
    }

    /**
     * Start the Kafka Streams topology
     */
    void start() {
        kafkaStreams.start();
    }

    /**
     * Stop the Kafka Streams topology.
     */
    void stop() {
        kafkaStreams.close();
    }
}
