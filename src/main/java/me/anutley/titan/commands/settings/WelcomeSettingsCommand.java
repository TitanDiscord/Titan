package me.anutley.titan.commands.settings;

import me.anutley.titan.database.SQLiteDataSource;
import me.anutley.titan.database.util.WelcomeUtil;
import me.anutley.titan.util.PermissionUtil;
import me.anutley.titan.util.RoleUtil;
import me.anutley.titan.util.embeds.errors.HierarchyError;
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

public class WelcomeSettingsCommand {


    public static SubcommandGroupData WelcomeSettingsCommandData = new SubcommandGroupData("welcome", "Controls Titan's welcome message settings")
            .addSubcommands(new SubcommandData("enable", "Enables Titan welcoming new members"))
            .addSubcommands(new SubcommandData("disable", "Disables Titan welcoming new members"))
            .addSubcommands(new SubcommandData("channel", "Changes the channel Titan should send welcome messages to")
                    .addOption(OptionType.CHANNEL, "channel", "The channel Titan should send welcome messages to", true))
            .addSubcommands(new SubcommandData("message", "The message Titan should send when someone joins (Send without option to get a list of placeholders)")
                    .addOption(OptionType.STRING, "message", "The message Titan should send when someone joins"))
            .addSubcommands(new SubcommandData("role", "The role Titan should give to users when they join. Set to @everyone to disable")
                    .addOption(OptionType.ROLE, "role", "The role Titan should give to users when they join"));

    public void welcomeSettingsCommand(SlashCommandEvent event) {
        if (event.getSubcommandName().equals("enable")) toggleWelcome(event, true);
        if (event.getSubcommandName().equals("disable")) toggleWelcome(event, false);
        if (event.getSubcommandName().equals("channel")) changeWelcomeChannel(event);
        if (event.getSubcommandName().equals("message")) changeWelcomeMessage(event);
        if (event.getSubcommandName().equals("role")) changeWelcomeRole(event);
    }

    public void toggleWelcome(SlashCommandEvent event, boolean bool) {
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("UPDATE welcomes SET enabled = ? WHERE guild_id = ? ")) {

            preparedStatement.setBoolean(1, bool);
            preparedStatement.setString(2, event.getGuild().getId());
            preparedStatement.executeUpdate();

            EmbedBuilder builder = new EmbedBuilder();
            if (bool) builder.setTitle("Welcome messages has been enabled!");
            else builder.setTitle("Welcome messages has been disabled!");

            builder.setColor(EmbedColour.YES.getColour());

            event.replyEmbeds(builder.build()).queue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeWelcomeChannel(SlashCommandEvent event) {

        if (!event.getOption("channel").getChannelType().equals(ChannelType.TEXT)) {
            event.replyEmbeds(NotTextChannelEmbed.Embed().build()).queue();
            return;
        }

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("UPDATE welcomes SET channel_id = ? WHERE guild_id = ? ")) {

            preparedStatement.setString(1, event.getOption("channel").getAsGuildChannel().getId());
            preparedStatement.setString(2, event.getGuild().getId());
            preparedStatement.executeUpdate();

            EmbedBuilder builder = new EmbedBuilder();
            builder.setDescription("The welcome channel has been set to " + event.getOption("channel").getAsGuildChannel().getAsMention());

            builder.setColor(EmbedColour.YES.getColour());

            event.replyEmbeds(builder.build()).queue();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeWelcomeMessage(SlashCommandEvent event) {

        if (event.getOption("message") == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Available Placeholders:")
                    .setColor(EmbedColour.NEUTRAL.getColour())
                    .setDescription("%user% - The user who joined as a mention\n" +
                            "%username% - The username of the user\n" +
                            "%username_with_discriminator% - The username of the user with a discriminator\n" +
                            "%discriminator - The discriminator of the user\n" +
                            "%guild_name% - The name of the guild\n" +
                            "End the message with `-showavatar`, to show add the users avatar to the join message").build()).queue();
        } else {

            try (final Connection connection = SQLiteDataSource
                    .getConnection();
                 PreparedStatement preparedStatement = connection
                         .prepareStatement("UPDATE welcomes SET message = ? WHERE guild_id = ? ")) {

                preparedStatement.setString(1, event.getOption("message").getAsString());
                preparedStatement.setString(2, event.getGuild().getId());
                preparedStatement.executeUpdate();

                EmbedBuilder builder = new EmbedBuilder();
                builder.setDescription("The welcome message has been set to " + event.getOption("message").getAsString());

                builder.setColor(EmbedColour.YES.getColour());

                event.replyEmbeds(builder.build()).queue();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void changeWelcomeRole(SlashCommandEvent event) {
        if (!RoleUtil.isAdmin(event.getMember())) {
            event.replyEmbeds(PermissionUtil.needAdminEmbed(event).build()).setEphemeral(true).queue();
            return;
        }


        System.out.println("Role:" + event.getOption("role").getAsRole().getPosition());
        System.out.println("Member: " +  RoleUtil.highestRole(event.getGuild().getSelfMember()).getPosition());

        if (event.getOption("role").getAsRole().getPosition() >= RoleUtil.highestRole(event.getGuild().getSelfMember()).getPosition()) {
            event.replyEmbeds(HierarchyError.Embed(event).build()).queue();
            return;
        }

        if (event.getOption("role") == null) {
            if (WelcomeUtil.getWelcomeRole(event.getGuild()) == null) {
                event.replyEmbeds(new EmbedBuilder()
                        .setTitle("No welcome role set!")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).queue();
            } else {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("This guilds welcome role is " + WelcomeUtil.getWelcomeRole(event.getGuild()).getAsMention())
                        .setColor(EmbedColour.NEUTRAL.getColour())
                        .build()).queue();
            }
        } else {
            String id;

            if (event.getOption("role").getAsRole().getId().equals(event.getGuild().getPublicRole().getId())) id = null;
            else id = event.getOption("role").getAsRole().getId();
            try (final Connection connection = SQLiteDataSource.getConnection();
                 PreparedStatement preparedStatement = connection
                         .prepareStatement("UPDATE welcomes set role = ? where guild_id = ?")) {

                preparedStatement.setString(1, id);
                preparedStatement.setString(2, event.getGuild().getId());

                preparedStatement.executeUpdate();

                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("The guild's welcome role has been set to " + event.getOption("role").getAsRole().getAsMention() + "!")
                        .setColor(EmbedColour.YES.getColour())
                        .build()).queue();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
