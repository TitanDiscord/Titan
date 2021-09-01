package me.anutley.titan.commands.dev;

import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class ShutdownCommand {

    public void shutdownCommand(SlashCommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Shutting down...")
                .setColor(EmbedColour.NEUTRAL.getColour());

        event.replyEmbeds(builder.build()).queue();
        event.getJDA().shutdown();
    }



}
