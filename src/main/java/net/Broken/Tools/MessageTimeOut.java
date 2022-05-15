package net.Broken.Tools;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Auto dell message util
 */
public class MessageTimeOut extends Thread {
    List<Message> messages;
    int second;
    Logger logger = LogManager.getLogger();

    public MessageTimeOut(List<Message> messages, int second) {
        this.messages = messages;
        this.second = second;
    }

    public MessageTimeOut(int second, Message... messages) {
        this.messages = Arrays.asList(messages);
        this.second = second;
    }

    @Override
    public void run() {
        logger.debug("Timer for message deletion stated...");
        for (int i = 0; i < second; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.debug("Time out! Deleting message!");
        for (Message aMessage : messages) {
            try {
                aMessage.delete().queue();
            } catch (ErrorResponseException e) {
                logger.warn("Unknown Message");
            }

        }

    }
}
