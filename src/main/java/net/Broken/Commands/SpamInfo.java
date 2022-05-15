package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.MainBot;
import net.Broken.Tools.EmbedMessageUtils;
import net.Broken.Tools.MessageTimeOut;
import net.Broken.Tools.PrivateMessage;
import net.Broken.Tools.UserSpamUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Spam Info Command
 */
public class SpamInfo implements Commande {
    Logger logger = LogManager.getLogger();
    private HashMap<Member, MessageUpdater> threadHashMap = new HashMap<>();

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        Member user;
        if (event.getMessage().getMentionedUsers().size() == 0) {
            user = event.getMember();
        } else {
            user = event.getMessage().getMentionedMembers().get(0);
        }


        Message message = null;
        if (!MainBot.spamUtils.containsKey(user)) {
            if (!event.isFromType(ChannelType.PRIVATE))
                message = event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamInfo(user.getEffectiveName() + ":\n\t- Multiplicator: `1`\n\t- In spam: `No`")).complete();
            else
                PrivateMessage.send(event.getAuthor(), EmbedMessageUtils.getSpamInfo(user.getEffectiveName() + ":\n\t- Multiplicator: `1`\n\t- In spam: `No`"), logger);
        } else {
            UserSpamUtils util = MainBot.spamUtils.get(user);
            if (!util.isOnSpam()) {
                if (!event.isFromType(ChannelType.PRIVATE))
                    message = event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamInfo(user.getEffectiveName() + "\n\t- Multiplicator: `" + util.getMultip() + "`\n\t- In spam: `No`")).complete();
                else
                    PrivateMessage.send(event.getAuthor(), EmbedMessageUtils.getSpamInfo(user.getEffectiveName() + ":\n\t- Multiplicator: `" + util.getMultip() + "`\n\t- In spam: `No`"), logger);
            } else {
                if (!event.isFromType(ChannelType.PRIVATE))
                    message = event.getTextChannel().sendMessage(EmbedMessageUtils.getSpamInfo(user.getEffectiveName() + ":\n\t- Multiplicator: `" + util.getMultip() + "`\n\t- In spam: `Yes`\n\t- Time remaining: `" + formatSecond(util.getTimeLeft()) + "`")).complete();
                else
                    message = PrivateMessage.send(event.getAuthor(), EmbedMessageUtils.getSpamInfo(user.getEffectiveName() + "\n\t- Multiplicator: `" + util.getMultip() + "`\n\t- In spam: `Yes`\n\t- Time remaining: `" + formatSecond(util.getTimeLeft()) + "`"), logger);
            }
        }
        if (message != null) {
            if (threadHashMap.containsKey(user)) {
                MessageUpdater startedThread = threadHashMap.get(user);
                if (!message.getChannelType().equals(startedThread.message.getChannelType())) {
                    MessageUpdater newThread = new MessageUpdater(message, event.getMessage(), MainBot.spamUtils.get(user), user);
                    threadHashMap.put(user, newThread);
                    newThread.start();
                } else {
                    threadHashMap.get(user).stop = true;
                    MessageUpdater newThread = new MessageUpdater(message, event.getMessage(), MainBot.spamUtils.get(user), user);
                    threadHashMap.replace(user, newThread);
                    newThread.start();
                }
            } else {
                MessageUpdater newThread = new MessageUpdater(message, event.getMessage(), MainBot.spamUtils.get(user), user);
                threadHashMap.put(user, newThread);
                newThread.start();
            }
        }


    }

    @Override
    public boolean isPrivateUsable() {
        return true;
    }

    @Override
    public boolean isAdminCmd() {
        return false;
    }

    /**
     * Determines if the command is usable only by bot level admin user
     *
     * @return boolean
     */
    @Override
    public boolean isBotAdminCmd() {
        return false;
    }

    @Override
    public boolean isNSFW() {
        return false;
    }

    public String formatSecond(int second) {
        long days = TimeUnit.SECONDS.toDays(second);
        second -= TimeUnit.DAYS.toSeconds(days);

        long hours = TimeUnit.SECONDS.toHours(second);
        second -= TimeUnit.HOURS.toSeconds(hours);


        long minutes = TimeUnit.SECONDS.toMinutes(second);
        second -= TimeUnit.MINUTES.toSeconds(minutes);

        long seconds = TimeUnit.SECONDS.toSeconds(second);

        logger.debug("" + days + ":" + hours + ":" + minutes + ":" + seconds);
        String finalText = "";
        if (days != 0)
            finalText += days + " day(s) ";
        if (hours != 0)
            finalText += hours + "h ";
        if (minutes != 0)
            finalText += minutes + "min ";
        finalText += seconds + "s";

        return finalText;

    }


    private class MessageUpdater extends Thread {
        public Message message;
        public Message command;
        public UserSpamUtils util;
        public boolean stop;
        private int oldValue;
        private Member user;

        public MessageUpdater(Message message, Message command, UserSpamUtils util, Member user) {
            this.message = message;
            this.util = util;
            this.user = user;
            this.command = command;

        }

        @Override
        public void run() {
            logger.debug("Start " + user.getEffectiveName() + " theard!");
            if (util != null) {
                oldValue = util.getTimeLeft();
                while (util.getTimeLeft() != 0 && !stop && util.isOnSpam()) {
                    try {
                        Thread.sleep(500);
                        if (util.getTimeLeft() % 5 == 0 && oldValue - util.getTimeLeft() >= 5) {
                            message.editMessage(EmbedMessageUtils.getSpamInfo(user.getEffectiveName() + ":\n\t- Multiplicator: `" + util.getMultip() + "`\n\t- In spam: `Yes`\n\t- Time remaining: `" + formatSecond(util.getTimeLeft()) + "`")).complete();
                            oldValue = util.getTimeLeft();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                logger.debug("Kill " + user.getEffectiveName() + " theard!");
                if (stop)
                    message.editMessage(new EmbedBuilder().setColor(Color.RED).setTitle("Aborted").build()).complete();
                else
                    message.editMessage(EmbedMessageUtils.getSpamInfo(user.getEffectiveName() + "\n\t- Multiplicator: `" + util.getMultip() + "`\n\t- In spam: `No`")).complete();

            }
            logger.debug("Timer for message deletion of " + user.getEffectiveName() + " stated...");
            threadHashMap.remove(user);
            List<Message> messages = new ArrayList<>();
            messages.add(command);
            messages.add(message);
            new MessageTimeOut(messages, 15).start();


        }
    }
}
