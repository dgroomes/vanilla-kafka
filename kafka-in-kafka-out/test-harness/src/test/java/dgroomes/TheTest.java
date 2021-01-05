package dgroomes;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TheTest extends BaseTest {

    /**
     * This is a 'happy path' test case.
     *
     * Exercise the 'app' by sending a message to the input Kafka topic, waiting a brief time for the 'app' to process
     * the message, and then consume from the output topic with the expectation that the 'app' successfully processed
     * and published a quoted version of the input message. Assert te contents of the output message.
     */
    @Test
    @Order(1)
    void quote() throws Exception {
        // Arrange
        var msg = "Did you say \"hello\"?";

        // Act
        send(msg);

        // Assert
        var records = consume();
        assertThat(records).hasSize(1);
        var record = records.get(0);
        assertThat(record.value()).isEqualTo("\"Did you say \\\"hello\\\"?\"");
    }

    /**
     * Send a unique message and assert that it shows up on the output Kafka topic.
     */
    @Test
    @Order(2)
    void identity() throws Exception {
        // Arrange
        var time = LocalTime.now();
        var uniqueMsg = "The current time is: %s".formatted(time);

        // Act
        send(uniqueMsg);

        // Assert
        var records = consume();
        assertThat(records).hasSize(1);
        var record = records.get(0);
        var expected = "%s%s%s".formatted('"', uniqueMsg, '"');
        assertThat(record.value()).isEqualTo(expected);
    }
}
