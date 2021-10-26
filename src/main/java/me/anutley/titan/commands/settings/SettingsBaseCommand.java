package me.anutley.titan.commands.settings;

import me.anutley.titan.Titan;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class SettingsBaseCommand {

    public static CommandData SettingsCommandData = new CommandData("settings", "Controls Titan's settings")
            .addSubcommandGroups(GuildSettingsCommand.GuildSettingsCommandData)
            .addSubcommandGroups(LeaveSettingsCommand.LeaveSettingsCommandData)
            .addSubcommandGroups(PingProtectionSettingsCommand.PingProtectionSettingsCommandData)
            .addSubcommandGroups(WelcomeSettingsCommand.WelcomeSettingsCommandData);


    public void loadSuperclasses() {
        Titan.registerCommands(
                GuildSettingsCommand.class,
                LeaveSettingsCommand.class,
                PingProtectionSettingsCommand.class,
                WelcomeSettingsCommand.class
        );
    }

}
