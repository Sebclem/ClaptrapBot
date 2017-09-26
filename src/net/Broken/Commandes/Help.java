package net.Broken.Commandes;

import net.Broken.Commande;
import net.Broken.MainBot;
import net.Broken.Outils.EmbedMessageUtils;
import net.Broken.Outils.PrivateMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.Map;

/**
 * Created by seb65 on 23/10/2016.
 */
public class Help implements Commande {
    Logger logger = LogManager.getLogger();
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if(args.length>=1)
        {
            String argsString = args[0];
            //System.out.println(argsString);
            if (MainBot.commandes.containsKey(argsString))
            {
                logger.info("Aide demmander pour la cmd "+argsString+" par "+event.getAuthor().getName());
                if(!event.isFromType(ChannelType.PRIVATE))
                    event.getTextChannel().sendMessage(EmbedMessageUtils.getHelp(argsString,MainBot.commandes.get(argsString).help(args))).queue();
                else{
                    PrivateMessage.send(event.getAuthor(), MainBot.commandes.get(argsString).help(args),logger);
                }


            }
            else
            {
                if(!event.isFromType(ChannelType.PRIVATE))
                    event.getTextChannel().sendMessage(EmbedMessageUtils.getUnknowCommand()).queue();
                else{
                    PrivateMessage.send(event.getAuthor(),EmbedMessageUtils.getUnknowCommand(),logger);
                }
                logger.info("Commande Inconnue!");
            }
        }
        else
        {
            StringBuilder txt= new StringBuilder();
            for (Map.Entry<String, Commande> e : MainBot.commandes.entrySet()) {
                txt.append("\n- ").append(e.getKey());
            }
            if(!event.isFromType(ChannelType.PRIVATE))

                event.getTextChannel().sendMessage(new EmbedBuilder().setTitle("Commandes envoyées par message privé").setColor(Color.green).build()).queue();
            PrivateMessage.send(event.getAuthor(),new EmbedBuilder().setTitle("Commandes du bot").setDescription(txt.toString()).setFooter("Utilise '//help <commande>' pour plus de détails.",null).setColor(Color.green).setThumbnail(event.getJDA().getSelfUser().getAvatarUrl()).build(),logger);





        }


    }

    @Override
    public String help(String[] args) {
        return null;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public boolean isPrivateUsable() {
        return true;
    }
}
