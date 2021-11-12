package me.anutley.titan.listeners;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {

        Logger logger = LoggerFactory.getLogger(ReadyListener.class);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                () -> ForkJoinPool.commonPool().execute(
                        () -> event.getJDA().getPresence().setActivity(Activity.watching("over " + event.getJDA().getGuilds().size() + " guilds! | /help"))
                ), 0, 1, TimeUnit.HOURS);

        logger.info(event.getJDA().getSelfUser().getAsTag() + " is ready in " + event.getJDA().getGuilds().size() + " guilds!");


    }
}
