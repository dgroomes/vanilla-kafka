package dgroomes.vanillakafka;

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

    private static Logger log = LoggerFactory.getLogger(Application.class);

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
        log.info("Enter '{}' to start consuming from Kafka", CMD_START);
        log.info("Enter '{}' to stop consuming from Kafka", CMD_STOP);
        log.info("Enter '{}' to reset Kafka offsets to the beginning", CMD_RESET);
        log.info("Enter '{}' to rewind Kafka offsets by a few spots", CMD_REWIND);
        log.info("Enter '{}' to get current Kafka offsets", CMD_CURRENT_OFFSETS);
        var reader = new BufferedReader(new InputStreamReader(System.in));
        String command;
        while ((command = reader.readLine()) != null) {
            if (command.equals(CMD_START)) {
                messages.start();
            } else if (command.equals(CMD_STOP)) {
                messages.stop();
            } else if (command.equals(CMD_RESET)) {
                messages.reset();
            } else if (command.equals(CMD_REWIND)) {
                messages.rewind(5);
            } else if (command.equals(CMD_CURRENT_OFFSETS)) {
                messages.currentOffsets();
            } else {
                log.info("command '{}' not recognized", command);
            }
        }
    }
}
