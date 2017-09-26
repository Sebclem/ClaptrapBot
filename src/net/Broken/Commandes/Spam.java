package net.Broken.Commandes;

import net.Broken.Commande;
import net.Broken.MainBot;
import net.Broken.Outils.AntiSpam;
import net.Broken.Outils.EmbedMessageUtils;
import net.Broken.Outils.UserSpamUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.List;
import java.util.Objects;


/**
 * Created by seb65 on 27/10/2016.
 */
public class Spam implements Commande {
    Logger logger = LogManager.getLogger();
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

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
    public String help(String[] args)
    {
        return "`//spam extermine <@utilisateur> <multiplicateur>`\n:arrow_right:\t*Punir un spammeur.*\n\n`//spam pardon <@utilisateur>`\n:arrow_right:\t*Annuler la punition d'un utilisateur.*\n\n`//spam reset <@utilisateur>`\n:arrow_right:\t*RAZ du multiplicateur d'un utilisateur.*";
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event)
    {

    }

    @Override
    public boolean isPrivateUsable() {
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
            List<User> userL = event.getMessage().getMentionedUsers();


            /****************************
             * verif utilisteur trouver *
             ****************************/
            if(userL.size()<1)
            {
                logger.error("Utilisateur introuvable.");
                event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError(":arrow_right: Utilisateur introuvable. ","pardon")).queue();
            }
            else {
                Member user = serveur.getMember(userL.get(0));
                logger.info("Tentative de pardon de " + user.getEffectiveName() + " par l'utilisateur " + event.getMember().getEffectiveName());
                /****************************
                 * c'est un big dady    *
                 ****************************/

                if (event.getMember().getRoles().get(0)==serveur.getRolesByName("Big_Daddy",false).get(0)) {
                    logger.info("Autorisation suffisante, pardon autorisé");

                    /****************************
                     * virif si en spammer    *
                     ****************************/
                    if (MainBot.spamUtils.containsKey(user.getUser())) {
                        if (MainBot.spamUtils.get(user.getUser()).isOnSpam()) {
                            MainBot.spamUtils.get(user.getUser()).setOnSpam(false);
                        } else {
                            logger.warn("Utilisateur pas en spam.");
                            event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError(":arrow_right: Utilisateur non spammeur.","pardon")).queue();
                        }


                    } else {
                        logger.warn("Utilisateur pas en spam.");
                        event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError(":arrow_right: Utilisateur non spammeur.","pardon")).queue();
                    }

                } else {
                    logger.warn("Autorisation insuffisante, pardon refusé");
                    event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError("Vous n'avez pas l'autorisation de faire ça!")).queue();
                }
            }
        }
        else
        {
            logger.warn("Argument manquant.");
            event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError("Argument manquant!","pardon")).queue();
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
                logger.warn("Mentionnement Incorect (Spam).");
                event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError("Utilisateur mal mentioner. ","extermine")).queue();
            }
            else{


                Guild serveur = event.getGuild();
                Member user = serveur.getMember(userL.get(0));
                logger.info("Tentative d'extermination de "+user.getEffectiveName()+" par l'utilisateur "+event.getAuthor().getName());
                /****************************
                 * c'est un big dady    *
                 ****************************/

                if(event.getMember().getRoles().get(0)==serveur.getRolesByName("Big_Daddy",false).get(0))
                {
                    logger.info("Autorisation suffisante, extermination autorisé");
                    String multiStr =args[2];


                    /****************************
                     * virif pas deja en spammer    *
                     ****************************/
                    if(MainBot.spamUtils.containsKey(user.getUser()))
                    {
                        if(!MainBot.spamUtils.get(user.getUser()).isOnSpam())
                        {
                            this.goSpam(user,multiStr,serveur,event);
                        }
                        else
                        {
                            logger.warn("Utilisateur deja en spam.");
                            event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError("Utilisateur déjà spammeur.","extermine")).queue();
                        }


                    }
                    else
                    {
                        this.goSpam(user,multiStr,serveur,event);
                    }

                }
                else
                {
                    logger.warn("Autorisation insuffisante, extermination refusé");
                    event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError("Vous n'avez pas l'autorisation de faire ça!")).queue();
                }

            }


        }
        else
        {
            logger.warn("Argument manquant.");
            event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError("Argument manquant!","extermine")).queue();
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
                List<User> userL = event.getMessage().getMentionedUsers();


                /****************************
                 * verif utilisteur trouver *
                 ****************************/
                if(userL.size()<1)
                {
                    logger.warn("Utilisateur introuvable.");
                    event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError("Utilisateur introuvable.","reset")).queue();

                }
                else {
                    Member user = serveur.getMember(userL.get(0));
                    logger.info("Tentative de reset de " + user.getEffectiveName() + " par l'utilisateur " + event.getMember().getEffectiveName());
                    /****************************
                     * c'est un big dady    *
                     ****************************/

                    if ( event.getMember().getRoles().get(0) == serveur.getRolesByName("Big_Daddy", false).get(0)) {
                        logger.info("Autorisation suffisante, pardon autorisé");
                        /****************************
                         * verif utilisteur trouver *
                         ****************************/
                        if (MainBot.spamUtils.containsKey(user.getUser())) {
                            logger.info("Reset du multiplicateur de " + user.getEffectiveName() + " réussi");
                            event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + "\n *Le multiplcicateur de " + user.getEffectiveName() + " a été remit a zéro.*").queue();
                            MainBot.spamUtils.remove(user.getUser());

                        }
                    } else {
                        logger.warn("Autorisation insuffisante, reset refusé");
                        event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError("Vous n'avez pas l'autorisation de faire ca!")).queue();

                    }
                }
            }
            else
            {
                logger.warn("Argument manquant.");
                event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamError("Argument manquant!","reset")).queue();
            }
        }
        else
        {
            if (args[0].equals("all"))
            {
                logger.info("Reset automatique des multiplicateur.");
                for (User unUser: MainBot.spamUtils.keySet() )         //=for(int i=0; i<saveRoleUser.size(); i++)
                {
                    MainBot.message_compteur.remove(unUser);
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
                MainBot.spamUtils.put(user.getUser(),new UserSpamUtils(user));
                MainBot.spamUtils.get(user.getUser()).setMultip(multi);
            }

            new AntiSpam().extermine(user,serveur,serveur.getManager(),false,event);

        }
    }
}
