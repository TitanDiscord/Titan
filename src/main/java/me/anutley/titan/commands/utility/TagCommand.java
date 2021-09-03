package me.anutley.titan.commands.utility;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.SQLiteDataSource;
import me.anutley.titan.util.PermissionUtil;
import me.anutley.titan.util.RoleUtil;
import me.anutley.titan.util.embeds.errors.NoTagEmbed;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TagCommand extends Command {

    public static CommandData TagCommandData = new CommandData("tag", "Base command for accessing Titan's tag feature")

            .addSubcommands(new SubcommandData("create", "Creates a embedded tag with the data you input")
                    .addOption(OptionType.STRING, "trigger", "The trigger of the tag", true)
                    .addOption(OptionType.STRING, "title", "The title of the tag", true)
                    .addOption(OptionType.STRING, "content", "The content of the tag")
                    .addOption(OptionType.STRING, "colour", "The hex colour of the tag (EG: #ff00ff)")
                    .addOption(OptionType.STRING, "thumbnail", "The url of the thumbnail image of the tag"))

            .addSubcommands(new SubcommandData("get", "Gets a tag by its trigger")
                    .addOption(OptionType.STRING, "trigger", "The trigger of the tag to get", true))

            .addSubcommands(new SubcommandData("list", "Lists all the tags"))

            .addSubcommands(new SubcommandData("remove", "Removes a tag by its trigger")
                    .addOption(OptionType.STRING, "trigger", "The trigger of the tag to remove", true))

            .addSubcommands(new SubcommandData("edit", "Edits a tag by its trigger")
                    .addOption(OptionType.STRING, "trigger", "The trigger of the tag", true)
                    .addOption(OptionType.STRING, "title", "The title of the tag")
                    .addOption(OptionType.STRING, "content", "The description of the tag")
                    .addOption(OptionType.STRING, "colour", "The hex colour of the tag")
                    .addOption(OptionType.STRING, "thumbnail", "The url of the thumbnail image of the tag"))

            .addSubcommands(new SubcommandData("role", "Set the role that can manage tags in this guild (Run without the option to get the current role)")
                    .addOption(OptionType.ROLE, "role", "The role you want to give admin tag permissions to"));

    @Override
    public void onSlashCommand(SlashCommandEvent event) {

        if (!event.getName().equals("tag")) return;

        try {
            if (!RoleUtil.isTagManager(event.getMember())) {
                if (event.getSubcommandName().equals("create") || event.getSubcommandName().equals("edit") || event.getSubcommandName().equals("remove")) {
                    event.replyEmbeds(PermissionUtil.needRoleEmbed(event, RoleUtil.getTagManagementRole(event.getGuild())).build()).setEphemeral(true).queue();
                    return;
                }
            }

            if (!RoleUtil.isAdmin(event.getMember())) {
                if (event.getSubcommandName().equals("role")) {
                    event.replyEmbeds(PermissionUtil.needAdminEmbed(event).build()).setEphemeral(true).queue();
                    return;
                }
            }

            if (event.getOption("title") != null) {
                if (event.getSubcommandName().equals("create") || event.getSubcommandName().equals("edit")) {
                    if (event.getOption("title").getAsString().length() > 256) {
                        event.replyEmbeds(new EmbedBuilder()
                                .setTitle("The title was too long! The maxmimum amount of characters is 256.")
                                .setColor(EmbedColour.NO.getColour())
                                .build()).setEphemeral(true).queue();
                        return;
                    }
                }
                if (event.getOption("content") != null) {
                    if (event.getSubcommandName().equals("create") || event.getSubcommandName().equals("edit")) {
                        if (event.getOption("content").getAsString().length() > 4096) {
                            event.replyEmbeds(new EmbedBuilder()
                                    .setTitle("The content was too long! The maxmimum amount of characters is 256.")
                                    .setColor(EmbedColour.NO.getColour())
                                    .build()).setEphemeral(true).queue();
                            return;
                        }
                    }
                }
            }

            if (event.getSubcommandName().equals("create")) createTag(event);
            if (event.getSubcommandName().equals("edit")) editTag(event);
            if (event.getSubcommandName().equals("remove")) removeTag(event);
            if (event.getSubcommandName().equals("role")) modifyTagPermissions(event);
            if (event.getSubcommandName().equals("get")) getTag(event);
            if (event.getSubcommandName().equals("list")) listTags(event);

        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTag(SlashCommandEvent event) {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT COUNT(*) FROM tags WHERE guild_id = ? AND trigger = ?")) {

            preparedStatement.setString(1, event.getGuild().getId());
            preparedStatement.setString(2, event.getOption("trigger").getAsString());

            ResultSet result = preparedStatement.executeQuery();

            if (Integer.parseInt(result.getString("COUNT(*)")) != 0) {
                event.replyEmbeds(new EmbedBuilder()
                        .setTitle("This tag already exists!")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).queue();
                return;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("insert into tags(trigger, title, content, colour, thumbnail, guild_id) values (?, ?, ?, ?, ?, ?)")) {

            String title = event.getOption("title").getAsString();
            String content = event.getOption("content") != null ? event.getOption("content").getAsString().replaceAll("\\\\n", "\n") : null;
            String colour = event.getOption("colour") != null ? event.getOption("colour").getAsString() : null;
            String thumbnail = event.getOption("thumbnail") != null ? event.getOption("thumbnail").getAsString() : null;

            if (colour != null) {
                if (!colour.startsWith("#")) colour = "#" + colour;
            }

            preparedStatement.setString(1, event.getOption("trigger").getAsString());
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, content);
            preparedStatement.setString(4, colour);
            preparedStatement.setString(5, thumbnail);
            preparedStatement.setString(6, event.getGuild().getId());


            preparedStatement.executeUpdate();

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(title)
                    .setDescription(content)
                    .setColor(colour != null ? Color.decode(colour) : null)
                    .setThumbnail(thumbnail);

            event.replyEmbeds(builder.build()).queue();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getTag(SlashCommandEvent event) throws SQLException {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM tags WHERE guild_id = ? AND trigger = ? ")) {

            preparedStatement.setString(1, event.getGuild().getId());
            preparedStatement.setString(2, event.getOption("trigger").getAsString());


            try (final ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    String title = resultSet.getString("title");
                    String content = resultSet.getString("content") != null ? resultSet.getString("content").replaceAll("\\\\n", "\n") : null;
                    String colour = resultSet.getString("colour");
                    String thumbnail = resultSet.getString("thumbnail");

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle(title)
                            .setDescription(content)
                            .setColor(colour != null ? Color.decode(colour) : null)
                            .setThumbnail(thumbnail);

                    event.replyEmbeds(builder.build()).queue();
                } else {
                    event.replyEmbeds(new EmbedBuilder(NoTagEmbed.Embed())
                            .build()).queue();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listTags(SlashCommandEvent event) throws SQLException {
        int count;

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement countQuery = connection
                     .prepareStatement("SELECT COUNT(*) FROM tags WHERE guild_id = ? ")) {

            countQuery.setString(1, event.getGuild().getId());


            try (final ResultSet countResult = countQuery.executeQuery()) {
                count = countResult.getInt("COUNT(*)");
            }

            final PreparedStatement tagQuery = SQLiteDataSource
                    .getConnection()
                    .prepareStatement("SELECT * FROM tags WHERE guild_id = ?");

            tagQuery.setString(1, event.getGuild().getId());


            try (final ResultSet tagResult = tagQuery.executeQuery()) {
                int tagCount = 0;
                int pageNumber = 0;

                if (count != 0) {

                    event.replyEmbeds(new EmbedBuilder()
                            .setTitle("Available tags")
                            .setColor(EmbedColour.NEUTRAL.getColour())
                            .setDescription("Format: \n**Tag trigger** \nTag title \n ").build()).queue();

                    for (int i = 0; i < count; i += 24) {
                        pageNumber++;
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setTitle("Page " + pageNumber)
                                .setColor(EmbedColour.NEUTRAL.getColour());

                        while (tagResult.next()) {
                            builder.addField(tagResult.getString("trigger"), tagResult.getString("title"), true);
                            tagCount++;

                            if (tagCount % 24 == 0) break;

                        }
                        event.getChannel().sendMessageEmbeds(builder.build()).queue();
                    }
                    connection.close();
                } else {
                    event.replyEmbeds(new EmbedBuilder()
                            .setTitle("This guild has no tags!")
                            .setColor(EmbedColour.NO.getColour())
                            .build()).queue();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void removeTag(SlashCommandEvent event) throws SQLException {

        try (final PreparedStatement preparedStatement = SQLiteDataSource
                .getConnection()
                .prepareStatement("SELECT COUNT(*) FROM tags WHERE guild_id = ? AND trigger = ?")) {

            preparedStatement.setString(1, event.getGuild().getId());
            preparedStatement.setString(2, event.getOption("trigger").getAsString());

            ResultSet result = preparedStatement.executeQuery();

            if (Integer.parseInt(result.getString("COUNT(*)")) == 0) {
                event.replyEmbeds(new EmbedBuilder(NoTagEmbed.Embed())
                        .build()).queue();
                return;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (final PreparedStatement preparedStatement = SQLiteDataSource
                .getConnection()
                .prepareStatement("DELETE from tags where guild_id = ? and trigger = ?")) {

            preparedStatement.setString(1, event.getGuild().getId());
            preparedStatement.setString(2, event.getOption("trigger").getAsString());

            preparedStatement.executeUpdate();

            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Tag deleted!")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editTag(SlashCommandEvent event) throws SQLException {

        String trigger = event.getOption("trigger").getAsString();

        try (final PreparedStatement preparedStatement = SQLiteDataSource
                .getConnection()
                .prepareStatement("SELECT COUNT(*) FROM tags WHERE guild_id = ? AND trigger = ?")) {

            preparedStatement.setString(1, event.getGuild().getId());
            preparedStatement.setString(2, trigger);

            ResultSet result = preparedStatement.executeQuery();

            if (Integer.parseInt(result.getString("COUNT(*)")) == 0) {
                event.replyEmbeds(new EmbedBuilder(NoTagEmbed.Embed())
                        .build()).queue();
                return;
            }
        }

        if (event.getOption("title") != null) {
            try (final Connection connection = SQLiteDataSource
                    .getConnection();
                 PreparedStatement preparedStatement = connection
                         .prepareStatement("UPDATE tags SET title = ? WHERE guild_id = ? and trigger = ?")) {

                preparedStatement.setString(1, event.getOption("title").getAsString());
                preparedStatement.setString(2, event.getGuild().getId());
                preparedStatement.setString(3, trigger);

                preparedStatement.executeUpdate();
            }
        }

        if (event.getOption("content") != null) {
            try (final Connection connection = SQLiteDataSource
                    .getConnection();
                 PreparedStatement preparedStatement = connection
                         .prepareStatement("UPDATE tags SET content = ? WHERE guild_id = ? and trigger = ?")) {

                preparedStatement.setString(1, event.getOption("content").getAsString());
                preparedStatement.setString(2, event.getGuild().getId());
                preparedStatement.setString(3, trigger);

                preparedStatement.executeUpdate();
            }
        }
        if (event.getOption("colour") != null) {
            try (final Connection connection = SQLiteDataSource
                    .getConnection();
                 PreparedStatement preparedStatement = connection
                         .prepareStatement("UPDATE tags SET colour = ? WHERE guild_id = ? and trigger = ?")) {

                String colour = event.getOption("colour").getAsString();

                preparedStatement.setString(1, colour.startsWith("#") ? colour : "#" + colour);
                preparedStatement.setString(2, event.getGuild().getId());
                preparedStatement.setString(3, trigger);
                preparedStatement.executeUpdate();
            }

        }
        if (event.getOption("thumbnail") != null) {
            try (final Connection connection = SQLiteDataSource
                    .getConnection();
                 PreparedStatement preparedStatement = connection
                         .prepareStatement("UPDATE tags SET thumbnail = ? WHERE guild_id = ? and trigger = ?")) {

                preparedStatement.setString(1, event.getOption("thumbnail").getAsString());
                preparedStatement.setString(2, event.getGuild().getId());
                preparedStatement.setString(3, trigger);
                preparedStatement.executeUpdate();
            }
        }
        if (event.getOption("title") == null
                && event.getOption("content") == null
                && event.getOption("colour") == null
                && event.getOption("thumbnail") == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("You did not specify anything to edit!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
        }
        else {
            event.replyEmbeds(tagEditedEmbed().build()).queue();
            event.getChannel().sendMessageEmbeds(getTagEmbed(event).build()).queue();
        }
    }

    public void modifyTagPermissions(SlashCommandEvent event) throws SQLException {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("UPDATE guild_settings SET tag_management_role = ? WHERE guild_id = ? ")) {

            preparedStatement.setString(1, event.getOption("role") != null ? event.getOption("role").getAsRole().getId() : null);
            preparedStatement.setString(2, event.getGuild().getId());

            preparedStatement.executeUpdate();

            if (event.getOption("role") != null) {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("The role that manages tags has been set to " + event.getOption("role").getAsRole().getAsMention())
                        .setColor(EmbedColour.YES.getColour())
                        .build()).queue();
            } else {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("The role that manages tags is " + RoleUtil.getTagManagementRole(event.getGuild()).getAsMention())
                        .setColor(EmbedColour.NEUTRAL.getColour())
                        .build()).queue();
            }
        }
    }

    public EmbedBuilder getTagEmbed(SlashCommandEvent event) throws SQLException {
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM tags WHERE guild_id = ? AND trigger = ? ")) {

            preparedStatement.setString(1, event.getGuild().getId());
            preparedStatement.setString(2, event.getOption("trigger").getAsString());


            try (final ResultSet resultSet = preparedStatement.executeQuery()) {

                String title = resultSet.getString("title");
                String content = resultSet.getString("content") != null ? resultSet.getString("content").replaceAll("\\\\n", "\n") : null;
                String colour = resultSet.getString("colour");
                String thumbnail = resultSet.getString("thumbnail");

                EmbedBuilder builder = new EmbedBuilder();
                return builder.setTitle(title)
                        .setDescription(content)
                        .setColor(colour != null ? Color.decode(colour) : null)
                        .setThumbnail(thumbnail);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public EmbedBuilder tagEditedEmbed() {

        EmbedBuilder builder = new EmbedBuilder();
        return builder.setTitle("Tag edited!")
                .setDescription("New embed below 👇")
                .setColor(EmbedColour.YES.getColour());
    }


    @Override
    public String getCommandName() {
        return "tag";
    }

    @Override
    public String getCommandDescription() {
        return "Allows the managing of tags.";
    }

    @Override
    public String getCommandUsage() {
        return "/tag create <trigger> <title> [content] [colour] [thumbnail]" +
                "\n/tag get <trigger>" +
                "\n/tag list" +
                "\n/tag remove <trigger>" +
                "\n/tag edit <trigger> [title] [content] [colour] [thumbnail]" +
                "\n/tag setrole <role>";
    }
}