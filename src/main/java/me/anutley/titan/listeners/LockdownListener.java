package me.anutley.titan.listeners;


import me.anutley.titan.database.util.GuildSettingsDBUtil;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LockdownListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        boolean lockdown = GuildSettingsDBUtil.isLockdownEnabled(event.getGuild());

            if (lockdown) {
                event.getUser().openPrivateChannel().complete()
                        .sendMessage("Lockdown is currently enabled in " + event.getGuild().getName() + ". Please try joining again later!").queue();
                event.getMember().kick("Tried to join while lockdown was enabled").queue();

            }

    }
}
