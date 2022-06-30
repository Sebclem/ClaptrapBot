package net.Broken.SlashCommands;


import net.Broken.Audio.GuildAudioBotService;
import net.Broken.SlashCommand;
import net.Broken.Tools.EmbedMessageUtils;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Command that return a random picture of cat.
 */
public class Music implements SlashCommand {
    private final Logger logger = LogManager.getLogger();

    @Override
    public void action(SlashCommandEvent event) {
        GuildAudioBotService audio = GuildAudioBotService.getInstance(event.getGuild());
        String action = event.getSubcommandName();
        event.deferReply().queue();
        switch (action) {
            case "play":
                if (event.getMember().getVoiceState().inVoiceChannel()) {
                    VoiceChannel voiceChanel = event.getMember().getVoiceState().getChannel();
                    logger.info("Connecting to " + voiceChanel.getName() + "...");
                    OptionMapping url = event.getOption("url");
                    if (event.getOption("playlist-limit") == null) {
                        audio.loadAndPlay(event, voiceChanel, url.getAsString(), 30, false);
                    } else {
                        long limit = event.getOption("playlist-limit").getAsLong();
                        audio.loadAndPlay(event, voiceChanel, url.getAsString(), (int) limit, false);
                    }
                } else {
                    Message message = new MessageBuilder().setEmbeds(EmbedMessageUtils.getMusicError("You are not in a voice channel !")).build();
                    event.getHook().setEphemeral(true).sendMessage(message).queue();
                }
                break;
            case "add":
                OptionMapping url = event.getOption("url");
                boolean next = false;
                if (event.getOption("next") != null) {
                    next = event.getOption("next").getAsBoolean();
                }
                if (event.getOption("playlist-limit") == null) {
                    audio.add(event, url.getAsString(), 30, next);
                } else {
                    long limit = event.getOption("playlist-limit").getAsLong();
                    audio.add(event, url.getAsString(), (int) limit, next);
                }
                break;
            case "pause":
                audio.pause(event);
                break;
            case "resume":
                audio.resume(event);
                break;
            case "next":
                audio.skipTrack(event);
                break;
            case "stop":
            case "disconnect":
                audio.stop(event);
                break;
            case "info":
                audio.info(event);
                break;
            case "flush":
                audio.flush(event);
                break;
            case "list":
                audio.list(event);
                break;
        }
    }

    @Override
    public String getDescription() {
        return "Music Bot !";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        ArrayList<SubcommandData> subCommandList = new ArrayList<>();
        subCommandList.add(new SubcommandData("play", "Play music")
                .addOption(OptionType.STRING, "url", "The URL of the video to play", true)
                .addOption(OptionType.INTEGER, "playlist-limit", "If a playlist is loaded, enter the max number of loaded tracks (default to 30)"));
        subCommandList.add(new SubcommandData("add", "Add track to queue")
                .addOption(OptionType.STRING, "url", " The URL of the video to play", true)
                .addOption(OptionType.BOOLEAN, "next", "If true, track will be added on top of the playlist and will be the next to play")
                .addOption(OptionType.INTEGER, "playlist-limit", "If a playlist is loaded, enter the max number of loaded tracks (default to 30)"));
        subCommandList.add(new SubcommandData("pause", "Pause playback"));
        subCommandList.add(new SubcommandData("resume", "Resume playback"));
        subCommandList.add(new SubcommandData("stop", "Stop playback"));
        subCommandList.add(new SubcommandData("next", "Next music"));
        subCommandList.add(new SubcommandData("info", "Get currently playing info"));
        subCommandList.add(new SubcommandData("flush", "Flush queue"));
        subCommandList.add(new SubcommandData("list", "Get queue"));
        return subCommandList;
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

    @Override
    public boolean isPrivateUsable() {
        return false;
    }

    @Override
    public boolean isDisableByDefault() {
        return false;
    }
}
