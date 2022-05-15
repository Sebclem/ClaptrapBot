package net.Broken.RestApi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.HashMap;
import java.util.Set;

public class ApiCommandLoader {
    public static HashMap<String, CommandInterface> apiCommands = new HashMap<>();
    private static Logger logger = LogManager.getLogger();

    public static void load() {
        logger.info("Loading Api Command...");
//        Reflections reflections = new Reflections("net.Broken.RestApi.Command");
        Reflections reflections = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage(
                "net.Broken.RestApi.Commands",
                ClasspathHelper.contextClassLoader(),
                ClasspathHelper.staticClassLoader()))
        );
        Set<Class<? extends CommandInterface>> modules =
                reflections.getSubTypesOf(CommandInterface.class);
        logger.info("Find " + modules.size() + " Command:");
        for (Class<? extends CommandInterface> apiClass : modules) {

            String reference = apiClass.getName();
            String[] splited = reference.split("\\.");
            String name = splited[splited.length - 1].toUpperCase();

            logger.info("..." + name);

            try {
                apiCommands.put(name, apiClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error("Failed to load " + name + "!");
            }

        }
    }

}
