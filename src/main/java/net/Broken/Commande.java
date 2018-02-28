package net.Broken;


import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Interface that define command structure.
 */
public interface Commande {
    /**
     * Main action of command
     * @param args Command args.
     * @param event Command MessageReceivedEvent
     */
    void action(String[] args, MessageReceivedEvent event);

    /**
     * Determines if the command is usable whit private message
     * @return boolean
     */
    boolean isPrivateUsable();

    /**
     * Determines if the command is usable only by admin user
     * @return boolean
     */
    boolean isAdminCmd();

    /**
     * Determines if the command is only usable on NSFW channels
     * @return boolean
     */
    boolean isNSFW();


}
