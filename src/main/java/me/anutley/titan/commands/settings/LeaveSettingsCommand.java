package me.anutley.titan.commands.settings;

import me.anutley.titan.database.SQLiteDataSource;
import me.anutley.titan.util.embeds.errors.NotTextChannelEmbed;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LeaveSettingsCommand {


    public static SubcommandGroupData LeaveSettingsCommandData = new SubcommandGroupData("leave", "Controls Titan's leave message settings")
            .addSubcommands(new SubcommandData("enable", "Enables Titan's leave messages"))
            .addSubcommands(new SubcommandData("disable", "Disables Titan's leave nessages"))
            .addSubcommands(new SubcommandData("channel", "Changes the channel Titan should send leave messages to")
                    .addOption(OptionType.CHANNEL, "channel", "The channel Titan should send leave messages to", true))
            .addSubcommands(new SubcommandData("message", "The message Titan should send when someone leaves (Send without option to get the placeholders)")
                    .addOption(OptionType.STRING, "message", "The message Titan should send when someone leaves"));

    public void leaveSettingsCommand(SlashCommandEvent event) {
        if (event.getSubcommandName().equals("enable")) toggleLeave(event, true);
        if (event.getSubcommandName().equals("disable")) toggleLeave(event, false);
        if (event.getSubcommandName().equals("channel")) changeLeaveChannel(event);
        if (event.getSubcommandName().equals("message")) changeLeaveMessage(event);
    }

    public void toggleLeave(SlashCommandEvent event, boolean bool) {
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("UPDATE leave SET enabled = ? WHERE guild_id = ? ")) {

            preparedStatement.setBoolean(1, bool);
            preparedStatement.setString(2, event.getGuild().getId());
            preparedStatement.executeUpdate();

            EmbedBuilder builder = new EmbedBuilder();
            if (bool) builder.setTitle("Leave messages has been enabled!");
            else builder.setTitle("Leave messages has been disabled!");

            builder.setColor(EmbedColour.YES.getColour());

            event.replyEmbeds(builder.build()).queue();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeLeaveChannel (SlashCommandEvent event) {

        if (!event.getOption("channel").getChannelType().equals(ChannelType.TEXT)) {
            event.replyEmbeds(NotTextChannelEmbed.Embed().build()).queue();
            return;
        }

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("UPDATE leave SET channel_id = ? WHERE guild_id = ? ")) {

            preparedStatement.setString(1, event.getOption("channel").getAsGuildChannel().getId());
            preparedStatement.setString(2, event.getGuild().getId());
            preparedStatement.executeUpdate();

            EmbedBuilder builder = new EmbedBuilder();
            builder.setDescription("The leave channel has been set to " + event.getOption("channel").getAsGuildChannel().getAsMention());

            builder.setColor(EmbedColour.YES.getColour());

            event.replyEmbeds(builder.build()).queue();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeLeaveMessage (SlashCommandEvent event) {

        if (event.getOption("message") == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Available Placeholders:")
                    .setColor(EmbedColour.NEUTRAL.getColour())
                    .setDescription("%user% - The user who left as a mention\n" +
                            "%username% - The username of the user\n" +
                            "%username_with_discriminator% - The username of the user with a discriminator\n" +
                            "%discriminator - The discriminator of the user\n" +
                            "%guild_name% - The name of the guild\n" +
                            "End the message with `-showavatar`, to show add the users avatar to the join message").build()).queue();
        }

        else {

            try (final Connection connection = SQLiteDataSource
                    .getConnection();
                 PreparedStatement preparedStatement = connection
                         .prepareStatement("UPDATE leave SET message = ? WHERE guild_id = ? ")) {

                preparedStatement.setString(1, event.getOption("message").getAsString());
                preparedStatement.setString(2, event.getGuild().getId());
                preparedStatement.executeUpdate();

                EmbedBuilder builder = new EmbedBuilder();
                builder.setDescription("The leave message has been set to " + event.getOption("message").getAsString());

                builder.setColor(EmbedColour.YES.getColour());

                event.replyEmbeds(builder.build()).queue();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
