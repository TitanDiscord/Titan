package me.anutley.titan.database.util;

import me.anutley.titan.database.SQLiteDataSource;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IllegalPingCountUtil {

    public static boolean resetCount(Member member, Guild guild) {
        try (final Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM ping_protection_data where guild_id = ? and member_id = ?")) {

            preparedStatement.setString(1, guild.getId());
            preparedStatement.setString(2, member.getId());

            ResultSet result = preparedStatement.executeQuery();

            if (!result.next()) {
                return false;
            } else {
                try (PreparedStatement updatePings = connection
                        .prepareStatement("update ping_protection_data set illegal_ping_count = 0 where guild_id = ? and member_id = ?")) {

                    updatePings.setString(1, guild.getId());
                    updatePings.setString(2, member.getId());

                    updatePings.executeUpdate();
                    connection.close();

                    return true;

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void kickUserForIllegalPing(GuildMessageReceivedEvent event) {
        event.getMessage().replyEmbeds(new EmbedBuilder()
                .setTitle(event.getMember().getUser().getAsTag() + " has been kicked for bypassing the illegal ping threshold!")
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();

        event.getMember().kick("bypassing the illegal ping count!").queue();
    }

    public static void banUserForIllegalPing(GuildMessageReceivedEvent event) {
        event.getMessage().replyEmbeds(new EmbedBuilder()
                .setTitle(event.getMember().getUser().getAsTag() + " has been banned for bypassing the illegal ping threshold!")
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();

        event.getMember().ban(0, "bypassing the illegal ping count!").queue();
    }

    public static void warnUserForIllegalPing(GuildMessageReceivedEvent event) {
        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("insert into warnings(guild_id, user_id, moderator_id, content) VALUES (?, ?, ?, ?)")) {

            preparedStatement.setString(1, event.getGuild().getId());
            preparedStatement.setString(2, event.getMember().getId());
            preparedStatement.setString(3, "SERVER");
            preparedStatement.setString(4, "Excessive mentioning of staff!");
            preparedStatement.executeUpdate();

            event.getMessage().replyEmbeds(new EmbedBuilder()
                    .setTitle(event.getMember().getUser().getAsTag() + " has been warned for bypassing the illegal ping threshold!")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
