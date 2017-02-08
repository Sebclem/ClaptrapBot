package net.borken;


import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by seb65 on 19/10/2016.
 */
public interface Commande {
    boolean called(String[] args, MessageReceivedEvent event);
    void action(String[] args, MessageReceivedEvent event);
    String help(String[] args);
    void executed(boolean success, MessageReceivedEvent event);


}
