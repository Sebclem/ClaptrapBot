package net.Broken.Outils;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class MessageTimeOut extends Thread{
    List<Message> messages;
    int second;
    Logger logger = LogManager.getLogger();
    public MessageTimeOut(List<Message> messages, int second) {
        this.messages = messages;
        this.second = second;
    }

    @Override
    public void run() {
        logger.debug("Timer for message deletion stated...");
        for(int i=0; i<second; i++){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(Message aMessage: messages)
        {
            try {
                logger.debug(aMessage.getContent());
                aMessage.delete().queue();
            }catch (ErrorResponseException e){
                logger.warn("Unknown Message");
            }

        }

    }
}
