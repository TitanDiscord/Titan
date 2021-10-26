package me.anutley.titan.util.embeds.errors;

import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class HierarchyError {

    public static EmbedBuilder self(SlashCommandEvent event) {

        return new EmbedBuilder()
                .setAuthor(event.getUser().getAsTag(), null, event.getUser().getAvatarUrl())
                .setDescription("I could not perform this action, due to the target's highest role being higher or equal to mine!")
                .setColor(EmbedColour.NO.getColour());
    }

    public static EmbedBuilder other(SlashCommandEvent event) {

        return new EmbedBuilder()
                .setAuthor(event.getUser().getAsTag(), null, event.getUser().getAvatarUrl())
                .setDescription("You cannot perform this action because the target is either equal or above you in the hierarchy!")
                .setColor(EmbedColour.NO.getColour());
    }
}
