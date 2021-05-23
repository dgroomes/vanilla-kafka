package dgroomes.kafkaplayground.streamszipcodes;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
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

    /**
     * Configure a stream that works like this:
     * <p>
     * 1. Reads from the ZIP areas Kafka topic
     * 2. Re-key each message by its city-state pair
     * 3. Group all ZIP areas by city-state into a collection
     * 4. Compute the average ZIP area population by city
     */
    private void configureStream(final StreamsBuilder builder) {

        // Pay the type and serialization tax with some boilerplate Jackson/Kafka serialization setup!
        var zipAreaSerde = new JsonSerde<>(new TypeReference<ZipArea>() {
        });
        var cityStateSerde = new JsonSerde<>(new TypeReference<CityState>() {
        });
        var hashSetSerde = new JsonSerde<>(new TypeReference<HashSet<ZipArea>>() {
        });

        KStream<String, ZipArea> source = builder.stream(INPUT_TOPIC, Consumed.with(Serdes.String(), zipAreaSerde));

        KStream<CityState, ZipArea> keyed = source
                .map((_cityState, zipArea) -> {
                    log.trace("Input ZipArea message received: {}", zipArea);
                    var cityState = new CityState(zipArea.city(), zipArea.state());
                    return new KeyValue<>(cityState, zipArea);
                });

        KGroupedStream<CityState, ZipArea> grouped = keyed.groupByKey(Grouped.with(cityStateSerde, zipAreaSerde));

        KTable<CityState, HashSet<ZipArea>> collected = grouped
                .aggregate(HashSet::new, (_cityState, zipArea, set) -> {
                    set.add(zipArea);
                    return set;
                }, Materialized.with(cityStateSerde, hashSetSerde));

        KTable<CityState, Integer> counts = collected
                .mapValues(collection -> {
                    @SuppressWarnings("OptionalGetWithoutIsPresent")
                    var avg = collection.stream()
                            .mapToInt(ZipArea::pop)
                            .average()
                            .getAsDouble();
                    return (int) avg;
                }, Materialized.with(cityStateSerde, Serdes.Integer()));

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
