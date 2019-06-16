package net.Broken.Commands;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import net.Broken.Commande;
import net.Broken.DB.Entity.UserEntity;
import net.Broken.DB.Repository.GuildPreferenceRepository;
import net.Broken.DB.Repository.UserRepository;
import net.Broken.SpringContext;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.MessageTimeOut;
import net.Broken.Tools.PrivateMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class Code implements Commande {

    private UserRepository userRepository;
    private Logger logger = LogManager.getLogger();

    public Code (){
        ApplicationContext context = SpringContext.getAppContext();
        userRepository = (UserRepository) context.getBean("userRepository");
    }


    @Override
    public void action(String[] args, MessageReceivedEvent event) {


        StringBuilder stringBuilder = new StringBuilder();
        for(String arg : args){
            stringBuilder.append(arg);
            stringBuilder.append(" ");
        }

        Binding binding = new Binding();
        binding.setVariable("event", event);
        GroovyShell shell = new GroovyShell(binding);
        EmbedBuilder builder;
        try{
            Object value = shell.evaluate(stringBuilder.toString());
            StringBuilder stringResult = new StringBuilder();

            if(value.getClass().isArray()){
                Object[] array = (Object[]) value;
                for(Object obj : array){
                    if(stringResult.length() < 1800){
                        stringResult.append(obj.toString()).append("\n\n");
                    }
                    else{
                        stringResult.append("\n...");
                        break;
                    }

                }
            }else{
                stringResult.append(value.toString());
            }
            builder = new EmbedBuilder().setColor(Color.orange).setTitle(":hammer_pick: Compilation Successful :hammer_pick:").setDescription("```java\n" + stringResult.toString() + "```");
        }catch (Exception ex){
            builder = new EmbedBuilder().setColor(Color.red).setTitle(":x: Compilation Failed :x:").setDescription("```java\n" + ex.toString() + "```");
        }


        event.getChannel().sendMessage(builder.build()).queue();


    }

    @Override
    public boolean isPrivateUsable() {
        return true;
    }

    @Override
    public boolean isAdminCmd() {
        return true;
    }

    /**
     * Determines if the command is usable only by bot level admin user
     *
     * @return boolean
     */
    @Override
    public boolean isBotAdminCmd() {
        return true;
    }

    @Override
    public boolean isNSFW() {
        return false;
    }
}
