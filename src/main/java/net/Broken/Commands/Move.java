package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.MainBot;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.MessageTimeOut;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.managers.GuildManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.ArrayList;
import java.util.List;


/**
 * Move Command
 */
public class Move implements Commande {

    Logger logger = LogManager.getLogger();
    private String HELP="`//move <@user> <@Role>`\n:arrow_right:\t*Move a user to a specified role.*";
    public List<Role> saveRoleUser;
    public Member user;
    public Guild serveur;
    public GuildManager serveurManager;
    public GuildController guildController;

    /** Perform a move (Reset is role and add target(s) role(s)
     *
     * @param user User to move
     * @param cible Complete list of new role
     * @param reset
     * @param serveur Guild
     * @param serveurManager GuildManager
     * @return success
     */
    public boolean exc(Member user, List<Role> cible , boolean reset, Guild serveur, GuildManager serveurManager) throws HierarchyException
    {
        MainBot.roleFlag = true;
        guildController = new GuildController(serveur);
        boolean erreur = false;
        List<Role> allRoll = serveur.getRoles();



        //On recupere les roles de l'utilisateur

        List<Role> roleUserList = user.getRoles();

        logger.info("Roles de " + user.getEffectiveName() + ":");

        //On les save
        saveRoleUser = roleUserList;

        //Ajout du role cible

        //On transforme la le role a ajouter en une liste pour pouvoir l'utiliser dans modifyMemberRoles


        //on fait ensuite les modif
        guildController.modifyMemberRoles(user,cible).complete();

        logger.info("Role " + cible + " attribuer a " + user.getEffectiveName());

        this.user=user;
        this.serveur=serveur;
        this.serveurManager=serveurManager;
        return erreur;
    }

    /** Command handler
     *
     * @param args
     * @param event
     */
    public void action(String[] args, MessageReceivedEvent event)
    {
        if(!event.isFromType(ChannelType.PRIVATE))
        {
            if(args.length>=2)
            {
                serveur=event.getGuild();
                List<User> userL = event.getMessage().getMentionedUsers();
                List<Role> roleL = event.getMessage().getMentionedRoles();

                if(userL.size()<1 ||roleL.size()<1)
                {
                    logger.warn("Mention Incorect.");
                    Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getMoveError("Error, please check if the user and/or the role are existing.")).complete();
                    List<Message> messages = new ArrayList<Message>(){{
                        add(rest);
                        add(event.getMessage());
                    }};
                    new MessageTimeOut(messages,MainBot.messageTimeOut).start();
                }
                else
                {
                    user = serveur.getMember(userL.get(0));
                    serveur=event.getGuild();
                    logger.info("Tentative de déplacement de "+user.getEffectiveName()+" vers "+roleL+" par l'utilisateur "+event.getAuthor().getName());

                    logger.info("Autorisation suffisante, deplacement autorisé");
                    logger.debug("Utilisateur trouvée");
                    try {
                        boolean erreur=this.exc(user,roleL,true,serveur,serveur.getManager());
                        if(erreur)
                        {
                            Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getMoveError("Check the targeted role. ")).complete();
                            List<Message> messages = new ArrayList<Message>(){{
                                add(rest);
                                add(event.getMessage());
                            }};
                            new MessageTimeOut(messages,MainBot.messageTimeOut).start();
                        }
                        else
                        {
                            StringBuilder roleStr = new StringBuilder("");
                            boolean first = true;
                            for( Role role : roleL)
                            {
                                if (!first) {
                                    roleStr.append(", ");

                                }
                                else
                                    first = false;
                                roleStr.append("__");
                                roleStr.append(role.getName());
                                roleStr.append("__");
                            }


                            Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getMoveOk("User "+user.getEffectiveName()+" as been successfully moved to "+roleStr.toString())).complete();
                            List<Message> messages = new ArrayList<Message>(){{
                                add(rest);
                                add(event.getMessage());
                            }};
                            new MessageTimeOut(messages,MainBot.messageTimeOut).start();
                        }
                    }catch (HierarchyException e){
                        Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getMoveError("You cannot move "+user.getRoles().get(0).getAsMention())).complete();
                        List<Message> messages = new ArrayList<Message>(){{
                            add(rest);
                            add(event.getMessage());
                        }};
                        new MessageTimeOut(messages,MainBot.messageTimeOut).start();
                        logger.error("Hierarchy error, please move bot's role on top!");
                    }



                }

            }
            else
            {
                logger.warn("Arguments maquants.");
                Message rest = event.getTextChannel().sendMessage(EmbedMessageUtils.getMoveError("Missing argument.")).complete();
                List<Message> messages = new ArrayList<Message>(){{
                    add(rest);
                    add(event.getMessage());
                }};
                new MessageTimeOut(messages,MainBot.messageTimeOut).start();
            }
        }
        else
            event.getPrivateChannel().sendMessage(EmbedMessageUtils.getNoPrivate());








    }

    @Override
    public boolean isPrivateUsable() {
        return false;
    }

    @Override
    public boolean isAdminCmd() {
        return true;
    }

    @Override
    public boolean isNSFW() {
        return false;
    }
}
