package me.anutley.titan.database.objects;

import me.anutley.titan.database.SQLiteDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Reminder {

    private String id;
    private String guildId;
    private String channelId;
    private String userId;
    private String content;
    private long timeInMilliseconds;
    private long timeCreated;

    public Reminder(String id) {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM reminders WHERE id = ? ")) {

            preparedStatement.setString(1, id);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next())
                this
                        .setId(id)
                        .setGuildId(result.getString("guild_id"))
                        .setChannelId(result.getString("channel_id"))
                        .setUserId(result.getString("user_id"))
                        .setContent(result.getString("content"))
                        .setTimeInMilliseconds(result.getLong("time_in_milliseconds"))
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


    public Reminder save() {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM reminders WHERE id = ? ")) {

            preparedStatement.setString(1, this.getId());

            ResultSet result = preparedStatement.executeQuery();

            if (!result.next()) {

                PreparedStatement newReminder = connection
                        .prepareStatement("INSERT INTO reminders (guild_id, channel_id, user_id, content, time_in_milliseconds, time_created) VALUES (?, ?, ?, ?, ?, ?)");

                newReminder.setString(1, this.getGuildId());
                newReminder.setString(2, this.getChannelId());
                newReminder.setString(3, this.getUserId());
                newReminder.setString(4, this.getContent());
                newReminder.setLong(5, this.getTimeInMilliseconds());
                newReminder.setLong(6, this.getTimeCreated());
                newReminder.executeUpdate();

                ResultSet rs = newReminder.getGeneratedKeys();

                this.setId(rs.getString(1));
            } else {
                PreparedStatement editReminder = connection
                        .prepareStatement("UPDATE reminders set guild_id = ?, channel_id = ?, user_id = ?, content = ?, time_in_milliseconds = ?, time_created = ? where id = ?");

                editReminder.setString(1, this.getGuildId());
                editReminder.setString(2, this.getChannelId());
                editReminder.setString(3, this.getUserId());
                editReminder.setString(4, this.getContent());
                editReminder.setLong(5, this.getTimeInMilliseconds());
                editReminder.setLong(6, this.getTimeCreated());
                editReminder.setString(7, this.getId());
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

    public String getChannelId() {
        return channelId;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public long getTimeInMilliseconds() {
        return timeInMilliseconds;
    }

    public long getTimeCreated() {
        return timeCreated;
    }


    public Reminder setId(String id) {
        this.id = id;
        return this;
    }

    public Reminder setGuildId(String guildId) {
        this.guildId = guildId;
        return this;
    }

    public Reminder setChannelId(String channelId) {
        this.channelId = channelId;
        return this;
    }

    public Reminder setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Reminder setContent(String content) {
        this.content = content;
        return this;
    }

    public Reminder setTimeInMilliseconds(long timeInMilliseconds) {
        this.timeInMilliseconds = timeInMilliseconds;
        return this;
    }

    public Reminder setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
        return this;
    }

}
