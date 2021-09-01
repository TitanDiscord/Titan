package me.anutley.titan.listeners;

import me.anutley.titan.database.SQLiteDataSource;
import me.anutley.titan.database.util.IllegalPingCountUtil;
import me.anutley.titan.util.RoleUtil;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class IllegalPingListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {


        int ping = 0;
        int threshold;
        String action;

        if (!(event.getMessage().getMentionedMembers().size() > 0)) return;


        try (final Connection connection = SQLiteDataSource.getConnection();
             PreparedStatement pingProtectionSettings = connection
                     .prepareStatement("SELECT * FROM ping_protection_settings where guild_id = ?")) {

            pingProtectionSettings.setString(1, event.getGuild().getId());
            ResultSet pingProtectionSettingsResults = pingProtectionSettings.executeQuery();

            if (!pingProtectionSettingsResults.getBoolean("enabled")) return;
            if (event.getMember().getUser().isBot()) return;


            try (PreparedStatement pingProtectionData = connection
                    .prepareStatement("SELECT * FROM ping_protection_data where guild_id = ? and member_id = ?")) {

                pingProtectionData.setString(1, event.getGuild().getId());
                pingProtectionData.setString(2, event.getMember().getId());

                ResultSet pingProtectionDataResult = pingProtectionData.executeQuery();

                if (!pingProtectionDataResult.next()) {

                    PreparedStatement pingProtectionDataCreation = connection
                            .prepareStatement("INSERT INTO ping_protection_data (guild_id, member_id, illegal_ping_count) VALUES (?, ?, ?)");

                    pingProtectionDataCreation.setString(1, event.getGuild().getId());
                    pingProtectionDataCreation.setString(2, event.getMember().getId());
                    pingProtectionDataCreation.setInt(3, ping);

                    pingProtectionDataCreation.executeUpdate();
                }

                pingProtectionDataResult = pingProtectionData.executeQuery();


                ping = pingProtectionDataResult.getInt("illegal_ping_count");
                threshold = pingProtectionSettingsResults.getInt("threshold");
                action = pingProtectionSettingsResults.getString("action");

                if (pingProtectionSettingsResults.getString("roles") == null) return;

                String[] pingProtectedRoles = pingProtectionSettingsResults.getString("roles")
                        .replaceAll("\\[", "")
                        .replaceAll("]", "")
                        .split(",");


                for (Role role : event.getMember().getRoles()) {
                    if (Arrays.stream(pingProtectedRoles).anyMatch(r -> r.contains(role.getId())))
                        return; // Don't warn someone if they have a protected role
                }

                boolean hasPinged = false;
                for (Member member : event.getMessage().getMentionedMembers()) {
                    if (member.getUser().isBot()) return;

                    if (member.getId().equals(event.getMember().getId())) return;

                    for (String string : pingProtectedRoles) {
                        if (RoleUtil.hasRole(member, string)) {
                            hasPinged = true;
                            ping++;
                            break;
                        }
                    }
                }

                if (ping > 0 && hasPinged) {

                    if (ping % threshold == 0) {

                        connection.close();

                        switch (action) {
                            case ("kick"):
                                IllegalPingCountUtil.kickUserForIllegalPing(event);
                                break;

                            case ("ban"):
                                IllegalPingCountUtil.banUserForIllegalPing(event);
                                break;

                            case ("warn"):
                                IllegalPingCountUtil.warnUserForIllegalPing(event);
                                break;
                        }
                        ping = 0;
                        IllegalPingCountUtil.resetCount(event.getMember(), event.getGuild());
                    } else {

                        try (PreparedStatement pingProtectionDataUpdate = connection
                                .prepareStatement("UPDATE ping_protection_data set illegal_ping_count = ? where guild_id = ? and member_id = ?")) {

                            pingProtectionDataUpdate.setInt(1, ping);
                            pingProtectionDataUpdate.setString(2, event.getGuild().getId());
                            pingProtectionDataUpdate.setString(3, event.getMember().getId());
                            pingProtectionDataUpdate.executeUpdate();
                        }


                        EmbedBuilder builder = new EmbedBuilder()
                                .setTitle("Please don't ping members with ping protected roles")
                                .setColor(EmbedColour.NO.getColour());

                        if (ping == (threshold - 1))
                            builder.setFooter("Warning " + ping + " out of " + (threshold - 1) + " (Last Warning)");
                        else builder.setFooter("Warning " + ping + " out of " + (threshold - 1));


                        event.getChannel().sendMessageEmbeds(builder.build()).queue();

                        connection.close();
                    }
                }
            }


        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }
}
