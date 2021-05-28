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
    public static final String INPUT_TOPIC = "streams-zip-codes-zip-areas";
    private final KafkaStreams kafkaStreams;

    private static final JsonSerde<ZipArea> zipAreaSerde = new JsonSerde<>(new TypeReference<>() {
    });
    private static final JsonSerde<City> citySerde = new JsonSerde<>(new TypeReference<City>() {
    });
    private static final JsonSerde<CityStats> cityStatsSerde = new JsonSerde<>(new TypeReference<>() {
    });
    private static final JsonSerde<StateStats> stateStatsSerde = new JsonSerde<>(new TypeReference<>() {
    });

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

        KStream<Void, ZipArea> zipAreasNoKey = inputZips(builder);
        KStream<String, ZipArea> zipAreas = keyOnZipCode(zipAreasNoKey);
        KTable<String, ZipArea> zipAreasTable = zipsToTable(zipAreas);

        KGroupedTable<City, ZipArea> cityGrouped = groupByCity(zipAreasTable);
        KTable<City, HashSet<ZipArea>> cityAggregated = aggregateCities(cityGrouped);
        KTable<City, CityStats> cityStats = computeCityStats(cityAggregated);

        KGroupedTable<String, CityStats> stateGrouped = groupByState(cityStats);
        KTable<String, HashSet<CityStats>> stateAggregated = aggregateByState(stateGrouped);
        @SuppressWarnings("unused")
        KTable<String, StateStats> stateStats = computeStateStats(stateAggregated);

        return builder.build();
    }

    // Compute the state-level ZIP area population statistics
    private static KTable<String, StateStats> computeStateStats(KTable<String, HashSet<CityStats>> stateAggregated) {
        return stateAggregated.mapValues(
                set -> {
                    // I have no idea why the "mapValues" operation is invoked if the set is empty. So, return
                    // null to avoid a divide by zero exception later.
                    if (set.isEmpty()) {
                        return null;
                    }
                    var numberZipAreas = set.stream()
                            .mapToInt(CityStats::zipAreas)
                            .sum();
                    var pop = set.stream()
                            .mapToInt(CityStats::totalPop)
                            .sum();

                    var avg = pop / numberZipAreas;
                    return new StateStats(numberZipAreas, pop, avg);
                },
                Named.as("state-stats-computer"),
                Materialized.<String, StateStats, KeyValueStore<Bytes, byte[]>>as("state-stats")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(stateStatsSerde));
    }

    // Aggregate the city stats into a set
    private static KTable<String, HashSet<CityStats>> aggregateByState(KGroupedTable<String, CityStats> stateGrouped) {
        return stateGrouped.aggregate(
                HashSet::new,
                (state, stats, set) -> {
                    set.add(stats);
                    return set;
                },
                (state, stats, set) -> {
                    set.remove(stats);
                    return set;
                },
                Named.as("by-state-aggregator"),
                Materialized.<String, HashSet<CityStats>, KeyValueStore<Bytes, byte[]>>as("by-state")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(new JsonSerde<HashSet<CityStats>>(new TypeReference<>() {
                        })));
    }

    // Group the city stats by state
    private static KGroupedTable<String, CityStats> groupByState(KTable<City, CityStats> cityStats) {
        return cityStats.groupBy(
                (city, stats) -> new KeyValue<>(city.state(), stats),
                Grouped.<String, CityStats>as("by-state")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(cityStatsSerde));
    }

    // Compute the city-level ZIP area population statistics
    private static KTable<City, CityStats> computeCityStats(KTable<City, HashSet<ZipArea>> cityAggregated) {
        return cityAggregated.mapValues(
                zips -> {
                    var numberZips = zips.size();
                    var pop = zips.stream()
                            .mapToInt(ZipArea::pop)
                            .sum();
                    var avgPop = pop / numberZips;
                    return new CityStats(numberZips, pop, avgPop);
                },
                Named.as("city-stats-computer"),
                Materialized.<City, CityStats, KeyValueStore<Bytes, byte[]>>as("city-stats")
                        .withKeySerde(citySerde)
                        .withValueSerde(cityStatsSerde));
    }

    // Aggregate the grouped cities into a set.
    private static KTable<City, HashSet<ZipArea>> aggregateCities(KGroupedTable<City, ZipArea> cityGrouped) {
        return cityGrouped.aggregate(
                HashSet::new,
                (key, value, aggregate) -> {
                    aggregate.add(value);
                    return aggregate;
                },
                (key, value, aggregate) -> {
                    aggregate.remove(value);
                    return aggregate;
                },
                Named.as("by-city-aggregator"),
                Materialized.<City, HashSet<ZipArea>, KeyValueStore<Bytes, byte[]>>as("by-city")
                        .withKeySerde(citySerde)
                        .withValueSerde(new JsonSerde<HashSet<ZipArea>>(new TypeReference<>() {
                        })));
    }

    // Group the ZIP areas by city.
    //
    // Think of SQL's "group by". A "group by" always has an accompanying aggregation and it's useful to think about
    // the grouping and the aggregation in tandem. Look down a few lines at the "aggregate" statement.
    private static KGroupedTable<City, ZipArea> groupByCity(KTable<String, ZipArea> zipAreasTable) {
        return zipAreasTable.groupBy(
                (key, value) -> new KeyValue<>(new City(value.city(), value.state()), value),
                Grouped.<City, ZipArea>as("by-city")
                        .withKeySerde(citySerde)
                        .withValueSerde(zipAreaSerde));
    }

    // Represent ZIP areas as a KTable. ZIP areas need to be represented as a changelog not as a record stream (KStream).
    // ZIP areas are upserted over time, so we need a KTable.
    private static KTable<String, ZipArea> zipsToTable(KStream<String, ZipArea> zipAreas) {
        return zipAreas.toTable(
                Named.as("zip-areas-to-tabler"),
                Materialized.<String, ZipArea, KeyValueStore<Bytes, byte[]>>as("zip-areas")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(zipAreaSerde));
    }

    // Key ZIP area records on the input topic by ZIP code and pipe to a new topic. Kafka Streams requires keys!
    private static KStream<String, ZipArea> keyOnZipCode(KStream<Void, ZipArea> zipAreasNoKey) {
        return zipAreasNoKey.map(
                (_key, zipArea) -> new KeyValue<>(zipArea._id(), zipArea),
                Named.as("zip-areas-keyer"));
    }

    // Input. The input topic is not keyed.
    private static KStream<Void, ZipArea> inputZips(StreamsBuilder builder) {
        return builder.stream(INPUT_TOPIC, Consumed.with(Serdes.Void(), zipAreaSerde));
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
