package dgroomes.kafkaplayground.streamszipcodes;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Properties;

/**
 * A Kafka Streams app
 */
public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    public static final String INPUT_TOPIC = "streams-zip-codes-zip-areas";
    public static final String OUTPUT_TOPIC = "streams-zip-codes-avg-pop-by-city";
    private final KafkaStreams kafkaStreams;

    public App() {
        final Properties props = config();
        Topology topology = topology();
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
     * Create a topology that works like this:
     * <p>
     * 1. Reads from the ZIP areas Kafka topic
     * 2. Re-key each message by its city-state pair
     * 3. Group all ZIP areas by city-state into a collection
     * 4. Compute the average ZIP area population by city
     */
    public static Topology topology() {
        var builder = new StreamsBuilder();
        // Pay the type and serialization tax with some boilerplate Jackson/Kafka serialization setup!
        var zipAreaSerde = new JsonSerde<>(new TypeReference<ZipArea>() {
        });
        var cityStateSerde = new JsonSerde<>(new TypeReference<CityState>() {
        });
        var hashSetSerde = new JsonSerde<>(new TypeReference<HashSet<ZipArea>>() {
        });

        // Input. The input topic is not keyed.
        KStream<Void, ZipArea> zipAreasNoKey = builder.stream(INPUT_TOPIC, Consumed.with(Serdes.Void(), zipAreaSerde));

        // Key records on the input topic into a new topic. Kafka Streams requires keys!
        KStream<CityState, ZipArea> zipAreas = zipAreasNoKey.map((_key, zipArea) -> {
            var cityState = new CityState(zipArea.city(), zipArea.state());
            return new KeyValue<>(cityState, zipArea);
        });

        // Represent ZIP areas as a KTable. ZIP areas need to be represented as a changelog not as a record stream (KStream).
        // ZIP areas are upserted over time, so we need a KTable.
        KTable<CityState, ZipArea> zipAreasTable = zipAreas.toTable(Materialized.with(cityStateSerde, zipAreaSerde));

        // Group the ZIP areas by city
        KGroupedTable<CityState, ZipArea> cityGrouped = zipAreasTable.groupBy(KeyValue::new);
        KTable<CityState, HashSet<ZipArea>> cityAggregated = cityGrouped.aggregate(HashSet::new, (key, value, aggregate) -> {
            aggregate.add(value);
            return aggregate;
        }, (key, value, aggregate) -> {
            aggregate.remove(value);
            return aggregate;
        }, Materialized.with(cityStateSerde, hashSetSerde));

        // Compute the city-level ZIP area population statistics
        KTable<CityState, Integer> cityStats = cityAggregated
                .mapValues(collection -> {
                    @SuppressWarnings("OptionalGetWithoutIsPresent")
                    var avg = collection.stream()
                            .mapToInt(ZipArea::pop)
                            .average()
                            .getAsDouble();
                    return (int) avg;
                }, Materialized.with(cityStateSerde, Serdes.Integer()));

        // Output the KTable to a stream.
        cityStats.toStream().to(OUTPUT_TOPIC);

        return builder.build();
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
