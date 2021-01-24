package dgroomes.kafkaplayground.springinterfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Wiring up a simple Spring application context");
        var context = new AnnotationConfigApplicationContext(Main.class.getPackageName());

        var container = context.getBean(KafkaMessageListenerContainer.class);
        container.start();
    }
}
