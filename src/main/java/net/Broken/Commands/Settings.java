package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.DB.Repository.GuildPreferenceRepository;
import net.Broken.MainBot;
import net.Broken.SpringContext;
import net.Broken.Tools.EmbedMessageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.awt.*;

public class Settings implements Commande {

    private Logger logger = LogManager.getLogger();

    private GuildPreferenceRepository guildPreferenceRepository;

    public Settings() {

        ApplicationContext context = SpringContext.getAppContext();
        guildPreferenceRepository = (GuildPreferenceRepository) context.getBean("guildPreferenceRepository");
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Settings")
                .setDescription("You can do all the configuration on the web page in the \"Bot Settings\" menu.\nhttps://" + MainBot.url).setColor(Color.green);
        event.getTextChannel().sendMessage(EmbedMessageUtils.buildStandar(builder)).queue();


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
}
