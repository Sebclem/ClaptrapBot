package net.Broken.Tools;

import net.Broken.MainBot;
import net.Broken.audio.Youtube.SearchResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Pre build Message Embed
 */
public class EmbedMessageUtils {
    public static MessageEmbed getUnknowCommand() {
        return new EmbedBuilder().setTitle(":warning: Unknown command! :warning:").setDescription(":arrow_right: Use `//help` to see the available commands.").setColor(Color.orange).setFooter(MainBot.url, MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).build();

    }

    public static EmbedBuilder getError(String message) {
        EmbedBuilder temp = new EmbedBuilder().setTitle(":warning: Error! :warning:").setColor(Color.red).setDescription(message);
        return temp;


    }

    public static MessageEmbed getNoPrivate() {
        EmbedBuilder temp = new EmbedBuilder().setTitle(":warning: Command not available in private :warning:").setDescription(":arrow_right: Use `//help` to see the available commands.").setColor(Color.red);
        return buildStandar(temp);
    }

    public static MessageEmbed getMusicError(String message) {
        return new EmbedBuilder().setTitle(":warning: Musique Error :warning:").setDescription(":arrow_right: " + message).setFooter("'//help music' for more info.", MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).setColor(Color.red).setFooter(MainBot.url, MainBot.jda.getSelfUser().getAvatarUrl()).build();

    }

    public static MessageEmbed getMusicOk(String message) {
        EmbedBuilder temp = new EmbedBuilder().setTitle(":loud_sound:  Music :loud_sound:").setDescription(message).setColor(Color.green);
        return buildStandar(temp);
    }

    public static MessageEmbed getSpamExtermine(Member autor, int multi) {
        return new EmbedBuilder().setTitle(":mute: Spam Hunter :mute:").setDescription(autor.getAsMention() + " detected as spam !\n\nSee you in __**" + multi + "**__ minutes!").setImage("https://media.giphy.com/media/WVudyGEaizNeg/giphy.gif").setFooter("Spam info available with '//spaminfo' in private.", MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).setTimestamp(Instant.now()).setColor(Color.orange).setColor(Color.orange).build();
    }

    public static MessageEmbed getSpamPardon(Member autor) {
        return new EmbedBuilder().setTitle(":mute: Spam Hunter :mute:").setDescription(autor.getAsMention() + " is back , watch out!\nI'm watching you!").setImage("https://media.giphy.com/media/3o7TKwBctlv08kY08M/giphy.gif").setFooter("Spam info available with '//spaminfo' in private.", MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).setColor(Color.orange).build();
    }

    public static MessageEmbed getHelp(String command) throws FileNotFoundException {
        String name = command.substring(0, 1).toUpperCase() + command.substring(1).toLowerCase();
        String message = new ResourceLoader().getFile("Help/" + name + "/en/main.md");
        EmbedBuilder temp = new EmbedBuilder().setTitle(":question: " + command.substring(0, 1).toUpperCase() + command.substring(1).toLowerCase() + " :question: ").setDescription(message).setColor(Color.green);
        return buildStandar(temp);
    }

    public static MessageEmbed getMoveError(String message) {
        return new EmbedBuilder().setTitle(":warning: Move Error :warning: ").setDescription(message).setColor(Color.red).setFooter("'//help move' for more info.", MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).build();
    }

    public static MessageEmbed getMoveOk(String message) {
        EmbedBuilder temp = new EmbedBuilder().setTitle(":arrow_left: Move :arrow_right:  ").setDescription(message).setColor(Color.green);
        return buildStandar(temp);
    }

    public static MessageEmbed getSpamError(String message) {
        return new EmbedBuilder().setTitle(":warning: Spam Error :warning: ").setDescription(message).setColor(Color.red).setFooter("'//help spam' for more info.", MainBot.jda.getSelfUser().getAvatarUrl()).build();
    }

    public static MessageEmbed getSpamError(String message, String sub) {
        return new EmbedBuilder().setTitle(":warning: Spam Error :warning: ").setDescription(message).setColor(Color.red).setFooter("'//help spam " + sub + "' for more info.", MainBot.jda.getSelfUser().getAvatarUrl()).setThumbnail(MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).build();
    }

    public static MessageEmbed getSpamInfo(String message) {
        EmbedBuilder temp = new EmbedBuilder().setTitle(":hourglass: Spam Info :hourglass:").setDescription(message).setColor(Color.green);
        return buildStandar(temp);
    }

    public static MessageEmbed getFlushError(String message) {
        return new EmbedBuilder().setTitle(":warning: Flush Error :warning: ").setDescription(message).setColor(Color.red).setFooter("'//help flush' for more info. ", MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).build();
    }

    public static MessageEmbed getRegister(String message) {
        return buildStandar(new EmbedBuilder().setTitle(":pencil: Web Registration :pencil:").setDescription(message).setColor(Color.green));
    }

    public static MessageEmbed getInternalError() {
        return buildStandar(getError("I... I... I don't feel so good ~~mr stark~~...  :thermometer_face: \nPlease contact my developer!").setImage("https://i.imgur.com/anKv8U5.gif"));
    }

    public static MessageEmbed buildStandar(EmbedBuilder embedBuilder) {
        return embedBuilder.setFooter(MainBot.url, MainBot.jda.getSelfUser().getAvatarUrl()).setThumbnail(MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).build();
    }

    public static MessageEmbed getUnautorized() {
        return buildStandar(getError("You're not powerful enough to do that slave !").setImage("https://i.imgur.com/0OSsdvW.gif"));
    }

    public static MessageEmbed getHelpList(String role, String list) throws FileNotFoundException {
        String message = new ResourceLoader().getFile("Help/main.md");
        message = message.replace("@list", list);
        return new EmbedBuilder().setTitle("Bot Command (" + role + ")").setDescription(message).setFooter("Use '//help <command>' for more info", MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).setColor(Color.green).setThumbnail(MainBot.jda.getSelfUser().getAvatarUrl()).build();
    }

    public static MessageEmbed getLastMessageFromTextChannel(HashMap<String, String> message) {
        EmbedBuilder temp = new EmbedBuilder().setTitle("Channel uses checker").setDescription("Last message date for channels:").setColor(Color.green);
        for (Map.Entry<String, String> entry : message.entrySet()) {
            temp.addField(entry.getKey(), entry.getValue(), false);
        }

        return buildStandar(temp);
    }

    public static MessageEmbed getReportUsersError() {
        return new EmbedBuilder().setTitle(":warning: Command error :warning: ").setDescription("").setColor(Color.red).setFooter("'//help move' for more info.", MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).build();
    }

    public static MessageEmbed searchResult(SearchResult result) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.CYAN)
                .setTitle(result.title)
                .setImage(result.imageUrl)
                .addField("Duration: ", result.duration, false)
                .addField("URL:", "https://www.youtube.com/watch?v=" + result.id, false)
                .addField("Chanel:", result.channelTittle, false);
        return buildStandar(builder);
    }


}