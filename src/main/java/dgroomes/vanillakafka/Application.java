package dgroomes.vanillakafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Application {

    public static final String CMD_RESET = "reset";
    public static final String CMD_REWIND = "rewind";

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
    void acceptInput() throws IOException, InterruptedException {
        log.info("Enter '{}' to reset Kafka offsets to the beginning", CMD_RESET);
        log.info("Enter '{}' to rewind Kafka offsets by a few spots", CMD_REWIND);
        var reader = new BufferedReader(new InputStreamReader(System.in));
        String command;
        while ((command = reader.readLine()) != null) {
            if (command.equals(CMD_RESET)) {
                messages.reset();
            } else if (command.equals(CMD_REWIND)) {
                messages.rewind(5);
            } else {
                log.info("command '{}' not recognized", command);
            }
        }
    }
}
