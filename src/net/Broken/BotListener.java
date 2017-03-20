package net.Broken;

import net.Broken.Outils.AntiSpam;
import net.Broken.Outils.Moderateur;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Created by seb65 on 19/10/2016.
 */

public class BotListener extends ListenerAdapter {
    AntiSpam antispam=new AntiSpam();
    Moderateur modo = new Moderateur();
    Logger logger = LogManager.getLogger();


    @Override
    public void onReady(ReadyEvent event) {
        logger.info("Connection succees");
    }



    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //                                                      ----------------------Test pour eviter eco de commande-------------------------
        try
        {

            if (event.getMessage().getContent().startsWith("//") && event.getMessage().getAuthor().getId() != event.getJDA().getSelfUser().getId()) {
                //On a detecter que c'etait une commande
                //System.out.println(event.getMessage().getContent());

                MainBot.handleCommand(MainBot.parser.parse(event.getMessage().getContent(), event));



            }
            else if (event.getMessage().getAuthor().getId() != event.getJDA().getSelfUser().getId() && !event.getTextChannel().getName().equals("le_dongeon"))
            {
                //N'est pas une commande

                Guild serveur=event.getGuild();
                GuildManager guildManager = serveur.getManager();
                Member user = event.getMember();

                if(!event.isFromType(ChannelType.PRIVATE))
                {
                    // appel de la methode d'analyse de message de "Moderateur"
                    if(!event.getAuthor().getName().equals("Aethex") && event.getMessage().getContent().length()>0) {

                        if (modo.analyse(user, serveur, guildManager, event) == 1) {
                            antispam.extermine(user, serveur, guildManager,true, event);
                        }
                    }
                    else if(event.getMessage().getContent().length() == 0)
                        logger.error("Image detected, ignoring it.");
                }




            }
        }catch (Exception e)
        {
            if (e.getMessage()==null) {
                logger.error("NullPointerException");
            } else {
                logger.error(e.getMessage());
            }
        }

    }
}
