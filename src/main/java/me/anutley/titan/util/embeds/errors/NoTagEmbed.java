package me.anutley.titan.util.embeds.errors;

import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;

public class NoTagEmbed {
    public static EmbedBuilder Embed() {

        EmbedBuilder builder = new EmbedBuilder();

        return builder.setTitle("This tag does not exist!")
                .setColor(EmbedColour.NO.getColour());
    }
}
