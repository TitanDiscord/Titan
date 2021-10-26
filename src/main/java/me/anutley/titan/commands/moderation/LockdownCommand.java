package me.anutley.titan.commands.moderation;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.objects.GuildSettings;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class LockdownCommand {

    public static CommandData LockdownCommandData = new CommandData("lockdown", "Toggles lockdown for the server. Members are kicked while trying to join if lockdown is enabled")
            .addSubcommands(new SubcommandData("enable", "Enables lockdown for the server"))
            .addSubcommands(new SubcommandData("disable", "Disables lockdown for the server"))
            .addSubcommands(new SubcommandData("status", "Shows whether lockdown is enabled or not"));

    @Command(name = "lockdown.enable", description = "", permission = "command.moderation.lockdown.enable")
    public static void enableLockdown(SlashCommandEvent event) {

        new GuildSettings(event.getGuild().getId())
                .setLockdown(true)
                .save();

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("Lockdown has been enabled! Anyone who tries to join while lockdown is enabled will be kicked")
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();
    }

    @Command(name = "lockdown.disable", description = "", permission = "command.moderation.lockdown.disable")
    public static void disableLockdown(SlashCommandEvent event) {

        new GuildSettings(event.getGuild().getId())
                .setLockdown(false)
                .save();

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("Lockdown has been disabled! Lockdown has been disabled! Users can now join")
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();
    }

    @Command(name = "lockdown.status", description = "", permission = "command.moderation.lockdown.status")
    public static void lockdownStatus(SlashCommandEvent event) {

        boolean lockdown = new GuildSettings(event.getGuild().getId()).isLockdown();

        EmbedBuilder builder = new EmbedBuilder()
                .setColor(EmbedColour.NEUTRAL.getColour());

        if (lockdown) builder.setDescription("Lockdown is currently enabled!");
        else builder.setDescription("Lockdown is currently disabled!");

        event.replyEmbeds(builder.build()).queue();
    }

}
