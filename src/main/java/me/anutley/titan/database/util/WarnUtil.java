package me.anutley.titan.database.util;

import me.anutley.titan.database.SQLiteDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WarnUtil {

    public static void newWarn(String guildId, String userId, String moderatorId, String content) {
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("insert into warnings(guild_id, user_id, moderator_id, content) VALUES (?, ?, ?, ?)")) {

            preparedStatement.setString(1, guildId);
            preparedStatement.setString(2, userId);
            preparedStatement.setString(3, moderatorId);
            preparedStatement.setString(4, content);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
