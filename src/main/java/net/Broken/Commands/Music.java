package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.MainBot;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.MessageTimeOut;
import net.Broken.audio.AudioM;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.ArrayList;
import java.util.List;

/**
 * Music commands
 */

public class Music implements Commande {
    private Logger logger = LogManager.getLogger();


    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        AudioM audio = AudioM.getInstance(event.getGuild());
        if(args.length >= 1){
            switch (args[0]){
                case "play":
                    event.getTextChannel().sendTyping().queue();

                    if(args.length>=2){
                        if(event.getMember().getVoiceState().inVoiceChannel()){

                            VoiceChannel voiceChanel = event.getMember().getVoiceState().getChannel();
                            logger.info("Connecting to "+voiceChanel.getName()+"...");
                            if(args.length ==2){
                                audio.loadAndPlay(event,voiceChanel,args[1],30,false);
                            }
                            else if(args.length == 3){
                                try{
                                    int limit = Integer.parseInt(args[2]);
                                    audio.loadAndPlay(event,voiceChanel,args[1],limit,false);
                                }catch (NumberFormatException e){
                                    audio.loadAndPlay(event,voiceChanel,args[1],30,false);
                                }
                            }
                        }
                        else{
                            Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("You are not in a voice channel !")).complete();
                            List<Message> messages = new ArrayList<Message>(){{
                                add(message);
                                add(event.getMessage());
                            }};
                            new MessageTimeOut(messages, MainBot.messageTimeOut).start();
                        }
                    }
                    else{
                        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Missing argument!")).complete();
                        List<Message> messages = new ArrayList<Message>(){{
                            add(message);
                            add(event.getMessage());
                        }};
                        new MessageTimeOut(messages, MainBot.messageTimeOut).start();
                    }

                    break;
                case "pause":
                    audio.pause(event);
                    break;
                case "resume":
                    audio.resume(event);
                    break;
                case "next":
                    audio.skipTrack(event);
                    break;
                case "stop":
                    audio.stop(event);
                    break;
                case "info":
                    audio.info(event);
                    break;
                case "flush":
                    audio.flush(event);
                    break;
                case "list":
                    audio.list(event);
                    break;
                case "add":
                    event.getTextChannel().sendTyping().queue();
                    if(args.length ==2){
                        audio.add(event,args[1],30,false);
                    }
                    else if(args.length == 3){
                        try{
                            int limit = Integer.parseInt(args[2]);
                            audio.add(event,args[1],limit,false);
                        }catch (NumberFormatException e){
                            audio.add(event,args[1],30,false);
                        }
                    }
                    else{
                        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Missing argument!")).complete();
                        List<Message> messages = new ArrayList<Message>(){{
                            add(message);
                            add(event.getMessage());
                        }};
                        new MessageTimeOut(messages, MainBot.messageTimeOut).start();
                    }
                    break;

                case "addNext":
                    event.getTextChannel().sendTyping().queue();
                    if(args.length >=2){
                        audio.add(event,args[1],1,true);
                    }
                    else{
                        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Missing argument!")).complete();
                        List<Message> messages = new ArrayList<Message>(){{
                            add(message);
                            add(event.getMessage());
                        }};
                        new MessageTimeOut(messages, MainBot.messageTimeOut).start();
                    }
                    break;

                case "disconnect":
                    audio.stop();
                    List<Message> messages = new ArrayList<Message>(){{
                        add(event.getMessage());
                    }};
                    new MessageTimeOut(messages, 0).start();
                    break;

                default:
                    Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Unknown argument!")).complete();
                    List<Message> messagess = new ArrayList<Message>(){{
                        add(message);
                        add(event.getMessage());
                    }};
                    new MessageTimeOut(messagess, MainBot.messageTimeOut).start();
                    break;

            }
        }
        else{
            Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Missing argument!")).complete();
            List<Message> messages = new ArrayList<Message>(){{
                add(message);
                add(event.getMessage());
            }};
            new MessageTimeOut(messages, MainBot.messageTimeOut).start();
        }
    }

    @Override
    public boolean isPrivateUsable() {
        return false;
    }

    @Override
    public boolean isAdminCmd() {
        return false;
    }

    /**
     * Determines if the command is usable only by bot level admin user
     *
     * @return boolean
     */
    @Override
    public boolean isBotAdminCmd() {
        return false;
    }

    @Override
    public boolean isNSFW() {
        return false;
    }
}
