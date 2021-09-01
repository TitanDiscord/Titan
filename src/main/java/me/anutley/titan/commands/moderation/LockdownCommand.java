package me.anutley.titan.commands.moderation;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.SQLiteDataSource;
import me.anutley.titan.database.util.GuildSettingsDBUtil;
import me.anutley.titan.util.PermissionUtil;
import me.anutley.titan.util.RoleUtil;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LockdownCommand extends Command {

    public static CommandData LockdownCommandData = new CommandData("lockdown", "Toggles lockdown for the server. Members are kicked while trying to join if lockdown is enabled")
            .addSubcommands(new SubcommandData("enable", "Enables lockdown for the server"))
            .addSubcommands(new SubcommandData("disable", "Disables lockdown for the server"))
            .addSubcommands(new SubcommandData("status", "Shows whether lockdown is enabled or not"));

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (!event.getName().equals("lockdown")) return;

        if (RoleUtil.isAdmin(event.getMember())) {
            if (event.getSubcommandName().equals("enable")) toggleLockdown(event, true);
            if (event.getSubcommandName().equals("disable")) toggleLockdown(event, false);
            if (event.getSubcommandName().equals("status")) lockdownStatus(event);
        } else {
            event.replyEmbeds(PermissionUtil.needAdminEmbed(event).build()).setEphemeral(true).queue();
        }
    }

    public void toggleLockdown(SlashCommandEvent event, boolean bool) {
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("UPDATE guild_settings SET lockdown = ? WHERE guild_id = ? ")) {

            preparedStatement.setBoolean(1, bool);
            preparedStatement.setString(2, event.getGuild().getId());
            preparedStatement.executeUpdate();

            EmbedBuilder builder = new EmbedBuilder();
            if (bool) builder.setTitle("Lockdown has been enabled! Anyone who tries to join while lockdown is enabled will be kicked");
            else builder.setTitle("Lockdown has been disabled! Members can now join");

            builder.setColor(EmbedColour.YES.getColour());

            event.replyEmbeds(builder.build()).queue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void lockdownStatus(SlashCommandEvent event) {
        boolean lockdown = GuildSettingsDBUtil.isLockdownEnabled(event.getGuild());

        EmbedBuilder builder = new EmbedBuilder()
                .setColor(EmbedColour.NEUTRAL.getColour());

        if (lockdown) builder.setTitle("Lockdown is currently enabled");
        else builder.setTitle("Lockdown is currently disabled");

        event.replyEmbeds(builder.build()).queue();
    }

    @Override
    public String getCommandName() {
        return "lockdown";
    }

    @Override
    public String getCommandDescription() {
        return "Toggles lockdown for the server. Members are kicked while trying to join if lockdown is enabled";
    }

    @Override
    public String getCommandUsage() {
        return "/lockdown <enable/disable>";
    }
}
