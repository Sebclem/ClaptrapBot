package net.Broken.Commands.Over18;

import net.Broken.Outils.Command.NumberedCommande;
import org.apache.logging.log4j.LogManager;

/**
 * Created by seb65 on 07/11/2016.
 */
public class Pipe extends NumberedCommande {
    public Pipe() {
        super(LogManager.getLogger(), "http://feelation.com/","featured-img","img");
    }

    /*
    System.out.println("ok");
                String pipeLine=null;
                URL pipeURL = new URL(redirect.get("http://feelation.com/random"));
                URLConnection cc = pipeURL.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(cc.getInputStream(), "UTF-8"));
                String inputLine;


                while ((inputLine = in.readLine()) != null)
                {
                    System.out.println(inputLine);
                    if(inputLine.contains("class=\"post photo\""))
                    {
                        String[] splited = inputLine.split("<");
                        for(String aString:splited)
                        {
                            if(aString.startsWith("<img src"))
                            {
                                pipeLine=aString;
                                pipeLine=inputLine.replaceAll("<img src=\"","");
                                pipeLine=pipeLine.substring(0,pipeLine.indexOf("\""));
                                System.out.println(pipeLine);
                                break;

                            }
                        }
                    }

                }

                in.close();
                event.getTextChannel().sendMessage(redirect.get("http://feelation.com/random")).queue();
     */
    @Override
    public String toString() {
        return "Pipe";
    }

    @Override
    public boolean isPrivateUsable() {
        return false;
    }
}
