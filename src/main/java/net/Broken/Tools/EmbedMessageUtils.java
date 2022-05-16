package net.Broken.Tools;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.Broken.MainBot;
import net.Broken.audio.UserAudioTrack;
import net.Broken.audio.Youtube.SearchResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

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
        EmbedBuilder temp = new EmbedBuilder().setTitle(":warning: Error!").setColor(Color.red).setDescription(message);
        return temp;
    }


    public static MessageEmbed getMusicError(String message) {
        return new EmbedBuilder().setTitle(":warning: Musique Error").setDescription(":arrow_right: " + message).setFooter("'//help music' for more info.", MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).setColor(Color.red).setFooter(MainBot.url, MainBot.jda.getSelfUser().getAvatarUrl()).build();
    }

    public static MessageEmbed getMusicOk(String message) {
// TODO better display for different action (add icon ?)
        EmbedBuilder temp = new EmbedBuilder().setTitle(":loud_sound: " + message).setColor(Color.green);
        return buildStandar(temp);
    }

    public static MessageEmbed getMusicInfo(AudioTrackInfo info, UserAudioTrack userAudioTrack) {
        EmbedBuilder temp = new EmbedBuilder().setTitle(":information_source: Currently playing")
                .addField("Title", info.title, false)
                .addField("Author", info.author, false)
                .addField("URL", info.uri, false)
                .addField("Submitted by", userAudioTrack.getSubmittedUser().getName(), false)
                .setThumbnail("https://img.youtube.com/vi/" + info.identifier + "/hqdefault.jpg")
                .setColor(Color.green);
        return buildStandar(temp);
    }

    public static MessageEmbed getMusicAdded(AudioTrackInfo info, Member member, int playlistSize){
        EmbedBuilder temp = new EmbedBuilder()
                .addField("Title", info.title, false)
                .addField("Author", info.author, false)
                .addField("Submitted by", member.getEffectiveName(), true)
                .setThumbnail("https://img.youtube.com/vi/" + info.identifier + "/hqdefault.jpg")
                .setColor(Color.green);
        if(playlistSize != -1){
            temp.addField("Loaded tracks", Integer.toString(playlistSize), true)
                    .setTitle(":loud_sound: Playlist added to queue");
        }
        else {
            temp.setTitle(":loud_sound: Track added to queue");
        }
        temp.addField("URL", info.uri, false);
        return buildStandar(temp);
    }

    public static MessageEmbed getFlushError(String message) {
        return buildStandar(new EmbedBuilder().setTitle(":warning: Flush Error").setDescription(message).setColor(Color.red));
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
        return new EmbedBuilder().setTitle(":warning: Command error").setDescription("").setColor(Color.red).setFooter("'//help move' for more info.", MainBot.jda.getSelfUser().getAvatarUrl()).setTimestamp(Instant.now()).build();
    }


}