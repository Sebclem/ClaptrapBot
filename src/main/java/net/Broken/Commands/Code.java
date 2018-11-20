package net.Broken.Commands;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import net.Broken.Commande;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class Code implements Commande {
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

    @Override
    public boolean isNSFW() {
        return false;
    }
}
