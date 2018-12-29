package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.DB.Repository.GuildPreferenceRepository;
import net.Broken.MainBot;
import net.Broken.SpringContext;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.MessageTimeOut;
import net.Broken.Tools.SettingsUtils;
import net.Broken.audio.AudioM;
import net.Broken.audio.NotConnectedException;
import net.Broken.audio.NullMusicManager;
import net.Broken.audio.Youtube.YoutubeTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.awt.*;
import java.io.IOException;
import java.util.List;

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
                .setDescription("You can do all the configuration on this web in the \"Bot Settings\" menu.\nhttps://"+MainBot.url).setColor(Color.green);
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

    @Override
    public boolean isNSFW() {
        return false;
    }
}
