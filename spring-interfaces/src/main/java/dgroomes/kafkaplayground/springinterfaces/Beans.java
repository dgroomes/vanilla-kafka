package dgroomes.kafkaplayground.springinterfaces;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

public class Beans {

    /**
     * KafkaMessageListenerContainer is the kernel of the "Spring for Apache Kafka" library (at least from my vantage
     * point). It is an effective abstraction over the Java Kafka client library.
     */
    public KafkaMessageListenerContainer<Void, String> kafkaMessageListenerContainer() {
        ContainerProperties containerProperties = new ContainerProperties("my-messages");
        containerProperties.setMessageListener(listener());
        return new KafkaMessageListenerContainer<>(consumerFactory(), containerProperties);
    }

    public ConsumerFactory<Void, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "spring-interfaces-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    public Listener listener() {
        return new Listener();
    }
}
