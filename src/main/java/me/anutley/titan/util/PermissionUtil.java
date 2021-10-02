package me.anutley.titan.util;

import me.anutley.titan.database.objects.GuildSettings;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class PermissionUtil {

    public static EmbedBuilder needRoleEmbed(SlashCommandEvent event, Role role) {

        return new EmbedBuilder()
                .setAuthor(event.getUser().getAsTag(), null, event.getUser().getAvatarUrl())
                .setDescription("```You do not have the required role to run this command. This command can only be ran by those with the moderator role or higher: ```" + role.getAsMention())
                .setColor(EmbedColour.NO.getColour());
    }

    public static EmbedBuilder needAdminEmbed(SlashCommandEvent event) {

        return new EmbedBuilder()
                .setAuthor(event.getUser().getAsTag(), null, event.getUser().getAvatarUrl())
                .setDescription("```You do not have the required roles to run this command. This command can only be ran by a member with the administrator role: ``` " + new GuildSettings(event.getGuild().getId()).getAdminRole().getAsMention())
                .setColor(EmbedColour.NO.getColour());
    }

}
