package me.anutley.titan.listeners;

import me.anutley.titan.database.SQLiteDataSource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TagListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getMentionedMembers().size() == 1) {
            if (event.getMessage().getContentRaw().split(" ").length < 2) return;
            if (event.getMessage().getMentionedMembers().get(0).getUser().getId()
                    .equals(event.getJDA().getSelfUser().getId())) {

                String[] args = event.getMessage().getContentRaw().split(" ");
                try (final Connection connection = SQLiteDataSource
                        .getConnection();
                     PreparedStatement preparedStatement = connection
                             .prepareStatement("SELECT * FROM tags WHERE guild_id = ? AND trigger = ? ")) {

                    preparedStatement.setString(1, event.getGuild().getId());
                    preparedStatement.setString(2, args[1]);


                    try (final ResultSet resultSet = preparedStatement.executeQuery()) {

                        if (resultSet.next()) {
                            String title = resultSet.getString("title");
                            String content = resultSet.getString("content") != null ? resultSet.getString("content").replaceAll("\\\\n", "\n") : null;
                            String colour = resultSet.getString("colour");
                            String thumbnail = resultSet.getString("thumbnail");

                            EmbedBuilder builder = new EmbedBuilder();
                            builder.setTitle(title)
                                    .setDescription(content)
                                    .setColor(colour != null ? Color.decode(colour) : null)
                                    .setThumbnail(thumbnail);

                            event.getChannel().sendMessageEmbeds(builder.build()).queue();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}