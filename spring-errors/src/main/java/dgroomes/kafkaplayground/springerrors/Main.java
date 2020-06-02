package dgroomes.kafkaplayground.springerrors;

import dgroomes.kafkaplayground.springerrors.model.Message;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }

    /**
     * Catch deserialization errors. See https://docs.spring.io/spring-kafka/reference/html/#error-handling-deserializer
     * <p>
     * Also, configure a JSON serializer (Spring Boot + Spring Kafka does not assume that the Kafka messages are JSON,
     * so we must configure it).
     */
    @Bean
    public DefaultKafkaConsumerFactoryCustomizer customizerForSerialization() {
        return consumerFactory -> {
            var jsonDeserializer = new JsonDeserializer<>(Message.class);
            var errorHandlingDeserializer = new ErrorHandlingDeserializer<>(jsonDeserializer);
            consumerFactory.setValueDeserializer((Deserializer) errorHandlingDeserializer);
        };
    }
}
