package me.anutley.titan.database.objects;

import me.anutley.titan.database.SQLiteDataSource;
import me.anutley.titan.database.util.TagUtil;
import me.anutley.titan.util.exceptions.NoTagFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmbedTag {

    public String trigger;
    public String guildId;
    public String title;
    public String description;
    public String colour;
    public String thumbnail;

    public EmbedTag(String trigger, String guildId) {

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
                        .setTitle(result.getString("title"))
                        .setDescription(result.getString("content"))
                        .setColour(result.getString("colour"))
                        .setThumbnail(result.getString("thumbnail"));
            else
                this
                        .setTrigger(trigger)
                        .setGuildId(guildId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public EmbedTag save() {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM tags WHERE guild_id = ? AND trigger = ? ")) {

            preparedStatement.setString(1, this.getGuildId());
            preparedStatement.setString(2, this.getTrigger());

            ResultSet result = preparedStatement.executeQuery();

            if (!result.next()) {
                if (this.getTitle() == null || this.getGuildId() == null || this.getTrigger() == null) return this;

                PreparedStatement newTag = connection
                        .prepareStatement("INSERT INTO tags (guild_id, trigger, title, content, colour, thumbnail, embed_tag) VALUES (?, ?, ?, ?, ?, ?, ?)");

                newTag.setString(1, this.getGuildId());
                newTag.setString(2, this.getTrigger());
                newTag.setString(3, this.getTitle());
                newTag.setString(4, this.getDescription());
                newTag.setString(5, this.getColour());
                newTag.setString(6, this.getThumbnail());
                newTag.setBoolean(7, true);
                newTag.executeUpdate();
            } else {
                if (!TagUtil.isEmbedTag(trigger, guildId)) return this;

                PreparedStatement editTag = connection
                        .prepareStatement("UPDATE tags SET title = ?, content = ?, colour = ?, thumbnail = ? WHERE guild_id = ? and trigger = ?");

                editTag.setString(1, this.getTitle());
                editTag.setString(2, this.getDescription());
                editTag.setString(3, this.getColour());
                editTag.setString(4, this.getThumbnail());
                editTag.setString(5, this.getGuildId());
                editTag.setString(6, this.getTrigger());

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

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getColour() {
        return colour;
    }

    public String getThumbnail() {
        return thumbnail;
    }


    public EmbedTag setTrigger(String trigger) {
        this.trigger = trigger;
        return this;
    }

    public EmbedTag setGuildId(String guildId) {
        this.guildId = guildId;
        return this;
    }

    public EmbedTag setTitle(String title) {
        this.title = title;
        return this;
    }

    public EmbedTag setDescription(String description) {
        this.description = description;
        return this;
    }

    public EmbedTag setColour(String colour) {
        this.colour = colour != null ?
                colour.startsWith("#")
                        ? colour : "#" + colour
                : null;
        return this;
    }

    public EmbedTag setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

}
