package me.anutley.titan.database.util;

import me.anutley.titan.database.SQLiteDataSource;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WelcomeUtil {

    public static Role getWelcomeRole (Guild guild) {
        try (final Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM welcomes where guild_id = ?")) {

            preparedStatement.setString(1, guild.getId());
            ResultSet result = preparedStatement.executeQuery();

            if (result.getString("role") == null) return null;
            else return guild.getRoleById(result.getString("role"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
