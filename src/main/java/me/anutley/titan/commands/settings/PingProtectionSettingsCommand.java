package me.anutley.titan.commands.settings;

import me.anutley.titan.database.SQLiteDataSource;
import me.anutley.titan.database.util.IllegalPingCountUtil;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class PingProtectionSettingsCommand {

    public static SubcommandGroupData PingProtectionSettingsCommandData = new SubcommandGroupData("pingprotection", "Controls Titan's ping protection settings")
            .addSubcommands(new SubcommandData("enable", "Enables ping protection for this guild"))
            .addSubcommands(new SubcommandData("disable", "Disables ping protection for this guild"))
            .addSubcommands(new SubcommandData("threshold", "Modifies the threshold of illegal pings someone can make before an action of your choice is taken")
                    .addOption(OptionType.INTEGER, "amount", "Amount of pings", true))
            .addSubcommands(new SubcommandData("add", "Adds a role to the ping protection")
                    .addOption(OptionType.ROLE, "role", "The role to add to the ping protection", true))
            .addSubcommands(new SubcommandData("remove", "Removes a role from the ping protection")
                    .addOption(OptionType.ROLE, "role", "The role to remove from the ping protection", true))
            .addSubcommands(new SubcommandData("list", "Lists all the roles that are protected from pings"))
            .addSubcommands(new SubcommandData("resetpings", "Resets a members illegal ping count")
                    .addOption(OptionType.USER, "member", "The member whose illegal ping count you want to reset", true))
            .addSubcommands(new SubcommandData("action", "Changes the action that Titan will take when someone surpasses the illegal ping threshold")
                    .addOption(OptionType.STRING, "action", "Possible options: warn, kick, ban", true));


    public void pingProtectionSettingsCommand(SlashCommandEvent event) {
        if (event.getSubcommandName().equals("enable")) togglePingProtection(event, true);
        if (event.getSubcommandName().equals("disable")) togglePingProtection(event, false);
        if (event.getSubcommandName().equals("threshold")) modifyPingThreshold(event);
        if (event.getSubcommandName().equals("add")) addRoleToPingProtection(event, true);
        if (event.getSubcommandName().equals("remove")) addRoleToPingProtection(event, false);
        if (event.getSubcommandName().equals("resetpings")) resetIllegalPings(event);
        if (event.getSubcommandName().equals("action")) changeActionWhenPingsSurpassThreshold(event);
        if (event.getSubcommandName().equals("list")) listPingProtectedRoles(event);
    }

    public void togglePingProtection(SlashCommandEvent event, boolean bool) {
        event.deferReply();
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("UPDATE ping_protection_settings SET enabled = ? WHERE guild_id = ? ")) {

            preparedStatement.setBoolean(1, bool);
            preparedStatement.setString(2, event.getGuild().getId());
            preparedStatement.executeUpdate();

            EmbedBuilder builder = new EmbedBuilder();
            if (bool) builder.setTitle("Ping protection has been enabled!");
            else builder.setTitle("Ping protection has been disabled!");

            builder.setColor(EmbedColour.YES.getColour());

            event.replyEmbeds(builder.build()).queue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifyPingThreshold(SlashCommandEvent event) {
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("UPDATE ping_protection_settings set threshold = ? where guild_id = ?")) {

            preparedStatement.setInt(1, (int) event.getOption("amount").getAsLong());
            preparedStatement.setString(2, event.getGuild().getId());

            preparedStatement.executeUpdate();

            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("The ping protection threshold has been set to " + event.getOption("amount").getAsString())
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addRoleToPingProtection(SlashCommandEvent event, boolean bool) {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * from ping_protection_settings WHERE guild_id = ? ")) {
            preparedStatement.setString(1, event.getGuild().getId());

            ResultSet getPingProtectedRolesResult = preparedStatement.executeQuery();

            ArrayList<String> pingProtectedRoles = new ArrayList<String>();

            if (getPingProtectedRolesResult.getString("roles") != null) {

                ArrayList<String> pingProtectedRolesAsArrayList = new ArrayList<String>(Arrays.asList(getPingProtectedRolesResult.getString("roles")
                        .replaceAll("\\[", "")
                        .replaceAll("]", "")
                        .split(",")));

                if (bool) {
                    if (getPingProtectedRolesResult.getString("roles").contains(event.getOption("role").getAsRole().getId())) {
                        event.replyEmbeds(new EmbedBuilder()
                                .setTitle("This role is already protected from pings!")
                                .setColor(EmbedColour.NO.getColour())
                                .build()).queue();
                        return;
                    }

                    event.replyEmbeds(new EmbedBuilder()
                            .setDescription(event.getOption("role").getAsRole().getAsMention() + " is now protected from pings!")
                            .setColor(EmbedColour.YES.getColour())
                            .build()).queue();

                    pingProtectedRolesAsArrayList.add(event.getOption("role").getAsRole().getId());
                }

                if (!bool) {
                    if (!getPingProtectedRolesResult.getString("roles").contains(event.getOption("role").getAsRole().getId())) {
                        event.replyEmbeds(new EmbedBuilder()
                                .setTitle("This role is not currently protected from pings!")
                                .setColor(EmbedColour.NO.getColour())
                                .build()).queue();
                        return;
                    }

                    event.replyEmbeds(new EmbedBuilder()
                            .setDescription(event.getOption("role").getAsRole().getAsMention() + " is no longer protected from pings!")
                            .setColor(EmbedColour.YES.getColour())
                            .build()).queue();

                    pingProtectedRolesAsArrayList.remove(event.getOption("role").getAsRole().getId());


                }
                pingProtectedRoles = pingProtectedRolesAsArrayList;

            } else {
                if (!bool) {
                    event.replyEmbeds(new EmbedBuilder()
                            .setTitle("This role is not protected from pings!")
                            .setColor(EmbedColour.NO.getColour())
                            .build()).queue();
                    return;
                } else {
                    pingProtectedRoles.add(event.getOption("role").getAsRole().getId());

                    event.replyEmbeds(new EmbedBuilder()
                            .setTitle(event.getOption("role").getAsRole().getAsMention() + " is now protected from pings!")
                            .setColor(EmbedColour.YES.getColour())
                            .build()).queue();

                }
            }


            String[] protectedRolesArray = pingProtectedRoles.toArray(new String[pingProtectedRoles.size()]);


            PreparedStatement addPingProtectedRoleUpdate =
                    connection.prepareStatement("UPDATE ping_protection_settings set roles = ? where guild_id = ?");
            {

                addPingProtectedRoleUpdate.setString(1, !Arrays.toString(protectedRolesArray).equals("[]") ? Arrays.toString(protectedRolesArray).replaceAll(" ", "") : null);
                addPingProtectedRoleUpdate.setString(2, event.getGuild().getId());

                addPingProtectedRoleUpdate.executeUpdate();


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listPingProtectedRoles(SlashCommandEvent event) {
        try (final Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM ping_protection_settings where guild_id = ?")) {

            preparedStatement.setString(1, event.getGuild().getId());

            ResultSet result = preparedStatement.executeQuery();

            if (result.getString("roles") == null) {
                event.replyEmbeds(new EmbedBuilder()
                        .setTitle("There are no ping protected roles!")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).queue();
                return;
            }

            String[] pingProtectedRoles = result.getString("roles")
                    .replaceAll("\\[", "")
                    .replaceAll("]", "")
                    .split(",");

            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle("Ping Protected Roles")
                    .setColor(EmbedColour.NEUTRAL.getColour());

            String content = "";

            for (String string : pingProtectedRoles) {
                content += event.getGuild().getRoleById(string).getAsMention();
            }

            builder.setDescription(content);

            event.replyEmbeds(builder.build()).queue();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetIllegalPings(SlashCommandEvent event) {

        if (!(IllegalPingCountUtil.resetCount(event.getOption("member").getAsMember(), event.getGuild()))) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription(event.getOption("member").getAsMember().getAsMention() + " is not in the illegal ping database, as they have not pinged anyone yet!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
        } else {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription(event.getOption("member").getAsUser().getAsMention() + "'s pings have been reset!")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();
        }

    }

    public void changeActionWhenPingsSurpassThreshold(SlashCommandEvent event) {

        if (!event.getOption("action").getAsString().equals("warn") &&
                !event.getOption("action").getAsString().equals("kick") &&
                !event.getOption("action").getAsString().equals("ban")) {

            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("That is not a valid option. You can choose between: warn, kick or ban!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
        } else {
            try (PreparedStatement updateAction = SQLiteDataSource.getConnection()
                    .prepareStatement("update ping_protection_settings set action = ? where guild_id = ? ")) {

                updateAction.setString(1, event.getOption("action").getAsString());
                updateAction.setString(2, event.getGuild().getId());

                updateAction.executeUpdate();

                event.replyEmbeds(new EmbedBuilder()
                        .setTitle("The action when someone surpasses the illegal ping threshold has been set to `" + event.getOption("action").getAsString() + "`!")
                        .setColor(EmbedColour.YES.getColour())
                        .build()).queue();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
