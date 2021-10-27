package me.anutley.titan.database.util;

import me.anutley.titan.Titan;
import me.anutley.titan.database.SQLiteDataSource;
import me.anutley.titan.database.objects.GuildSettings;
import me.anutley.titan.database.objects.Warning;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WarnUtil {

    public static void removeWarningById(String id) {
        try (final PreparedStatement preparedStatement = SQLiteDataSource
                .getConnection()
                .prepareStatement("DELETE from warnings where id = ?")) {

            preparedStatement.setString(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Warning> getUsersWarnings(String guildId, String userId) {
        ArrayList<Warning> userWarnings = new ArrayList<>();
        try (final Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM warnings where user_id = ? and guild_id = ?")) {

            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, guildId);

            ResultSet result = preparedStatement.executeQuery();

            while (result.next())
                userWarnings.add(new Warning(result.getString("id")));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userWarnings;
    }

    public static ArrayList<Warning> getGuildsWarnings(String guildId) {
        ArrayList<Warning> guildWarnings = new ArrayList<>();
        try (final Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM warnings where guild_id = ?")) {

            preparedStatement.setString(1, guildId);

            ResultSet result = preparedStatement.executeQuery();

            while (result.next())
                guildWarnings.add(new Warning(result.getString("id")));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return guildWarnings;
    }

    public static void clearUsersWarnings(String guildId, String userId) {
        for (Warning warning : getUsersWarnings(guildId, userId))
            removeWarningById(warning.getId());
    }

    public static Warning warnUser(Warning warn) {

        if (new GuildSettings(warn.getGuildId()).isDmOnWarn())
            Titan.getJda().getUserById(warn.getUserId()).openPrivateChannel().complete()
                    .sendMessageEmbeds(new EmbedBuilder()
                            .setDescription("You have been warned in " + Titan.getJda().getGuildById(warn.getGuildId()).getName() + " for `" + warn.getContent() + "`")
                            .setColor(EmbedColour.NEUTRAL.getColour())
                            .build()).queue(null, new ErrorHandler().ignore(ErrorResponse.CANNOT_SEND_TO_USER));
        return warn;
    }
}