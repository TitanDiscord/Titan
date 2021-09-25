package me.anutley.titan.database.objects;

import me.anutley.titan.database.SQLiteDataSource;
import me.anutley.titan.database.util.TagUtil;
import me.anutley.titan.util.exceptions.NoTagFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TextTag {

    public String trigger;
    public String guildId;
    public String content;

    public TextTag(String trigger, String guildId) {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM tags WHERE guild_id = ? AND trigger = ? ")) {

            preparedStatement.setString(1, guildId);
            preparedStatement.setString(2, trigger);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next())
                this
                        .setTrigger(trigger)
                        .setGuildId(guildId)
                        .setContent(result.getString("content"));
            else
                this
                        .setTrigger(trigger)
                        .setGuildId(guildId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TextTag save() {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM tags WHERE guild_id = ? AND trigger = ? ")) {

            preparedStatement.setString(1, this.getGuildId());
            preparedStatement.setString(2, this.getTrigger());

            ResultSet result = preparedStatement.executeQuery();

            if (!result.next()) {
                if (this.getContent() == null || this.getGuildId() == null || this.getTrigger() == null) return this;

                PreparedStatement newTag = connection
                        .prepareStatement("INSERT INTO tags (guild_id, trigger, content, embed_tag) VALUES (?, ?, ?, ?)");

                newTag.setString(1, this.getGuildId());
                newTag.setString(2, this.getTrigger());
                newTag.setString(3, this.getContent());
                newTag.setBoolean(4, false);
                newTag.executeUpdate();
            } else {
                if (TagUtil.isEmbedTag(trigger, guildId)) return this;
                PreparedStatement editTag = connection
                        .prepareStatement("UPDATE tags SET content = ? WHERE guild_id = ? and trigger = ?");

                editTag.setString(1, this.getContent());
                editTag.setString(2, this.getGuildId());
                editTag.setString(3, this.getTrigger());

                editTag.executeUpdate();
            }

        } catch (SQLException | NoTagFoundException ignored) {
        }
        return this;
    }

    public String getTrigger() {
        return trigger;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getContent() {
        return content;
    }


    public TextTag setTrigger(String trigger) {
        this.trigger = trigger;
        return this;
    }

    public TextTag setGuildId(String guildId) {
        this.guildId = guildId;
        return this;
    }

    public TextTag setContent(String content) {
        this.content = content;
        return this;
    }

}
