package dgroomes.kafkaplayground.streamszipcodes;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class TopologyTest {

    TopologyTestDriver driver;

    @BeforeEach
    void before() {
        driver = new TopologyTestDriver(App.topology(), App.config());
    }

    @AfterEach
    void close() {
        driver.close();
    }

    @Test
    void test() {
        var stringSerde = Serdes.String();
        var zipAreaSerde = new JsonSerde<>(new TypeReference<ZipArea>() {
        });
        var cityStateSerde = new JsonSerde<>(new TypeReference<CityState>() {
        });

        var input = driver.createInputTopic("streams-zip-codes-zip-areas", stringSerde.serializer(), zipAreaSerde.serializer());
        var output = driver.createOutputTopic("streams-zip-codes-avg-pop-by-city", cityStateSerde.deserializer(), Serdes.Integer().deserializer());


        input.pipeInput(new ZipArea("01103", "SPRINGFIELD", "MA", 2323));
        input.pipeInput(new ZipArea("01104", "SPRINGFIELD", "MA", 22115));
        input.pipeInput(new ZipArea("01105", "SPRINGFIELD", "MA", 14970));

        var outputRecords = output.readRecordsToList();

        assertThat(outputRecords)
                .map(record -> tuple(record.key(), record.value()))
                .containsExactly(
                        tuple(new CityState("SPRINGFIELD", "MA"), 2323),
                        tuple(new CityState("SPRINGFIELD", "MA"), 12219),
                        tuple(new CityState("SPRINGFIELD", "MA"), 13136));
    }
}
