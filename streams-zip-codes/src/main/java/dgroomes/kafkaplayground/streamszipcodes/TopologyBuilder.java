package dgroomes.kafkaplayground.streamszipcodes;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.HashSet;

/**
 * Build the Kafka Streams topology.
 */
public class TopologyBuilder {

    private static final String INPUT_TOPIC = "streams-zip-codes-zip-areas";

    public static final JsonSerde<ZipArea> zipAreaSerde = new JsonSerde<>(new TypeReference<>() {
    });
    public static final JsonSerde<City> citySerde = new JsonSerde<>(new TypeReference<>() {
    });
    public static final JsonSerde<CityStats> cityStatsSerde = new JsonSerde<>(new TypeReference<>() {
    });
    public static final JsonSerde<StateStats> stateStatsSerde = new JsonSerde<>(new TypeReference<>() {
    });

    /**
     * Create a topology that works like this:
     * <p>
     * 1. Reads from the ZIP areas Kafka topic
     * 2. Re-key each message by its city-state pair
     * 3. Group all ZIP areas by city-state into a collection
     * 4. Compute the average ZIP area population by city
     * 5. Group city-level statistics into the state level
     * 6. Compute the average ZIP are popultion by state
     */
    public Topology build() {
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
    private KTable<String, StateStats> computeStateStats(KTable<String, HashSet<CityStats>> stateAggregated) {
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
    private KTable<String, HashSet<CityStats>> aggregateByState(KGroupedTable<String, CityStats> stateGrouped) {
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
    private KGroupedTable<String, CityStats> groupByState(KTable<City, CityStats> cityStats) {
        return cityStats.groupBy(
                (city, stats) -> new KeyValue<>(city.state(), stats),
                Grouped.<String, CityStats>as("by-state")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(cityStatsSerde));
    }

    // Compute the city-level ZIP area population statistics
    private KTable<City, CityStats> computeCityStats(KTable<City, HashSet<ZipArea>> cityAggregated) {
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
    private KTable<City, HashSet<ZipArea>> aggregateCities(KGroupedTable<City, ZipArea> cityGrouped) {
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
    private KGroupedTable<City, ZipArea> groupByCity(KTable<String, ZipArea> zipAreasTable) {
        return zipAreasTable.groupBy(
                (key, value) -> new KeyValue<>(new City(value.city(), value.state()), value),
                Grouped.<City, ZipArea>as("by-city")
                        .withKeySerde(citySerde)
                        .withValueSerde(zipAreaSerde));
    }

    // Represent ZIP areas as a KTable. ZIP areas need to be represented as a changelog not as a record stream (KStream).
    // ZIP areas are upserted over time, so we need a KTable.
    private KTable<String, ZipArea> zipsToTable(KStream<String, ZipArea> zipAreas) {
        return zipAreas.toTable(
                Named.as("zip-areas-to-tabler"),
                Materialized.<String, ZipArea, KeyValueStore<Bytes, byte[]>>as("zip-areas")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(zipAreaSerde));
    }

    // Key ZIP area records on the input topic by ZIP code and pipe to a new topic. Kafka Streams requires keys!
    private KStream<String, ZipArea> keyOnZipCode(KStream<Void, ZipArea> zipAreasNoKey) {
        return zipAreasNoKey.map(
                (_key, zipArea) -> new KeyValue<>(zipArea._id(), zipArea),
                Named.as("zip-areas-keyer"));
    }

    // Input. The input topic is not keyed.
    private KStream<Void, ZipArea> inputZips(StreamsBuilder builder) {
        return builder.stream(INPUT_TOPIC, Consumed.with(Serdes.Void(), zipAreaSerde));
    }
}
