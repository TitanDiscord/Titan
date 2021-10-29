package me.anutley.titan.listeners;

import me.anutley.titan.database.objects.GuildSettings;
import me.anutley.titan.database.objects.LeaveSettings;
import me.anutley.titan.database.objects.WelcomeSettings;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberJoinLeaveListener extends ListenerAdapter {

    String userid;

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        if (new GuildSettings(event.getGuild().getId()).isLockdown()) {
            userid = event.getUser().getId();
            return;
        }

        WelcomeSettings welcomeSettings = new WelcomeSettings(event.getGuild().getId());

        if (welcomeSettings.getRoleId() != null) {
            if (!event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) return;
            if (!event.getGuild().getSelfMember().canInteract(welcomeSettings.getRole()))
                return;
            event.getGuild().addRoleToMember(event.getMember().getId(), welcomeSettings.getRole()).queue();
        }

        if (!welcomeSettings.isEnabled()) return;
        if (welcomeSettings.getChannelId() == null) return;

        String message = replacePlaceholders(welcomeSettings.getMessage(), event.getGuild(), event.getUser());

        EmbedBuilder builder = new EmbedBuilder();

        if (message.contains("-showavatar")) {
            message = message.replaceAll("-showavatar", " ");
            builder.setThumbnail(event.getUser().getAvatarUrl());
        }

        event.getGuild().getTextChannelById(welcomeSettings.getChannelId()).sendMessageEmbeds(builder
                .setColor(EmbedColour.YES.getColour())
                .setDescription(message.trim()).build()).queue();
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {

        if (event.getUser().getId().equals(userid)) return;

        LeaveSettings leaveSettings = new LeaveSettings(event.getGuild().getId());

            if (!leaveSettings.isEnabled()) return;
            if (leaveSettings.getChannelId() == null) return;

            String message = replacePlaceholders(leaveSettings.getMessage(), event.getGuild(), event.getUser());

            EmbedBuilder builder = new EmbedBuilder();

            if (message.contains("-showavatar")) {
                message = message.replaceAll("-showavatar", " ");
                builder.setThumbnail(event.getUser().getAvatarUrl());
            }

            event.getGuild().getTextChannelById(leaveSettings.getChannelId()).sendMessageEmbeds(builder
                    .setColor(EmbedColour.YES.getColour())
                    .setDescription(message.trim()).build()).queue();
    }


    public String replacePlaceholders(String str, Guild guild, User user) {
        return str.replaceAll("%user%", user.getAsMention())
                .replaceAll("%username%", user.getName())
                .replaceAll("%username_with_discriminator%", user.getAsTag())
                .replaceAll("%discriminator%", user.getDiscriminator())
                .replaceAll("%guild_name%", guild.getName());
    }

}
