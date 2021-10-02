package me.anutley.titan.database.objects;

import me.anutley.titan.database.SQLiteDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PingProtectionUserData {
    public String guildId;
    public String userId;
    public int count = 0;

    public PingProtectionUserData(String guildId, String memberId) {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM ping_protection_data WHERE guild_id = ? AND member_id = ? ")) {

            preparedStatement.setString(1, guildId);
            preparedStatement.setString(2, memberId);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next())
                this
                        .setGuildId(guildId)
                        .setUserId(memberId)
                        .setCount(result.getInt("illegal_ping_count"));
            else
                this
                        .setGuildId(guildId)
                        .setUserId(memberId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PingProtectionUserData save() {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM ping_protection_data WHERE guild_id = ? AND member_id = ? ")) {

            preparedStatement.setString(1, this.getGuildId());
            preparedStatement.setString(2, this.getUserId());

            ResultSet result = preparedStatement.executeQuery();

            if (!result.next()) {

                PreparedStatement newData = connection
                        .prepareStatement("INSERT INTO ping_protection_data (guild_id, member_id, illegal_ping_count) VALUES (?, ?, ?)");

                newData.setString(1, this.getGuildId());
                newData.setString(2, this.getUserId());
                newData.setInt(3, this.getCount());
                newData.executeUpdate();
            } else {
                PreparedStatement editData = connection
                        .prepareStatement("UPDATE ping_protection_data set illegal_ping_count = ? WHERE guild_id = ? and member_id = ? ");

                editData.setInt(1, this.getCount());
                editData.setString(2, this.getGuildId());
                editData.setString(3, this.getUserId());
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

    public String getUserId() {
        return userId;
    }

    public int getCount() {
        return count;
    }


    public PingProtectionUserData setGuildId(String guildId) {
        this.guildId = guildId;
        return this;
    }

    public PingProtectionUserData setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public PingProtectionUserData setCount(int count) {
        this.count = count;
        return this;
    }
}
