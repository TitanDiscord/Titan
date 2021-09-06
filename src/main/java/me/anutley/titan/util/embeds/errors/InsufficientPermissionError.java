package me.anutley.titan.util.embeds.errors;

import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class InsufficientPermissionError {
    public static EmbedBuilder Embed(SlashCommandEvent event, Permission permission) {

        return new EmbedBuilder()
                .setAuthor(event.getUser().getAsTag(), null, event.getUser().getAvatarUrl())
                .setDescription("Could not perform this action, due to me not having the required permission `" + permission.toString() + "`!")
                .setColor(EmbedColour.NO.getColour());
    }
}
