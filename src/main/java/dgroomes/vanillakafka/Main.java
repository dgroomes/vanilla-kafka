package dgroomes.vanillakafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        log.info("Hello world. Let's use a vanilla Java Kafka consumer and see if we can learn something...");

        var messages = new Messages();
        Thread thread = new Thread(() -> {
            try {
                messages.start();
            } catch (InterruptedException e) {
                log.error("Exception while running Kafa consumer", e);
            }
        });

        thread.start();

        while(true) {
            var msg = messages.take();
            log.info("Got message: {}", msg);
        }
    }
}
