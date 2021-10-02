package me.anutley.titan.commands.settings;

import me.anutley.titan.database.objects.GuildSettings;
import me.anutley.titan.util.PermissionUtil;
import me.anutley.titan.util.RoleUtil;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.Objects;

public class GuildSettingsCommand {

    public static SubcommandGroupData GuildSettingsCommandData = new SubcommandGroupData("guild", "Controls Titan's guild settings")
            .addSubcommands(new SubcommandData("adminrole", "Controls the guilds admin role")
                    .addOption(OptionType.ROLE, "role", "The role to set the guild's admin role to"))
            .addSubcommands(new SubcommandData("modrole", "Controls the guilds moderator role")
                    .addOption(OptionType.ROLE, "role", "The role to set the guild's moderator role to"));

    public void guildSettingsCommand(SlashCommandEvent event) {
        if (Objects.equals(event.getSubcommandName(), "adminrole")) modifyGuildAdminRole(event);
        if (event.getSubcommandName().equals("modrole")) modifyGuildModRole(event);
    }

    public void modifyGuildAdminRole(SlashCommandEvent event) {

        if (!event.getMember().isOwner()) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Only the Guild Owner (" + event.getGuild().getOwner().getUser().getAsTag() + ") can run this command!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
            return;
        }

        GuildSettings guildSettings = new GuildSettings(event.getGuild().getId());

        if (event.getOption("role") == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("This guilds admin role is " + guildSettings.getAdminRole().getAsMention())
                    .setColor(EmbedColour.NEUTRAL.getColour())
                    .build()).queue();
            return;
        }

        guildSettings.setAdminRoleId(event.getOption("role").getAsRole().getId())
                .save();

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("The guild's admin role has been set to " + event.getOption("role").getAsRole().getAsMention() + "!")
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();


    }

    public void modifyGuildModRole(SlashCommandEvent event) {

        if (!RoleUtil.isAdmin(event.getMember())) {
            event.replyEmbeds(PermissionUtil.needAdminEmbed(event).build()).setEphemeral(true).queue();
            return;
        }

        GuildSettings guildSettings = new GuildSettings(event.getGuild().getId());
        if (event.getOption("role") == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("This guilds mod role is " + guildSettings.getModRole().getAsMention())
                    .setColor(EmbedColour.NEUTRAL.getColour())
                    .build()).queue();
            return;
        }


        guildSettings.setModRoleId(event.getOption("role").getAsRole().getId()).save();

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("The guild's mod role has been set to " + event.getOption("role").getAsRole().getAsMention() + "!")
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();

    }

}
