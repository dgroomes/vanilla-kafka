package dgroomes.kafkaplayground.streamszipcodes;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class TopologyTest {

    TopologyTestDriver driver;
    TestInputTopic<String, ZipArea> input;
    TestOutputTopic<City, CityStats> cityOutput;
    TestOutputTopic<String, StateStats> stateOutput;

    @BeforeEach
    void before() {
        driver = new TopologyTestDriver(new TopologyBuilder().build(), App.config());

        input = driver.createInputTopic("streams-zip-codes-zip-areas", Serdes.String().serializer(), TopologyBuilder.zipAreaSerde.serializer());
        cityOutput = driver.createOutputTopic("streams-zip-codes-city-stats-changelog", TopologyBuilder.citySerde.deserializer(), TopologyBuilder.cityStatsSerde.deserializer());
        stateOutput = driver.createOutputTopic("streams-zip-codes-state-stats-changelog", Serdes.String().deserializer(), TopologyBuilder.stateStatsSerde.deserializer());
    }

    @AfterEach
    void close() {
        driver.close();
    }

    /**
     * The topology should compute an average of ZIP area populations for each city
     */
    @Test
    void averageByCity() {
        input.pipeInput(new ZipArea("1", "SPRINGFIELD", "MA", 1));
        input.pipeInput(new ZipArea("2", "SPRINGFIELD", "MA", 3));

        var outputRecords = cityOutput.readRecordsToList();

        assertThat(outputRecords)
                .extracting("key", "value")
                .containsExactly(
                        tuple(new City("SPRINGFIELD", "MA"), new CityStats(1, 1, 1)),
                        tuple(new City("SPRINGFIELD", "MA"), new CityStats(2, 4, 2)));
    }

    /**
     * The topology should compute an average of ZIP area populations for each state
     */
    @Test
    void averageByState() {
        input.pipeInput(new ZipArea("1", "SPRINGFIELD", "MA", 1));
        input.pipeInput(new ZipArea("2", "BOSTON", "MA", 3));

        var outputRecords = stateOutput.readRecordsToList();

        assertThat(outputRecords)
                .extracting("key", "value")
                .containsExactly(
                        tuple("MA", new StateStats(1, 1, 1)),
                        tuple("MA", new StateStats(2, 4, 2)));
    }

    /**
     * Records for already-seen keys should replace the original record.
     */
    @Test
    void sameKeyUpdates() {
        input.pipeInput(new ZipArea("1", "SPRINGFIELD", "MA", 1));
        input.pipeInput(new ZipArea("2", "SPRINGFIELD", "MA", 3));
        input.pipeInput(new ZipArea("2", "SPRINGFIELD", "MA", 1));

        var outputRecords = cityOutput.readRecordsToList();

        assertThat(outputRecords)
                .map(record -> tuple(record.key(), record.value()))
                .containsExactly(
                        tuple(new City("SPRINGFIELD", "MA"), new CityStats(1, 1, 1)),
                        tuple(new City("SPRINGFIELD", "MA"), new CityStats(2, 4, 2)),

                        // When the second ZIP area record for ZIP code 2 occurs, it is an "upsert". As such, this engages
                        // the KTable's "subtractor" and "adder" operations which together make the effect of an upsert.
                        // This was surprising to me, but I guess it makes sense. I would prefer if there was a way to
                        // do proper upserts instead of separate delete ("subtractor") and insert ("adder") operations
                        // because it creates this awkward effect where for a brief time the city statistics actually
                        // goes backwards until the new value is added.
                        //
                        // I think there is a solution to this. I could research more. But also I think if I create a
                        // KStream out of the KTable it might solve it. But I want to minimize the Kafka topics.
                        tuple(new City("SPRINGFIELD", "MA"), new CityStats(1, 1, 1)),
                        tuple(new City("SPRINGFIELD", "MA"), new CityStats(2, 2, 1)));
    }
}
