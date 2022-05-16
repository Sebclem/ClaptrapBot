package net.Broken.SlashCommands;


import net.Broken.SlashCommand;
import net.Broken.Tools.EmbedMessageUtils;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Command that return a random picture of cat.
 */
public class Cat implements SlashCommand {
    private Logger logger = LogManager.getLogger();
    ;

    @Override
    public void action(SlashCommandEvent event) {
        try {
            URL urlC = new URL("http://aws.random.cat/meo");
            URLConnection yc = urlC.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder a = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                a.append(inputLine);
            in.close();

            JSONObject json = new JSONObject(a.toString());

            event.reply(json.getString("file")).queue();

        } catch (IOException e) {
            logger.catching(e);
            event.reply(new MessageBuilder().setEmbeds(EmbedMessageUtils.getInternalError()).build()).setEphemeral(true).queue();
        }


    }

    @Override
    public String getDescription() {
        return "Return a nice Cat !";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return null;
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
    public boolean isDisableByDefault() {
        return false;
    }
}
