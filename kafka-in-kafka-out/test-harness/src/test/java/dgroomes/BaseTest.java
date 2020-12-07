package dgroomes;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This base class provides the boilerplate and helper functionality related to producing and consuming to the local
 * Kafka cluster which is how we can exercise and assert the output of the 'app'.
 *
 * We are using the "PER_CLASS" JUnit lifecycle value so we can use "before all" and "after all". See https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-instance-lifecycle
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {

    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    private static final String KAFKA_BROKER_HOST = "localhost:9092";
    private static final String INPUT_TOPIC = "input-text";
    private static final String OUTPUT_TOPIC = "quoted-text";
    private static final TopicPartition OUTPUT_TOPIC_PARTITION = new TopicPartition(OUTPUT_TOPIC, 0); // There will only be one partition (partition '0') so we can hardcode to it.
    private static final Duration POLL_DURATION = Duration.ofMillis(200);

    protected final Producer<Void,String> producer;
    protected final Consumer<Void,String> consumer;

    /**
     * Set up a producer object to produce test messages
     * Set up a consumer object to consume messages output from 'app' so that we can assert on them.
     */
    public BaseTest() {
        this.producer = kafkaProducer();
        this.consumer = kafkaConsumer();
    }

    /**
     * Assign the consumer and poll it to jump start it into working. I'm trying to de-mystify how
     * the consumer works. I don't quite get it. There is some peculiar stuff under the hood. It seems to be necessary
     * to poll it once as an initialization step.
     */
    @BeforeAll
    public void beforeAll() {
        consumer.assign(List.of(OUTPUT_TOPIC_PARTITION));
        log.debug("[beforeAll] Polling the Kafka consumer to jump start it");
        consumer.poll(POLL_DURATION);
    }

    /**
     * Clean up and shutdown.
     */
    @AfterAll
    public void afterAll() {
        producer.close();
        consumer.close();
    }

    /**
     * (Synchronous) Send a test message to the input Kafka topic. This will block until the message is sent.
     * @param msg the message to send to Kafka
     */
    protected void send(String msg) throws ExecutionException, InterruptedException {
        log.debug("Sending message: {}", msg);
        ProducerRecord<Void, String> record = new ProducerRecord<>(INPUT_TOPIC, msg);
        Future<RecordMetadata> future = producer.send(record);
        future.get();
    }

    /**
     * Consume from the output Kafka topic.
     * @return all messages consumed from the topic.
     */
    protected List<ConsumerRecord<Void,String>> consume() {
        log.debug("Consuming from the output topic via a call to 'poll'");
        ConsumerRecords<Void, String> records = consumer.poll(POLL_DURATION);
        var specificRecords = records.records(OUTPUT_TOPIC_PARTITION);
        log.debug("Found {} messages after polling", specificRecords.size());

        for (ConsumerRecord<Void, String> record : specificRecords) {
            log.debug("Message: {}", record.value());
        }

        return specificRecords;
    }

    /**
     * Construct a KafkaConsumer
     */
    public static KafkaConsumer<Void,String> kafkaConsumer() {
        Properties config = new Properties();
        config.put("bootstrap.servers", KAFKA_BROKER_HOST);
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return new KafkaConsumer<>(config);
    }

    /**
     * Construct a KafkaProducer
     */
    public static KafkaProducer<Void,String> kafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_BROKER_HOST);
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<>(props);
    }
}
