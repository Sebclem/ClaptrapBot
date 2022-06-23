package net.Broken.Tools.Command;

import net.Broken.BotConfigLoader;
import net.Broken.MainBot;
import net.Broken.SlashCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;


/**
 * Find and load bot's command
 */
public class SlashCommandLoader {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Search all implemented Command interface class and add it to MainBot.commands HashMap
     */
    public static void load(BotConfigLoader config) {
        logger.info("Loading Slash Command...");
        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(
                "net.Broken.SlashCommands",
                ClasspathHelper.contextClassLoader(),
                ClasspathHelper.staticClassLoader()))
        );
        Set<Class<? extends SlashCommand>> modules = reflections.getSubTypesOf(SlashCommand.class);

        logger.info("Find " + modules.size() + " Command:");
        for (Class<? extends SlashCommand> command : modules) {

            String reference = command.getName();
            String[] splited = reference.split("\\.");
            String name = splited[splited.length - 1].toLowerCase();
            if (!command.isAnnotationPresent(Ignore.class)) {
                logger.info("..." + name);

                if (command.isAnnotationPresent(NoDev.class) && config.mode().equals("DEV")) {
                    logger.warn("Command disabled in dev mode");
                } else {
                    try {
                        MainBot.slashCommands.put(name, command.getDeclaredConstructor().newInstance());
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        logger.error("Failed to load " + name + "!");
                    }
                }
            } else {
                logger.trace("Ignored command: " + name);
            }
        }
    }

    public static void registerSlashCommands(CommandListUpdateAction commandListUpdateAction) {
        MainBot.slashCommands.forEach((k, v) -> {
            CommandData command = new CommandData(k, v.getDescription());
            if (v.getOptions() != null)
                command.addOptions(v.getOptions());
            if (v.getSubcommands() != null) {
                command.addSubcommands(v.getSubcommands());
            }
            command.setDefaultEnabled(!v.isDisableByDefault());
            commandListUpdateAction.addCommands(command);
        });
        commandListUpdateAction.queue();

    }
}
