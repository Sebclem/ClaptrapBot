package net.Broken.Commands;

import net.Broken.Commande;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class ListRoles implements Commande {
    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        List<Role> roles = event.getGuild().getRoles();
        EmbedBuilder messageB = new EmbedBuilder();
        for (Role role : roles) {
            messageB.addField(role.getName(), "```id: " + role.getId() + "```", false);
        }

        messageB.setColor(Color.green);
        event.getTextChannel().sendMessage(messageB.build()).complete();
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
