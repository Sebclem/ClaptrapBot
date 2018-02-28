package net.Broken.audio;

import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Used to find general voice channels
 */
public class FindGeneral {
    static Logger logger = LogManager.getLogger();

    /**
     * Search for 🤖 char on category name, if this category can't be find, auto create it
     * @param guild Current guild
     * @return General Category
     */
    public static Category find(Guild guild){
        List<Category> categories = guild.getCategories();
        Category finded = null;
        for(Category cat : categories){
            if(cat.getName().contains("\uD83E\uDD16")){
                finded = cat;
                break;
            }
        }
        if(finded == null)
            finded = create(guild);

        return finded;

    }

    /**
     * Create default category "🤖 Salons Vocaux 🤖", and create basic voice channel on it.
     * @param guild Current guild
     * @return Brand new  General Category
     */
    private static Category create(Guild guild){
        logger.info("Can't find general voice chanel, creating it!");
        Channel temp = guild.getController().createCategory("\uD83E\uDD16 Salons Vocaux \uD83E\uDD16").complete();
        Category cat = guild.getCategoryById(temp.getId());
        cat.createVoiceChannel("Général").complete();
        cat.createVoiceChannel("Cour").complete();
        cat.createVoiceChannel("\uD83C\uDFAE Game 1 \uD83C\uDFAE").complete();
        cat.createVoiceChannel("\uD83C\uDFAE Game 2 \uD83C\uDFAE").complete();
        cat.createVoiceChannel("\uD83C\uDFAE Game 3 \uD83C\uDFAE").complete();
        cat.createVoiceChannel("AFK").complete();
        cat = guild.getCategoryById(temp.getId());
        return cat;
    }
}
