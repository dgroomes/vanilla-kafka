package dgroomes.vanillakafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The application runner
 */
public class Main {

    private static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        log.info("Let's code to a vanilla KafkaConsumer (no frameworks) and learn something in the process!");
        var app = new Application();
        app.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                log.info("Stopping the app");
                app.stop();
            } catch (InterruptedException e) {
                throw new RuntimeException("Shutdown thread was interrupted. Failed to stop the app.", e);
            }
        }));

        app.acceptInput();
    }
}
