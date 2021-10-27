package me.anutley.titan.database.objects;

import me.anutley.titan.database.SQLiteDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Warning {

    private String id;
    private String guildId;
    private String userId;
    private String moderatorId;
    private String content;
    private long timeCreated;

    public Warning(String id) {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM warnings WHERE id = ? ")) {

            preparedStatement.setString(1, id);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next())
                this
                        .setId(id)
                        .setGuildId(result.getString("guild_id"))
                        .setUserId(result.getString("user_id"))
                        .setModeratorId(result.getString("moderator_id"))
                        .setContent(result.getString("content"))
                        .setTimeCreated(result.getLong("time_created"));

            else {
                if (id != null)
                    this
                            .setId(id);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Warning save() {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM warnings WHERE id = ? ")) {

            preparedStatement.setString(1, this.getId());

            ResultSet result = preparedStatement.executeQuery();

            if (!result.next()) {

                PreparedStatement newReminder = connection
                        .prepareStatement("INSERT INTO warnings(guild_id, user_id, moderator_id, content, time_created) VALUES (?, ?, ?, ?, ?)");

                newReminder.setString(1, this.getGuildId());
                newReminder.setString(2, this.getUserId());
                newReminder.setString(3, this.getModeratorId());
                newReminder.setString(4, this.getContent());
                newReminder.setLong(5, this.getTimeCreated());
                newReminder.executeUpdate();

                ResultSet rs = newReminder.getGeneratedKeys();

                this.setId(rs.getString(1));
            } else {
                PreparedStatement editReminder = connection
                        .prepareStatement("UPDATE warnings set guild_id = ?, user_id = ?, moderator_id = ?, content = ?, time_created = ? where id = ?");

                editReminder.setString(1, this.getGuildId());
                editReminder.setString(2, this.getUserId());
                editReminder.setString(3, this.getModeratorId());
                editReminder.setString(4, this.getContent());
                editReminder.setLong(5, this.getTimeCreated());
                editReminder.setString(6, this.getId());
                editReminder.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getId() {
        return id;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getUserId() {
        return userId;
    }

    public String getModeratorId() {
        return moderatorId;
    }

    public String getContent() {
        return content;
    }

    public long getTimeCreated() {
        return timeCreated;
    }


    public Warning setId(String id) {
        this.id = id;
        return this;
    }

    public Warning setGuildId(String guildId) {
        this.guildId = guildId;
        return this;
    }

    public Warning setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Warning setModeratorId(String moderatorId) {
        this.moderatorId = moderatorId;
        return this;
    }

    public Warning setContent(String content) {
        this.content = content;
        return this;
    }

    public Warning setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
        return this;
    }
}
