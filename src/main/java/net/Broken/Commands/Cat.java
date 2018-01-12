package net.Broken.Commands;


import net.Broken.Commande;
import net.Broken.Outils.Redirection;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Seb on 06/02/2017.
 */
public class Cat implements Commande {
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        Redirection redirect= new Redirection();
        if(!event.isFromType(ChannelType.PRIVATE))
        {
            try {
                String catLine=null;

                redirect.get("http://random.cat");
                URL cat = new URL(redirect.get("http://random.cat"));
                URLConnection cc = cat.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(cc.getInputStream(), "UTF-8"));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                {
                    if(inputLine.contains("id=\"cat\""))
                        catLine = inputLine;
                }
                in.close();
                String[] splited = catLine.split(" ");
                String finalLineCat=null;
                for(String aString:splited)
                {
                    if(aString.startsWith("src"))
                    {
                        finalLineCat=aString;
                    }
                }
                System.out.println(finalLineCat);
                finalLineCat=finalLineCat.replaceAll("src=\"","");
                finalLineCat=finalLineCat.replaceAll("\"","");
                event.getTextChannel().sendMessage("http://random.cat/"+finalLineCat).queue();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            event.getPrivateChannel().sendMessage("\n:warning: **__Commande non disponible en priver!__** :warning:");

    }

    @Override
    public String help(String[] args) {
        return "";
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public boolean isPrivateUsable() {
        return false;
    }
}
