package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.audio.AudioM;
import net.Broken.audio.NotConnectedException;
import net.Broken.audio.NullMusicManager;
import net.Broken.audio.Youtube.YoutubeTools;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.IOException;

public class ytTest implements Commande {
    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        YoutubeTools yt = YoutubeTools.getInstance(null);

//        try {
////            event.getTextChannel().sendMessage(yt.getRelatedVideo(args[0])).queue();
////            AudioM.getInstance(null).getGuildMusicManager().scheduler.autoPlay();
//
//        } catch (NotConnectedException e) {
//            e.printStackTrace();
//        } catch (NullMusicManager nullMusicManager) {
//            nullMusicManager.printStackTrace();
//        }
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
