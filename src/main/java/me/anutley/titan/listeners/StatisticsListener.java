package me.anutley.titan.listeners;

import me.anutley.titan.Config;
import me.anutley.titan.Titan;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class StatisticsListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {

        String serversChannelId = getOrCreateChannel("Total Servers:").getId();
        String usersChannelId = getOrCreateChannel("Total Users:").getId();

        long servers = event.getJDA().getGuilds().size();
        AtomicLong totalUsers = new AtomicLong();

        for (Guild guild : event.getJDA().getGuilds())
            totalUsers.set(totalUsers.get() + guild.getMemberCount());

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                () -> ForkJoinPool.commonPool().execute(
                        () -> {
                            event.getJDA().getVoiceChannelById(serversChannelId).getManager().setName("Total Servers: " + servers).queue();
                            event.getJDA().getVoiceChannelById(usersChannelId).getManager().setName("Total Users: " + totalUsers.get()).queue();
                        }
                ), 0, 10, TimeUnit.MINUTES);

    }

    public Category getCategory() {
        return Titan.getJda().getCategoryById(Config.getInstance().get("STATISTICS_CATEGORY"));
    }

    public VoiceChannel getOrCreateChannel(String name) {
        return getCategory().getVoiceChannels().stream()
                .filter(c -> c.getName().startsWith(name))
                .min(Comparator.comparing(GuildChannel::getPosition))
                .orElseGet(() -> getCategory().createVoiceChannel(name)
                        .addPermissionOverride(getCategory().getGuild().getPublicRole(), null, Collections.singleton(Permission.VOICE_CONNECT))
                        .complete()
                );
    }
}
