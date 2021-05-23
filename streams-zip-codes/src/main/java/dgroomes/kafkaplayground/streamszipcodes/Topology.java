package dgroomes.kafkaplayground.streamszipcodes;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * A toy implementation of a Kafka Streams topology. Designed for educational purposes!
 */
public class Topology {

    private static final Logger log = LoggerFactory.getLogger(Topology.class);
    public static final String INPUT_TOPIC = "streams-zip-codes-zip-areas";
    public static final String OUTPUT_TOPIC = "streams-zip-codes-avg-pop-by-city";
    private final KafkaStreams kafkaStreams;

    public Topology() {
        final Properties props = getStreamsConfig();

        final StreamsBuilder builder = new StreamsBuilder();
        configureStream(builder);
        var topology = builder.build();
        log.info("Initialized Kafka Streams topology to {}", topology.describe());
        kafkaStreams = new KafkaStreams(topology, props);
        kafkaStreams.cleanUp();
    }

    static Properties getStreamsConfig() {
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

    private void configureStream(final StreamsBuilder builder) {
        var zipAreaSerde = new JsonSerde<>(ZipArea.class);
        KStream<String, ZipArea> source = builder.stream(INPUT_TOPIC, Consumed.with(Serdes.String(), zipAreaSerde));

        KTable<String, Long> counts = source
                .mapValues(zipArea -> {
                    // Note: this is a do-nothing mapper. It is only used to log the incoming ZipArea data. It doesn't
                    // actually map the incoming type to another type.
                    log.trace("Input ZipArea message received: {}", zipArea);
                    return zipArea;
                })
                .groupBy((key, value) -> value._id(), Grouped.with(Serdes.String(), zipAreaSerde))
                .count();

        // Output the KTable to a stream.
        counts.toStream().to(OUTPUT_TOPIC);
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
