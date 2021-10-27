package me.anutley.titan.database.objects;

import me.anutley.titan.database.SQLiteDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Note {

    private String id;
    private String guildId;
    private String targetId;
    private String authorId;
    private String content;
    private long timeCreated;

    public Note(String id, String guildId) {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM guild_notes WHERE id = ? and guild_id = ?")) {

            preparedStatement.setString(1, id);
            preparedStatement.setString(2, guildId);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                this
                        .setId(id)
                        .setGuildId(guildId)
                        .setTargetId(result.getString("target_id"))
                        .setAuthorId(result.getString("author_id"))
                        .setContent(result.getString("content"))
                        .setTimeCreated(result.getLong("time_created"));

            } else {
                if (id != null)
                    this.setId(id);
                this.setGuildId(guildId);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Note save() {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM guild_notes WHERE id = ? and guild_id = ?")) {

            preparedStatement.setString(1, this.getId());
            preparedStatement.setString(2, this.getGuildId());

            ResultSet result = preparedStatement.executeQuery();

            if (!result.next()) {

                PreparedStatement newNote = connection
                        .prepareStatement("INSERT INTO guild_notes (guild_id, target_id, author_id, content, time_created) VALUES (?, ?, ?, ?, ?)");

                newNote.setString(1, this.getGuildId());
                newNote.setString(2, this.getTargetId());
                newNote.setString(3, this.getAuthorId());
                newNote.setString(4, this.getContent());
                newNote.setLong(5, this.getTimeCreated());
                newNote.executeUpdate();

                ResultSet rs = newNote.getGeneratedKeys();

                this.setId(rs.getString(1));
            } else {
                PreparedStatement editNote = connection
                        .prepareStatement("UPDATE guild_notes set target_id = ?, author_id = ?, content = ?, time_created = ? where id = ? and guild_id = ?");

                editNote.setString(1, this.getTargetId());
                editNote.setString(2, this.getAuthorId());
                editNote.setString(3, this.getContent());
                editNote.setLong(4, this.getTimeCreated());
                editNote.setString(5, this.getId());
                editNote.setString(6, this.getGuildId());
                editNote.executeUpdate();
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

    public String getTargetId() {
        return targetId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getContent() {
        return content;
    }

    public long getTimeCreated() {
        return timeCreated;
    }


    public Note setId(String id) {
        this.id = id;
        return this;
    }

    public Note setGuildId(String guildId) {
        this.guildId = guildId;
        return this;
    }

    public Note setTargetId(String targetId) {
        this.targetId = targetId;
        return this;
    }

    public Note setAuthorId(String authorId) {
        this.authorId = authorId;
        return this;
    }

    public Note setContent(String content) {
        this.content = content;
        return this;
    }

    public Note setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
        return this;
    }
}
