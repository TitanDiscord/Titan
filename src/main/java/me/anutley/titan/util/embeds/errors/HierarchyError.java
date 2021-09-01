package me.anutley.titan.util.embeds.errors;

import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class HierarchyError {
    public static EmbedBuilder Embed(SlashCommandEvent event) {

        return new EmbedBuilder()
                .setAuthor(event.getUser().getAsTag(), null, event.getUser().getAvatarUrl())
                .setDescription("Could not perform this action, due to the target's highest role being higher or equal to mine!")
                .setColor(EmbedColour.NO.getColour());
    }
}
