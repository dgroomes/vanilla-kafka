package dgroomes.vanillakafka;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TODO actually implement some tests!
 *
 * Implementing some tests first requires a design choice. Pick one of two options: 1) write integration tests that
 * require a running version of Kafka or 2) write more "traditional" unit tests by mocking out collaborating objects.
 *
 * If 2) is chosen and we want to mock out collaborating objects, then the object design needs to be changed because
 * the "Application" class codes to the "Messages" class which codes to the "KafkaConsumer" class. This tight coupling
 * requires us to actually run Kafka and then our test is not actually a unit test. So, we must code to interfaces
 * and/or use dependency injection (remember, plain old constructors count as dependency injection!) and mock the
 * injected classes using something like Mockito.
 */
class ApplicationTest {

    @Test
    void todo() {
        assertTrue(true);
    }
}