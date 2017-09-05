package net.Broken.Commandes;

import net.Broken.Commande;
import net.Broken.MainBot;
import net.Broken.Outils.PrivateMessage;
import net.Broken.Outils.UserSpamUtils;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Created by sebastien on 13/03/17.
 */
public class SpamInfo implements Commande{
    Logger logger = LogManager.getLogger();
    private String HELP="`//spaminfo <@utilisateur> `\n:arrow_right:\t*Affiche les infos relative aux punitions contre le spam de l'utilisateur mentionnée (de l'auteur si pas de mention)*";
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



        if(!MainBot.spamUtils.containsKey(user)){
            if(!event.isFromType(ChannelType.PRIVATE))
                event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n\n__**Spam info de "+user.getName()+":**__\n\n\t- Multiplicateur: `1`\n\t- En spam: `Non`").queue();
            else
                PrivateMessage.send(event.getAuthor(),"__**Spam info de "+user.getName()+":**__\n\n\t- Multiplicateur: `1`\n\t- En spam: `Non`",logger);
        }
        else{
            UserSpamUtils util = MainBot.spamUtils.get(user);
            if(!util.isOnSpam()){
                if(!event.isFromType(ChannelType.PRIVATE))
                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n\n__**Spam info de "+user.getName()+":**__\n\n\t- Multiplicateur: `"+util.getMultip()+"`\n\t- En spam: `Non`").queue();
                else
                    PrivateMessage.send(event.getAuthor(),"__**Spam info de "+user.getName()+":**__\n\n\t- Multiplicateur: `"+util.getMultip()+"`\n\t- En spam: `Non`",logger);
            }
            else{
                if(!event.isFromType(ChannelType.PRIVATE))
                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n\n__**Spam info de "+user.getName()+":**__\n\n\t- Multiplicateur: `"+util.getMultip()+"`\n\t- En spam: `Oui`\n\t- Temps restant: `"+formatSecond(util.getTimeLeft())+"`").queue();
                else
                    PrivateMessage.send(event.getAuthor(),"__**Spam info de "+user.getName()+":**__\n\n\t- Multiplicateur: `"+util.getMultip()+"`\n\t- En spam: `Oui`\n\t- Temps restant: `"+formatSecond(util.getTimeLeft())+"`",logger);
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
}
