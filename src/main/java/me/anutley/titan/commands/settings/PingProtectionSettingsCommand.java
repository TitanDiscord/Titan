package me.anutley.titan.commands.settings;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.ActionLogger;
import me.anutley.titan.database.objects.PingProtectionSettings;
import me.anutley.titan.database.objects.PingProtectionUserData;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.ArrayList;
import java.util.Arrays;

public class PingProtectionSettingsCommand {

    public static SubcommandGroupData PingProtectionSettingsCommandData = new SubcommandGroupData("pingprotection", "Controls Titan's ping protection settings")
            .addSubcommands(new SubcommandData("enable", "Enables ping protection for this guild"))
            .addSubcommands(new SubcommandData("disable", "Disables ping protection for this guild"))
            .addSubcommands(new SubcommandData("threshold", "Modifies the threshold of illegal pings someone can make before an action of your choice is taken")
                    .addOption(OptionType.INTEGER, "amount", "Amount of pings", true))
            .addSubcommands(new SubcommandData("add", "Adds a role to the ping protection")
                    .addOption(OptionType.ROLE, "role", "The role to add to the ping protection", true))
            .addSubcommands(new SubcommandData("remove", "Removes a role from the ping protection")
                    .addOption(OptionType.ROLE, "role", "The role to remove from the ping protection", true))
            .addSubcommands(new SubcommandData("list", "Lists all the roles that are protected from pings"))
            .addSubcommands(new SubcommandData("resetpings", "Resets a members illegal ping count")
                    .addOption(OptionType.USER, "member", "The member whose illegal ping count you want to reset", true))
            .addSubcommands(new SubcommandData("action", "Changes the action that Titan will take when someone surpasses the illegal ping threshold")
                    .addOption(OptionType.STRING, "action", "Possible options: warn, kick, ban", true));


    @Command(name = "settings.pingprotection.enable", description = "Enables ping protection for this guild", permission = "command.settings.pingprotection.enable")
    public static void enablePingProtection(SlashCommandEvent event) {

        PingProtectionSettings pingProtectionSettings = new PingProtectionSettings(event.getGuild().getId());

        if (pingProtectionSettings.isEnabled()) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Ping protection is already enabled!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
            return;
        }

        event.replyEmbeds(new EmbedBuilder()
                .setColor(EmbedColour.YES.getColour())
                .setDescription("Ping protection has been enabled!")
                .build()).queue();

        new ActionLogger(event.getGuild())
                .addAction("Ping protection enabled")
                .addModerator(event.getUser())
                .log();

