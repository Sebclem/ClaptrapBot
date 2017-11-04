package net.Broken.Commandes;

import net.Broken.Commande;
import net.Broken.MainBot;
import net.Broken.Outils.EmbedMessageUtils;
import net.Broken.Outils.MessageTimeOut;
import net.Broken.audio.AudioM;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.ArrayList;
import java.util.List;

public class Music implements Commande {
    AudioM audio;
    Logger logger = LogManager.getLogger();
    public Music() {
        audio = new AudioM();
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
                            new MessageTimeOut(messages, MainBot.messageTimeOut).run();
                        }
                    }
                    else{
                        Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Arguments manquant!")).complete();
                        List<Message> messages = new ArrayList<Message>(){{
                            add(message);
                            add(event.getMessage());
                        }};
                        new MessageTimeOut(messages, MainBot.messageTimeOut).run();
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
                    event.getGuild().getAudioManager().closeAudioConnection();
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
                        new MessageTimeOut(messages, MainBot.messageTimeOut).run();
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
                        new MessageTimeOut(messages, MainBot.messageTimeOut).run();
                    }
                    break;

                default:
                    Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Arguments inconu!")).complete();
                    List<Message> messages = new ArrayList<Message>(){{
                        add(message);
                        add(event.getMessage());
                    }};
                    new MessageTimeOut(messages, MainBot.messageTimeOut).run();
                    break;

            }
        }
        else{
            Message message = event.getTextChannel().sendMessage(EmbedMessageUtils.getMusicError("Arguments manquant!")).complete();
            List<Message> messages = new ArrayList<Message>(){{
                add(message);
                add(event.getMessage());
            }};
            new MessageTimeOut(messages, MainBot.messageTimeOut).run();
        }
    }

    @Override
    public String help(String[] args) {
        return "`//music play <url>`\n:arrow_right:\t*Let's dance! Deffinit le chat vocal à utiliser.*\n\n`//music pause`\n:arrow_right:\t*Mise en pause de la piste en cours.*\n\n`//music resume`\n:arrow_right:\t*Reprise de la lecture de la piste en cours.*\n\n`//music next`\n:arrow_right:\t*Change le piste en cours.*\n\n`//music stop`\n:arrow_right:\t*Arrête la piste en cours.*\n\n`//music info`\n:arrow_right:\t*Affiche les infos de la piste en cours.*\n\n`//music flush`\n:arrow_right:\t*Supprime la playlist en cours.*\n\n`//music list`\n:arrow_right:\t*Affiche la playlist en cours.*\n\n`//music add(Next) <url>`\n:arrow_right:\t*Ajoute l'url à la playlist en cour.*";

    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public boolean isPrivateUsable() {
        return false;
    }
}
