package net.Broken.RestApi.Commands;

import net.Broken.RestApi.CommandInterface;
import net.Broken.RestApi.Data.CommandPostData;
import net.Broken.RestApi.Data.CommandResponseData;
import net.Broken.audio.AudioM;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Connect to vocal channel RestApi command
 */
public class Connect implements CommandInterface {
    @Override
    public ResponseEntity<CommandResponseData> action(CommandPostData data, User user, Guild guild) {
        AudioM audioM = AudioM.getInstance(guild);
        if (data.chanelId == null)
            return new ResponseEntity<>(new CommandResponseData(data.command, "Missing chanelId"), HttpStatus.BAD_REQUEST);
        VoiceChannel voiceChannel = null;
        try {
            voiceChannel = guild.getVoiceChannelById(data.chanelId);
        } catch (NumberFormatException ignored) {
        }

        if (voiceChannel == null) {
            return new ResponseEntity<>(new CommandResponseData(data.command, "Channel Not found"), HttpStatus.BAD_REQUEST);
        }

        audioM.getGuildAudioPlayer();
        guild.getAudioManager().openAudioConnection(guild.getVoiceChannelById(data.chanelId));
        audioM.setPlayedChanel(voiceChannel);
        return new ResponseEntity<>(new CommandResponseData(data.command, "Accepted"), HttpStatus.OK);
    }
}
