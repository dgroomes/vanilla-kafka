package dgroomes.vanillakafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Application {

    private static Logger log = LoggerFactory.getLogger(Application.class);

    private Thread messageTakerThread;
    private Messages messages;

    /**
     * (Non-blocking) Start the application
     */
    void start() {
        messages = new Messages();
        messages.start();

        messageTakerThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String msg = messages.take();
                    log.info("Got message: {}", msg);
                }
            } catch (InterruptedException e) {
                log.info("'messageTaker' thread was interrupted.");
            }
        });
        messageTakerThread.start();
    }

    /**
     * Stop the application.
     * <p>
     * Stops each worker tasks and worker threads.
     */
    void stop() throws InterruptedException {
        messages.stop();
        log.info("interrupting messageTakerThread");
        messageTakerThread.interrupt();
        log.info("joining messageTakerThread");
        messageTakerThread.join();
    }

    /**
     * (Blocking) Accept input from standard input.
     */
    void acceptInput() throws IOException {
        var reader = new BufferedReader(new InputStreamReader(System.in));
        String command;
        while ((command = reader.readLine()) != null) {
            if (command.equals("reset")) {
                log.info("Requested to reset offsets...");
                messages.reset();
            } else if (command.equals("rewind")) {
                log.info("Requested to rewind offsets...");
                messages.rewind(5);
            } else {
                log.info("command '{}' not recognized", command);
            }
        }
    }
}
