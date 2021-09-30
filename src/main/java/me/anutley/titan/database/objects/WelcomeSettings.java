package me.anutley.titan.database.objects;

import me.anutley.titan.Titan;
import me.anutley.titan.database.SQLiteDataSource;
import net.dv8tion.jda.api.entities.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WelcomeSettings {
    public String guildId;
    public boolean enabled;
    public String channelId;
    public String message;
    public String roleId;

    public WelcomeSettings(String guildId) {
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM welcomes WHERE guild_id = ?")) {

            preparedStatement.setString(1, guildId);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next())
                this
                        .setGuildId(guildId)
                        .setEnabled(result.getBoolean("enabled"))
                        .setChannelId(result.getString("channel_id"))
                        .setMessage(result.getString("message"))
                        .setRoleId(result.getString("role"));
            else
                this
                        .setGuildId(guildId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public WelcomeSettings save() {
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM welcomes WHERE guild_id = ?")) {

            preparedStatement.setString(1, this.getGuildId());

            ResultSet result = preparedStatement.executeQuery();

            if (!result.next()) {
                if (this.getGuildId() == null) return this;

                PreparedStatement newGuildSettings = connection
                        .prepareStatement("INSERT INTO welcomes (guild_id, enabled, channel_id, message, role) VALUES (?, ?, ?, ?, ?)");

                newGuildSettings.setString(1, this.getGuildId());
                newGuildSettings.setBoolean(2, this.isEnabled());
                newGuildSettings.setString(3, this.getChannelId());
                newGuildSettings.setString(4, this.getMessage());
                newGuildSettings.setString(5, this.getRoleId());
                newGuildSettings.executeUpdate(); 
            } else {

                PreparedStatement editGuildSettings = connection
                        .prepareStatement("UPDATE welcomes set enabled = ?, channel_id = ?, message = ?, role = ? where guild_id = ?");

                editGuildSettings.setBoolean(1, this.isEnabled());
                editGuildSettings.setString(2, this.getChannelId());
                editGuildSettings.setString(3, this.getMessage());
                editGuildSettings.setString(4, this.getRoleId());
                editGuildSettings.setString(5, this.getGuildId());

                editGuildSettings.executeUpdate();


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

    public String getRoleId() {
        return roleId;
    }

    public Role getRole() {
        return Titan.getJda().getRoleById(roleId);
    }

    public WelcomeSettings setGuildId(String guildId) {
        this.guildId = guildId;
        return this;
    }

    public WelcomeSettings setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public WelcomeSettings setChannelId(String channelId) {
        this.channelId = channelId;
        return this;
    }

    public WelcomeSettings setMessage(String message) {
        this.message = message;
        return this;
    }

    public WelcomeSettings setRoleId(String roleId) {
        this.roleId = roleId;
        return this;
    }
}
