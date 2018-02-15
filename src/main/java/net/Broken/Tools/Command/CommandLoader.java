package net.Broken.Tools.Command;

import net.Broken.Commande;
import net.Broken.MainBot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.Set;

public class CommandLoader {
    private static Logger logger = LogManager.getLogger();
    public static void load(){
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
            String name = splited[splited.length-1].toLowerCase();

            logger.info("..." + name);
            try {
                MainBot.commandes.put(name, command.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error("Failed to load " + name + "!");
            }

        }
    }
}
