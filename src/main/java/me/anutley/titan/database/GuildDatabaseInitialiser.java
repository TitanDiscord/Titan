package me.anutley.titan.database;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildDatabaseInitialiser extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {

        for (Guild guild : event.getJDA().getGuilds()) {

            String guildId = guild.getId();

            initialiseGuildSettings(guild);
            initialisePingProtectionSettingsTable(guildId);
            initialiseWelcomeSettingsTable(guildId);
            initialiseLeaveSettingsTable(guildId);
        }
    }



    public void initialiseGuildSettings(Guild guild) {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT guild_id FROM guild_settings WHERE guild_id = ?")) {

            preparedStatement.setString(1, guild.getId());

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return;
                }
            }

            try (final PreparedStatement insertStatement = SQLiteDataSource.getConnection()
                    .prepareStatement("INSERT INTO guild_settings(guild_id, guild_admin_role, guild_mod_role, tag_management_role) VALUES(?, ?, ?, ?)")) {

                insertStatement.setString(1, guild.getId());
                insertStatement.setString(2, guild.getRoleById(guild.getRoles().get(0).getId()).getId());
                insertStatement.setString(3, guild.getRoleById(guild.getRoles().get(0).getId()).getId());
                insertStatement.setString(4, guild.getRoleById(guild.getRoles().get(0).getId()).getId());

                insertStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initialisePingProtectionSettingsTable(String guildId) {
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT guild_id FROM ping_protection_settings WHERE guild_id = ?")) {

            preparedStatement.setString(1, String.valueOf(guildId));

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return;
                }
            }

            try (final PreparedStatement insertStatement = SQLiteDataSource.getConnection()
                    .prepareStatement("INSERT INTO ping_protection_settings(guild_id) VALUES(?)")) {

                insertStatement.setString(1, String.valueOf(guildId));

                insertStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initialiseWelcomeSettingsTable(String guildId) {
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT guild_id FROM welcomes WHERE guild_id = ?")) {

            preparedStatement.setString(1, String.valueOf(guildId));

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return;
                }
            }

            try (final PreparedStatement insertStatement = SQLiteDataSource.getConnection()
                    .prepareStatement("INSERT INTO welcomes(guild_id) VALUES(?)")) {

                insertStatement.setString(1, String.valueOf(guildId));

                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initialiseLeaveSettingsTable(String guildId) {
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT guild_id FROM leave WHERE guild_id = ?")) {

            preparedStatement.setString(1, String.valueOf(guildId));

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return;
                }
            }

            try (final PreparedStatement insertStatement = SQLiteDataSource.getConnection()
                    .prepareStatement("INSERT INTO leave(guild_id) VALUES(?)")) {

                insertStatement.setString(1, String.valueOf(guildId));

                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
