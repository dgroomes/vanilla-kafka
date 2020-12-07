package dgroomes;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TheTest extends BaseTest {

    /**
     * This is a 'happy path' test case.
     *
     * Exercise the 'app' by sending a message to the input Kafka topic, waiting a brief time for the 'app' to process
     * the message, and then consume from the output topic with the expectation that the 'app' successfully processed
     * and published a quoted version of the input message. Assert te contents of the output message.
     */
    @Test
    void quote() throws Exception {
        // Arrange
        var msg = "Did you say \"hello\"?";

        // Act
        send(msg);
        Thread.sleep(200);

        // Assert
        var records = consume();
        assertThat(records).hasSize(1);
        var record = records.get(0);
        assertThat(record.value()).isEqualTo("\"Did you say \\\"hello\\\"?\"");
    }
}
