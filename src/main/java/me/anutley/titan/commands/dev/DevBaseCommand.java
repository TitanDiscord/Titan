package me.anutley.titan.commands.dev;

import me.anutley.titan.Config;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public abstract class DevBaseCommand extends ListenerAdapter {

    public abstract void onDevCommand(GuildMessageReceivedEvent event);
    public abstract String getName();

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Message message = event.getMessage();

        if (!message.getMentionedUsers().contains(message.getJDA().getSelfUser())) return;
        if (!message.getAuthor().getId().equals(Config.getInstance().get("BOT_OWNER"))) return;

        String[] args = message.getContentRaw().split(" ");
        if (args.length < 2 || !args[1].equalsIgnoreCase(getName())) return;

        onDevCommand(event);
    }

}
