package net.Broken.Commands;

import net.Broken.Commande;
import net.Broken.Tools.EmbedMessageUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;


public class ReportUsers implements Commande {

    @Override
    public void action(String[] args, MessageReceivedEvent event) {

        event.getGuild().loadMembers().onSuccess(members -> {
            if (event.getMessage().getMentionedRoles().size() == 1 && args.length == 1) {
                RestAction<Void> restAction = null;
                for (Member member : members) {
                    if (member.getRoles().size() == 1) { //check if the member has a role
                        if (member.getRoles().contains(event.getMessage().getMentionedRoles().get(0))) { //check if the mentioned role is the same as the member's role
                            if (restAction == null) {
                                restAction = event.getTextChannel().sendMessage("List des membres : ").and(event.getTextChannel().sendMessage(member.getEffectiveName()));
                            } else {
                                restAction = restAction.and(event.getTextChannel().sendMessage(member.getEffectiveName()));
                            }
                        }
                    }
                }
                if (restAction != null)
                    restAction.queue();
            } else if (args.length == 0) {

                for (Member member : members) {
                    if (member.getRoles().size() == 0) {
                        event.getTextChannel().sendMessage(member.getEffectiveName()).complete();
                    }
                }
            } else {
                event.getTextChannel().sendMessage(EmbedMessageUtils.getReportUsersError()).complete();
            }
        });
    }

    @Override
    public boolean isPrivateUsable() {
        return false;
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
        return false;
    }

    @Override
    public boolean isNSFW() {
        return false;
    }
}
