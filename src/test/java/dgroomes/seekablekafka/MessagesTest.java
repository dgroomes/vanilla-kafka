package dgroomes.seekablekafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.kafka.template.default-topic=my-messages"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessagesTest {

    @Autowired
    KafkaTemplate<?, String> kafkaTemplate;

    @Autowired
    Messages messages;

    /**
     * Produce a test message and assert that it is consumed by the app
     */
    @Test
    @Timeout(10)
    void poll() throws ExecutionException, InterruptedException {
        long now = Instant.now().getEpochSecond();
        String msg = String.format("hello: %d", now);

        kafkaTemplate.sendDefault(msg).get();
        String found = messages.take();

        assertThat(found).isEqualTo(msg);
    }
}