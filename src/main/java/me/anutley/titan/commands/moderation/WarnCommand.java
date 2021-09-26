package me.anutley.titan.commands.moderation;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.SQLiteDataSource;
import me.anutley.titan.database.objects.GuildSettings;
import me.anutley.titan.util.PermissionUtil;
import me.anutley.titan.util.RoleUtil;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class WarnCommand extends Command {

    public static CommandData WarnCommandData = new CommandData("warn", "Warn base command")
            .addSubcommands(new SubcommandData("add", "Warns a user")
                    .addOption(OptionType.USER, "user", "The user to warn", true)
                    .addOption(OptionType.STRING, "reason", "The reason to warn the user", true))
            .addSubcommands(new SubcommandData("list", "Lists all the warnings of a user")
                    .addOption(OptionType.USER, "user", "The user to warn", true))
            .addSubcommands(new SubcommandData("remove", "Removes a warn from a user by its punishment id")
                    .addOption(OptionType.STRING, "id", "The id of the warn you want to remove (Can be found by doing /warn list)", true))
            .addSubcommands(new SubcommandData("clear", "Clears all warns from a user")
                    .addOption(OptionType.USER, "user", "The user whose warns you want to clear"));

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {

        if (!event.getName().equals("warn")) return;

        if (!RoleUtil.isStaff(event.getMember())) {
            if (!event.getSubcommandName().equals("list")) {
                event.replyEmbeds(PermissionUtil.needRoleEmbed(event, new GuildSettings(event.getGuild().getId()).getModRole()).build()).queue();
                return;
            }
        }

        if (event.getSubcommandName().equals("add")) addWarn(event);
        if (event.getSubcommandName().equals("remove")) removeWarn(event);
        if (event.getSubcommandName().equals("clear")) clearWarns(event);
        if (event.getSubcommandName().equals("list")) listWarns(event);

    }

    public void addWarn(SlashCommandEvent event) {

        String reason = event.getOption("reason").getAsString();

        if (reason.length() > 250) reason = reason.substring(0, 250);

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("insert into warnings(guild_id, user_id, moderator_id, content) VALUES (?, ?, ?, ?)")) {

            preparedStatement.setString(1, event.getGuild().getId());
            preparedStatement.setString(2, event.getOption("user").getAsUser().getId());
            preparedStatement.setString(3, event.getMember().getId());
            preparedStatement.setString(4, reason);
            preparedStatement.executeUpdate();


            event.replyEmbeds(new EmbedBuilder()
                    .setColor(EmbedColour.YES.getColour())
                    .setDescription(event.getOption("user").getAsUser().getAsMention() + " has been warned for " + reason)
                    .build()).queue();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listWarns(SlashCommandEvent event) {
        int count;

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement countQuery = connection
                     .prepareStatement("SELECT COUNT(*) FROM warnings WHERE guild_id = ? and user_id = ?")) {

            countQuery.setString(1, event.getGuild().getId());
            countQuery.setString(2, event.getOption("user").getAsUser().getId());


            try (final ResultSet countResult = countQuery.executeQuery()) {
                count = countResult.getInt("COUNT(*)");
            }

            final PreparedStatement warnQuery = SQLiteDataSource
                    .getConnection()
                    .prepareStatement("SELECT * FROM warnings WHERE guild_id = ? and user_id = ?");

            warnQuery.setString(1, event.getGuild().getId());
            warnQuery.setString(2, event.getOption("user").getAsUser().getId());


            try (final ResultSet warnResult = warnQuery.executeQuery()) {
                int tagCount = 0;
                int pageNumber = 0;

                if (count != 0) {
                    event.replyEmbeds(new EmbedBuilder()
                            .setTitle("Warnings for " + event.getOption("user").getAsUser().getAsTag())
                            .setColor(EmbedColour.NEUTRAL.getColour())
                            .setDescription("Format: \n**Punishment ID** \nPunishment Description\n ").build()).queue();

                    for (int i = 0; i < count; i += 24) {
                        pageNumber++;
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setTitle("Page " + pageNumber)
                                .setColor(EmbedColour.NEUTRAL.getColour());

                        while (warnResult.next()) {
                            String moderator = warnResult.getString("moderator_id").contains("SERVER") ? "SERVER" : Objects.requireNonNull(event.getJDA().getUserById(warnResult.getString("moderator_id"))).getAsTag();

                            builder.addField(warnResult.getString("id") + " - by " + moderator, warnResult.getString("content"), false);

                            tagCount++;

                            if (tagCount % 24 == 0) break;

                        }
                        event.getChannel().sendMessageEmbeds(builder.build()).queue();
                    }
                    connection.close();
                } else {
                    event.replyEmbeds(new EmbedBuilder()
                            .setDescription(event.getOption("user").getAsMember().getAsMention() + " has no warnings!")
                            .setColor(EmbedColour.NO.getColour())
                            .build()).queue();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void removeWarn(SlashCommandEvent event) {
        try {
            PreparedStatement preparedStatement = SQLiteDataSource
                    .getConnection()
                    .prepareStatement("select * FROM warnings WHERE id = ? and guild_id = ?");

            preparedStatement.setString(1, event.getOption("id").getAsString());
            preparedStatement.setString(2, event.getGuild().getId());
            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                result.close();
                PreparedStatement delete = SQLiteDataSource
                        .getConnection()
                        .prepareStatement("delete FROM warnings WHERE id = ? and guild_id = ?");

                delete.setString(1, event.getOption("id").getAsString());
                delete.setString(2, event.getGuild().getId());
                delete.executeUpdate();

                event.replyEmbeds(new EmbedBuilder()
                        .setTitle("Warning (" + event.getOption("id").getAsString() + ") has been deleted!")
                        .setColor(EmbedColour.YES.getColour())
                        .build()).queue();
            } else {
                event.replyEmbeds(new EmbedBuilder()
                        .setTitle("A warning with the id of `" + event.getOption("id").getAsString() + "` could not be found!")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).queue();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearWarns(SlashCommandEvent event) {
        try {
            PreparedStatement preparedStatement = SQLiteDataSource
                    .getConnection()
                    .prepareStatement("select * FROM warnings WHERE user_id = ? and guild_id = ?");

            preparedStatement.setString(1, event.getOption("user").getAsUser().getId());
            preparedStatement.setString(2, event.getGuild().getId());
            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                PreparedStatement delete = SQLiteDataSource
                        .getConnection()
                        .prepareStatement("delete FROM warnings WHERE user_id = ? and guild_id = ?");

                result.close();

                delete.setString(1, event.getOption("user").getAsUser().getId());
                delete.setString(2, event.getGuild().getId());
                delete.executeUpdate();

                event.replyEmbeds(new EmbedBuilder()
                        .setDescription(event.getOption("user").getAsUser().getAsMention() + "'s warnings have been cleared!")
                        .setColor(EmbedColour.YES.getColour())
                        .build()).queue();
            } else {
                event.replyEmbeds(new EmbedBuilder()
                        .setTitle(event.getOption("user").getAsUser().getAsMention() + "does not have any warnings")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).queue();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCommandName() {
        return "warn";
    }

    @Override
    public String getCommandDescription() {
        return "Controls Titan's warn functionality";
    }

    @Override
    public String getCommandUsage() {
        return "/warn add <user> <reason>" +
                "\n/warn remove <user> " +
                "\n/warn list <user>" +
                "\n/warn clear <user>";
    }
}
