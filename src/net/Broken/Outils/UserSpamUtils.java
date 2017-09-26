package net.Broken.Outils;

import net.dv8tion.jda.core.entities.Member;

public class UserSpamUtils {
    private AntiSpam.Minuteur minuteur;
    private Member user;
    private int multip = 0;
    private boolean onSpam = false;

    public int getTimeLeft(){
        return minuteur.timeLeft;
    }

    public int getMultip()
    {
        return multip;
    }

    public UserSpamUtils(Member user) {
        this.user = user;
    }


    public void setMinuteur(AntiSpam.Minuteur minuteur) {
        this.minuteur = minuteur;
    }

    public void launchMinuteur(){
        minuteur.start();
    }

    public void setMultip(int multip) {
        this.multip = multip;
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
}
