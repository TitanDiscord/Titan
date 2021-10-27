package me.anutley.titan.database.util;

import me.anutley.titan.database.SQLiteDataSource;
import me.anutley.titan.database.objects.Note;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class NoteUtil {

    public static ArrayList<Note> getNotes(String guildId) {

        ArrayList<Note> notes = new ArrayList<>();

        try (final Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM guild_notes where guild_id = ?")) {

            preparedStatement.setString(1, guildId);
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                notes.add(new Note(result.getString("id"), guildId)
                        .setGuildId(result.getString("guild_id"))
                        .setAuthorId(result.getString("author_id"))
                        .setTargetId(result.getString("target_id"))
                        .setContent(result.getString("content"))
                        .setTimeCreated(result.getLong("time_created")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notes;
    }

    public static ArrayList<Note> getUsersNotes(String guildId, String userId) {
        ArrayList<Note> notes = getNotes(guildId);
        ArrayList<Note> userNotes = new ArrayList<>();

        for (Note note : notes) {
            if (note.getTargetId().equals(userId))
                userNotes.add(note);
        }

        return userNotes;
    }

    public static void removeNoteById(String id) {
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("DELETE from guild_notes where id = ?")) {

            preparedStatement.setString(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
