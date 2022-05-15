package net.Broken.Tools;

import net.Broken.MainBot;
import net.Broken.audio.Youtube.SearchResult;
import net.dv8tion.jda.api.EmbedBuilder;
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

    public static EmbedBuilder getError(String message) {
        EmbedBuilder temp = new EmbedBuilder().setTitle(":warning: Error! :warning:").setColor(Color.red).setDescription(message);
        return temp;
    }


    public static MessageEmbed getMusicError(String message) {
        return new EmbedBuilder().setTitle(":warning: Musique Error :warning:").setDescription(":arrow_right: " + message).setFooter("'//help music' for more info.", MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).setColor(Color.red).setFooter(MainBot.url, MainBot.jda.getSelfUser().getAvatarUrl()).build();
    }

    public static MessageEmbed getMusicOk(String message) {
        EmbedBuilder temp = new EmbedBuilder().setTitle(":loud_sound:  Music :loud_sound:").setDescription(message).setColor(Color.green);
        return buildStandar(temp);
    }

    public static MessageEmbed getFlushError(String message) {
        return buildStandar(new EmbedBuilder().setTitle(":warning: Flush Error :warning: ").setDescription(message).setColor(Color.red));
    }


    public static MessageEmbed getInternalError() {
        return buildStandar(getError("I... I... I don't feel so good ~~mr stark~~...  :thermometer_face: \nPlease contact my developer!").setImage("https://i.imgur.com/anKv8U5.gif"));
    }

    public static MessageEmbed buildStandar(EmbedBuilder embedBuilder) {
        return embedBuilder.setFooter(MainBot.url, MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).build();
    }

    public static MessageEmbed getUnautorized() {
        return buildStandar(getError("You're not powerful enough to do that slave !").setImage("https://i.imgur.com/0OSsdvW.gif"));
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


}