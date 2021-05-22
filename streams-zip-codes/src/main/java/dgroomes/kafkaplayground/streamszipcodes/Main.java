package dgroomes.kafkaplayground.streamszipcodes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * The application runner. Initializes and starts the Kafka Streams topology. Handles shutdown.
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("This is an intermediate Kafka Streams application to aggregate ZIP code data!");

        var topology = new Topology();
        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread("streams-wordcount-shutdown-hook") {
            @Override
            public void run() {
                log.info("Stopping the topology");
                topology.stop();
                latch.countDown();
            }
        });

        try {
            topology.start();
            latch.await();
        } catch (final Throwable e) {
            log.error("Error. Did not shutdown as expected.", e);
            System.exit(1);
        }
        System.exit(0);
    }
}
