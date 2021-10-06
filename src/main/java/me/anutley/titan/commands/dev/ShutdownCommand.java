package me.anutley.titan.commands.dev;

import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ShutdownCommand extends DevBaseCommand {

    @Override
    public void onDevCommand(GuildMessageReceivedEvent event) {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Shutting down...")
                .setColor(EmbedColour.NEUTRAL.getColour());

        event.getChannel().sendMessageEmbeds(builder.build()).queue();
        event.getJDA().shutdown();
    }

    @Override
    public String getName() {
        return "shutdown";
    }

}
