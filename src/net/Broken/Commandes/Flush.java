package net.Broken.Commandes;

import net.Broken.Commande;
import net.Broken.Outils.EmbedMessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


public class Flush implements Commande{
    Logger logger = LogManager.getLogger();
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if(args.length<1){
            event.getTextChannel().sendMessage(EmbedMessageUtils.getFlushError("Argument manquant!")).queue();

        }
        else
        {
            if(event.getMember().hasPermission(Permission.ADMINISTRATOR)){
                try {
                    int limit = Integer.parseInt(args[0]);
                    MessageChannel chanel = event.getChannel();
                    MessageHistory history = chanel.getHistoryAround(chanel.getLatestMessageIdLong(), 100).complete();
                    logger.debug(history.getRetrievedHistory().size());
                    List<Message> retrieved = history.getRetrievedHistory();
                    if(limit > retrieved.size())
                        limit = retrieved.size()-1;
                    for(int i = 0; i<limit+1; i++){
                        logger.debug(retrieved.get(i).getContent());
                        retrieved.get(i).delete().queue();
                    }
                }catch (NumberFormatException e){
                    event.getTextChannel().sendMessage(EmbedMessageUtils.getFlushError("L'argument n'est pas valide!")).queue();
                }
            }
            else
            {
                event.getTextChannel().sendMessage(EmbedMessageUtils.getFlushError("Vous n'avez pas l'autorisation de faire ça!")).queue();
            }



        }
    }

    @Override
    public String help(String[] args) {
        return "`//flush <nbr>`\n:arrow_right:\t*Efface les n derniers messages (Max = 100)*";
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public boolean isPrivateUsable() {
        return false;
    }
}
