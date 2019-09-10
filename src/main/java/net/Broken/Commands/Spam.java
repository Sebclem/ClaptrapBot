package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.MainBot;
import net.Broken.Tools.AntiSpam;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.MessageTimeOut;
import net.Broken.Tools.UserSpamUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Spam admin command
 */

// TODO Rebuild this ...
public class Spam implements Commande {
    private Logger logger = LogManager.getLogger();

    @Override
    public void action(String[] args, MessageReceivedEvent event)
    {
        /****************************
         *   Verif argument         *
         ****************************/
        if(args.length>=1)
        {

            String commande = args[0];
            /****************************
             * on traite la commande    *
             ****************************/
            switch (commande) {

                case "pardon":
                    this.pardon(event,args);
                    break;

                case "extermine":
                    try {
                        this.extermine(event,args);
                    } catch (RateLimitedException e) {
                        e.printStackTrace();
                    }

                    break;

                case "reset":
                    try {
                        this.reset(event,args);
                    } catch (RateLimitedException e) {
                        e.printStackTrace();
                    }
                    break;



            }

        }
    }


    @Override
    public boolean isPrivateUsable() {
        return false;
    }

    @Override
    public boolean isAdminCmd() {
        return true;
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

    public void pardon(MessageReceivedEvent event, String[] args){

        Guild serveur = event.getGuild();
        /****************************
         * verif argument           *
         ****************************/
        if (args.length >= 1)
        {
            /****************************
             * On recupere l'utilisateur et le role cible
             ****************************/
            List<Member> userL = event.getMessage().getMentionedMembers();


            /****************************
             * verif utilisteur trouver *
             ****************************/
            if(userL.size()<1)
            {
                logger.error("User unknown.");
                Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError(":arrow_right: User not found. ","pardon")).complete();
                List<Message> messages = new ArrayList<Message>(){{
                    add(rest);
                    add(event.getMessage());
                }};
                new MessageTimeOut(messages,MainBot.messageTimeOut).start();
            }
            else {
                Member user = userL.get(0);
                logger.info("Attempt to forgive  " + user.getEffectiveName() + " by " + event.getMember().getEffectiveName());
            /****************************
                 * virif si en spammer    *
                 ****************************/
                if (MainBot.spamUtils.containsKey(user)) {
                    if (MainBot.spamUtils.get(user).isOnSpam()) {
                        MainBot.spamUtils.get(user).setOnSpam(false);
                    } else {
                        logger.warn("User is not in spam.");
                        Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError(":arrow_right: This user is not in spam.","pardon")).complete();
                        List<Message> messages = new ArrayList<Message>(){{
                            add(rest);
                            add(event.getMessage());
                        }};
                        new MessageTimeOut(messages,MainBot.messageTimeOut).start();
                    }


                } else {
                    logger.warn("User is not in spam.");
                    Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError(":arrow_right: This user is not in spam.","pardon")).complete();
                    List<Message> messages = new ArrayList<Message>(){{
                        add(rest);
                        add(event.getMessage());
                    }};
                    new MessageTimeOut(messages,MainBot.messageTimeOut).start();
                }
            }
        }
        else
        {
            logger.warn("Missing argument.");
            Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError("Missing argument!","pardon")).complete();
            List<Message> messages = new ArrayList<Message>(){{
                add(rest);
                add(event.getMessage());
            }};
            new MessageTimeOut(messages,MainBot.messageTimeOut).start();
        }


    }

    public void extermine(MessageReceivedEvent event, String[] args) throws RateLimitedException {
        /****************************
         * verif argument           *
         ****************************/
        if (args.length >= 3)
        {
            /****************************
             * On recupere l'utilisateur et le role cible
             ****************************/
            List<User> userL = event.getMessage().getMentionedUsers();


            /****************************
             * verif utilisteur trouver *
             ****************************/
            if(userL.size()<1)
            {
                logger.warn("Wrong mention (Spam).");
                Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError("Wrong mention. ","extermine")).complete();
            }
            else{


                Guild serveur = event.getGuild();
                Member user = serveur.getMember(userL.get(0));
                logger.info("Starting protocol 66 on "+user.getEffectiveName()+" by the command of "+event.getAuthor().getName());


                String multiStr =args[2];


                /****************************
                 * virif pas deja en spammer    *
                 ****************************/
                if(MainBot.spamUtils.containsKey(user))
                {
                    if(!MainBot.spamUtils.get(user).isOnSpam())
                    {
                        this.goSpam(user,multiStr,serveur,event);
                    }
                    else
                    {
                        logger.warn("User already in spam.");
                        Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError("User already in spam.","extermine")).complete();
                        List<Message> messages = new ArrayList<Message>(){{
                            add(rest);
                            add(event.getMessage());
                        }};
                        new MessageTimeOut(messages,MainBot.messageTimeOut).start();
                    }


                }
                else
                {
                    this.goSpam(user,multiStr,serveur,event);
                }

            }


        }
        else
        {
            logger.warn("Missing argument.");
            Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError("Missing argument!","extermine")).complete();
            List<Message> messages = new ArrayList<Message>(){{
                add(rest);
                add(event.getMessage());
            }};
            new MessageTimeOut(messages,MainBot.messageTimeOut).start();
        }
    }

    public void reset(MessageReceivedEvent event, String[] args) throws RateLimitedException {
        if(event!=null)
        {
            if(args.length>=2)
            {

                Guild serveur = event.getGuild();
                /****************************
                 * On recupere l'utilisateur et le role cible
                 ****************************/
                List<Member> userL = event.getMessage().getMentionedMembers();


                /****************************
                 * verif utilisteur trouver *
                 ****************************/
                if(userL.size()<1)
                {
                    logger.warn("User not found.");
                    Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError("User not found.","reset")).complete();
                    List<Message> messages = new ArrayList<Message>(){{
                        add(rest);
                        add(event.getMessage());
                    }};
                    new MessageTimeOut(messages,MainBot.messageTimeOut).start();

                }
                else {
                    Member user = userL.get(0);
                    logger.info("Attempt spam reset of " + user.getEffectiveName() + " by " + event.getMember().getEffectiveName());


                    /****************************
                     * verif utilisteur trouver *
                     ****************************/
                    if (MainBot.spamUtils.containsKey(user)) {
                        logger.info("Multiplictor reset for " + user.getEffectiveName() + " done.");
                        Message rest = event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n *The spam multiplicator of " + user.getEffectiveName() + " is now down to zero.*").complete();
                        List<Message> messages = new ArrayList<Message>(){{
                            add(rest);
                            add(event.getMessage());
                        }};
                        new MessageTimeOut(messages,MainBot.messageTimeOut).start();
                        MainBot.spamUtils.remove(user);

                    }

                }
            }
            else
            {
                logger.warn("Missing argument.");
                Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError("Missing argument!","reset")).complete();
                List<Message> messages = new ArrayList<Message>(){{
                    add(rest);
                    add(event.getMessage());
                }};
                new MessageTimeOut(messages,MainBot.messageTimeOut).start();
            }
        }
        else
        {
            if (args[0].equals("all"))
            {
                logger.info("Multiplicator reseted automaticly.");
                for (Member unUser: MainBot.spamUtils.keySet())
                {

                    MainBot.message_compteur.remove(unUser); //TODO resolve garbage collector error ????
                }
            }
        }



    }




    public void goSpam(Member user, String multiStr, Guild serveur, MessageReceivedEvent event)
    {
        if(Objects.equals(multiStr, "/"))
        {
            new AntiSpam().extermine(user,serveur,serveur.getManager(),true,event);

        }
        else
        {
            int multi = Integer.parseInt(multiStr);
            if(MainBot.spamUtils.containsKey(user))
            {
                MainBot.spamUtils.get(user).setMultip(multi);
            }
            else
            {
                MainBot.spamUtils.put(user,new UserSpamUtils(user,new ArrayList<>()));
                MainBot.spamUtils.get(user).setMultip(multi);
            }

            new AntiSpam().extermine(user,serveur,serveur.getManager(),false,event);

        }
    }
}
