package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.MainBot;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.MessageTimeOut;
import net.Broken.Tools.PrivateMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
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
                MessageEmbed messageEmbed;
                try {
                    messageEmbed = EmbedMessageUtils.getHelp(argsString);
                } catch (FileNotFoundException e) {
                    try {
                        messageEmbed = EmbedMessageUtils.getHelp("Default");
                    } catch (FileNotFoundException e1) {
                        messageEmbed = EmbedMessageUtils.getInternalError();
                        logger.catching(e1);
                    }
                }
                if(!event.isFromType(ChannelType.PRIVATE)) {
                    Message rest = event.getTextChannel().sendMessage(messageEmbed).complete();
                    if(args.length<=1)
                    {
                        Message finalRest = rest;
                        List<Message> messages = new ArrayList<Message>(){{
                            add(finalRest);
                            add(event.getMessage());
                        }};
                        new MessageTimeOut(messages,MainBot.messageTimeOut).start();
                    }
                    else if(!args[1].toLowerCase().equals("true")){
                        Message finalRest1 = rest;
                        List<Message> messages = new ArrayList<Message>(){{
                            add(finalRest1);
                            add(event.getMessage());
                        }};
                        new MessageTimeOut(messages,MainBot.messageTimeOut).start();
                    }

                } else{
                    PrivateMessage.send(event.getAuthor(), messageEmbed,logger);
                }


            }
            else
            {
                if(!event.isFromType(ChannelType.PRIVATE)) {
                    Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getUnknowCommand()).complete();
                    List<Message> messages = new ArrayList<Message>(){{
                        add(rest);
                        add(event.getMessage());
                    }};
                    new MessageTimeOut(messages,MainBot.messageTimeOut).start();
                } else{
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

            if(!event.isFromType(ChannelType.PRIVATE)){
                Message rest = event.getTextChannel().sendMessage(new EmbedBuilder().setTitle("Command envoyées par message privé").setColor(Color.green).build()).complete();
                List<Message> messages = new ArrayList<Message>(){{
                   add(rest);
                   add(event.getMessage());
                }};
                new MessageTimeOut(messages,MainBot.messageTimeOut).start();
            }

            PrivateMessage.send(event.getAuthor(),new EmbedBuilder().setTitle("Command du bot").setDescription(txt.toString()).setFooter("Utilise '//help <commande>' pour plus de détails.",null).setColor(Color.green).setThumbnail(event.getJDA().getSelfUser().getAvatarUrl()).build(),logger);





        }


    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public boolean isPrivateUsable() {
        return true;
    }

    @Override
    public boolean isAdminCmd() {
        return false;
    }
}
