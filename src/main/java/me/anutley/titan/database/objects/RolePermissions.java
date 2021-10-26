package me.anutley.titan.database.objects;

import me.anutley.titan.database.SQLiteDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class RolePermissions {
    public String guildId;
    public String roleId;
    public ArrayList<String> permissions;

    public RolePermissions(String guildId, String roleId) {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM guild_permissions WHERE guild_id = ? AND role_id = ? ")) {

            preparedStatement.setString(1, guildId);
            preparedStatement.setString(2, roleId);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next())
                this
                        .setGuildId(guildId)
                        .setRoleId(roleId)
                        .setPermissions(
                                result.getString("permissions") != null ?
                                        new ArrayList<>(Arrays.asList(result.getString("permissions")
                                                .replaceAll("\\[", "")
                                                .replaceAll("]", "")
                                                .split(",")))
                                        : null
                        );
            else
                this
                        .setGuildId(guildId)
                        .setRoleId(roleId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public RolePermissions save() {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM guild_permissions WHERE guild_id = ? AND role_id = ? ")) {

            preparedStatement.setString(1, this.getGuildId());
            preparedStatement.setString(2, this.getRoleId());

            ResultSet result = preparedStatement.executeQuery();

            if (!result.next()) {

                PreparedStatement newData = connection
                        .prepareStatement("INSERT INTO guild_permissions (guild_id, role_id, permissions) VALUES (?, ?, ?)");

                newData.setString(1, this.getGuildId());
                newData.setString(2, this.getRoleId());
                newData.setString(3, !this.getPermissions().toString().equals("[]") ? this.getPermissions().toString().replaceAll(" ", "") : null);
                newData.executeUpdate();
            } else {
                PreparedStatement editData = connection
                        .prepareStatement("UPDATE guild_permissions set permissions = ? WHERE guild_id = ? and role_id = ? ");

                editData.setString(1, !this.getPermissions().toString().equals("[]") ? this.getPermissions().toString().replaceAll(" ", "") : null);
                editData.setString(2, this.getGuildId());
                editData.setString(3, this.getRoleId());
                editData.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getRoleId() {
        return roleId;
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }


    public RolePermissions setGuildId(String guildId) {
        this.guildId = guildId;
        return this;
    }

    public RolePermissions setRoleId(String roleId) {
        this.roleId = roleId;
        return this;
    }

    public RolePermissions setPermissions(ArrayList<String> permissions) {
        this.permissions = permissions;
        return this;
    }
}
