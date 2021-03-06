package me.anutley.titan.listeners;


import me.anutley.titan.database.objects.GuildSettings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;

public class LockdownListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        if (!event.getGuild().getSelfMember().hasPermission(Permission.KICK_MEMBERS)) return;
            if (new GuildSettings(event.getGuild().getId()).isLockdown()) {
                event.getUser().openPrivateChannel().complete()
                        .sendMessage("Lockdown is currently enabled in " + event.getGuild().getName() + ". Please try joining again later!").queue(null, new ErrorHandler().ignore(ErrorResponse.CANNOT_SEND_TO_USER));
                event.getMember().kick("Tried to join while lockdown was enabled").queue();

            }

    }
}
