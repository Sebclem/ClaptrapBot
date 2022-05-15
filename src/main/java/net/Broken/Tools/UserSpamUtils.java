package net.Broken.Tools;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

/**
 * Spam info for one user
 */
public class UserSpamUtils {
    private AntiSpam.Minuteur minuteur;
    private Member user;
    private int multip = 0;
    private boolean onSpam = false;
    private List<Message> messages;

    public UserSpamUtils(Member user, List<Message> messages) {
        this.user = user;
        this.messages = messages;
    }

    public int getTimeLeft() {
        return minuteur.timeLeft;
    }

    public int getMultip() {
        return multip;
    }

    public void setMultip(int multip) {
        this.multip = multip;
    }

    public void setMinuteur(AntiSpam.Minuteur minuteur) {
        this.minuteur = minuteur;
    }

    public void launchMinuteur() {
        minuteur.start();
    }

    public boolean isOnSpam() {
        return onSpam;
    }

    public void setOnSpam(boolean onSpam) {
        this.onSpam = onSpam;
    }

    public Member getUser() {
        return user;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void clearAndAdd(Message message) {
        messages.clear();
        messages.add(message);
    }
}
