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

public class Music implements Commande {
    public AudioM audio;
    Logger logger = LogManager.getLogger();
    public Music() {
        audio = new AudioM(MainBot.jda.getGuilds().get(0));
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {

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
                            Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Non connecté sur un chanel vocal!")).complete();
                            List<Message> messages = new ArrayList<Message>(){{
                                add(message);
                                add(event.getMessage());
                            }};
                            new MessageTimeOut(messages, MainBot.messageTimeOut).start();
                        }
                    }
                    else{
                        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Arguments manquant!")).complete();
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
                        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Arguments manquant!")).complete();
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
                        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Arguments manquant!")).complete();
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
                    Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Arguments inconu!")).complete();
                    List<Message> messagess = new ArrayList<Message>(){{
                        add(message);
                        add(event.getMessage());
                    }};
                    new MessageTimeOut(messagess, MainBot.messageTimeOut).start();
                    break;

            }
        }
        else{
            Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Arguments manquant!")).complete();
            List<Message> messages = new ArrayList<Message>(){{
                add(message);
                add(event.getMessage());
            }};
            new MessageTimeOut(messages, MainBot.messageTimeOut).start();
        }
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public boolean isPrivateUsable() {
        return false;
    }

    @Override
    public boolean isAdminCmd() {
        return false;
    }

    @Override
    public boolean isNSFW() {
        return false;
    }

    public AudioM getAudioManager(){
        return audio;
    }
}
