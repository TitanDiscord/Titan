package me.anutley.titan.commands.settings;

import me.anutley.titan.database.SQLiteDataSource;
import me.anutley.titan.util.PermissionUtil;
import me.anutley.titan.util.RoleUtil;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GuildSettingsCommand {

    public static SubcommandGroupData GuildSettingsCommandData = new SubcommandGroupData("guild", "Controls Titan's guild settings")
            .addSubcommands(new SubcommandData("adminrole", "Controls the guilds admin role")
                    .addOption(OptionType.ROLE, "role", "The role to set the guild's admin role to"))
            .addSubcommands(new SubcommandData("modrole", "Controls the guilds moderator role")
                    .addOption(OptionType.ROLE, "role", "The role to set the guild's moderator role to"));

    public void guildSettingsCommand(SlashCommandEvent event) {
        if (event.getSubcommandName().equals("adminrole")) modifyGuildAdminRole(event);
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

        if (event.getOption("role") == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("This guilds admin role is " + RoleUtil.getAdminRole(event.getGuild()).getAsMention())
                    .setColor(EmbedColour.NEUTRAL.getColour())
                    .build()).queue();
            return;
        }

        try (final Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("UPDATE guild_settings set guild_admin_role= ? where guild_id = ?")) {

            preparedStatement.setString(1, event.getOption("role").getAsRole().getId());
            preparedStatement.setString(2, event.getGuild().getId());

            preparedStatement.executeUpdate();

            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("The guild's admin role has been set to " + event.getOption("role").getAsRole().getAsMention() + "!")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void modifyGuildModRole(SlashCommandEvent event) {

        if (!RoleUtil.isAdmin(event.getMember())) {
            event.replyEmbeds(PermissionUtil.needAdminEmbed(event).build()).setEphemeral(true).queue();
            return;
        }

        if (event.getOption("role") == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("This guilds mod role is " + RoleUtil.getModRole(event.getGuild()).getAsMention())
                    .setColor(EmbedColour.NEUTRAL.getColour())
                    .build()).queue();
            return;
        }


        try (final Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("UPDATE guild_settings set guild_mod_role = ? where guild_id = ?")) {

            preparedStatement.setString(1, event.getOption("role").getAsRole().getId());
            preparedStatement.setString(2, event.getGuild().getId());

            preparedStatement.executeUpdate();

            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("The guild's mod role has been set to " + event.getOption("role").getAsRole().getAsMention() + "!")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
