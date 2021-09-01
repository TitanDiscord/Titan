package me.anutley.titan.listeners;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {

        Logger logger = LoggerFactory.getLogger(ReadyListener.class);

        logger.info(event.getJDA().getSelfUser().getAsTag() + " is ready in " + event.getJDA().getGuilds().size() + " guilds!");

        event.getJDA().getPresence().setActivity(Activity.watching("over " + event.getJDA().getGuilds().size() + " guilds!"));

    }
}
