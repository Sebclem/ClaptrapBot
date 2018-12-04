package net.Broken.Commands;


import net.Broken.Commande;
import net.Broken.Tools.Redirection;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Command that return a random picture of cat.
 */
public class Cat implements Commande {

    @Override
    public void action(String[] args, MessageReceivedEvent event) {

        if(!event.isFromType(ChannelType.PRIVATE))
        {
            try {
                URL urlC = new URL("http://aws.random.cat/meow");
                URLConnection yc = urlC.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        yc.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    a.append(inputLine);
                in.close();

                JSONObject json = new JSONObject(a.toString());

                event.getTextChannel().sendMessage(json.getString("file")).queue();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            event.getPrivateChannel().sendMessage("\n:warning: **__Commande non disponible en priver!__** :warning:");

    }

    @Override
    public boolean isPrivateUsable() {
        return false;
    }

    @Override
    public boolean isAdminCmd() {
        return false;
    }

    @Override
    public boolean isNSFW() {
        return false;
    }
}
