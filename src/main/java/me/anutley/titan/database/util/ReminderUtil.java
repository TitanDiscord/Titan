package me.anutley.titan.database.util;

import me.anutley.titan.database.SQLiteDataSource;
import me.anutley.titan.database.objects.Reminder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReminderUtil {

    public static ArrayList<Reminder> getReminders() {

        ArrayList<Reminder> reminders = new ArrayList<>();
        try (final Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM reminders")) {
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                reminders.add(new Reminder(result.getString("id"))
                        .setGuildId(result.getString("guild_id"))
                        .setChannelId(result.getString("channel_id"))
                        .setUserId(result.getString("user_id"))
                        .setContent(result.getString("content"))
                        .setTimeInMilliseconds(result.getLong("time_in_milliseconds")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reminders;
    }

    public static ArrayList<Reminder> getUsersReminders(String guildId, String userId) {
        ArrayList<Reminder> reminders = getReminders();
        ArrayList<Reminder> userReminders = new ArrayList<>();

        for (Reminder reminder : reminders) {
            if (reminder.getGuildId().equals(guildId) && reminder.getUserId().equals(userId))
                userReminders.add(reminder);
        }

        return userReminders;
    }

    public static void removeReminderById(String id) {
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("DELETE from reminders where id = ?")) {

            preparedStatement.setString(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
