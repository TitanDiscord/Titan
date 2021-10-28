package me.anutley.titan.commands.moderation;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.ActionLogger;
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

    @Command(name = "lockdown.enable", description = "Enables lockdown for the server", permission = "command.moderation.lockdown.enable")
    public static void enableLockdown(SlashCommandEvent event) {

        GuildSettings guildSettings = new GuildSettings(event.getGuild().getId());

        if (guildSettings.isLockdown()) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Lockdown is already enabled")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
            return;
        }

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("Lockdown has been enabled! Anyone who tries to join while lockdown is enabled will be kicked")
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();

        guildSettings.setLockdown(true)
                .save();

        new ActionLogger(event.getGuild())
                .addAction("Lockdown enabled")
                .addModerator(event.getUser())
                .addOldValue(false)
                .addNewValue(true)
                .log();
    }

    @Command(name = "lockdown.disable", description = "Disables lockdown for the server", permission = "command.moderation.lockdown.disable")
    public static void disableLockdown(SlashCommandEvent event) {

        GuildSettings guildSettings = new GuildSettings(event.getGuild().getId());

        if (!guildSettings.isLockdown()) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Lockdown is already disabled")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
            return;
        }

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("Lockdown has been disabled! Lockdown has been disabled! Users can now join")
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();

        guildSettings.setLockdown(false)
                .save();

        new ActionLogger(event.getGuild())
                .addAction("Lockdown disabled")
                .addModerator(event.getUser())
                .addOldValue(true)
                .addNewValue(false)
                .log();
    }

    @Command(name = "lockdown.status", description = "Shows whether lockdown is enabled or not", permission = "command.moderation.lockdown.status")
    public static void lockdownStatus(SlashCommandEvent event) {

        boolean lockdown = new GuildSettings(event.getGuild().getId()).isLockdown();

        EmbedBuilder builder = new EmbedBuilder()
                .setColor(EmbedColour.NEUTRAL.getColour());

        if (lockdown) builder.setDescription("Lockdown is currently enabled!");
        else builder.setDescription("Lockdown is currently disabled!");

        event.replyEmbeds(builder.build()).queue();
    }

}
