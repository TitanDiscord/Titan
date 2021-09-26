package me.anutley.titan.database.objects;

import me.anutley.titan.Titan;
import me.anutley.titan.database.SQLiteDataSource;
import net.dv8tion.jda.api.entities.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildSettings {

    public String guildId;
    public String adminRoleId;
    public String modRoleId;
    public String tagManagementRoleId;
    public String muteRoleId;
    public boolean lockdown;

    public GuildSettings(String guildId) {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM guild_settings WHERE guild_id = ?")) {

            preparedStatement.setString(1, guildId);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next())
                this
                        .setGuildId(guildId)
                        .setAdminRoleId(result.getString("guild_admin_role"))
                        .setModRoleId(result.getString("guild_mod_role"))
                        .setTagManagementRoleId(result.getString("tag_management_role"))
                        .setMuteRoleId(result.getString("mute_role"))
                        .setLockdown(result.getBoolean("lockdown"));
            else
                this
                        .setGuildId(guildId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public GuildSettings save() {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM guild_settings WHERE guild_id = ?")) {

            preparedStatement.setString(1, this.getGuildId());

            ResultSet result = preparedStatement.executeQuery();

            if (!result.next()) {
                if (this.getGuildId() == null) return this;

                PreparedStatement newGuildSettings = connection
                        .prepareStatement("INSERT INTO guild_settings (guild_id, guild_admin_role, guild_mod_role, tag_management_role, mute_role, lockdown) VALUES (?, ?, ?, ?, ?, ?)");

                newGuildSettings.setString(1, this.getGuildId());
                newGuildSettings.setString(2, this.getAdminRoleId());
                newGuildSettings.setString(3, this.getModRoleId());
                newGuildSettings.setString(4, this.getTagManagementRoleId());
                newGuildSettings.setString(5, this.getMuteRoleId());
                newGuildSettings.setBoolean(6, this.isLockdown());
                newGuildSettings.executeUpdate();
            } else {

                PreparedStatement editGuildSettings = connection
                        .prepareStatement("UPDATE guild_settings set guild_admin_role = ?, guild_mod_role = ?, tag_management_role = ?, mute_role = ?, lockdown = ? where guild_id = ?");

                editGuildSettings.setString(1, this.getAdminRoleId());
                editGuildSettings.setString(2, this.getModRoleId());
                editGuildSettings.setString(3, this.getTagManagementRoleId());
                editGuildSettings.setString(4, this.getMuteRoleId());
                editGuildSettings.setBoolean(5, this.isLockdown());
                editGuildSettings.setString(6, this.getGuildId());

                editGuildSettings.executeUpdate();
            }

        } catch (SQLException ignored) {
        }
        return this;
    }


    public String getGuildId() {
        return guildId;
    }

    public String getAdminRoleId() {
        return adminRoleId;
    }

    public Role getAdminRole() {
        return Titan.getJda().getRoleById(adminRoleId);
    }

    public String getModRoleId() {
        return modRoleId;
    }

    public Role getModRole() {
        return Titan.getJda().getRoleById(modRoleId);
    }

    public String getTagManagementRoleId() {
        return tagManagementRoleId;
    }

    public Role getTagManagementRole() {
        return Titan.getJda().getRoleById(tagManagementRoleId);
    }

    public String getMuteRoleId() {
        return muteRoleId;
    }

    public Role getMuteRole() {
        return Titan.getJda().getRoleById(muteRoleId);
    }

    public boolean isLockdown() {
        return lockdown;
    }


    public GuildSettings setGuildId(String guildId) {
        this.guildId = guildId;
        return this;
    }

    public GuildSettings setAdminRoleId(String adminRoleId) {
        this.adminRoleId = adminRoleId;
        return this;
    }

    public GuildSettings setModRoleId(String modRoleId) {
        this.modRoleId = modRoleId;
        return this;
    }

    public GuildSettings setTagManagementRoleId(String tagManagementRoleId) {
        this.tagManagementRoleId = tagManagementRoleId;
        return this;
    }

    public GuildSettings setMuteRoleId(String muteRoleId) {
        this.muteRoleId = muteRoleId;
        return this;
    }

    public GuildSettings setLockdown(boolean lockdown) {
        this.lockdown = lockdown;
        return this;
    }

}
