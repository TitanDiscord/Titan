package me.anutley.titan.listeners;

import me.anutley.titan.database.objects.PingProtectionSettings;
import me.anutley.titan.database.objects.PingProtectionUserData;
import me.anutley.titan.database.objects.Warning;
import me.anutley.titan.util.RoleUtil;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class IllegalPingListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {

        int ping;
        int threshold;
        String action;

        if (!(event.getMessage().getMentionedMembers().size() > 0)) return;

        PingProtectionSettings pingProtectionSettings = new PingProtectionSettings(event.getGuild().getId());
        PingProtectionUserData pingProtectionUserData = new PingProtectionUserData(event.getGuild().getId(), event.getMember().getId());

        if (!pingProtectionSettings.isEnabled()) return;
        if (event.getMember().getUser().isBot()) return;

        ping = pingProtectionUserData.getCount();
        threshold = pingProtectionSettings.getThreshold();
        action = pingProtectionSettings.getAction();

        if (pingProtectionSettings.getRoles() == null) return;

        for (String id : pingProtectionSettings.getRoles()) {
            if (RoleUtil.hasRole(event.getMember(), id))
                return; // Don't warn someone if they have a protected role
        }

        boolean hasPinged = false;
        for (Member member : event.getMessage().getMentionedMembers()) {

            if (member.getUser().isBot()) return;
            if (member.getId().equals(event.getMember().getId())) return;

            for (String string : pingProtectionSettings.getRoles()) {
                if (RoleUtil.hasRole(member, string)) {
                    hasPinged = true;
                    ping++;
                    break;
                }
            }
        }

        if (ping > 0 && hasPinged) {

            if (ping % threshold == 0) {
                completeAction(action, event);
                pingProtectionUserData.setCount(0).save();
            } else {

                pingProtectionUserData.setCount(ping).save();

                EmbedBuilder builder = new EmbedBuilder()
                        .setDescription("Please don't ping members with ping protected roles")
                        .setColor(EmbedColour.NO.getColour());

                if (ping == (threshold - 1))
                    builder.setFooter("Warning " + ping + " out of " + (threshold - 1) + " (Last Warning)");
                else builder.setFooter("Warning " + ping + " out of " + (threshold - 1));

                event.getChannel().sendMessageEmbeds(builder.build()).queue();
            }
        }
    }


    public void completeAction(String action, GuildMessageReceivedEvent event) {

        action += action.equals("ban") ?
                "ned" :
                "ed";

        event.getMessage().replyEmbeds(new EmbedBuilder()
                .setDescription(event.getMember().getUser().getAsTag() + " has been " + action + " for bypassing the illegal ping threshold!")
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();

        switch (action) {
            case "kicked":
                if (!event.getGuild().getSelfMember().hasPermission(Permission.KICK_MEMBERS)) break;
                event.getMember().kick("Bypassing the illegal ping count!").queue();
                break;
            case "banned":
                if (!event.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS)) break;
                event.getMember().ban(0, "Bypassing the illegal ping count!").queue();
                break;
            case "warned":
                new Warning(null)
                        .setGuildId(event.getGuild().getId())
                        .setUserId(event.getMember().getId())
                        .setModeratorId(event.getJDA().getSelfUser().getId())
                        .setContent("Excessive mentioning of staff")
                        .setTimeCreated(System.currentTimeMillis())
                        .save();
                break;
        }
    }
}
