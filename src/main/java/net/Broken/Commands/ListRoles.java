package net.Broken.Commands;

import net.Broken.Commande;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class ListRoles implements Commande {
    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        List<Role> roles = event.getGuild().getRoles();
        EmbedBuilder messageB = new EmbedBuilder();
        for (Role role : roles){
            messageB.addField(role.getName(),"```id: " + role.getId() + "```",false);
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

    @Override
    public boolean isNSFW() {
        return false;
    }
}
