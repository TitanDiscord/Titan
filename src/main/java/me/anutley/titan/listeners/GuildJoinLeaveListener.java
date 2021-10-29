package me.anutley.titan.listeners;

import me.anutley.titan.Config;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicLong;

public class GuildJoinLeaveListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        TextChannel channel = event.getJDA().getTextChannelById(Config.getInstance().get("GUILD_LOG_CHANNEL"));

        event.getGuild().loadMembers().onSuccess(members -> {
            channel.sendMessageEmbeds(new EmbedBuilder()
                    .setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
                    .setColor(EmbedColour.YES.getColour())
                    .setTitle("Guild Join")
                    .addField("ID", event.getGuild().getId(), false)
                    .addField("Members", String.valueOf(members.size()), false)
                    .addField("Bots", String.valueOf(membersWhoAreBots(event.getGuild())), false)
                    .setFooter("Owner: " + event.getGuild().getOwner().getUser().getAsTag(), event.getGuild().getOwner().getUser().getAvatarUrl())
                    .build()).queue();
        });
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        TextChannel channel = event.getJDA().getTextChannelById(Config.getInstance().get("GUILD_LOG_CHANNEL"));

        channel.sendMessageEmbeds(new EmbedBuilder()
                .setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
                .setColor(EmbedColour.NO.getColour())
                .setTitle("Guild Left")
                .addField("ID", event.getGuild().getId(), false)
                .addField("Members", String.valueOf(event.getGuild().getMemberCount()), false)
                .build()).queue();
    }


    public AtomicLong membersWhoAreBots(Guild guild) {
        AtomicLong atomicLong = new AtomicLong();
        guild.loadMembers().onSuccess(members -> {
            for (Member member : members)
                if (member.getUser().isBot())
                    atomicLong.incrementAndGet();
        });

        return atomicLong;
    }
}
