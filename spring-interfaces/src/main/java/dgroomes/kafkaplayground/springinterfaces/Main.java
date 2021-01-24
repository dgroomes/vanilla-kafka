package dgroomes.kafkaplayground.springinterfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Listening for Kafka messages using Spring for Apache Kafka's very useful abstractions");

        Beans beans = new Beans();
        var container = beans.kafkaMessageListenerContainer();
        container.start();
    }
}
