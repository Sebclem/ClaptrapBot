package net.Broken.Tools;

import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.MainBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Pre build Message Embed
 */
public class EmbedMessageUtils {
    public static MessageEmbed getUnknowCommand() {
        return new EmbedBuilder().setTitle(":warning: Commande inconnue! :warning:").setDescription(":arrow_right: Utilisez `//help` pour voirs les commandes disponible.").setColor(Color.orange).setFooter("bot.seb6596.ovh", MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).build();

    }

    public static EmbedBuilder getError(String message) {
        EmbedBuilder temp = new EmbedBuilder().setTitle(":warning: Error! :warning:").setColor(Color.red).setDescription(message);
        return temp;


    }

    public static MessageEmbed getNoPrivate(){
        EmbedBuilder temp = new EmbedBuilder().setTitle(":warning: Commande non disponible en privé! :warning:").setDescription(":arrow_right: Utilisez `//help` pour voirs les commandes disponible.").setColor(Color.red);
        return buildStandar(temp);
    }

    public static MessageEmbed getMusicError(String message){
        return new EmbedBuilder().setTitle(":warning: Musique Error :warning:").setDescription(":arrow_right: "+message).setFooter("'//help music' pour plus d'info",MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).setColor(Color.red).setFooter("bot.seb6596.ovh", MainBot.jda.getSelfUser().getAvatarUrl()).build();

    }

    public static MessageEmbed getMusicOk(String message) {
        EmbedBuilder temp = new EmbedBuilder().setTitle(":loud_sound:  Music :loud_sound:").setDescription(message).setColor(Color.green);
        return buildStandar(temp);
    }

    public static MessageEmbed getSpamExtermine(Member autor, int multi) {
        return new EmbedBuilder().setTitle(":mute: Spam Hunter :mute:").setDescription(autor.getAsMention() + " détecté comme spammer !\n\nOn te revoit dans __**" + multi + "**__ min!").setImage("https://media.giphy.com/media/WVudyGEaizNeg/giphy.gif").setFooter("Spam info disponible via '//spaminfo' en privé", MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).setTimestamp(Instant.now()).setColor(Color.orange).setColor(Color.orange).build();
    }

    public static MessageEmbed getSpamPardon(Member autor) {
        return new EmbedBuilder().setTitle(":mute: Spam Hunter :mute:").setDescription(autor.getAsMention() + " est de retour, fais gaffe!\nJe te surveille!").setImage("https://media.giphy.com/media/3o7TKwBctlv08kY08M/giphy.gif").setFooter("Spam info disponible via '//spaminfo' en privé", MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).setColor(Color.orange).build();
    }

    public static MessageEmbed getHelp(String command) throws FileNotFoundException {
        String name = command.substring(0, 1).toUpperCase() + command.substring(1).toLowerCase();
        String message = new ResourceLoader().getFile("Help/"+name+"/fr/main.md");
        EmbedBuilder temp = new EmbedBuilder().setTitle(":question: " + command.substring(0, 1).toUpperCase() + command.substring(1).toLowerCase() + " :question: ").setDescription(message).setColor(Color.green);
        return buildStandar(temp);
    }

    public static MessageEmbed getMoveError(String message) {
        return new EmbedBuilder().setTitle(":warning: Move Error :warning: ").setDescription(message).setColor(Color.red).setFooter("'//help move' pour plus d'info ", MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).build();
    }

    public static MessageEmbed getMoveOk(String message) {
        EmbedBuilder temp = new EmbedBuilder().setTitle(":arrow_left: Move :arrow_right:  ").setDescription(message).setColor(Color.green);
        return buildStandar(temp);
    }

    public static MessageEmbed getSpamError(String message) {
        return new EmbedBuilder().setTitle(":warning: Spam Error :warning: ").setDescription(message).setColor(Color.red).setFooter("'//help spam' pour plus d'info ", MainBot.jda.getSelfUser().getAvatarUrl()).build();
    }

