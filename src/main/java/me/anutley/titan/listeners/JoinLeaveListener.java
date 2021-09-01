package me.anutley.titan.listeners;

import me.anutley.titan.database.SQLiteDataSource;
import me.anutley.titan.database.util.GuildSettingsDBUtil;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JoinLeaveListener extends ListenerAdapter {

    String userid;
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        if (GuildSettingsDBUtil.isLockdownEnabled(event.getGuild())) {
            userid = event.getUser().getId();
            return;
        }

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * from welcomes WHERE guild_id = ? ")) {

            preparedStatement.setString(1, event.getGuild().getId());
            ResultSet result = preparedStatement.executeQuery();

            if (!result.getBoolean("enabled")) {
                return;
            }

            if (result.getString("channel_id") == null) {
                return;
            }

            String message = replacePlaceholders(result.getString("message"), event.getGuild(), event.getUser());

            EmbedBuilder builder = new EmbedBuilder();

            if (message.contains("-showavatar")) {
                message = message.replaceAll("-showavatar", " ");
                builder.setThumbnail(event.getUser().getAvatarUrl());

            }

            event.getGuild().getTextChannelById(result.getString("channel_id")).sendMessageEmbeds(builder
                    .setColor(EmbedColour.YES.getColour())
                    .setDescription(message.trim()).build()).queue();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {

        if (event.getUser().getId().equals(userid)) {
            return;
        }

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * from leave WHERE guild_id = ? ")) {

            preparedStatement.setString(1, event.getGuild().getId());
            ResultSet result = preparedStatement.executeQuery();

            if (!result.getBoolean("enabled")) {
                return;
            }

            if (result.getString("channel_id") == null) {
                return;
            }

            String message = replacePlaceholders(result.getString("message"), event.getGuild(), event.getUser());

            EmbedBuilder builder = new EmbedBuilder();

            if (message.contains("-showavatar")) {
                message = message.replaceAll("-showavatar", " ");
                builder.setThumbnail(event.getUser().getAvatarUrl());

            }

            event.getGuild().getTextChannelById(result.getString("channel_id")).sendMessageEmbeds(builder
                    .setColor(EmbedColour.YES.getColour())
                    .setDescription(message.trim()).build()).queue();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public String replacePlaceholders(String str, Guild guild, User user) {
        return str.replaceAll("%user%", user.getAsMention())
                .replaceAll("%username%", user.getName())
                .replaceAll("%username_with_discriminator%", user.getAsTag())
                .replaceAll("%discriminator%", user.getDiscriminator())
                .replaceAll("%guild_name%", guild.getName());
    }

}
