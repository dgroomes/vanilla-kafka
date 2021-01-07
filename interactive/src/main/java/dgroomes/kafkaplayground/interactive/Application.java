package dgroomes.kafkaplayground.interactive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Application {

    private static final String CMD_START = "start";
    private static final String CMD_STOP = "stop";
    private static final String CMD_RESET = "reset";
    private static final String CMD_REWIND = "rewind";
    private static final String CMD_CURRENT_OFFSETS = "current-offsets";

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private Messages messages;

    /**
     * (Non-blocking) Start the application
     */
    void start() {
        messages = new Messages(msg -> log.info("Got message: {}", msg));
        messages.start();
    }

    /**
     * Stop the application.
     */
    void stop() throws InterruptedException {
        messages.stop();
    }

    /**
     * (Blocking) Accept input from standard input.
     */
    void acceptInput() throws IOException, InterruptedException {
        printUsage();
        var reader = new BufferedReader(new InputStreamReader(System.in));
        String command;
        while ((command = reader.readLine()) != null) {
            switch (command) {
                case CMD_START:
                    messages.start();
                    break;
                case CMD_STOP:
                    messages.stop();
                    break;
                case CMD_RESET:
                    messages.reset();
                    break;
                case CMD_REWIND:
                    messages.rewind(5);
                    break;
                case CMD_CURRENT_OFFSETS:
                    messages.currentOffsets();
                    break;
                default:
                    if (command.isBlank()) {
                        log.warn("Nothing was entered. Please enter a supported command.");
                    } else {
                        log.warn("command '{}' not recognized", command);
                    }
                    printUsage();
                    break;
            }
        }
    }

    /**
     * Print usage information.
     */
    private void printUsage() {
        log.info("Enter '{}' to start consuming from Kafka", CMD_START);
        log.info("Enter '{}' to stop consuming from Kafka", CMD_STOP);
        log.info("Enter '{}' to reset Kafka offsets to the beginning", CMD_RESET);
        log.info("Enter '{}' to rewind Kafka offsets by a few spots", CMD_REWIND);
        log.info("Enter '{}' to get current Kafka offsets", CMD_CURRENT_OFFSETS);
    }
}
