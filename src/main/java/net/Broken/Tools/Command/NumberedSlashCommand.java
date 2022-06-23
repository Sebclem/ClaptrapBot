package net.Broken.Tools.Command;

import net.Broken.SlashCommand;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.FindContentOnWebPage;
import net.Broken.Tools.LimitChecker;
import net.Broken.Tools.Random.TrueRandom;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Abstact class used for all command that need to find the max number of page on a web site.
 */
@Ignore
public abstract class NumberedSlashCommand implements SlashCommand {
    protected final int minNumber = 1;
    protected int maxNumber = -1;
    protected final String baseURL;
    protected final String divClass;
    protected final String htmlType;
    protected final String urlSuffix;
    protected final LinkedBlockingQueue<Integer> randomQueue = new LinkedBlockingQueue<>();
    private Logger logger = LogManager.getLogger();

    /**
     * Default constructor
     *
     * @param logger   Logger used for logs
     * @param baseURL  WebSite base url
     * @param divClass DivClass to search to extract image
     * @param htmlType HTML tag to extract image (img)
     */
    public NumberedSlashCommand(Logger logger, String baseURL, String urlSuffix, String divClass, String htmlType) {
        this.logger = logger;
        this.baseURL = baseURL;
        this.divClass = divClass;
        this.htmlType = htmlType;
        this.urlSuffix = urlSuffix;
        try {
            logger.debug("Checking max...");
            maxNumber = LimitChecker.doYourJob(baseURL, 2, urlSuffix);
            logger.info("Limit is " + maxNumber);
        } catch (IOException e) {
            logger.catching(e);
        }
    }

    public NumberedSlashCommand(Logger logger, String baseURL, String urlSuffix) {
        this(logger, baseURL, urlSuffix, null, null);

    }

    @Override
    public void action(SlashCommandEvent event) {
        event.deferReply().queue();
        try {
            String result = poll();
            event.getHook().sendMessage(event.getMember().getAsMention() + "\n" + result).queue();
        } catch (IOException e) {
            logger.catching(e);
            MessageEmbed message = EmbedMessageUtils.getInternalError();
            event.getHook().setEphemeral(true).sendMessageEmbeds(message).queue();
        }
    }


    private void fillRandomQueue() throws IOException {
        TrueRandom trueRandom = TrueRandom.getINSTANCE();
        List<Integer> numbers = trueRandom.getNumbers(minNumber, maxNumber);

        randomQueue.addAll(numbers);

    }

    protected void checkRandom() throws IOException {
        logger.trace("Queue size: " + randomQueue.size());
        if (randomQueue.isEmpty()) {
            logger.debug("Queue empty, update it.");
            fillRandomQueue();
        }
    }

    public String poll() throws IOException {
        checkRandom();
        int randomResult = randomQueue.poll();
        return FindContentOnWebPage.doYourJob(baseURL + randomResult + urlSuffix, divClass, htmlType);
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return null;
    }

}