        pingProtectionSettings.setEnabled(true).save();
    }

    @Command(name = "settings.pingprotection.disable", description = "Disables ping protection for this guild", permission = "command.settings.pingprotection.disable")
    public static void disablePingProtection(SlashCommandEvent event) {

        PingProtectionSettings pingProtectionSettings = new PingProtectionSettings(event.getGuild().getId());

        if (!pingProtectionSettings.isEnabled()) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Ping protection is already disabled!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
            return;
        }

        event.replyEmbeds(new EmbedBuilder()
                .setColor(EmbedColour.YES.getColour())
                .setDescription("Ping protection has been disabled!")
                .build()).queue();

        new ActionLogger(event.getGuild())
                .addAction("Ping protection disabled")
                .addModerator(event.getUser())
                .log();

        pingProtectionSettings.setEnabled(false).save();
    }

    @Command(name = "settings.pingprotection.threshold", description = "Modifies the threshold of illegal pings someone can make before an action of your choice is taken", permission = "command.settings.pingprotection.threshold")
    public static void modifyPingThreshold(SlashCommandEvent event) {

        int oldVal = new PingProtectionSettings(event.getGuild().getId()).getThreshold();

        new PingProtectionSettings(event.getGuild().getId())
                .setThreshold(Integer.parseInt(event.getOption("amount").getAsString()))
                .save();

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("The ping protection threshold has been set to " + event.getOption("amount").getAsString())
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();

        new ActionLogger(event.getGuild())
                .addAction("Ping protection threshold modified")
                .addModerator(event.getUser())
                .addOldValue(String.valueOf(oldVal))
                .addNewValue(event.getOption("amount").getAsString())
                .log();
    }

    @Command(name = "settings.pingprotection.add", description = "Adds a role to the ping protection", permission = "command.settings.pingprotection.add")
    public static void addRolePingProtection(SlashCommandEvent event) {

        PingProtectionSettings pingProtectionSettings = new PingProtectionSettings(event.getGuild().getId());
        ArrayList<String> pingProtectedRoles = new ArrayList<>();

        ActionLogger logger = new ActionLogger(event.getGuild())
                .addAction("Role added to ping protection")
                .addModerator(event.getUser())
                .addTarget(event.getOption("role").getAsRole());

        if (pingProtectionSettings.getRoles() != null) {

            ArrayList<String> pingProtectedRolesAsArrayList = new ArrayList<>(Arrays.asList(pingProtectionSettings.getRoles().toString()
                    .replaceAll("\\[", "")
                    .replaceAll("]", "")
                    .split(",")));

            if (pingProtectionSettings.getRoles().contains(event.getOption("role").getAsRole().getId())) {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("This role is already protected from pings!")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).queue();
                return;
            }

            event.replyEmbeds(new EmbedBuilder()
                    .setDescription(event.getOption("role").getAsRole().getAsMention() + " is now protected from pings!")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();

            pingProtectedRolesAsArrayList.add(event.getOption("role").getAsRole().getId());

            pingProtectedRoles = pingProtectedRolesAsArrayList;

            logger.log();

        } else {
            pingProtectedRoles.add(event.getOption("role").getAsRole().getId());

            event.replyEmbeds(new EmbedBuilder()
                    .setDescription(event.getOption("role").getAsRole().getAsMention() + " is now protected from pings!")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();

            logger.log();
        }
        pingProtectionSettings.setRoles(pingProtectedRoles).save();
    }

    @Command(name = "settings.pingprotection.remove", description = "Removes a role from the ping protection", permission = "command.settings.pingprotection.remove")
    public static void removeRolePingProtection(SlashCommandEvent event) {

        PingProtectionSettings pingProtectionSettings = new PingProtectionSettings(event.getGuild().getId());
        ArrayList<String> pingProtectedRoles = new ArrayList<>();

        if (pingProtectionSettings.getRoles() != null) {

            ArrayList<String> pingProtectedRolesAsArrayList = new ArrayList<>(Arrays.asList(pingProtectionSettings.getRoles().toString()
                    .replaceAll("\\[", "")
                    .replaceAll("]", "")
                    .split(",")));

            if (!pingProtectionSettings.getRoles().contains(event.getOption("role").getAsRole().getId())) {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("This role is not currently protected from pings!")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).queue();
                return;
            }

            event.replyEmbeds(new EmbedBuilder()
                    .setDescription(event.getOption("role").getAsRole().getAsMention() + " is no longer protected from pings!")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();

            pingProtectedRolesAsArrayList.remove(event.getOption("role").getAsRole().getId());

            new ActionLogger(event.getGuild())
                    .addAction("Role removed from ping protection")
                    .addModerator(event.getUser())
                    .addTarget(event.getOption("role").getAsRole())
                    .log();

            pingProtectedRoles = pingProtectedRolesAsArrayList;

        } else {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("This role is not protected from pings!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
        }
        pingProtectionSettings.setRoles(pingProtectedRoles).save();
    }

    @Command(name = "settings.pingprotection.list", description = "Lists all the roles that are protected from pings", permission = "command.settings.pingprotection.list")
    public static void listPingProtectedRoles(SlashCommandEvent event) {

        PingProtectionSettings pingProtectionSettings = new PingProtectionSettings(event.getGuild().getId());

        if (pingProtectionSettings.getRoles() == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("There are no ping protected roles!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Ping Protected Roles")
                .setColor(EmbedColour.NEUTRAL.getColour());

        StringBuilder content = new StringBuilder();

        for (String string : pingProtectionSettings.getRoles()) {
            content.append(event.getGuild().getRoleById(string.trim()).getAsMention());
        }

        builder.setDescription(content.toString());
        event.replyEmbeds(builder.build()).queue();
    }

    @Command(name = "settings.pingprotection.resetpings", description = "Resets a members illegal ping count", permission = "command.settings.pingprotection.resetpings")
    public static void resetIllegalPings(SlashCommandEvent event) {

        new PingProtectionUserData(event.getGuild().getId(), event.getOption("member").getAsUser().getId())
                .setCount(0)
                .save();

        event.replyEmbeds(new EmbedBuilder()
                .setDescription(event.getOption("member").getAsUser().getAsMention() + "'s pings have been reset!")
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();

        new ActionLogger(event.getGuild())
                .addAction("Illegal ping count reset")
                .addModerator(event.getUser())
                .addTarget(event.getOption("member").getAsUser())
                .log();
    }

    @Command(name = "settings.pingprotection.action", description = "Changes the action that Titan will take when someone surpasses the illegal ping threshold", permission = "command.settings.pingprotection.action")
    public static void changeActionWhenPingsSurpassThreshold(SlashCommandEvent event) {

        if (!event.getOption("action").getAsString().equals("warn") &&
                !event.getOption("action").getAsString().equals("kick") &&
                !event.getOption("action").getAsString().equals("ban")) {

            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("That is not a valid option. You can choose between: warn, kick or ban!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
        } else {

            PingProtectionSettings pingProtectionSettings = new PingProtectionSettings(event.getGuild().getId());

            String oldVal = pingProtectionSettings.getAction();

            pingProtectionSettings
                    .setAction(event.getOption("action").getAsString())
                    .save();

            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("The action when someone surpasses the illegal ping threshold has been set to `" + event.getOption("action").getAsString() + "`!")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();

            new ActionLogger(event.getGuild())
                    .addAction("Illegal ping count threshold action changed")
                    .addModerator(event.getUser())
                    .addOldValue(oldVal)
                    .addNewValue(event.getOption("action").getAsString())
                    .log();
        }
    }
}
