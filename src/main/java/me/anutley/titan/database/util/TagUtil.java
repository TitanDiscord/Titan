package me.anutley.titan.database.util;

import me.anutley.titan.database.SQLiteDataSource;
import me.anutley.titan.database.objects.EmbedTag;
import me.anutley.titan.database.objects.TextTag;
import me.anutley.titan.util.exceptions.NoTagFoundException;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TagUtil {

    public static boolean isEmbedTag(String trigger, String guildId) throws NoTagFoundException {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM tags WHERE guild_id = ? AND trigger = ? ")) {

            preparedStatement.setString(1, guildId);
            preparedStatement.setString(2, trigger);

            ResultSet result = preparedStatement.executeQuery();

            if (!result.next()) throw new NoTagFoundException("This tag does not exist in this guild");
            else return result.getBoolean("embed_tag");

        } catch (SQLException ignored) {
        }
        return false;
    }

    public static boolean doesTagExist(String trigger, String guildId) {
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM tags WHERE guild_id = ? AND trigger = ? ")) {

            preparedStatement.setString(1, guildId);
            preparedStatement.setString(2, trigger);

            ResultSet result = preparedStatement.executeQuery();

            return result.next();

        } catch (SQLException ignored) {
        }
        return false;
    }

    public static void deleteTagByTrigger(String trigger, String guildId) {
        try (final PreparedStatement preparedStatement = SQLiteDataSource
                .getConnection()
                .prepareStatement("DELETE from tags where guild_id = ? and trigger = ?")) {

            preparedStatement.setString(1, guildId);
            preparedStatement.setString(2, trigger);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteGuildsTags(String guildId) {
        try (final PreparedStatement preparedStatement = SQLiteDataSource
                .getConnection()
                .prepareStatement("DELETE from tags where guild_id = ?")) {

            preparedStatement.setString(1, guildId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<EmbedTag> getGuildsEmbedTags(String guildId) {
        ArrayList<EmbedTag> arrayList = new ArrayList<>();

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM tags WHERE guild_id = ? AND embed_tag = true")) {

            preparedStatement.setString(1, guildId);
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                arrayList.add(new EmbedTag(result.getString("trigger"), guildId));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public static ArrayList<TextTag> getGuildsTextTags(String guildId) {
        ArrayList<TextTag> arrayList = new ArrayList<>();

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM tags WHERE guild_id = ? AND embed_tag = false")) {

            preparedStatement.setString(1, guildId);
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                arrayList.add(new TextTag(result.getString("trigger"), guildId));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public static EmbedBuilder getTagEmbedBuilder(EmbedTag tag) {
        EmbedBuilder builder = new EmbedBuilder();
        try {
            builder.setTitle(tag.getTitle())
                    .setDescription(tag.getDescription())
                    .setColor(tag.getColour() != null ? Color.decode(tag.getColour()) : null);

            if (tag.getThumbnail() != null)
                if (tag.getThumbnail().startsWith("https") || tag.getThumbnail().startsWith("http"))
                    builder.setThumbnail(tag.getThumbnail());

        } catch (NumberFormatException e) {
            builder.setColor(null);
        }
        return builder;
    }


}
