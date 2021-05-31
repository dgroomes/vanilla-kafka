package dgroomes;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class TheTest extends BaseTest {

    /**
     * This is a 'happy path' test case.
     * <p>
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

        // Assert
        var record = consumer.take();
        assertThat(record.value()).isEqualTo("\"Did you say \\\"hello\\\"?\"");
    }

    /**
     * Send a unique message and assert that it shows up on the output Kafka topic.
     */
    @Test
    void identity() throws Exception {
        // Arrange
        var time = LocalTime.now();
        var uniqueMsg = String.format("The current time is: %s", time);

        // Act
        send(uniqueMsg);

        // Assert
        var record = consumer.take();
        var expected = String.format("%s%s%s", '"', uniqueMsg, '"');
        assertThat(record.value()).isEqualTo(expected);
    }
}
