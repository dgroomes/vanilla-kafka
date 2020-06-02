package dgroomes.kafkaplayground.springerrors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.converter.BytesJsonMessageConverter;
import org.springframework.kafka.support.converter.JsonMessageConverter;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }

    /**
     * Supply a JsonMessageConverter to the application context to engage the Spring Boot + Spring Kafka
     * (de)serialization machinery to deserialize messages in a way that assumes they are JSON. See the closely related
     * documentation section https://docs.spring.io/spring-kafka/docs/2.5.1.RELEASE/reference/html/#messaging-message-conversion
     */
    @Bean
    public JsonMessageConverter converter() {
        return new BytesJsonMessageConverter();
    }
}
