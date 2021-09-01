package me.anutley.titan.database.util;

import me.anutley.titan.database.SQLiteDataSource;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildSettingsDBUtil {
    public static boolean isDiscordPermissionCheckEnabled(Guild guild) {

        try (PreparedStatement preparedStatement = SQLiteDataSource.getConnection()
                .prepareStatement("SELECT * FROM guild_settings where guild_id = ?")) {

            preparedStatement.setString(1, guild.getId());

            ResultSet result = preparedStatement.executeQuery();

            return result.getBoolean("DISCORD_PERMISSIONS_CHECK");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isLockdownEnabled(Guild guild) {
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * from guild_settings WHERE guild_id = ? ")) {

            preparedStatement.setString(1, guild.getId());
            ResultSet result = preparedStatement.executeQuery();
            return result.getBoolean("lockdown");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
