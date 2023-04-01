package net.Broken.SlashCommands;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.Broken.SlashCommand;
import net.Broken.Tools.EmbedMessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

/**
 * Command that return a random picture of cat.
 */
public class Cat implements SlashCommand {
    private final Logger logger = LogManager.getLogger();

    @Override
    public void action(SlashCommandInteractionEvent event) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://aws.random.cat/meow"))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.warn("[CAT] Fail to fetch cat: Status Code: {} Body: {}", response.statusCode(),
                        response.body());
                throw new IOException();
            }

            TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {
            };
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, String> json = mapper.readValue(response.body(), typeRef);
            event.reply(json.get("file")).queue();

        } catch (InterruptedException | IOException e) {
            logger.catching(e);
            event.reply(new MessageCreateBuilder().setEmbeds(EmbedMessageUtils.getInternalError()).build()).setEphemeral(true)
                    .queue();
        }

    }

    @Override
    public String getDescription() {
        return "Return a nice Cat !";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return Collections.emptyList();
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
        return true;
    }

    @Override
    public DefaultMemberPermissions getDefaultPermissions(){
        return DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND);
    } 

}
