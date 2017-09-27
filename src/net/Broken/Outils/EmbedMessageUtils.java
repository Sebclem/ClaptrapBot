package net.Broken.Outils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class EmbedMessageUtils {
    public static MessageEmbed getUnknowCommand() {
        return new EmbedBuilder().setTitle(":warning: Commande inconnue! :warning:").setDescription(":arrow_right: Utilisez `//help` pour voirs les commandes disponible.").setColor(Color.orange).build();

    }

    public static MessageEmbed getNoPrivate(){
        return new EmbedBuilder().setTitle(":warning: Commande non disponible en privé! :warning:").setDescription(":arrow_right: Utilisez `//help` pour voirs les commandes disponible.").setColor(Color.red).build();

    }

    public static MessageEmbed getSpamExtermine(Member autor, int multi) {
        return new EmbedBuilder().setTitle(":mute: Spam Hunter :mute:").setDescription(autor.getAsMention() + " détecté comme spammer !\n\nOn te revoit dans __**" + multi + "**__ min!").setImage("https://media.giphy.com/media/WVudyGEaizNeg/giphy.gif").setFooter("Spam info disponible via '//spaminfo' en privé", null).setColor(Color.orange).build();
    }

    public static MessageEmbed getSpamPardon(Member autor) {
        return new EmbedBuilder().setTitle(":mute: Spam Hunter :mute:").setDescription(autor.getAsMention() + " est de retour, fais gaffe!\nJe te surveille!").setImage("https://media.giphy.com/media/3o7TKwBctlv08kY08M/giphy.gif").setFooter("Spam info disponible via '//spaminfo' en privé", null).setColor(Color.orange).build();
    }

    public static MessageEmbed getHelp(String name, String helpMessage) {
        return new EmbedBuilder().setTitle(":question: " + name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase() + " :question: ").setDescription("\n" + helpMessage).setColor(Color.green).build();
    }

    public static MessageEmbed getMoveError(String message) {
        return new EmbedBuilder().setTitle(":warning: Move Error :warning: ").setDescription(message).setColor(Color.red).setFooter("'//help move' pour plus d'info ", null).build();
    }

    public static MessageEmbed getMoveOk(String message) {
        return new EmbedBuilder().setTitle(":arrow_left: Move :arrow_right:  ").setDescription(message).setColor(Color.green).setFooter("'//help move' pour plus d'info ", null).build();
    }

    public static MessageEmbed getSpamError(String message) {
        return new EmbedBuilder().setTitle(":warning: Spam Error :warning: ").setDescription(message).setColor(Color.red).setFooter("'//help spam' pour plus d'info ", null).build();
    }

    public static MessageEmbed getSpamError(String message, String sub) {
        return new EmbedBuilder().setTitle(":warning: Spam Error :warning: ").setDescription(message).setColor(Color.red).setFooter("'//help spam "+sub+"' pour plus d'info ", null).build();
    }

    public static MessageEmbed getSpamInfo(String message) {
        return new EmbedBuilder().setTitle(":hourglass:  Spam Info :hourglass:").setDescription(message).setColor(Color.green).setFooter("'//help spaminfo' pour plus d'info ", null).build();
    }
}