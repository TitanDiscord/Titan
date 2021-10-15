package me.anutley.titan.commands.dev;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ShutdownCommand extends DevBaseCommand {

    @Override
    public void onDevCommand(GuildMessageReceivedEvent event) {

        event.getMessage().addReaction("accepted:898671459126378517").queue();
        event.getJDA().shutdown();
    }

    @Override
    public String getName() {
        return "shutdown";
    }

}
