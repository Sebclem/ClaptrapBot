package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.MainBot;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.MessageTimeOut;
import net.Broken.Tools.PrivateMessage;
import net.Broken.Tools.TableRenderer;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
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
 * Help Command.
 */
public class Help implements Commande {
    Logger logger = LogManager.getLogger();
    private int cellLenght = 25;

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if(args.length>=1)
        {
            String argsString = args[0];
            //System.out.println(argsString);
            if (MainBot.commandes.containsKey(argsString))
            {


                Commande cmdObj = MainBot.commandes.get(argsString);
                if(!cmdObj.isAdminCmd() || isAdmin(event))
                {
                    logger.debug("Help for "+argsString+" by "+event.getAuthor().getName());
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
                        event.getTextChannel().sendMessage(messageEmbed).queue();

                    } else{
                        PrivateMessage.send(event.getAuthor(), messageEmbed,logger);
                    }
                }
                else
                {
                    logger.info("Help wanted for admin command, Denied!");
                    if(!event.isFromType(ChannelType.PRIVATE)) {
                        Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getUnautorized()).complete();
                        List<Message> messages = new ArrayList<Message>(){{
                            add(rest);
                            add(event.getMessage());
                        }};
                        new MessageTimeOut(messages,MainBot.messageTimeOut).start();

                    } else{
                        PrivateMessage.send(event.getAuthor(), EmbedMessageUtils.getUnautorized(), logger);
                    }
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
                logger.debug("Unknown command!");
            }
        }
        else
        {
            TableRenderer table = new TableRenderer();
            table.setHeader("Command","PU");

            TableRenderer nsfwTable = new TableRenderer();
            nsfwTable.setHeader("NSFW Only\u00A0", "PU");
            List<String> noPu = new ArrayList<>();

            for (Map.Entry<String, Commande> e : MainBot.commandes.entrySet()) {
                if(!e.getValue().isAdminCmd() || isAdmin(event)){
                    if(e.getValue().isPrivateUsable())
                        table.addRow(e.getKey(), "XX");
                    else if(e.getValue().isNSFW())
                        nsfwTable.addRow(e.getKey(),"");
                    else
                        noPu.add(e.getKey());
                }


            }

            for(String key : noPu)
                table.addRow(key, "");

            String txt = table.build();
            txt += "\n\n";
            txt += nsfwTable.build();

            if(!event.isFromType(ChannelType.PRIVATE)){
                Message rest = event.getTextChannel().sendMessage(new EmbedBuilder().setTitle("Commands sent by private message").setColor(Color.green).build()).complete();
                new MessageTimeOut(MainBot.messageTimeOut, rest, event.getMessage()).start();
            }


            String role;
            if(isAdmin(event))
                role = "Admin";
            else
                role = "Non Admin";

            try {
                PrivateMessage.send(event.getAuthor(), EmbedMessageUtils.getHelpList(role, txt),logger);
            } catch (FileNotFoundException e) {
                logger.catching(e);
                PrivateMessage.send(event.getAuthor(), EmbedMessageUtils.getInternalError(), logger);

            }


        }


    }

    @Override
    public boolean isPrivateUsable() {
        return true;
    }

    @Override
    public boolean isAdminCmd() {
        return false;
    }

    @Override
    public boolean isNSFW() {
        return false;
    }


    public boolean isAdmin(MessageReceivedEvent event){

        if(event.isFromType(ChannelType.PRIVATE)){
            List<Guild> guilds = event.getAuthor().getMutualGuilds();
            for(Guild iterator : guilds){
                if(iterator.getMember(event.getAuthor()).hasPermission(Permission.ADMINISTRATOR)){
                    return true;
                }

            }

        }
        else
            return event.getMember().hasPermission(Permission.ADMINISTRATOR);
        return false;
    }
}
