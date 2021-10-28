package me.anutley.titan.database.objects;

import me.anutley.titan.Titan;
import me.anutley.titan.database.SQLiteDataSource;
import net.dv8tion.jda.api.entities.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildSettings {

    private String guildId;
    private String muteRoleId;
    private boolean lockdown;
    private boolean autoQuote;
    private boolean dmOnWarn;
    private String botLogChannelId;

    public GuildSettings(String guildId) {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM guild_settings WHERE guild_id = ?")) {

            preparedStatement.setString(1, guildId);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next())
                this
                        .setGuildId(guildId)
                        .setMuteRoleId(result.getString("mute_role"))
                        .setLockdown(result.getBoolean("lockdown"))
                        .setAutoQuote(result.getBoolean("auto_quote"))
                        .setDmOnWarn(result.getBoolean("dm_on_warn"))
                        .setBotLogsChannel(result.getString("bot_logs_channel"));
            else
                this
                        .setGuildId(guildId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public GuildSettings save() {

        try (final Connection connection = SQLiteDataSource
                .getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM guild_settings WHERE guild_id = ?")) {

            preparedStatement.setString(1, this.getGuildId());

            ResultSet result = preparedStatement.executeQuery();

            if (!result.next()) {
                if (this.getGuildId() == null) return this;

                PreparedStatement newGuildSettings = connection
                        .prepareStatement("INSERT INTO guild_settings (guild_id, mute_role, lockdown, auto_quote, dm_on_warn, bot_logs_channel) VALUES (?, ?, ?, ?, ?, ?)");

                newGuildSettings.setString(1, this.getGuildId());
                newGuildSettings.setString(2, this.getMuteRoleId());
                newGuildSettings.setBoolean(3, this.isLockdown());
                newGuildSettings.setBoolean(4, this.isAutoQuote());
                newGuildSettings.setBoolean(5, this.isDmOnWarn());
                newGuildSettings.setString(6, this.getBotLogChannelId());
                newGuildSettings.executeUpdate();
            } else {

                PreparedStatement editGuildSettings = connection
                        .prepareStatement("UPDATE guild_settings set mute_role = ?, lockdown = ?, auto_quote = ?, dm_on_warn = ?, bot_logs_channel = ? where guild_id = ?");

                editGuildSettings.setString(1, this.getMuteRoleId());
                editGuildSettings.setBoolean(2, this.isLockdown());
                editGuildSettings.setBoolean(3, this.isAutoQuote());
                editGuildSettings.setBoolean(4, this.isDmOnWarn());
                editGuildSettings.setString(5, this.getBotLogChannelId());
                editGuildSettings.setString(6, this.getGuildId());

                editGuildSettings.executeUpdate();
            }

        } catch (SQLException ignored) {
        }
        return this;
    }


    public String getGuildId() {
        return guildId;
    }

    public String getMuteRoleId() {
        return muteRoleId;
    }

    public Role getMuteRole() {
        return Titan.getJda().getRoleById(muteRoleId);
    }

    public boolean isLockdown() {
        return lockdown;
    }

    public boolean isAutoQuote() {
        return autoQuote;
    }

    public boolean isDmOnWarn() {
        return dmOnWarn;
    }

    public String getBotLogChannelId() {
        return botLogChannelId;
    }

    public GuildSettings setGuildId(String guildId) {
        this.guildId = guildId;
        return this;
    }

    public GuildSettings setMuteRoleId(String muteRoleId) {
        this.muteRoleId = muteRoleId;
        return this;
    }

    public GuildSettings setLockdown(boolean lockdown) {
        this.lockdown = lockdown;
        return this;
    }

    public GuildSettings setAutoQuote(boolean autoQuote) {
        this.autoQuote = autoQuote;
        return this;
    }

    public GuildSettings setDmOnWarn(boolean dmOnWarn) {
        this.dmOnWarn = dmOnWarn;
        return this;
    }

    public GuildSettings setBotLogsChannel(String botLogChannelId) {
        this.botLogChannelId = botLogChannelId;
        return this;
    }
}