    public static MessageEmbed getSpamError(String message, String sub) {
        return new EmbedBuilder().setTitle(":warning: Spam Error :warning: ").setDescription(message).setColor(Color.red).setFooter("'//help spam "+sub+"' pour plus d'info ", MainBot.jda.getSelfUser().getAvatarUrl()).setThumbnail(MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).build();
    }

    public static MessageEmbed getSpamInfo(String message) {
        EmbedBuilder temp = new EmbedBuilder().setTitle(":hourglass:  Spam Info :hourglass:").setDescription(message).setColor(Color.green);
        return buildStandar(temp);
    }

    public static MessageEmbed getFlushError(String message) {
        return new EmbedBuilder().setTitle(":warning: Flush Error :warning: ").setDescription(message).setColor(Color.red).setFooter("'//help flush' pour plus d'info ", MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).build();
    }

    public static MessageEmbed getRegister(String message) {
        return buildStandar(new EmbedBuilder().setTitle(":pencil: Web Registration :pencil:").setDescription(message).setColor(Color.green));
    }

    public static MessageEmbed getInternalError(){
        return buildStandar(getError("Je... Je... je ne me sens pas bien...  :thermometer_face: \nContactez vite mon developeur!").setImage("https://i.imgur.com/anKv8U5.gif"));
    }

    public static MessageEmbed buildStandar(EmbedBuilder embedBuilder){
        return embedBuilder.setFooter("bot.seb6596.ovh", MainBot.jda.getSelfUser().getAvatarUrl()).setThumbnail(MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).build();
    }

    public static MessageEmbed getUnautorized(){
        return buildStandar(getError("Vous n'avez pas l'autorisation de faire ça!").setImage("https://i.imgur.com/0OSsdvW.gif"));
    }

    public static MessageEmbed getHelpList(String role, String list) throws FileNotFoundException {
        String message = new ResourceLoader().getFile("Help/main.md");
        message = message.replace("@list", list);
        return new EmbedBuilder().setTitle("Command du bot ("+role+")").setDescription(message).setFooter("Utilise '//help <commande>' pour plus de détails.",MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).setColor(Color.green).setThumbnail(MainBot.jda.getSelfUser().getAvatarUrl()).build();
    }

    public static MessageEmbed getPref(GuildPreferenceEntity guildPref){
        EmbedBuilder messageB = new EmbedBuilder()
                .setColor(Color.green)
                .setTitle("**Guild config**")
                .setDescription("_This is the current config of bot for this Guild_")
                .addField("> Guild ID", "This is the curent guild ID\n```java\n" + guildPref.getGuildId() + "```Edit: :lock:", false)
                .addField("> Anti Spam", "Activate Spam hunter\n```java\n" + String.valueOf(guildPref.isAntiSpam()) + "```Edit: :unlock:\nKey: anti_spam", false)
                .addField("> Default Role", "Activate auto move to default role\n```java\n" + String.valueOf(guildPref.isDefaultRole()) + "```Edit: :unlock:\nKey: default_role", false)
                .addField("> Default Role ID", "Role id for auto move\n```java\n" + guildPref.getDefaultRoleId() + "```Edit: :unlock:\nKey: default_role_id", false)
                .addField("> Welcome", "Activate welcome message\n```java\n" + String.valueOf(guildPref.isWelcome()) + "```Edit: :unlock:\nKey: welcome", false)
                .addField("> Welcome chanel ID", "Chane id for welcome message\n```" + guildPref.getWelcomeChanelID() + "```Edit: :unlock:\nKey: welcome_chanel_id", false)
                .addField("> Welcome message", "Welcome message (@name for mention)\n```markdown\n" + guildPref.getWelcomeMessage() + "```Edit: :unlock:\nKey: welcome_message", false)
                .addField("> Daily Madame", "Activate daily madame message\n```java\n" + String.valueOf(guildPref.isDailyMadame()) + "```Edit: :unlock:\nKey: daily_madame", false);

        return buildStandar(messageB);
    }




}