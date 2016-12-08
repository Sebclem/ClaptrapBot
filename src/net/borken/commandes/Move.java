package net.borken.commandes;

import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.xpath.internal.operations.Bool;
import net.borken.Commande;
import net.borken.Outils.Entete;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.managers.GuildManager;
import org.apache.commons.lang3.ObjectUtils;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by seb65 on 20/10/2016.
 */

/**
 *
 */
public class Move implements Commande {


    private String HELP="`//move <@utilisateur> <@rôleCible>`\n:arrow_right:\t*Deplacement d'un utilisateur vers un rôle cible, attention à bien faire des montions.*";
    public List<Role> saveRoleUser;
    public Member user;
    public Guild serveur;
    public GuildManager serveurManager;
    public GuildController guildController;
    public static Entete entete=new Entete();

    /**
     *
     * @param user
     * @param cible
     * @param reset
     * @param serveur
     * @param serveurManager
     * @return
     */
    public boolean exc(Member user, Role cible , boolean reset, Guild serveur, GuildManager serveurManager)
    {
        guildController = new GuildController(serveur);
        boolean erreur = false;
        List<Role> allRoll = serveur.getRoles();
        System.out.println();


        //On recupere les roles de l'utilisateur

        List<Role> roleUserList = user.getRoles();
        System.out.println();
        System.out.println(entete.get("Info","MOVER")+"Roles de " + user.getEffectiveName() + ":");

        //On les save
        saveRoleUser = roleUserList;

        //Ajout du role cible

        //On transforme la le role a ajouter en une liste pour pouvoir l'utiliser dans modifyMemberRoles
        Collection<Role> temp = new ArrayList<>();
        temp.add(cible);

        //on fait ensuite les modif
        guildController.modifyMemberRoles(user,temp,saveRoleUser).queue();

        System.out.println(entete.get("Info","MOVER")+"Role " + cible + " attribuer a " + user.getEffectiveName());

        this.user=user;
        this.serveur=serveur;
        this.serveurManager=serveurManager;
        return erreur;
    }

    @Override

    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    /**
     *
     * @param args
     * @param event
     */
    public void action(String[] args, MessageReceivedEvent event)
    {
        if(args.length>=2)
        {
            serveur=event.getGuild();
            List<User> userL = event.getMessage().getMentionedUsers();
            List<Role> roleL = event.getMessage().getMentionedRoles();

            if(userL.size()<1 ||roleL.size()<1)
            {
                System.out.println(entete.get("ERREUR","MOVE")+"Mentionnement Incorect.");
                event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:warning: **__Erreur de déplacement__** :warning:\n:arrow_right: Erreur, Utilisateur ou Role mal mentioner. `//help move` pour plus d'info ").queue();
            }
            else
            {
                System.out.println(userL.get(0));
                user = serveur.getMember(userL.get(0));
                Role roleCible = roleL.get(0);
                serveur=event.getGuild();
                System.out.println(entete.get("Info","CMD")+"Tentative de déplacement de "+user.getEffectiveName()+" vers "+roleCible.getName()+" par l'utilisateur "+event.getAuthor().getName());
                if(event.getMember().getRoles().contains(serveur.getRolesByName("Big_Daddy",false).get(0)))
                {

                    System.out.println(entete.get("Info","MOVE")+"Autorisation suffisante, deplacement autorisé");
                    System.out.println(entete.get("Info","MOVE")+"Utilisateur trouvée");
                    boolean erreur=this.exc(user,roleCible,true,serveur,serveur.getManager());
                    if(erreur)
                    {
                        event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:warning: **__Erreur de déplacement.__** :warning:\n:arrow_right: Verifier le rôle cible. ").queue();
                    }
                    else
                    {
                        event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:ok: **Déplacement de "+user.getEffectiveName()+" vers "+roleCible.getName()+" reussi.** :ok:").queue();
                    }
                }
                else
                {
                    System.out.println(entete.get("Info","MOVE")+"Autorisation insuffisante, deplacement refusé");
                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:warning: **__Vous n'avez pas l'autorisation de faire ca!__**:warning: ").queue();

                }
            }

        }
        else
        {
            System.out.println(entete.get("ERREUR","MOVE")+"Arguments maquant.");
            event.getTextChannel().sendMessage(event.getAuthor().getAsMention()+"\n:warning: **__Erreur de déplacement__** :warning:\n:arrow_right: Arguments manquant. `//help move` pour plus d'info ").queue();

        }
        



            


    }

    /**
     *
     * @param args
     * @return
     */
    @Override
    public String help(String[] args) {
        return HELP;
    }

    /**
     *
     * @param success
     * @param event
     */
    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

}
