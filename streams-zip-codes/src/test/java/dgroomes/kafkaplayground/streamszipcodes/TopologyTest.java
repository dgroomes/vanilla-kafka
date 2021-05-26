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
    TestOutputTopic<CityState, Integer> output;

    @BeforeEach
    void before() {
        driver = new TopologyTestDriver(App.topology(), App.config());

        var stringSerde = Serdes.String();
        var zipAreaSerde = new JsonSerde<>(new TypeReference<ZipArea>() {
        });
        var cityStateSerde = new JsonSerde<>(new TypeReference<CityState>() {
        });
        input = driver.createInputTopic("streams-zip-codes-zip-areas", stringSerde.serializer(), zipAreaSerde.serializer());
        output = driver.createOutputTopic("streams-zip-codes-avg-pop-by-city", cityStateSerde.deserializer(), Serdes.Integer().deserializer());
    }

    @AfterEach
    void close() {
        driver.close();
    }

    /**
     * The topology should compute an average of ZIP area populations
     */
    @Test
    void average() {
        input.pipeInput(new ZipArea("1", "SPRINGFIELD", "MA", 1));
        input.pipeInput(new ZipArea("2", "SPRINGFIELD", "MA", 3));

        var outputRecords = output.readRecordsToList();

        assertThat(outputRecords)
                .map(record -> tuple(record.key(), record.value()))
                .containsExactly(
                        tuple(new CityState("SPRINGFIELD", "MA"), 1),
                        tuple(new CityState("SPRINGFIELD", "MA"), 2));
    }

    /**
     * Records for already-seen keys should replace the original record.
     */
    @Test
    void sameKeyUpdates() {
        input.pipeInput(new ZipArea("1", "SPRINGFIELD", "MA", 1));
        input.pipeInput(new ZipArea("2", "SPRINGFIELD", "MA", 3));
        input.pipeInput(new ZipArea("3", "SPRINGFIELD", "MA", 1));

        var outputRecords = output.readRecordsToList();

        assertThat(outputRecords)
                .map(record -> tuple(record.key(), record.value()))
                .containsExactly(
                        tuple(new CityState("SPRINGFIELD", "MA"), 1),
                        tuple(new CityState("SPRINGFIELD", "MA"), 2),
                        tuple(new CityState("SPRINGFIELD", "MA"), 1));
    }
}
