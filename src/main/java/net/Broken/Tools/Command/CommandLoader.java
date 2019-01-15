package net.Broken.Tools.Command;

import net.Broken.Commande;
import net.Broken.MainBot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.Set;


/**
 * Find and load bot's command
 */
public class CommandLoader {
    private static Logger logger = LogManager.getLogger();

    /**
     * Search all implemented Command interface class and add it to MainBot.commands HashMap
     */
    public static void load() {
        logger.info("Loading Command...");
        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(
                "net.Broken.Commands",
                ClasspathHelper.contextClassLoader(),
                ClasspathHelper.staticClassLoader()))
        );
        Set<Class<? extends Commande>> modules = reflections.getSubTypesOf(Commande.class);

        logger.info("Find " + modules.size() + " Command:");
        for (Class<? extends Commande> command : modules) {

            String reference = command.getName();
            String[] splited = reference.split("\\.");
            String name = splited[splited.length - 1].toLowerCase();
            if (!command.isAnnotationPresent(Ignore.class)) {
                logger.info("..." + name);

                if (command.isAnnotationPresent(NoDev.class) && MainBot.dev) {
                    logger.warn("Command disabled in dev mode");
                }else{
                    try {
                        MainBot.commandes.put(name, command.newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                        logger.error("Failed to load " + name + "!");
                    }
                    
                }

            } else {
                logger.trace("Ignored command: " + name);
            }

        }
    }
}
