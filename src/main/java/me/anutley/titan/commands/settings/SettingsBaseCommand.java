package me.anutley.titan.commands.settings;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.PermissionUtil;
import me.anutley.titan.util.RoleUtil;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class SettingsBaseCommand extends Command {

    public static CommandData SettingsCommandData = new CommandData("settings", "Controls Titan's settings")
            .addSubcommandGroups(PingProtectionSettingsCommand.PingProtectionSettingsCommandData)
            .addSubcommandGroups(GuildSettingsCommand.GuildSettingsCommandData)
            .addSubcommandGroups(WelcomeSettingsCommand.WelcomeSettingsCommandData)
            .addSubcommandGroups(LeaveSettingsCommand.LeaveSettingsCommandData);



    PingProtectionSettingsCommand pingProtectionSettingsCommand = new PingProtectionSettingsCommand();
    GuildSettingsCommand guildSettingsCommand = new GuildSettingsCommand();
    WelcomeSettingsCommand welcomeSettingsCommand = new WelcomeSettingsCommand();
    LeaveSettingsCommand leaveSettingsCommand = new LeaveSettingsCommand();

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (!event.getName().equals("settings")) return;

        if (!RoleUtil.isAdmin(event.getMember())) {
            event.replyEmbeds(PermissionUtil.needAdminEmbed(event).build()).setEphemeral(true).queue();
            return;
        }

        if (event.getSubcommandGroup().equals("pingprotection")) pingProtectionSettingsCommand.pingProtectionSettingsCommand(event);
        if (event.getSubcommandGroup().equals("guild")) guildSettingsCommand.guildSettingsCommand(event);
        if (event.getSubcommandGroup().equals("welcome")) welcomeSettingsCommand.welcomeSettingsCommand(event);
        if (event.getSubcommandGroup().equals("leave")) leaveSettingsCommand.leaveSettingsCommand(event);
    }

    @Override
    public String getCommandName() {
        return "settings";
    }

    @Override
    public String getCommandDescription() {
        return "Controls Titan's settings";
    }

    @Override
    public String getCommandUsage() {
        return "/settings <module> <module settings> [input]";
    }


}
