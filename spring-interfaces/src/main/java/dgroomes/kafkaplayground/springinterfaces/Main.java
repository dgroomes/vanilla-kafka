package dgroomes.kafkaplayground.springinterfaces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Wiring up a simple Spring application context");
        new AnnotationConfigApplicationContext(Main.class.getPackageName());
    }
}
