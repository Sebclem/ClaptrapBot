package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.Tools.EmbedMessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class GetChannels implements Commande{
    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        event.getGuild().getTextChannels();
        EmbedBuilder eb = new EmbedBuilder().setColor(Color.green);
        for(TextChannel channel : event.getGuild().getTextChannels())
            eb.addField(channel.getName(),"ID: " + channel.getId(), false);

        event.getTextChannel().sendMessage(EmbedMessageUtils.buildStandar(eb)).complete();
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
