package me.anutley.titan.util.embeds.errors;

import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;

public class NotTextChannelEmbed {
    public static EmbedBuilder Embed() {

        EmbedBuilder builder = new EmbedBuilder();

        return builder.setTitle("This channel is not a text channel!")
                .setColor(EmbedColour.NO.getColour());
    }
}
