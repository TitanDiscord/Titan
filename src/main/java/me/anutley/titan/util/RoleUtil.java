package me.anutley.titan.util;

import me.anutley.titan.database.SQLiteDataSource;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class RoleUtil {
    public static boolean hasRole(Member member, Role roleToCheck) {
        for (Role role : member.getRoles()) {
            if (role == roleToCheck) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRole(Member member, String roleId) {
        for (Role role : member.getRoles()) {
            if (role.getId().equals(roleId)) {
                return true;
            }
        }
        return false;
    }

    public static Role highestRole(Member member) {
        return member.getRoles().size() != 0 ? member.getRoles().get(0) : member.getGuild().getPublicRole();

    }


    public static boolean isAdmin(Member member) {
        return RoleUtil.hasRole(member, getAdminRoleId(member.getGuild())) || member.isOwner();
    }

    public static String getAdminRoleId(Guild guild) {
        try (final Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM guild_settings where guild_id = ?")) {

            preparedStatement.setString(1, guild.getId());
            ResultSet result = preparedStatement.executeQuery();

            return result.getString("guild_admin_role");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Role getAdminRole(Guild guild) {
        try (final Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM guild_settings where guild_id = ?")) {

            preparedStatement.setString(1, guild.getId());
            ResultSet result = preparedStatement.executeQuery();

            return guild.getRoleById(result.getString("guild_admin_role"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean isMod(Member member) {
        return RoleUtil.hasRole(member, getModRoleId(member.getGuild()));
    }

    public static String getModRoleId(Guild guild) {
        try (final Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM guild_settings where guild_id = ?")) {

            preparedStatement.setString(1, guild.getId());
            ResultSet result = preparedStatement.executeQuery();

            return result.getString("guild_mod_role");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Role getModRole(Guild guild) {
        try (final Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM guild_settings where guild_id = ?")) {

            preparedStatement.setString(1, guild.getId());
            ResultSet result = preparedStatement.executeQuery();

            return guild.getRoleById(result.getString("guild_mod_role"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean isStaff (Member member) {
        return isAdmin(member) || isMod(member);
    }


    public static boolean isTagManager(Member member) {
        return RoleUtil.hasRole(member, getTagManagementRoleId(member.getGuild())) || isMod(member) || isAdmin(member);
    }

    public static String getTagManagementRoleId(Guild guild) {
        try (final Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM guild_settings where guild_id = ?")) {

            preparedStatement.setString(1, guild.getId());
            ResultSet result = preparedStatement.executeQuery();

            if (result.getString("tag_management_role") == null) {
                return getAdminRoleId(guild);
            }
            else {
                return result.getString("tag_management_role");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Role getTagManagementRole(Guild guild) {
        try (final Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM guild_settings where guild_id = ?")) {

            preparedStatement.setString(1, guild.getId());
            ResultSet result = preparedStatement.executeQuery();

            if (result.getString("tag_management_role") == null) {
                return guild.getRoleById(getAdminRoleId(guild));
            }
            else {
                return guild.getRoleById(result.getString("tag_management_role"));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



}
