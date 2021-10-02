package me.anutley.titan.database.objects;

import me.anutley.titan.database.SQLiteDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LeaveSettings {

    public String guildId;
    public boolean enabled;
    public String channelId;
    public String message;

    public LeaveSettings(String guildId) {
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM leave WHERE guild_id = ?")) {

            preparedStatement.setString(1, guildId);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next())
                this
                        .setGuildId(guildId)
                        .setEnabled(result.getBoolean("enabled"))
                        .setChannelId(result.getString("channel_id"))
                        .setMessage(result.getString("message"));
            else
                this
                        .setGuildId(guildId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public LeaveSettings save() {
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM leave WHERE guild_id = ?")) {

            preparedStatement.setString(1, this.getGuildId());

            ResultSet result = preparedStatement.executeQuery();

            if (!result.next()) {
                if (this.getGuildId() == null) return this;

                PreparedStatement newLeaveSettings = connection
                        .prepareStatement("INSERT INTO leave (guild_id, enabled, channel_id, message) VALUES (?, ?, ?, ?)");

                newLeaveSettings.setString(1, this.getGuildId());
                newLeaveSettings.setBoolean(2, this.isEnabled());
                newLeaveSettings.setString(3, this.getChannelId());
                newLeaveSettings.setString(4, this.getMessage());
                newLeaveSettings.executeUpdate();
            } else {

                PreparedStatement editLeaveSettings = connection
                        .prepareStatement("UPDATE leave set enabled = ?, channel_id = ?, message = ? where guild_id = ?");

                editLeaveSettings.setBoolean(1, this.isEnabled());
                editLeaveSettings.setString(2, this.getChannelId());
                editLeaveSettings.setString(3, this.getMessage());
                editLeaveSettings.setString(4, this.getGuildId());

                editLeaveSettings.executeUpdate();


            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getGuildId() {
        return guildId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getMessage() {
        return message;
    }

    public LeaveSettings setGuildId(String guildId) {
        this.guildId = guildId;
        return this;
    }

    public LeaveSettings setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public LeaveSettings setChannelId(String channelId) {
        this.channelId = channelId;
        return this;
    }

    public LeaveSettings setMessage(String message) {
        this.message = message;
        return this;
    }
}
