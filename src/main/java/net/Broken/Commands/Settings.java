package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.DB.Entity.GuildPreferenceEntity;
import net.Broken.DB.Repository.GuildPreferenceRepository;
import net.Broken.MainBot;
import net.Broken.SpringContext;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.MessageTimeOut;
import net.Broken.Tools.SettingsUtils;
import net.Broken.audio.AudioM;
import net.Broken.audio.NotConnectedException;
import net.Broken.audio.NullMusicManager;
import net.Broken.audio.Youtube.YoutubeTools;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class Settings implements Commande {

    private Logger logger = LogManager.getLogger();

    private GuildPreferenceRepository guildPreferenceRepository;

    public Settings() {

        ApplicationContext context = SpringContext.getAppContext();
        guildPreferenceRepository = (GuildPreferenceRepository) context.getBean("guildPreferenceRepository");
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if(args.length == 0){
            GuildPreferenceEntity guildPref = SettingsUtils.getInstance().getPreference(event.getGuild());
            MessageEmbed message = EmbedMessageUtils.getPref(guildPref);
            event.getTextChannel().sendMessage(message).complete();

        }
        else{
            switch (args[0]){
                case "set":
                    if(args.length >= 3){
                        StringBuilder val = new StringBuilder();
                        for(int i = 2; i < args.length; i++){
                            val.append(args[i]).append(" ");
                        }
                        set(event, args[1], val.toString());
                    }

                    else{

                        MessageEmbed msg = EmbedMessageUtils.buildStandar(EmbedMessageUtils.getError("Missing argument.\n:arrow_right: Please use `//help preference`"));
                        Message sended = event.getTextChannel().sendMessage(msg).complete();
                    }
                    break;

                default:
                    MessageEmbed msg = EmbedMessageUtils.buildStandar(EmbedMessageUtils.getError("\nUnknown argument`\n\nMore info with `//help preference`"));
                    Message sended = event.getTextChannel().sendMessage(msg).complete();

                    break;

            }
        }



    }

    @Override
    public boolean isPrivateUsable() {
        return false;
    }

    @Override
    public boolean isAdminCmd() {
        return true;
    }

    @Override
    public boolean isNSFW() {
        return false;
    }


    private void set(MessageReceivedEvent event, String key, String value){
        GuildPreferenceEntity pref = SettingsUtils.getInstance().getPreference(event.getGuild());
        switch (key){
            case "anti_spam":
                value = value.replaceAll(" ", "");
                if(value.toLowerCase().equals("true") || value.toLowerCase().equals("false")){
                    boolean result = Boolean.parseBoolean(value);
                    pref.setAntiSpam(result);
                    pref = guildPreferenceRepository.save(pref);
                    EmbedBuilder eb = new EmbedBuilder().addField(":ok: Ok :ok:","",false).addField("> Anti Spam", "```java\n" + String.valueOf(pref.isAntiSpam()) + "```", false).setColor(Color.green);
                    Message sended = event.getTextChannel().sendMessage(EmbedMessageUtils.buildStandar(eb)).complete();
                }else{
                    MessageEmbed msg = EmbedMessageUtils.buildStandar(EmbedMessageUtils.getError("\nWrong value, expect `true` or `false`\n\nMore info with `//help preference`"));
                    Message sended = event.getTextChannel().sendMessage(msg).complete();
                }

                break;


            case "default_role":
                value = value.replaceAll(" ", "");
                if(value.toLowerCase().equals("true") || value.toLowerCase().equals("false")){
                    boolean result = Boolean.parseBoolean(value);
                    pref.setDefaultRole(result);
                    pref = guildPreferenceRepository.save(pref);
                    EmbedBuilder eb = new EmbedBuilder().addField(":ok: Ok :ok:","",false).addField("> Default Role", "```java\n" + String.valueOf(pref.isDefaultRole()) + "```", false).setColor(Color.green);
                    Message sended = event.getTextChannel().sendMessage(EmbedMessageUtils.buildStandar(eb)).complete();
                }else{
                    MessageEmbed msg = EmbedMessageUtils.buildStandar(EmbedMessageUtils.getError("\nWrong value, expect `true` or `false`\n\nMore info with `//help preference`"));
                    Message sended = event.getTextChannel().sendMessage(msg).complete();
                }
                break;
            case "default_role_id":
                try{
                    value = value.replaceAll(" ", "");
                    Role role = event.getGuild().getRoleById(value);
                    if(role != null){
                        pref.setDefaultRoleId(role.getId());

                        pref = guildPreferenceRepository.save(pref);
                        EmbedBuilder eb = new EmbedBuilder().addField(":ok: Ok :ok:","",false).addField("> Default Role ID", "```java\n" + pref.getDefaultRoleId()+ "```", false).setColor(Color.green);
                        Message sended = event.getTextChannel().sendMessage(EmbedMessageUtils.buildStandar(eb)).complete();

                    }
                    else
                    {
                        throw  new NumberFormatException();
                    }
                }catch (NumberFormatException e){
                    MessageEmbed msg = EmbedMessageUtils.buildStandar(EmbedMessageUtils.getError("\nRole not found!\n\nUse `//listroles` to get roles id"));
                    Message sended = event.getTextChannel().sendMessage(msg).complete();
                }

                break;
            case "welcome":
                value = value.replaceAll(" ", "");
                if(value.toLowerCase().equals("true") || value.toLowerCase().equals("false")){
                    boolean result = Boolean.parseBoolean(value);
                    pref.setWelcome(result);
                    pref = guildPreferenceRepository.save(pref);
                    EmbedBuilder eb = new EmbedBuilder().addField(":ok: Ok :ok:","",false).addField("> Welcome", "```java\n" + String.valueOf(pref.isWelcome()) + "```", false).setColor(Color.green);
                    Message sended = event.getTextChannel().sendMessage(EmbedMessageUtils.buildStandar(eb)).complete();
                }else{
                    MessageEmbed msg = EmbedMessageUtils.buildStandar(EmbedMessageUtils.getError("\nWrong value, expect `true` or `false`\n\nMore info with `//help preference`"));
                    Message sended = event.getTextChannel().sendMessage(msg).complete();
                }
                break;
            case  "welcome_chanel_id":
                try{
                    value = value.replaceAll(" ", "");
                    TextChannel chanel = event.getGuild().getTextChannelById(value);
                    if(chanel != null){
                        pref.setWelcomeChanelID(chanel.getId());

                        pref = guildPreferenceRepository.save(pref);
                        EmbedBuilder eb = new EmbedBuilder().addField(":ok: Ok :ok:","",false).addField("> Welcome chanel ID", "```java\n" + pref.getWelcomeChanelID() + "```", false).setColor(Color.green);
                        Message sended = event.getTextChannel().sendMessage(EmbedMessageUtils.buildStandar(eb)).complete();

                    }
                    else
                    {
                        throw  new NumberFormatException();
                    }
                }catch (NumberFormatException e){
                    MessageEmbed msg = EmbedMessageUtils.buildStandar(EmbedMessageUtils.getError("\nText channel not found!\n\nUse `//listroles` to get roles id"));
                    Message sended = event.getTextChannel().sendMessage(msg).complete();
                }
                break;
            case "welcome_message":
                pref.setWelcomeMessage(value);
                pref = guildPreferenceRepository.save(pref);
                EmbedBuilder eb = new EmbedBuilder().addField(":ok: Ok :ok:","",false).addField("> Welcome message", "```java\n" + pref.getWelcomeMessage() + "```", false).setColor(Color.green);
                Message sended = event.getTextChannel().sendMessage(EmbedMessageUtils.buildStandar(eb)).complete();
                break;

            case "daily_madame":
                value = value.replaceAll(" ", "");
                if(value.toLowerCase().equals("true") || value.toLowerCase().equals("false")){
                    boolean result = Boolean.parseBoolean(value);
                    pref.setDailyMadame(result);
                    pref = guildPreferenceRepository.save(pref);
                    EmbedBuilder ebd = new EmbedBuilder().addField(":ok: Ok :ok:","",false).addField("> Daily Madame", "Activate daily madame message\n```java\n" + String.valueOf(value) + "```", false).setColor(Color.green);
                    Message sendedm = event.getTextChannel().sendMessage(EmbedMessageUtils.buildStandar(ebd)).complete();
                }else{
                    MessageEmbed msg = EmbedMessageUtils.buildStandar(EmbedMessageUtils.getError("\nWrong value, expect `true` or `false`\n\nMore info with `//help preference`"));
                    Message sendedm = event.getTextChannel().sendMessage(msg).complete();
                }
                break;

            default:
                MessageEmbed msg2 = EmbedMessageUtils.buildStandar(EmbedMessageUtils.getError("\nUnknown id.\n\nUse `//preference` to see list"));
                Message sended2 = event.getTextChannel().sendMessage(msg2).complete();
                break;
        }


    }
}
