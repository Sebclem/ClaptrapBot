package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.MainBot;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.MessageTimeOut;
import net.Broken.Tools.PrivateMessage;
import net.Broken.Tools.UserSpamUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by sebastien on 13/03/17.
 */
public class SpamInfo implements Commande{
    private HashMap<User,MessageUpdater> threadHashMap = new HashMap<>();

    Logger logger = LogManager.getLogger();
    private String HELP="`//spaminfo <@utilisateur> `\n:arrow_right:\t*Affiche les infos relatives aux punitions contre le spam de l'utilisateur mentionnée (de l'auteur si pas de mention)*";
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        User user;
        if(event.getMessage().getMentionedUsers().size() == 0){
            user = event.getAuthor();
        }
        else {
            user = event.getMessage().getMentionedUsers().get(0);
        }


        Message message = null;
        if(!MainBot.spamUtils.containsKey(user)){
            if(!event.isFromType(ChannelType.PRIVATE))
                message = event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamInfo(user.getName()+":\n\t- Multiplicateur: `1`\n\t- En spam: `Non`")).complete();
            else
                PrivateMessage.send(event.getAuthor(),EmbedMessageUtils.getSpamInfo(user.getName()+":\n\t- Multiplicateur: `1`\n\t- En spam: `Non`"),logger);
        }
        else{
            UserSpamUtils util = MainBot.spamUtils.get(user);
            if(!util.isOnSpam()){
                if(!event.isFromType(ChannelType.PRIVATE))
                    message = event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamInfo(user.getName()+"\n\t- Multiplicateur: `"+util.getMultip()+"`\n\t- En spam: `Non`")).complete();
                else
                    PrivateMessage.send(event.getAuthor(),EmbedMessageUtils.getSpamInfo(user.getName()+":\n\t- Multiplicateur: `"+util.getMultip()+"`\n\t- En spam: `Non`"),logger);
            }
            else{
                if(!event.isFromType(ChannelType.PRIVATE))
                    message = event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamInfo(user.getName()+":\n\t- Multiplicateur: `"+util.getMultip()+"`\n\t- En spam: `Oui`\n\t- Temps restant: `"+formatSecond(util.getTimeLeft())+"`")).complete();
                else
                    message = PrivateMessage.send(event.getAuthor(),EmbedMessageUtils.getSpamInfo(user.getName()+"\n\t- Multiplicateur: `"+util.getMultip()+"`\n\t- En spam: `Oui`\n\t- Temps restant: `"+formatSecond(util.getTimeLeft())+"`"),logger);
                }
        }
        if(message != null){
            if(threadHashMap.containsKey(user)){
                MessageUpdater startedThread = threadHashMap.get(user);
                if(!message.getChannelType().equals(startedThread.message.getChannelType())){
                    MessageUpdater newThread = new MessageUpdater(message,event.getMessage(),MainBot.spamUtils.get(user),user);
                    threadHashMap.put(user,newThread);
                    newThread.start();
                }
                else
                {
                   threadHashMap.get(user).stop = true;
                    MessageUpdater newThread = new MessageUpdater(message,event.getMessage(),MainBot.spamUtils.get(user),user);
                    threadHashMap.replace(user, newThread);
                    newThread.start();
                }
            }
            else
            {
                MessageUpdater newThread = new MessageUpdater(message,event.getMessage(),MainBot.spamUtils.get(user),user);
                threadHashMap.put(user, newThread);
                newThread.start();
            }
        }




    }

    @Override
    public String help(String[] args) {
        return HELP;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public boolean isPrivateUsable() {
        return true;
    }

    public String formatSecond(int second){
        long days = TimeUnit.SECONDS.toDays(second);
        second -= TimeUnit.DAYS.toSeconds(days);

        long hours = TimeUnit.SECONDS.toHours(second);
        second -= TimeUnit.HOURS.toSeconds(hours);


        long minutes = TimeUnit.SECONDS.toMinutes(second);
        second -= TimeUnit.MINUTES.toSeconds(minutes);

        long seconds = TimeUnit.SECONDS.toSeconds(second);

        logger.debug(""+days+":"+hours+":"+minutes+":"+seconds);
        String finalText = "";
        if(days!=0)
            finalText += days+" jour(s) ";
        if(hours!=0)
            finalText += hours+"h ";
        if(minutes!=0)
            finalText += minutes+"min ";
        finalText += seconds+"s";

        return finalText;

    }


    private class MessageUpdater extends Thread{
        public Message message;
        public Message command;
        public UserSpamUtils util;
        public boolean stop;
        private int oldValue;
        private User user;

        public MessageUpdater(Message message,Message command, UserSpamUtils util,User user) {
            this.message = message;
            this.util = util;
            this.user = user;
            this.command = command;

        }

        @Override
        public void run() {
            logger.debug("Start "+user.getName()+" theard!");
            if(util != null){
                oldValue = util.getTimeLeft();
                while (util.getTimeLeft()!=0 && !stop && util.isOnSpam()){
                    try {
                        Thread.sleep(500);
                        if(util.getTimeLeft()%5 == 0 && oldValue - util.getTimeLeft() >= 5){
                            message.editMessage(EmbedMessageUtils.getSpamInfo(user.getName()+":\n\t- Multiplicateur: `"+util.getMultip()+"`\n\t- En spam: `Oui`\n\t- Temps restant: `"+formatSecond(util.getTimeLeft())+"`")).complete();
                            oldValue = util.getTimeLeft();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                logger.debug("Kill "+user.getName()+" theard!");
                if(stop)
                    message.editMessage(new EmbedBuilder().setColor(Color.RED).setTitle("Aborted").build()).complete();
                else
                    message.editMessage(EmbedMessageUtils.getSpamInfo(user.getName()+"\n\t- Multiplicateur: `"+util.getMultip()+"`\n\t- En spam: `Non`")).complete();

            }
            logger.debug("Timer for message deletion of "+user.getName()+" stated...");
            threadHashMap.remove(user);
            List<Message> messages = new ArrayList<>();
            messages.add(command);
            messages.add(message);
            new MessageTimeOut(messages,15).start();





        }
    }
}
