package me.anutley.titan.database.objects;

import me.anutley.titan.database.SQLiteDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class PingProtectionSettings {

    private String guildId;
    private boolean enabled;
    private ArrayList<String> roles;
    private int threshold;
    private String action;

    public PingProtectionSettings(String guildId) {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM ping_protection_settings WHERE guild_id = ?")) {

            preparedStatement.setString(1, guildId);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next())
                this
                        .setGuildId(guildId)
                        .setEnabled(result.getBoolean("enabled"))
                        .setRoles(
                                result.getString("roles") != null ?
                                new ArrayList<>(Arrays.asList(result.getString("roles")
                                        .replaceAll("\\[", "")
                                        .replaceAll("]", "")
                                        .split(",")))
                                        : null
                        )
                        .setThreshold(result.getInt("threshold"))
                        .setAction(result.getString("action"));
            else
                this
                        .setGuildId(guildId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PingProtectionSettings save() {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM ping_protection_settings WHERE guild_id = ?")) {

            preparedStatement.setString(1, this.getGuildId());

            ResultSet result = preparedStatement.executeQuery();

            if (!result.next()) {

                PreparedStatement newPingProtectionSettings = connection
                        .prepareStatement("INSERT INTO ping_protection_settings (guild_id, enabled, roles, threshold, action) VALUES (?, ?, ?, ?, ?)");

                newPingProtectionSettings.setString(1, this.getGuildId());
                newPingProtectionSettings.setBoolean(2, this.isEnabled());
                newPingProtectionSettings.setString(3, this.getRoles() != null ? !this.getRoles().toString().equals("[]") ? this.getRoles().toString().replaceAll(" ", "") : null : null);
                newPingProtectionSettings.setInt(4, this.getThreshold());
                newPingProtectionSettings.setString(5, this.getAction());
                newPingProtectionSettings.executeUpdate();
            } else {

                PreparedStatement editPingProtectionSettings = connection
                        .prepareStatement("UPDATE ping_protection_settings set enabled = ?, roles = ?, threshold = ?, action = ? where guild_id = ?");

                editPingProtectionSettings.setBoolean(1, this.isEnabled());
                editPingProtectionSettings.setString(2, this.getRoles() != null ? !this.getRoles().toString().equals("[]") ? this.getRoles().toString().replaceAll(" ", "") : null : null);
                editPingProtectionSettings.setInt(3, this.getThreshold());
                editPingProtectionSettings.setString(4, this.getAction());
                editPingProtectionSettings.setString(5, this.getGuildId());

                editPingProtectionSettings.executeUpdate();
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

    public ArrayList<String> getRoles() {
        return roles;
    }

    public int getThreshold() {
        return threshold;
    }

    public String getAction() {
        return action == null ? "kick" : action;
    }


    public PingProtectionSettings setGuildId(String guildId) {
        this.guildId = guildId;
        return this;
    }

    public PingProtectionSettings setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public PingProtectionSettings setRoles(ArrayList<String> roles) {
        this.roles = roles;
        return this;
    }

    public PingProtectionSettings setThreshold(int threshold) {
        this.threshold = threshold;
        return this;
    }

    public PingProtectionSettings setAction(String action) {
        this.action = action;
        return this;
    }
}
