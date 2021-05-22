package dgroomes.kafkaplayground.streamszipcodes;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

/**
 * A toy implementation of a Kafka Streams topology. Designed for educational purposes!
 */
public class Topology {

    private static final Logger log = LoggerFactory.getLogger(Topology.class);
    public static final String INPUT_TOPIC = "zip-areas";
    public static final String STREAMS_INPUT_TOPIC = "streams-zip-codes-zip-areas";
    public static final String STREAMS_OUTPUT_TOPIC = "streams-zip-codes-avg-pop-by-city";
    public static final int INPUT_MESSAGE_SLEEP = 1000;
    private final KafkaStreams kafkaStreams;

    public Topology() {
        final Properties props = getStreamsConfig();

        final StreamsBuilder builder = new StreamsBuilder();
        createWordCountStream(builder);
        var topology = builder.build();
        log.info("Initialized Kafka Streams topology to {}", topology.describe());
        kafkaStreams = new KafkaStreams(topology, props);
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

        // setting offset reset to earliest so that we can re-run the demo code with the same pre-loaded data
        // Note: To re-run the demo, you need to use the offset reset tool:
        // https://cwiki.apache.org/confluence/display/KAFKA/Kafka+Streams+Application+Reset+Tool
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    static void createWordCountStream(final StreamsBuilder builder) {
        final KStream<String, String> source = builder.stream(INPUT_TOPIC);

        final KTable<String, Long> counts = source
                // Pass the plaintext-input topic data into a "streams-plaintext-input" topic which is configured with
                // a higher number of partitions so we can get more concurrency out of the topology. We want the
                // "streams-plaintext-input" to get messages produced to it with an even distribution across its
                // partitions so we hash on the value to get an approximate even distribution for partition assignment.
                .through(STREAMS_INPUT_TOPIC, Produced.streamPartitioner(new StreamPartitioner<String, String>() {
                    @Override
                    public Integer partition(String topic, String key, String value, int numPartitions) {
                        var bytes = value.getBytes();
                        // Similar to https://github.com/apache/kafka/blob/c6adcca95f03758089715c60e806a8090f5422d9/clients/src/main/java/org/apache/kafka/clients/producer/internals/DefaultPartitioner.java#L71
                        return Utils.toPositive(Utils.murmur2(bytes)) % numPartitions;
                    }
                }))
                .mapValues(value -> {
                    try {
                        log.info("Input message received: {}. Sleeping for {}ms", value, INPUT_MESSAGE_SLEEP);
                        Thread.sleep(INPUT_MESSAGE_SLEEP);
                    } catch (InterruptedException e) {
                        log.error("Interrupted while sleeping", e);
                    }
                    return value;
                })
                .flatMapValues(value -> Arrays.asList(value.toLowerCase(Locale.getDefault()).split(" ")))
                .groupBy((key, value) -> value)
                .count();

        // need to override value serde to Long type
        counts.toStream().to(STREAMS_OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));
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
