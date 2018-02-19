package net.Broken.RestApi.Commands;

import net.Broken.Commands.Music;
import net.Broken.RestApi.CommandInterface;
import net.Broken.RestApi.Data.CommandPostData;
import net.Broken.RestApi.Data.CommandResponseData;
import net.Broken.audio.AudioM;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Connect implements CommandInterface{
    @Override
    public ResponseEntity<CommandResponseData> action(Music musicCommande, CommandPostData data, User user) {
        AudioM audioM = musicCommande.getAudioManager();
        if(data.chanelId == null)
            return new ResponseEntity<>(new CommandResponseData(data.command,"Missing chanelId"),HttpStatus.BAD_REQUEST);
        VoiceChannel voiceChannel = null;
        try{
            voiceChannel = audioM.getGuild().getVoiceChannelById(data.chanelId);
        }catch (NumberFormatException ignored){}

        if(voiceChannel == null){
            return new ResponseEntity<>(new CommandResponseData(data.command,"Channel Not found"), HttpStatus.BAD_REQUEST);
        }

        audioM.getGuildAudioPlayer(musicCommande.getAudioManager().getGuild());
        audioM.getGuild().getAudioManager().openAudioConnection(audioM.getGuild().getVoiceChannelById(data.chanelId));
        audioM.setPlayedChanel(voiceChannel);
        return new ResponseEntity<>(new CommandResponseData(data.command,"Accepted"),HttpStatus.OK);
    }
}
